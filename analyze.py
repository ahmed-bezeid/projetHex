#!/usr/bin/env python3
"""
analyze.py — Extraction des logs HEX vers CSV
==============================================
Format des logs :
    DATA | Size:10 | BudgetRED:1024 | BudgetBLUE:2048 | Winner:RED

Usage :
    python3 analyze.py --log-dir ./logs --out-dir ./csv
"""

import re
import csv
import glob
import argparse
import os
from pathlib import Path
from collections import defaultdict

SCRIPT_ALGOS = {
    1: ("MCTS", "MCTS"),
    2: ("MCTS", "RAVE"),
    3: ("RAVE", "RAVE"),
    4: ("RAVE", "MCTS"),
}

LINE_RE = re.compile(
    r"DATA\s*\|\s*Size:(\d+)\s*\|\s*BudgetRED:(\d+)\s*\|\s*BudgetBLUE:(\d+)\s*\|\s*Winner:(RED|BLUE)"
)
FILE_RE = re.compile(r"size[_\-](\d+)[_\-](\d+)\.log", re.IGNORECASE)

# Ratios attendus (budgetBLUE / budgetRED)
RATIO_MAP = {
    1.0:  "1:1",
    2.0:  "1:2",
    5.0:  "1:5",
    0.5:  "2:1",
    0.2:  "5:1",
}

def get_ratio(budget_red, budget_blue):
    if budget_red == 0:
        return None
    r = round(budget_blue / budget_red, 4)
    closest = min(RATIO_MAP.keys(), key=lambda x: abs(x - r))
    if abs(closest - r) < 0.01:
        return RATIO_MAP[closest]
    return f"{budget_red}:{budget_blue}"


def parse_log_file(path: str) -> list:
    fname = os.path.basename(path)
    m = FILE_RE.search(fname)
    if not m:
        print(f"  [SKIP] Nom non reconnu : {fname}")
        return []

    size_file = int(m.group(1))
    script_id = int(m.group(2))
    algo_red, algo_blue = SCRIPT_ALGOS.get(script_id, ("?", "?"))
    matchup = f"{algo_red} vs {algo_blue}"

    records = []
    with open(path, encoding="utf-8", errors="replace") as f:
        for line in f:
            m2 = LINE_RE.search(line)
            if not m2:
                continue
            size_log    = int(m2.group(1))
            budget_red  = int(m2.group(2))
            budget_blue = int(m2.group(3))
            winner      = m2.group(4)
            ratio       = get_ratio(budget_red, budget_blue)

            records.append({
                "source_file": fname,
                "script_id":   script_id,
                "matchup":     matchup,
                "algo_red":    algo_red,
                "algo_blue":   algo_blue,
                "size":        size_log,
                "budget_red":  budget_red,
                "budget_blue": budget_blue,
                "ratio":       ratio,
                "winner":      winner,
                "red_wins":    1 if winner == "RED" else 0,
                "blue_wins":   1 if winner == "BLUE" else 0,
            })

    print(f"  OK  {fname:45s}  {len(records):6d} parties  ({matchup}, size={size_file})")
    return records


def make_aggregated(records: list) -> list:
    groups = defaultdict(list)
    for r in records:
        key = (r["size"], r["matchup"], r["algo_red"], r["algo_blue"],
               r["budget_red"], r["budget_blue"], r["ratio"])
        groups[key].append(r)

    rows = []
    for (size, matchup, algo_red, algo_blue, budget_red, budget_blue, ratio), group in sorted(groups.items()):
        n      = len(group)
        red_w  = sum(g["red_wins"] for g in group)
        blue_w = n - red_w
        rows.append({
            "size":         size,
            "matchup":      matchup,
            "algo_red":     algo_red,
            "algo_blue":    algo_blue,
            "budget_red":   budget_red,
            "budget_blue":  budget_blue,
            "ratio":        ratio,
            "n_games":      n,
            "red_wins":     red_w,
            "blue_wins":    blue_w,
            "red_winrate":  round(red_w  / n, 4),
            "blue_winrate": round(blue_w / n, 4),
        })
    return rows


def write_csv(path: Path, rows: list, fields: list):
    with open(path, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fields, extrasaction="ignore")
        writer.writeheader()
        writer.writerows(rows)
    print(f"      -> {path.name}  ({len(rows)} lignes)")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--log-dir", default="logs", help="Dossier contenant les .log")
    parser.add_argument("--out-dir", default="csv",  help="Dossier de sortie CSV")
    args = parser.parse_args()

    log_dir = Path(args.log_dir)
    out_dir = Path(args.out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)

    log_files = sorted(glob.glob(str(log_dir / "*.log")))
    if not log_files:
        print(f"[ERREUR] Aucun .log trouvé dans : {log_dir.resolve()}")
        return

    print(f"\n{'='*60}")
    print(f"  Parsing de {len(log_files)} fichier(s) log")
    print(f"{'='*60}")

    all_records = []
    for lf in log_files:
        all_records.extend(parse_log_file(lf))

    print(f"\n  Total parties : {len(all_records)}\n")

    if not all_records:
        print("[ERREUR] Aucune ligne DATA parsée.")
        return

    print(f"{'='*60}")
    print("  Écriture des CSV")
    print(f"{'='*60}")

    write_csv(
        out_dir / "all_games.csv",
        all_records,
        ["source_file", "script_id", "matchup", "algo_red", "algo_blue",
         "size", "budget_red", "budget_blue", "ratio", "winner", "red_wins", "blue_wins"]
    )

    agg = make_aggregated(all_records)
    write_csv(
        out_dir / "aggregated.csv",
        agg,
        ["size", "matchup", "algo_red", "algo_blue", "budget_red", "budget_blue",
         "ratio", "n_games", "red_wins", "blue_wins", "red_winrate", "blue_winrate"]
    )

    print(f"\n  Terminé ! CSV dans : {out_dir.resolve()}\n")


if __name__ == "__main__":
    main()
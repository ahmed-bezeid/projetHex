#!/bin/bash
# visualize.sh — Génère les graphes gnuplot depuis all_games.csv
# Axe X : somme des budgets (budget_red + budget_blue)
# Usage : bash visualize.sh [csv_dir] [plots_dir]

CSV_DIR="${1:-./csv}"
PLOTS_DIR="${2:-./plots}"
ALL_GAMES="$CSV_DIR/all_games.csv"

mkdir -p "$PLOTS_DIR"

MATCHUPS=("MCTS vs MCTS" "MCTS vs RAVE" "RAVE vs MCTS" "RAVE vs RAVE")
SIZES="4"

get_title() {
    case "$1" in
        "MCTS vs MCTS") echo "Evolution du taux de victoire RED par somme de budget (R:MCTS, B:MCTS)" ;;
        "MCTS vs RAVE")  echo "Evolution du taux de victoire RED par somme de budget (R:MCTS, B:RAVE)" ;;
        "RAVE vs MCTS")  echo "Evolution du taux de victoire RED par somme de budget (R:RAVE, B:MCTS)" ;;
        "RAVE vs RAVE")  echo "Evolution du taux de victoire RED par somme de budget (R:RAVE, B:RAVE)" ;;
    esac
}

for matchup in "${MATCHUPS[@]}"; do
    for size in $SIZES; do
        safe_matchup="${matchup// /_}"
        outfile="$PLOTS_DIR/${safe_matchup}_size${size}.png"
        TITLE=$(get_title "$matchup")

        RATIOS=("1:1" "1:2" "2:1" "1:5" "5:1")
        TMPFILES=()
        HAS_DATA=0

        for ratio in "${RATIOS[@]}"; do
            tmpfile=$(mktemp /tmp/gnuplot_XXXXXX.dat)
            TMPFILES+=("$tmpfile")

            python3 - "$ALL_GAMES" "$matchup" "$size" "$ratio" "$tmpfile" << 'PYEOF'
import sys, csv
from collections import defaultdict

all_games, matchup, size, ratio, outfile = sys.argv[1], sys.argv[2], int(sys.argv[3]), sys.argv[4], sys.argv[5]

with open(all_games) as f:
    rows = [r for r in csv.DictReader(f)
            if r["matchup"] == matchup and int(r["size"]) == size and r["ratio"] == ratio]

budget_data = defaultdict(lambda: {"n": 0, "wins": 0})

for row in rows:
    br = int(row["budget_red"])
    bb = int(row["budget_blue"])
    total_budget = br + bb          # ← SOMME au lieu de min
    if total_budget > 0:
        budget_data[total_budget]["n"] += 1
        budget_data[total_budget]["wins"] += int(row["red_wins"])

with open(outfile, "w") as f:
    for b in sorted(budget_data.keys()):
        d = budget_data[b]
        winrate = d["wins"] / d["n"] * 100 if d["n"] > 0 else 0
        f.write(f"{b} {winrate:.2f}\n")
PYEOF

            if [ -s "$tmpfile" ]; then
                HAS_DATA=1
            fi
        done

        if [ $HAS_DATA -eq 0 ]; then
            echo "  [SKIP] Pas de données : $matchup | size=$size"
            rm -f "${TMPFILES[@]}"
            continue
        fi

        GP_SCRIPT=$(mktemp /tmp/gnuplot_XXXXXX.gp)

        PLOT_CMD=""
        RATIO_LABELS=("1:1" "1:2" "2:1" "1:5" "5:1")
        COLORS=("#1f77b4" "#ff7f0e" "#d62728" "#2ca02c" "#9467bd")
        POINTS=(7 5 9 11 13)

        for i in "${!TMPFILES[@]}"; do
            if [ -s "${TMPFILES[$i]}" ]; then
                label="${RATIO_LABELS[$i]}"
                color="${COLORS[$i]}"
                pt="${POINTS[$i]}"
                if [ -z "$PLOT_CMD" ]; then
                    PLOT_CMD="\"${TMPFILES[$i]}\" using 1:2 title \"$label\" lc rgb \"$color\" pt $pt ps 1.2 lw 2"
                else
                    PLOT_CMD="$PLOT_CMD, \"${TMPFILES[$i]}\" using 1:2 title \"$label\" lc rgb \"$color\" pt $pt ps 1.2 lw 2"
                fi
            fi
        done

        PLOT_CMD="$PLOT_CMD, 50 notitle lc rgb \"gray\" lw 1 dt 2"

        cat > "$GP_SCRIPT" << GPEOF
set terminal png size 900,500 font "Arial,11"
set output "${outfile}"
set title "${TITLE}" font "Arial,10"
set xlabel "Somme des budgets (budget\_red + budget\_blue)" font "Arial,11"
set ylabel "Taux de victoire RED (%)" font "Arial,11"
set logscale x 2
set yrange [0:100]
set ytics 10
set grid
set key outside right top title "Ratio Budget" box
set label "Taille du plateau : ${size}" at graph 0.97, 0.05 right front
set style data linespoints
plot $PLOT_CMD
GPEOF

        gnuplot "$GP_SCRIPT"
        rm -f "$GP_SCRIPT" "${TMPFILES[@]}"
        echo "  OK $outfile"
    done
done

echo ""
echo "  Termine ! Graphes dans : $PLOTS_DIR"

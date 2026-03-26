#!/bin/bash

# Compilation du programme Java
javac -d bin -cp src src/Main.java src/Game/*.java src/Rave/*.java src/Mcts/*.java


# Paramètres d'expérimentation
MAX_SIZE=21
STARTINGPLAYER=("RED")
LOG_DIR="logs"
LOG_FILE="logs/experiments_size_10_2.log"


RAPPORT=(1 2 5)

# Créer le répertoire de logs
mkdir -p "$LOG_DIR"

calc_jobs() {
    local mem_per_job=2500  # 2.5GB par job
    local total_mem=$(free -m | awk '/Mem:/{print $7}')
    local safe_jobs=$(( total_mem / mem_per_job ))
    # Limiter à 4 jobs max même sur machines puissantes
    [[ $safe_jobs -gt 4 ]] && safe_jobs=4
    echo $safe_jobs
}

# Calculer le nombre de jobs (3/4 des cœurs)
CORES=$(nproc)
JOBS=$(( CORES * 3 / 4 ))
[[ $JOBS -lt 1 ]] && JOBS=1

JVM_OPTS="-Xmx2g -Xms512m -XX:+UseSerialGC -Xss256k"

# Générer toutes les commandes
commands=()

  echo $LOG_FILE


for i in $(seq 0 15); do
    size=10
    budget=$((2**i))

    echo "début budget $budget"

    for j in ${RAPPORT[@]}; do
        budgetRED=$budget
        budgetBLUE=$((budget * j))

        cmd1="java $JVM_OPTS -cp bin Main $size $STARTINGPLAYER $budgetRED MCTS $budgetBLUE RAVE >> $LOG_FILE"
        commands+=("$cmd1")

        if ((j > 1)); then
            cmd2="java $JVM_OPTS -cp bin Main $size $STARTINGPLAYER $budgetBLUE MCTS $budgetRED RAVE >> $LOG_FILE"
            commands+=("$cmd2")
        fi
    done

    echo "FIN budget $budget"

done

# Exécuter en parallèle avec verrouillage implicite de '>>'
printf "%s\n" "${commands[@]}" | xargs -P $JOBS -I {} bash -c 'eval "$1"' _ {}


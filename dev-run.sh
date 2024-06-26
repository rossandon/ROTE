#!/bin/bash
cd src/core
if ! ./gradlew classes; then
  exit
fi
export SPRING_PROFILES_ACTIVE='dev'

function parallel_commands() {
  for cmd in "$@"; do {
    echo "Process \"$cmd\" started";
    $cmd & pid=$!
    PID_LIST+=" $pid";
  } done

  trap "kill $PID_LIST" SIGINT

  echo "Parallel processes have started";

  wait $PID_LIST

  echo
  echo "All processes have completed";
}

parallel_commands "./gradlew tradingEngineService:bootRun --no-rebuild --console=plain" "./gradlew webService:bootRun --no-rebuild --console=plain -Dprofile=dev"
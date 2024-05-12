./gradlew classes
$env:SPRING_PROFILES_ACTIVE = 'dev'
wt --title "Trading Engine" pwsh.exe -c "./gradlew tradingEngineService:bootRun --no-rebuild --console=plain" `; nt --title "Web" pwsh.exe -c "./gradlew webService:bootRun --no-rebuild --console=plain -Dprofile=dev"
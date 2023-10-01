./gradlew classes
export SPRING_PROFILES_ACTIVE='dev'
./gradlew tradingEngineService:bootRun --no-rebuild --console=plain & ./gradlew webService:bootRun --no-rebuild --console=plain -Dprofile=dev
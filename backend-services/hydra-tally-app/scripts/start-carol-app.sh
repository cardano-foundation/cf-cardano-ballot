export SPRING_CONFIG_LOCATION=classpath:/application.properties,classpath:/application-devnet--carol.properties
export SPRING_PROFILES_ACTIVE=devnet--carol
./gradlew clean build && java -jar build/libs/hydra-tally-app-1.0.0-SNAPSHOT.jar

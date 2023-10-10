export SPRING_CONFIG_LOCATION=classpath:/application.properties,classpath:/application-devnet--alice.properties
export SPRING_PROFILES_ACTIVE=devnet--alice
./gradlew clean build && java -jar build/libs/hydra-tally-app-1.0.0-SNAPSHOT.jar

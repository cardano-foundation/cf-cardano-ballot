export SPRING_CONFIG_LOCATION=classpath:/application-devnet.properties,classpath:/application-devnet--bob.properties
export SPRING_PROFILES_ACTIVE=devnet--bob
./gradlew clean build && java -jar build/libs/hydra-tally-app-1.0.0-SNAPSHOT.jar

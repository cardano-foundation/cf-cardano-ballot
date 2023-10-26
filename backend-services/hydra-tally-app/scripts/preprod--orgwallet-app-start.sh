export SPRING_CONFIG_LOCATION=classpath:/application-preprod.properties,classpath:/application-preprod--orgwallet.properties
export SPRING_PROFILES_ACTIVE=preprod--orgwallet

#./gradlew clean build && java -jar build/libs/hydra-tally-app-1.0.0-SNAPSHOT.jar
./hydra-tally-app-1.0.0-SNAPSHOT.jar

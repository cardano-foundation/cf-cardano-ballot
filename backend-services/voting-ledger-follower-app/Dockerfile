<<<<<<< Updated upstream
FROM openjdk:20-jdk-slim
ADD ./build/libs/*SNAPSHOT.jar /app/app.jar
=======
FROM openjdk:17-jdk-slim
ADD ./build/libs/*.jar /app/app.jar
>>>>>>> Stashed changes
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM openjdk:20-jdk-slim
ADD ./build/libs/*SNAPSHOT.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

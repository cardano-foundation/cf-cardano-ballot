FROM openjdk:21-jdk-slim
ADD ./build/libs/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

FROM openjdk:18-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN ./gradlew clean bootJar -DskipTests

FROM openjdk:18-jdk-slim
COPY --from=build /app/build/libs/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

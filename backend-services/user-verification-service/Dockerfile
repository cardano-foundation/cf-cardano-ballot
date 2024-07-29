FROM openjdk:21-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN ./gradlew bootJar

FROM openjdk:21-jdk-slim AS runtime
COPY --from=build /app/build/libs/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

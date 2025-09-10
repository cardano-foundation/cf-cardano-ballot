FROM gradle:8-jdk21-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle bootJar

FROM openjdk:21-jdk-slim AS runtime
COPY --from=build /app/build/libs/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

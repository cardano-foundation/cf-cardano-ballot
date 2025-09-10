FROM gradle:8-jdk21-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle clean build

FROM openjdk:21-jdk-slim AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

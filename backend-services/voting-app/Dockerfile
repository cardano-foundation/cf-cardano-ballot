FROM openjdk:18-jdk-slim AS build
COPY . /build
WORKDIR /build
RUN ls -lart
RUN ./gradlew bootJar

FROM openjdk:18-jdk-slim
RUN ls -lart
COPY --from=build /build/libs/*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]

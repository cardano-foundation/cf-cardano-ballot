plugins {
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.4"
	java
}

group = "org.cardano.foundation"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot application for managing candidates"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

val mapstructVersion = "1.5.5.Final"
val lombokVersion = "1.18.30" // replace this with your actual lombok version

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// Database and Migration
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	// Validation
	implementation("jakarta.validation:jakarta.validation-api")
	implementation("org.hibernate.validator:hibernate-validator")

	// MapStruct
	implementation("org.mapstruct:mapstruct:$mapstructVersion")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

	// Lombok
	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")

	// Lombok-MapStruct Binding
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// Springdoc
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// spring-boot overridden dependencies:
	runtimeOnly("com.h2database:h2:2.2.224")
}

tasks.withType<JavaCompile> {
	options.annotationProcessorPath = configurations.annotationProcessor.get()
}

tasks.withType<Test> {
	useJUnitPlatform()
}

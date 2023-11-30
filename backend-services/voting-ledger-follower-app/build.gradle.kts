import cz.habarta.typescript.generator.JsonLibrary
import cz.habarta.typescript.generator.TypeScriptFileType
import cz.habarta.typescript.generator.TypeScriptOutputKind

plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	id("org.graalvm.buildtools.native") version "0.9.26"
    id("org.flywaydb.flyway") version "9.22.1"
	id("cz.habarta.typescript-generator") version "3.2.1263"
    id("com.github.ben-manes.versions") version "0.48.0"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
	jacoco
}

springBoot {
    buildInfo()
}

group = "org.cardano.foundation"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	testImplementation("io.rest-assured:rest-assured:5.3.2")
	testImplementation("org.wiremock:wiremock:3.2.0")

	implementation("org.springframework.boot:spring-boot-starter-cache")
	testCompileOnly("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("org.flywaydb:flyway-core")

	implementation("org.zalando:problem-spring-web-starter:0.29.1")

	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")

	testCompileOnly("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

	implementation("com.querydsl:querydsl-jpa")
    annotationProcessor("com.querydsl:querydsl-apt")

	implementation("com.bloxbean.cardano:cardano-client-crypto:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:0.5.0")
	implementation("com.bloxbean.cardano:aiken-java-binding:0.0.8")
	annotationProcessor("com.bloxbean.cardano:cardano-client-annotation-processor:0.5.0")

	implementation("com.bloxbean.cardano:yaci-store-spring-boot-starter:0.0.12")
	implementation("com.bloxbean.cardano:yaci-store-blocks-spring-boot-starter:0.0.12")
	implementation("com.bloxbean.cardano:yaci-store-transaction-spring-boot-starter:0.0.12")
	implementation("com.bloxbean.cardano:yaci-store-metadata-spring-boot-starter:0.0.12")

	implementation("io.blockfrost:blockfrost-java:0.1.3")

	implementation("io.vavr:vavr:0.10.4")

	runtimeOnly("org.postgresql:postgresql")

	implementation("org.cardanofoundation:cip30-data-signature-parser:0.0.11")
	implementation("org.cardanofoundation:cf-cardano-conversions-java:1.0.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	implementation("me.paulschwarz:spring-dotenv:4.0.0")

	// spring-boot overridden dependencies:
    runtimeOnly("com.h2database:h2:2.2.224") // GraalVM compatibility
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		csv.required.set(true)
	}
}

tasks {
    generateTypeScript {
        jsonLibrary = JsonLibrary.jackson2
        outputKind = TypeScriptOutputKind.module
        outputFileType = TypeScriptFileType.implementationFile
		classPatterns = listOf(
			"org.zalando.problem.Problem",
			"io.vavr.control.Either",
			"java.lang.Object",
			"java.lang.Number",
			"java.lang.Long",
			"java.util.Optional",
			"java.math.BigInteger",
			"org.cardano.foundation.voting.service.**",
			"org.cardano.foundation.voting.domain.**"
		).toMutableList()
		outputFile = "build/typescript-generator/voting-ledger-follower-types.ts"
    }
}

tasks.register<Copy>("buildAndCopyTypescriptTypes") {
	val uiProject = properties["ui_project_name"]

	if (uiProject != null) {
		println("buildAndCopyTypescriptTypes UI project name: $uiProject")

		dependsOn(tasks.generateTypeScript)
		from(layout.buildDirectory.file("typescript-generator/voting-ledger-follower-types.ts"))
		into(layout.projectDirectory.dir("../../ui/$uiProject/src/types"))
	} else {
		println("buildAndCopyTypescriptTypes ui_project_name param NOT set. Skipping.")
	}

}

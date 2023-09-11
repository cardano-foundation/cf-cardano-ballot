import cz.habarta.typescript.generator.JsonLibrary
import cz.habarta.typescript.generator.TypeScriptFileType
import cz.habarta.typescript.generator.TypeScriptOutputKind

plugins {
	java
	id("org.springframework.boot") version "3.0.8"
	id("io.spring.dependency-management") version "1.1.3"
	id("org.graalvm.buildtools.native") version "0.9.26"
    id("org.flywaydb.flyway") version "9.8.1"
	id("cz.habarta.typescript-generator") version "3.2.1263"
    id("com.github.ben-manes.versions") version "0.48.0"
}

springBoot {
    buildInfo()
}

group = "org.cardano.foundation"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
    mavenLocal()
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	testCompileOnly("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.zalando:problem-spring-web-starter:0.29.1")

	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("org.flywaydb:flyway-core")

    implementation("com.google.guava:guava:32.1.2-jre")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    implementation("com.bloxbean.cardano:yaci:0.2.2")

	compileOnly("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.projectlombok:lombok:1.18.28")

	testCompileOnly("org.projectlombok:lombok:1.18.28")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

	implementation("com.querydsl:querydsl-jpa")
    annotationProcessor("com.querydsl:querydsl-apt")

	implementation("com.nimbusds:nimbus-jose-jwt:9.31")
	implementation("com.google.crypto.tink:tink:1.10.0")

	implementation("com.bloxbean.cardano:cardano-client-crypto:0.5.0-beta3")
    implementation("com.bloxbean.cardano:cardano-client-address:0.5.0-beta3")
    implementation("com.bloxbean.cardano:cardano-client-metadata:0.5.0-beta3")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:0.5.0-beta3")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:0.5.0-beta3")
	implementation("com.bloxbean.cardano:cardano-client-cip30:0.5.0-beta3")

	implementation("io.blockfrost:blockfrost-java:0.1.3")

	implementation("io.vavr:vavr:0.10.4")

	runtimeOnly("org.postgresql:postgresql")

	implementation("org.cardanofoundation:merkle-tree-java:0.0.7")
	implementation("org.cardanofoundation:cip30-data-signature-parser:0.0.10")

    // spring-boot overridden dependencies:
    runtimeOnly("com.h2database:h2:2.2.222") // GraalVM compatibility
}

tasks.withType<Test> {
	useJUnitPlatform()
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
		outputFile = "build/typescript-generator/voting-app-types.ts"
    }
}

tasks.register<Copy>("buildAndCopyTypescriptTypes") {
	dependsOn(tasks.generateTypeScript)
    from(layout.buildDirectory.file("typescript-generator/voting-app-types.ts"))
    into(layout.projectDirectory.dir("../../ui/cip-1694/src/types"))
	into(layout.projectDirectory.dir("../../ui/summit-2023/src/types"))
}

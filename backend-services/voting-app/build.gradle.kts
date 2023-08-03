import cz.habarta.typescript.generator.JsonLibrary
import cz.habarta.typescript.generator.TypeScriptFileType
import cz.habarta.typescript.generator.TypeScriptOutputKind

plugins {
	java
	id("org.springframework.boot") version "3.0.7"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.graalvm.buildtools.native") version "0.9.22"
    id("org.flywaydb.flyway") version "9.8.1"
	id("cz.habarta.typescript-generator") version "3.2.1263"
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

extra["testcontainersVersion"] = "1.17.6"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	//implementation("org.springframework.boot:spring-boot-starter-jooq")
	testCompileOnly("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

	// TODO release to maven central
	implementation("org.cardanofoundation:cip30-data-signature-parser:0.0.9-SNAPSHOT")

    implementation("org.flywaydb:flyway-core")

	implementation("com.google.guava:guava:32.1.1-jre")

	implementation("org.zalando:problem-spring-web:0.29.1")
	implementation("org.zalando:jackson-datatype-problem:0.27.1")

	compileOnly("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.projectlombok:lombok:1.18.28")

	testCompileOnly("org.projectlombok:lombok:1.18.28")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

	implementation("com.querydsl:querydsl-jpa")
    annotationProcessor("com.querydsl:querydsl-apt")

	testImplementation("org.testcontainers:junit-jupiter")

	implementation("com.bloxbean.cardano:cardano-client-crypto:0.5.0-alpha.4")

	implementation("com.bloxbean.cardano:cardano-client-crypto:0.5.0-alpha.4")
    implementation("com.bloxbean.cardano:cardano-client-address:0.5.0-alpha.4")
    implementation("com.bloxbean.cardano:cardano-client-metadata:0.5.0-alpha.4")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:0.5.0-alpha.4")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:0.5.0-alpha.4")
	implementation("com.bloxbean.cardano:cardano-client-cip30:0.5.0-alpha.4")

	implementation("com.bloxbean.cardano:yaci-store-spring-boot-starter:0.0.11-beta")
	implementation("com.bloxbean.cardano:yaci-store-blocks-spring-boot-starter:0.0.11-beta")
	implementation("com.bloxbean.cardano:yaci-store-transaction-spring-boot-starter:0.0.11-beta")
	implementation("com.bloxbean.cardano:yaci-store-metadata-spring-boot-starter:0.0.11-beta")
	implementation("com.bloxbean.cardano:yaci-store-utxo-spring-boot-starter:0.0.11-beta")

	implementation("io.blockfrost:blockfrost-java:0.1.3")

	implementation("io.vavr:vavr:0.10.4")

    implementation("com.networknt:json-schema-validator:1.0.82")

	implementation("org.postgresql:postgresql")

	// TODO release to maven central
	implementation("org.cardanofoundation:merkle-tree-java:0.0.6-SNAPSHOT")
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
	}
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
		outputFile = "build/typescript-generator/backend-services-types.ts"
    }
}

tasks.register<Copy>("buildAndCopyTypescriptTypes") {
	dependsOn(tasks.generateTypeScript)
    from(layout.buildDirectory.file("typescript-generator/backend-services-types.ts"))
    into(layout.projectDirectory.dir("../../ui/cip-1694/src/types"))
}

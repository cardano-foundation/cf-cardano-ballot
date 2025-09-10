import cz.habarta.typescript.generator.JsonLibrary
import cz.habarta.typescript.generator.TypeScriptFileType
import cz.habarta.typescript.generator.TypeScriptOutputKind

plugins {
	java
	id("org.springframework.boot") version "3.3.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("cz.habarta.typescript-generator") version "3.2.1263"
    id("com.github.ben-manes.versions") version "0.52.0"
	jacoco
}

springBoot {
    buildInfo()
}

group = "org.cardano.foundation"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

val lombokVersion: String by project
val restAssuredVersion: String by project
val wiremockVersion: String by project
val problemSpringWebVersion: String by project
val cardanoClientVersion: String by project
val aikenJavaBindingVersion: String by project
val yaciStoreVersion: String by project
val blockfrostJavaVersion: String by project
val vavrVersion: String by project
val cip30DataSignatureParserVersion: String by project
val cfCardanoConversionsVersion: String by project
val springdocVersion: String by project
val springDotenvVersion: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
	testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")

	implementation("org.springframework.boot:spring-boot-starter-cache")
	testCompileOnly("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

	implementation("org.zalando:problem-spring-web-starter:$problemSpringWebVersion")

	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")

	testCompileOnly("org.projectlombok:lombok:$lombokVersion")
	testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

	implementation("com.querydsl:querydsl-jpa")
    annotationProcessor("com.querydsl:querydsl-apt")

	implementation("com.bloxbean.cardano:cardano-client-crypto:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:aiken-java-binding:$aikenJavaBindingVersion")
	annotationProcessor("com.bloxbean.cardano:cardano-client-annotation-processor:0.5.1")

	implementation("com.bloxbean.cardano:yaci-store-spring-boot-starter:$yaciStoreVersion")
	implementation("com.bloxbean.cardano:yaci-store-blocks-spring-boot-starter:$yaciStoreVersion")
	implementation("com.bloxbean.cardano:yaci-store-transaction-spring-boot-starter:$yaciStoreVersion")
	implementation("com.bloxbean.cardano:yaci-store-metadata-spring-boot-starter:$yaciStoreVersion")

	implementation("io.blockfrost:blockfrost-java:$blockfrostJavaVersion")

	implementation("io.vavr:vavr:$vavrVersion")

	runtimeOnly("org.postgresql:postgresql")

	implementation("org.cardanofoundation:cip30-data-signature-parser:$cip30DataSignatureParserVersion")
	implementation("org.cardanofoundation:cf-cardano-conversions-java:$cfCardanoConversionsVersion")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

	implementation("me.paulschwarz:spring-dotenv:$springDotenvVersion")

    runtimeOnly("com.h2database:h2")
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

fun isNonStable(version: String): Boolean {
	val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
	val regex = "^[0-9,.v-]+(-r)?$".toRegex()
	val isStable = stableKeyword || regex.matches(version)

	return isStable.not()
}


// https://github.com/ben-manes/gradle-versions-plugin
tasks.named("dependencyUpdates", com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class.java).configure {
	resolutionStrategy {
		componentSelection {
			all {
				if (isNonStable(candidate.version)) {
					reject("Release candidate")
				}
			}
		}
	}
}

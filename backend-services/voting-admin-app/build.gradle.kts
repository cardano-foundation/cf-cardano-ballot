plugins {
	java
	id("io.spring.dependency-management") version "1.1.7"
    id("com.github.ben-manes.versions") version "0.52.0"
	id("org.springframework.boot") version "3.3.6"
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

val lombokVersion: String by project
val springShellVersion: String by project
val cardanoClientVersion: String by project
val nimbusJoseJwtVersion: String by project
val tinkVersion: String by project
val blockfrostJavaVersion: String by project
val springDotenvVersion: String by project

dependencies {
	implementation("org.springframework.shell:spring-shell-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")

	testCompileOnly("org.projectlombok:lombok:$lombokVersion")
	testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

	implementation("com.bloxbean.cardano:cardano-client-crypto:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-address:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-metadata:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-cip30:$cardanoClientVersion")

	implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJoseJwtVersion")
	implementation("com.google.crypto.tink:tink:$tinkVersion")

	implementation("io.blockfrost:blockfrost-java:$blockfrostJavaVersion")

	implementation("me.paulschwarz:spring-dotenv:$springDotenvVersion")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.shell:spring-shell-dependencies:$springShellVersion")
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

tasks.withType<Test> {
	useJUnitPlatform()
}

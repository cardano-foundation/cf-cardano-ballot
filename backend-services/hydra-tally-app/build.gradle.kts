plugins {
	java
	id("io.spring.dependency-management") version "1.1.7"
	id("org.springframework.boot") version "3.3.6"
	id("com.github.ben-manes.versions") version "0.52.0"
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

val springShellVersion: String by project
val lombokVersion: String by project
val cardanoClientVersion: String by project
val hydraJavaVersion: String by project
val cip30DataSignatureParserVersion: String by project
val aikenJavaBindingVersion: String by project
val commonsCsvVersion: String by project
val vavrVersion: String by project
val problemSpringWebStarterVersion: String by project
val springDotenvVersion: String by project

dependencies {
	implementation("org.springframework.shell:spring-shell-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-reactor-netty")

	implementation("org.springframework.shell:spring-shell-starter")

	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")

	testCompileOnly("org.projectlombok:lombok:$lombokVersion")
	testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

	implementation("org.apache.commons:commons-csv:$commonsCsvVersion")

	implementation("org.cardanofoundation:cip30-data-signature-parser:$cip30DataSignatureParserVersion")

	implementation("com.bloxbean.cardano:cardano-client-crypto:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-address:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-metadata:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-cip30:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-core:$cardanoClientVersion")
	annotationProcessor("com.bloxbean.cardano:cardano-client-annotation-processor:$cardanoClientVersion")

	implementation("org.cardanofoundation:hydra-java-client:$hydraJavaVersion")
	implementation("org.cardanofoundation:hydra-java-cardano-client-lib-adapter:$hydraJavaVersion")
	implementation("org.cardanofoundation:hydra-java-reactive-reactor-client:$hydraJavaVersion")

    implementation("io.vavr:vavr:$vavrVersion")
    implementation("org.zalando:problem-spring-web-starter:$problemSpringWebStarterVersion")

	implementation("com.bloxbean.cardano:aiken-java-binding:$aikenJavaBindingVersion")

	implementation("me.paulschwarz:spring-dotenv:$springDotenvVersion")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.shell:spring-shell-dependencies:$springShellVersion")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
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
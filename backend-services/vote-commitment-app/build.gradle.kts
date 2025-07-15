plugins {
	java
	id("org.springframework.boot") version "3.3.6"
	id("io.spring.dependency-management") version "1.1.7"
  id("com.github.ben-manes.versions") version "0.52.0"
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
val problemSpringWebVersion: String by project
val guavaVersion: String by project
val junitJupiterVersion: String by project
val yaciVersion: String by project
val cardanoClientVersion: String by project
val blockfrostJavaVersion: String by project
val vavrVersion: String by project
val merkleTreeJavaVersion: String by project
val cip30DataSignatureParserVersion: String by project
val springDotenvVersion: String by project

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	testCompileOnly("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.zalando:problem-spring-web-starter:$problemSpringWebVersion")

	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("com.google.guava:guava:$guavaVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")

    implementation("com.bloxbean.cardano:yaci:$yaciVersion")

	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")

	testCompileOnly("org.projectlombok:lombok:$lombokVersion")
	testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

	implementation("com.querydsl:querydsl-jpa")
    annotationProcessor("com.querydsl:querydsl-apt")

	implementation("com.bloxbean.cardano:cardano-client-crypto:$cardanoClientVersion")
    implementation("com.bloxbean.cardano:cardano-client-address:$cardanoClientVersion")
    implementation("com.bloxbean.cardano:cardano-client-metadata:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:$cardanoClientVersion")
	implementation("com.bloxbean.cardano:cardano-client-cip30:$cardanoClientVersion")

	implementation("io.blockfrost:blockfrost-java:$blockfrostJavaVersion")

	implementation("io.vavr:vavr:$vavrVersion")

	runtimeOnly("org.postgresql:postgresql")

	implementation("org.cardanofoundation:merkle-tree-java:$merkleTreeJavaVersion")
	implementation("org.cardanofoundation:cip30-data-signature-parser:$cip30DataSignatureParserVersion")

	implementation("me.paulschwarz:spring-dotenv:$springDotenvVersion")

    // spring-boot overridden dependencies:
    runtimeOnly("com.h2database:h2")
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

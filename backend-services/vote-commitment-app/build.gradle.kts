plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
	id("org.graalvm.buildtools.native") version "0.9.27"
    id("com.github.ben-manes.versions") version "0.48.0"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
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
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	testCompileOnly("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.zalando:problem-spring-web-starter:0.29.1")

	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("com.google.guava:guava:32.1.2-jre")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    implementation("com.bloxbean.cardano:yaci:0.2.3")

	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")

	testCompileOnly("org.projectlombok:lombok:1.18.30")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

	implementation("com.querydsl:querydsl-jpa")
    annotationProcessor("com.querydsl:querydsl-apt")

	implementation("com.bloxbean.cardano:cardano-client-crypto:0.5.0")
    implementation("com.bloxbean.cardano:cardano-client-address:0.5.0")
    implementation("com.bloxbean.cardano:cardano-client-metadata:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-cip30:0.5.0")

	implementation("io.blockfrost:blockfrost-java:0.1.3")

	implementation("io.vavr:vavr:0.10.4")

	runtimeOnly("org.postgresql:postgresql")

	implementation("org.cardanofoundation:merkle-tree-java:0.0.7")
	implementation("org.cardanofoundation:cip30-data-signature-parser:0.0.11")

	implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // spring-boot overridden dependencies:
    runtimeOnly("com.h2database:h2:2.2.224") // GraalVM compatibility
}

tasks.withType<Test> {
	useJUnitPlatform()
}

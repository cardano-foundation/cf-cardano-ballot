plugins {
	java
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.graalvm.buildtools.native") version "0.9.22"
    id("org.flywaydb.flyway") version "9.8.1"
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
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
	implementation("com.bloxbean.cardano:cardano-client-cip30:0.5.0-alpha.4")

	implementation("com.bloxbean.cardano:yaci-store-spring-boot-starter:0.0.11-beta3")
	implementation("com.bloxbean.cardano:yaci-store-metadata-spring-boot-starter:0.0.11-beta3")

	implementation("io.vavr:vavr:0.10.4")

    implementation("com.networknt:json-schema-validator:1.0.82")

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

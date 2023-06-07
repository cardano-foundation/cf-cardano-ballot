plugins {
	java
	id("org.springframework.boot") version "3.0.7"
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
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	//implementation("org.springframework.boot:spring-boot-starter-jooq")
	testCompileOnly("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("io.micrometer:micrometer-registry-atlas")

    implementation("org.flywaydb:flyway-core")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    implementation("com.querydsl:querydsl-jpa")
    annotationProcessor("com.querydsl:querydsl-apt")

	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:junit-jupiter")

    implementation("com.bloxbean.cardano:cardano-client-crypto:0.4.3")
    implementation("com.bloxbean.cardano:cardano-client-address:0.4.3")
    implementation("com.bloxbean.cardano:cardano-client-metadata:0.4.3")

    // for canonical json support in jackson -> https://github.com/setl/canonical-json
    implementation("io.setl:canonical-json:2.3")
    implementation("com.networknt:json-schema-validator:1.0.82")

	implementation("org.postgresql:postgresql")

    implementation("com.google.guava:guava:31.1-jre")

	//implementation("org.cardanofoundation:merkle-tree-java:0.0.5-SNAPSHOT") // TODO release to maven central
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

plugins {
	java
	id("io.spring.dependency-management") version "1.1.3"
    id("org.graalvm.buildtools.native") version "0.9.27"
    id("com.github.ben-manes.versions") version "0.48.0"
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
	implementation("org.springframework.shell:spring-shell-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	compileOnly("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.projectlombok:lombok:1.18.28")

	testCompileOnly("org.projectlombok:lombok:1.18.28")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

    implementation("com.bloxbean.cardano:cardano-client-crypto:0.5.0-beta3")
    implementation("com.bloxbean.cardano:cardano-client-address:0.5.0-beta3")
    implementation("com.bloxbean.cardano:cardano-client-metadata:0.5.0-beta3")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:0.5.0-beta3")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:0.5.0-beta3")
	implementation("com.bloxbean.cardano:cardano-client-cip30:0.5.0-beta3")

	implementation("com.nimbusds:nimbus-jose-jwt:9.34")
	implementation("com.google.crypto.tink:tink:1.10.0")

	implementation("io.blockfrost:blockfrost-java:0.1.3")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.shell:spring-shell-dependencies:3.1.3")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

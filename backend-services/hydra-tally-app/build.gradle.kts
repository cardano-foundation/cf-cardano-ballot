plugins {
	java
	id("io.spring.dependency-management") version "1.1.3"
    id("org.graalvm.buildtools.native") version "0.9.27"
    id("com.github.ben-manes.versions") version "0.48.0"
	id("org.springframework.boot") version "3.1.4"
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

extra["springShellVersion"] = "3.1.4"

dependencies {
	implementation("org.springframework.shell:spring-shell-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("org.springframework.shell:spring-shell-starter")

	compileOnly("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.projectlombok:lombok:1.18.28")

	testCompileOnly("org.projectlombok:lombok:1.18.28")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

	implementation("org.apache.commons:commons-csv:1.10.0")

	implementation("org.cardanofoundation:cip30-data-signature-parser:0.0.11")

	implementation("com.bloxbean.cardano:cardano-client-crypto:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-address:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-metadata:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-quicktx:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-backend-blockfrost:0.5.0")
	implementation("com.bloxbean.cardano:cardano-client-cip30:0.5.0")

	implementation("org.cardanofoundation:hydra-java-client:0.0.6")
	implementation("org.cardanofoundation:hydra-java-cardano-client-lib-adapter:0.0.6")

	implementation("one.util:streamex:0.8.1")

    implementation("io.vavr:vavr:0.10.4")
    implementation("org.zalando:problem-spring-web-starter:0.29.1")

	implementation("com.bloxbean.cardano:aiken-java-binding:0.0.7")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.shell:spring-shell-dependencies:${property("springShellVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

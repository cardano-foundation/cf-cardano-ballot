import cz.habarta.typescript.generator.JsonLibrary
import cz.habarta.typescript.generator.TypeScriptFileType
import cz.habarta.typescript.generator.TypeScriptOutputKind

plugins {
	java
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	id("org.graalvm.buildtools.native")
    id("org.flywaydb.flyway")
	id("cz.habarta.typescript-generator")
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
    implementation("org.springframework.boot:spring-boot-starter:_")
	implementation("org.springframework.boot:spring-boot-starter-aop:_")
	implementation(Spring.boot.data.jpa)
	implementation(Spring.boot.data.rest)
	testCompileOnly(Spring.boot.test)
	implementation(Spring.boot.actuator)
	implementation(Spring.boot.validation)
	implementation(Spring.boot.web)

	testImplementation(Spring.boot.test)

	runtimeOnly("org.springframework.boot:spring-boot-properties-migrator:_")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus:_")

	implementation("com.google.guava:guava:_")

	implementation("org.zalando:problem-spring-web-starter:_")

	compileOnly("org.projectlombok:lombok:_")
	annotationProcessor("org.projectlombok:lombok:_")

	testCompileOnly("org.projectlombok:lombok:_")
	testAnnotationProcessor("org.projectlombok:lombok:_")

	implementation("org.flywaydb:flyway-core:_")

	implementation("com.googlecode.libphonenumber:libphonenumber:_")

	implementation("com.querydsl:querydsl-jpa:_")
    annotationProcessor("com.querydsl:querydsl-apt:_")

	implementation("com.bloxbean.cardano:cardano-client-address:_")

	implementation("software.amazon.awssdk:sns:_")

	implementation("io.vavr:vavr:_")

	runtimeOnly("org.postgresql:postgresql:_")
	runtimeOnly("com.h2database:h2:_")
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
		outputFile = "build/typescript-generator/user-verification-app-types.ts"
    }
}

tasks.register<Copy>("buildAndCopyTypescriptTypes") {
	dependsOn(tasks.generateTypeScript)
    from(layout.buildDirectory.file("typescript-generator/user-verification-app-types.ts"))
    into(layout.projectDirectory.dir("../../ui/summit-2023/src/types"))
}

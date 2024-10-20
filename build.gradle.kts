plugins {
	application
	id("org.springframework.boot") version "3.4.0-M3"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm")
}

group = "com.fazziclay"
version = "0.0.2-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

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
	implementation(files("./libs/api-1.0.jar"))

	implementation("com.squareup.retrofit2:retrofit:2.10.0")
	implementation("com.squareup.retrofit2:converter-gson:2.10.0")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.mysql:mysql-connector-j")
	implementation("com.google.code.gson:gson:2.11.0")
	implementation("jakarta.platform:jakarta.jakartaee-api:8.0.0")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}

plugins {
	java
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://www.jitpack.io" ) }}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.github.crazzyghost:alphavantage-java:1.7.0")
	runtimeOnly("com.mysql:mysql-connector-j")
	implementation("me.paulschwarz:spring-dotenv:4.0.0")
	implementation("io.github.cdimascio:dotenv-java:3.2.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// 3.15.0 is a commonly used version
	implementation("com.yahoofinance-api:YahooFinanceAPI:3.17.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

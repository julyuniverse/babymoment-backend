plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.benection"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}

	all {
		// console msg: Standard Commons Logging discovery in action with spring-jcl: please remove commons-logging.jar from classpath in order to avoid potential conflicts
		// 잠재적인 충돌을 피하기 위해서 commons-logging.jar 제거하기.
		exclude("commons-logging", "commons-logging")
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":client-aws"))
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0") // spring boot 3 버전 이상일 경우 open api 버전이 2 이상이어야 한다.
	implementation("com.google.code.gson:gson:2.11.0")
	implementation("commons-io:commons-io:2.16.1")
	implementation("com.auth0:jwks-rsa:0.22.1")
	implementation("com.auth0:java-jwt:4.4.0")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	enabled = false // 테스트 태스크 비활성화
}

springBoot {
	buildInfo()
}

tasks.getByName<Jar>("jar") {
	enabled = false // plain.jar 생성 방지
}

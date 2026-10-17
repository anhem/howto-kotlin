import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.spring") version "2.3.20"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // db
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // mics
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.jsonwebtoken:jjwt:0.12.7")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.1")
    implementation("com.linkedin.urls:url-detector:0.1.17")

    //  test
    testImplementation(platform("org.testcontainers:testcontainers-bom:2.0.5"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-resttestclient")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    testImplementation("org.assertj:assertj-core")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("com.github.anhem:test-populator:1.1.0")
    testImplementation("com.microsoft.playwright:playwright:1.49.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(kotlin("stdlib"))
}

val npmInstall by tasks.registering(Exec::class) {
    workingDir = file("frontend")
    commandLine("npm", "install")
    inputs.file("frontend/package.json")
    inputs.file("frontend/package-lock.json")
    outputs.dir("frontend/node_modules")
}

val npmBuild by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    workingDir = file("frontend")
    commandLine("npm", "run", "build")
    inputs.dir("frontend/src")
    inputs.file("frontend/index.html")
    inputs.file("frontend/vite.config.ts")
    inputs.file("frontend/package.json")
    inputs.file("frontend/tsconfig.json")
    outputs.dir("frontend/dist")
}

val npmTest by tasks.registering(Exec::class) {
    dependsOn(npmInstall)
    workingDir = file("frontend")
    commandLine("npm", "test")
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(npmBuild)
    from("frontend/dist") {
        into("static")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }
}

tasks.named<Test>("test") {
    dependsOn(npmTest)
}

tasks.clean {
    delete("frontend/dist")
}

tasks.register<BootRun>("bootRunLocal") {
    group = "application"
    description = "Runs the application with the 'local' profile"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set(tasks.named<BootRun>("bootRun").flatMap { it.mainClass })
    systemProperty("spring.profiles.active", "local")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

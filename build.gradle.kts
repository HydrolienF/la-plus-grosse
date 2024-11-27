plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

version = "0.1.0"
group = "fr.formiko.laplusgrosse"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "fr.formiko.laplusgrosse.App"
}

tasks.jar {
    manifest.attributes["Main-Class"] = "fr.formiko.laplusgrosse.App"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

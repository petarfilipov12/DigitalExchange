plugins {
    id("java")
    id("com.gradleup.shadow") version "9.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    named("main") {
        java.srcDir("sbe")
    }
}

dependencies {
    implementation("org.agrona:agrona:2.3.0-rc1")
    implementation("io.aeron:aeron-all:1.48.6")

    implementation("org.java-websocket:Java-WebSocket:1.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")

    implementation("ch.qos.logback:logback-classic:1.5.18")
}

tasks.shadowJar{
    manifest.attributes["Main-Class"] = "digital.exchange.ws.server.Main"
}

tasks.build{
    dependsOn(tasks.shadowJar)
}
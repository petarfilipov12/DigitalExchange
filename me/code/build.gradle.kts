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
    implementation("ch.qos.logback:logback-classic:1.5.18")
}

tasks.shadowJar{
    manifest.attributes["Main-Class"] = "digital.exchange.me.ClusterApp"
}

tasks.build{
    dependsOn(tasks.shadowJar)
}
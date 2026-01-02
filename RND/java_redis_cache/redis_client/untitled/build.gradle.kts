plugins {
    id("java")
    id("com.gradleup.shadow") version "9.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:6.1.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar{
    manifest.attributes["Main-Class"] = "org.example.Main"
}

tasks.build{
    dependsOn(tasks.shadowJar)
}
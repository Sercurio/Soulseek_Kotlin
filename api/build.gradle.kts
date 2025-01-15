plugins { kotlin("jvm") version "2.1.0" }

group = "fr.sercurio"

version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.ktor:ktor-network:2.2.3")

    implementation("org.slf4j:slf4j-simple:2.0.3")

    implementation("org.apache.commons:commons-configuration2:2.11.0")
}

tasks.test { useJUnitPlatform() }

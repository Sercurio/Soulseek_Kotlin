plugins { kotlin("jvm") version "1.9.22" }

group = "fr.sercurio"

version = "1.0-SNAPSHOT"

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

  testImplementation("org.junit.jupiter:junit-jupiter:5.11.1")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  implementation("io.ktor:ktor-network:2.2.3")

  implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
  implementation("org.slf4j:slf4j-simple:2.0.3")

  implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.1"))
  implementation("io.insert-koin:koin-core")
}

tasks.test { useJUnitPlatform() }

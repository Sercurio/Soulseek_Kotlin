plugins {
  id("com.android.application") version "7.2.2" apply true
  id("com.android.library") version "8.5.2" apply false
  id("org.jetbrains.kotlin.android") version "1.8.20-Beta" apply true
}

kotlin { jvmToolchain(16) }

android {
  namespace = "fr.sercurio.soulseek"
  compileSdk = 34
  defaultConfig {
    minSdk = 29
    applicationId = "fr.sercurio.soulseek.app"
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  buildFeatures { viewBinding = true }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
  }
}

dependencies {
  // Android Jetpack
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("androidx.preference:preference-ktx:1.2.1")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.recyclerview:recyclerview:1.3.2")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
  implementation("androidx.viewpager2:viewpager2:1.1.0")

  implementation("com.google.android.material:material:1.12.0")
  implementation("com.google.dagger:hilt-android:2.28-alpha")

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

  implementation("ch.acra:acra-mail:5.9.7")
  implementation("ch.acra:acra-toast:5.9.7")

  implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.25")

  implementation(project(":api"))
  implementation("androidx.test.ext:junit:1.2.1")

  testImplementation("junit:junit:4.13.2")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

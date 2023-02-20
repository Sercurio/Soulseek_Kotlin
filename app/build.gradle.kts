plugins {
    id("com.android.application") version "7.2.2" apply true
    id("com.android.library") version "7.4.0-beta02" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply true
}

android {
    compileSdkVersion(33)
    defaultConfig {
        minSdkVersion(19)
        applicationId = "fr.sercurio.soulseek.app"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Android Jetpack
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")

    implementation("ch.acra:acra:4.9.0")

    implementation("com.google.android.material:material:1.8.0")
    implementation("com.google.dagger:hilt-android:2.28-alpha")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
}
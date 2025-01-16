plugins {
    id("com.android.library") version "8.7.3" apply false
    id("com.android.application") version "7.2.2" apply true

    val kotlinVersion = "2.1.0"
    kotlin("android") version kotlinVersion
    kotlin("plugin.compose") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
}

kotlin { jvmToolchain(17) }

android {
    namespace = "fr.sercurio.soulseek"
    compileSdk = 35
    defaultConfig {
        minSdk = 29
        applicationId = "fr.sercurio.soulseek.app"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { viewBinding = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Android Jetpack
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    implementation("com.google.dagger:hilt-android:2.52")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    implementation("ch.acra:acra-mail:5.11.4")
    implementation("ch.acra:acra-toast:5.11.4")

    implementation(project(":api"))
    implementation("androidx.test.ext:junit:1.2.1")
    implementation("androidx.compose.runtime:runtime-android:1.7.6")
    implementation("androidx.compose.material3:material3-window-size-class-android:1.3.1")
    implementation("androidx.datastore:datastore-core-android:1.1.1")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.7.6")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")

    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Choose one of the following:
    // Material Design 3
    implementation("androidx.compose.material3:material3")
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    implementation("androidx.compose.ui:ui")
    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.9.3")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    // Optional - Integration with RxJava
    implementation("androidx.compose.runtime:runtime-rxjava2")

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation("androidx.compose.material:material-icons-core")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Add window size utils
    implementation("androidx.compose.material3.adaptive:adaptive")


    val navVersion = "2.8.5"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.1")

    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.4.0-alpha05")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.1.0"))
}
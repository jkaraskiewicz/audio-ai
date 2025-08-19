plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"
  id("org.jlleitschuh.gradle.ktlint")
}

android {
  namespace = "com.karaskiewicz.scribely"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.karaskiewicz.scribely"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    isCoreLibraryDesugaringEnabled = false
  }

  kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs +=
      listOf(
        "-opt-in=kotlin.RequiresOptIn",
        "-Xsuppress-version-warnings",
      )
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  lint {
    abortOnError = false
    warningsAsErrors = false
    checkReleaseBuilds = false
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  // Core Android
  implementation("androidx.core:core-ktx:1.17.0")
  implementation("androidx.appcompat:appcompat:1.7.1")
  implementation("androidx.activity:activity-compose:1.10.1")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")

  // Jetpack Compose BOM
  implementation(platform("androidx.compose:compose-bom:2025.08.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material-icons-extended")

  // Navigation Compose
  implementation("androidx.navigation:navigation-compose:2.9.3")

  // LiveData and ViewModel
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
  implementation("androidx.compose.runtime:runtime-livedata")

  // Preferences with Compose
  implementation("androidx.datastore:datastore-preferences:1.1.7")

  // Networking
  implementation("com.squareup.retrofit2:retrofit:3.0.0")
  implementation("com.squareup.retrofit2:converter-gson:3.0.0")
  implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

  // Koin for dependency injection
  implementation("io.insert-koin:koin-android:4.1.0")
  implementation("io.insert-koin:koin-androidx-compose:4.1.0")

  // Logging
  implementation("com.jakewharton.timber:timber:5.0.1")

  // Testing
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.mockito:mockito-core:5.19.0")
  testImplementation("org.mockito.kotlin:mockito-kotlin:6.0.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
  testImplementation("androidx.arch.core:core-testing:2.2.0")
  testImplementation("androidx.test:core:1.7.0")
  testImplementation("androidx.test:core-ktx:1.7.0")
  testImplementation("org.robolectric:robolectric:4.15.1")
  testImplementation(kotlin("test"))

  // Debug tools
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}

ktlint {
  version.set("1.0.1")
  android.set(true)
  ignoreFailures.set(false)
  reporters {
    reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
    reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
  }
  filter {
    exclude("**/test/**")
    exclude("**/androidTest/**")
  }
}

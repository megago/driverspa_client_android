plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.driverspa"
    compileSdk = 34

    // Correct way to enable ViewBinding in Kotlin DSL
    viewBinding {
        enable = true
    }

    defaultConfig {
        applicationId = "com.driverspa"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Switching to Java 8 compatibility
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

tasks.withType<JavaCompile>().configureEach {
    // Use addAll to avoid ambiguity in operator overloading
    options.compilerArgs.addAll(listOf("--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED"))
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.gson)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.pretty)
    implementation(libs.viewpager)
    implementation(libs.ripplebackground)
    implementation(libs.drawerlayout)

    implementation(project(":photoView"))
    implementation(project(":ProgressWheel"))
    implementation(project(":pullToRefresh_lib"))
    implementation(project(":clusterGoogleLib"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.gms:play-services-maps:18.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // If you still want to use ButterKnife, you can keep these lines
    implementation("com.jakewharton:butterknife:10.2.3")
    annotationProcessor("com.jakewharton:butterknife-compiler:10.2.3")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.sonarqube") version "4.4.1.3373"
}

android {
    namespace = "co.edu.uniandes.misw4203.equipo11.vinilos"
    compileSdk = 34

    defaultConfig {
        applicationId = "co.edu.uniandes.misw4203.equipo11.vinilos"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

task<JacocoReport>("codeCoverageReportDebug") {
    group = "Verification"
    description = "Generate Jacoco coverage report for the debug build."

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }

    sourceDirectories.setFrom("${project.projectDir}/src/main/java")
    classDirectories.setFrom("${project.buildDir}/tmp/kotlin-classes/debug")
    executionData.setFrom("${project.buildDir}/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
}

dependencies {
    val lifecycleVersion = "2.6.2"
    val navVersion = "2.7.4"
    val testJunitVersion = "1.1.5"
    val composeBomVersion = "2023.10.00"
    val roomVersion = "2.5.2"
    val fakerVersion = "1.15.0"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:$testJunitVersion")
    androidTestImplementation("androidx.test.ext:junit-ktx:$testJunitVersion")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("me.omico.compose:compose-material3-pullrefresh")

    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    ksp("com.github.bumptech.glide:ksp:5.0.0-rc01")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")

    implementation("com.android.volley:volley:1.2.1")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    testImplementation("io.github.serpro69:kotlin-faker:$fakerVersion")
}
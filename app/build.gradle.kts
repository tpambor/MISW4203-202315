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
        versionCode = 3
        versionName = "3.0"

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
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
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
        kotlinCompilerExtensionVersion = "1.5.4"
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

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    val coreVersion = "1.12.0"
    val lifecycleVersion = "2.6.2"
    val activityVersion = "1.8.1"
    val navVersion = "2.7.5"
    val testJunitVersion = "1.1.5"
    val composeBomVersion = "2023.10.01"
    val roomVersion = "2.6.1"
    val fakerVersion = "1.15.0"
    val mockkVersion = "1.13.8"
    val datastoreVersion = "1.0.0"
    val junitVersion = "4.13.2"
    val coroutinesTestVersion = "1.7.3"
    val glideVersion = "5.0.0-rc01"
    val glideComposeVersion = "1.0.0-beta01"
    val volleyVersion = "1.2.1"
    val gsonVersion = "2.10.1"
    val desugarVersion = "2.0.4"

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$desugarVersion")

    implementation("androidx.core:core-ktx:$coreVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:$activityVersion")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")
    implementation("me.omico.compose:compose-material3-pullrefresh")
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("com.github.bumptech.glide:compose:$glideComposeVersion")
    implementation("com.android.volley:volley:$volleyVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    ksp("com.github.bumptech.glide:ksp:$glideVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    testImplementation("io.github.serpro69:kotlin-faker:$fakerVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesTestVersion")

    androidTestImplementation("androidx.test.ext:junit:$testJunitVersion")
    androidTestImplementation("androidx.test.ext:junit-ktx:$testJunitVersion")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

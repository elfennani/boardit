import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/home/elfennani/keystores/main_key.jks")
            storePassword = "nizar2002"
            keyAlias = "elfennani"
            keyPassword = "nizar2002"
        }
    }
    namespace = "com.elfennani.boardit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.elfennani.boardit"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        properties.load(FileInputStream(project.rootProject.file("local.properties")))
        resValue("string", "GOOGLE_CLIENT_ID", properties.getProperty("GOOGLE_CLIENT_ID"))
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "30.0.3"
}

dependencies {

    // Work Manager
    val work_version = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")

    // Credential Manager
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Google Drive
    implementation("com.google.guava:guava:24.1-jre")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude(group = "org.apache.httpcomponents")
        exclude(module= "guava-jdk5")
    }
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0") {
        exclude(group = "org.apache.httpcomponents")
        exclude(module= "guava-jdk5")
    }

    // Jsoup
    implementation("org.jsoup:jsoup:1.17.2")

    // Zoomable
    implementation("net.engawapg.lib:zoomable:1.5.3")

    // MMKV
    implementation("com.tencent:mmkv:1.3.2")

    // Exif
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // Coil
    implementation("io.coil-kt:coil:2.5.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.4")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.49")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.hilt:hilt-work:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:2.49")

    // Compose Navigation
    val navVersion = "2.7.6"
    implementation("androidx.navigation:navigation-compose:$navVersion")


    // Other
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}

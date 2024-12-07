import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.routerush"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.routerush"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "MAPS_API_KEY", "\"${properties.getProperty("MAPS_API_KEY")}\"")
        buildConfigField("String", "MAPS_BASE_URL", "\"${properties.getProperty("MAPS_BASE_URL")}\"")
        buildConfigField("String", "AUTH_BASE_URL", "\"${properties.getProperty("DATABASE_BASE_URL")}\"")
    }

    buildTypes {
        debug {
            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())
            buildConfigField("String", "MAPS_API_KEY", "\"${properties.getProperty("MAPS_API_KEY")}\"")
            buildConfigField("String", "MAPS_BASE_URL", "\"${properties.getProperty("MAPS_BASE_URL")}\"")
            buildConfigField("String", "AUTH_BASE_URL", "\"${properties.getProperty("DATABASE_BASE_URL")}\"")
        }
        release {
            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())
            buildConfigField("String", "MAPS_API_KEY", "\"${properties.getProperty("MAPS_API_KEY")}\"")
            buildConfigField("String", "MAPS_BASE_URL", "\"${properties.getProperty("MAPS_BASE_URL")}\"")
            buildConfigField("String", "AUTH_BASE_URL", "\"${properties.getProperty("DATABASE_BASE_URL")}\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.logging.interceptor)

    implementation (libs.material.v190)

    implementation(libs.material)

    implementation (libs.androidx.datastore.preferences)

    implementation (libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.livedata.ktx)

    //maps 
    implementation (libs.play.services.maps.v1810)
    implementation (libs.play.services.places)
    implementation (libs.google.android.maps.utils)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
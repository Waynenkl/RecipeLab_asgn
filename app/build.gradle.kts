import java.io.FileInputStream
import java.util.Properties


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val apiConfigPropertiesFile = rootProject.file("api_config.properties")
val apiConfigProperties = Properties()
apiConfigProperties.load(FileInputStream(apiConfigPropertiesFile))


android {
    namespace = "student.inti.RecipeLab"
    compileSdk = 34

    defaultConfig {
        applicationId = "student.inti.RecipeLab"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true

    }
//    testOptions {
//        unitTests {
//            includeAndroidResources = true
//            animationsDisabled = true
//        }
//    }
    buildTypes.forEach {
        it.buildConfigField("String", "EDAMAM_API_ID",
            apiConfigProperties["EDAMAM_API_ID"] as String
        )
        it.buildConfigField("String", "EDAMAM_API_KEY",
            apiConfigProperties["EDAMAM_API_KEY"] as String
        )
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.parceler.api)
    implementation (libs.glide)
    implementation (libs.javax.annotation.api)
    implementation ("androidx.test:core:1.4.0")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.bom)
    implementation (libs.firebase.analytics)
    implementation (libs.firebase.database)
    implementation (libs.firebase.ui.database)
    annotationProcessor (libs.compiler)
    annotationProcessor (libs.parceler)
}

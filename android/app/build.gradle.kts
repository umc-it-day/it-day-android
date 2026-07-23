import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

val itdayProperties =
    Properties().apply {
        val file = rootProject.file("itday.properties")
        if (file.isFile) {
            file.inputStream().use { inputStream ->
                load(inputStream)
            }
        }
    }

fun configValue(
    name: String,
    defaultValue: String = "",
): String =
    providers
        .gradleProperty(name)
        .orElse(providers.environmentVariable(name))
        .orElse(itdayProperties.getProperty(name) ?: defaultValue)
        .get()

fun String.asBuildConfigString(): String = "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

fun configBoolean(
    name: String,
    defaultValue: Boolean,
): String {
    val value = configValue(name, defaultValue.toString())
    return value.toBooleanStrictOrNull()?.toString()
        ?: error("It-Day property '$name' must be true or false.")
}

detekt {
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

android {
    namespace = "com.example.itday"
    compileSdk {
        version =
            release(36) {
                minorApiLevel = 1
            }
    }

    defaultConfig {
        applicationId = "com.example.itday"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] =
            configValue("ITDAY_KAKAO_NATIVE_APP_KEY")
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "API_BASE_URL",
                configValue("ITDAY_DEBUG_API_BASE_URL", "http://10.0.2.2:8080/").asBuildConfigString(),
            )
            buildConfigField(
                "boolean",
                "USE_MOCK_DATA",
                configBoolean("ITDAY_DEBUG_USE_MOCK_DATA", true),
            )
            buildConfigField(
                "String",
                "KAKAO_NATIVE_APP_KEY",
                configValue("ITDAY_KAKAO_NATIVE_APP_KEY").asBuildConfigString(),
            )
            buildConfigField("String", "APP_ENV", "debug".asBuildConfigString())
        }
        release {
            buildConfigField(
                "String",
                "API_BASE_URL",
                configValue("ITDAY_RELEASE_API_BASE_URL").asBuildConfigString(),
            )
            buildConfigField(
                "boolean",
                "USE_MOCK_DATA",
                configBoolean("ITDAY_RELEASE_USE_MOCK_DATA", false),
            )
            buildConfigField(
                "String",
                "KAKAO_NATIVE_APP_KEY",
                configValue("ITDAY_KAKAO_NATIVE_APP_KEY").asBuildConfigString(),
            )
            buildConfigField("String", "APP_ENV", "release".asBuildConfigString())
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kakao.maps)
    implementation(libs.kakao.user)
    implementation(libs.play.services.location)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

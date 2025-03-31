plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.changelog)
}
fun properties(key: String) = providers.gradleProperty(key)

android {
    namespace = "com.lohni.darts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lohni.darts"
        minSdk = 26
        targetSdk = 35
        versionCode = properties("appVersionCode").get().toInt()
        versionName = properties("appVersion").get()
        version = versionName ?: "unspecified"

        //build system only includes language resource in the APK for these specified languages
        //should be the same as res/xml/locales_config.xml
        androidResources.localeFilters += "en"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = properties("repositoryUrl")
}

tasks.register("patchVersion") {
    this.notCompatibleWithConfigurationCache("")
    doLast {
        val newVersion: String = findProperty("newVersion").toString()
        val oldVersion: String = findProperty("version").toString()

        if (newVersion.isEmpty()) throw Exception()

        println("Bumping from $oldVersion to $newVersion")

        val currentProperties = projectDir.resolve("../gradle.properties").readText()
        val currentVersionCode = providers.gradleProperty("appVersionCode").get().toInt()

        val updatedProperties = currentProperties
            .replaceFirst("appVersion=$oldVersion", "appVersion=$newVersion")
            .replaceFirst("appVersionCode=$currentVersionCode", "appVersionCode=${currentVersionCode + 1}")

        projectDir.resolve("../gradle.properties").writeText(updatedProperties)
    }
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
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
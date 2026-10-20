import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val appVersionName = providers.gradleProperty("APP_VERSION_NAME").get()
val appVersionCode = providers.gradleProperty("APP_VERSION_CODE").get().toInt()
val productionAdMobAppId = providers.gradleProperty("ADMOB_APP_ID").orElse("").get()
val productionRewardedId = providers.gradleProperty("ADMOB_REWARDED_ID").orElse("").get()
check(productionAdMobAppId.isBlank() || productionAdMobAppId.matches(Regex("ca-app-pub-[0-9]{16}~[0-9]{10}"))) { "Invalid ADMOB_APP_ID" }
check(productionRewardedId.isBlank() || productionRewardedId.matches(Regex("ca-app-pub-[0-9]{16}/[0-9]{10}"))) { "Invalid ADMOB_REWARDED_ID" }
check(!productionAdMobAppId.contains("3940256099942544") && !productionRewardedId.contains("3940256099942544")) { "Production properties must not contain Google demo IDs" }
check(productionAdMobAppId.isBlank() == productionRewardedId.isBlank()) { "Provide both ADMOB_APP_ID and ADMOB_REWARDED_ID" }
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use(::load)
    }
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.orbitsalvagers.droneclicker"
        minSdk = 24
        targetSdk = 37
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }
    buildTypes {
        debug {
            manifestPlaceholders["adMobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("boolean", "TEST_ADS", "true")
            buildConfigField("boolean", "ADS_ENABLED", "true")
            buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            manifestPlaceholders["adMobAppId"] = productionAdMobAppId
            buildConfigField("boolean", "TEST_ADS", "false")
            buildConfigField("boolean", "ADS_ENABLED", "${productionAdMobAppId.isNotBlank() && productionRewardedId.isNotBlank()}")
            buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"$productionRewardedId\"")
            check(keystorePropertiesFile.exists()) {
                "Missing keystore.properties. Copy keystore.properties.example and provide release signing values."
            }
            signingConfig = signingConfigs.getByName("release")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

// Never distribute an advertising release with missing or demo publisher IDs.
tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    doFirst {
        check(productionAdMobAppId.isNotBlank() && productionRewardedId.isNotBlank()) {
            "Advertising release requires your ADMOB_APP_ID and ADMOB_REWARDED_ID. Demo IDs are not allowed."
        }
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

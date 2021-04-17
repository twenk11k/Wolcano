plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)

    defaultConfig {
        minSdkVersion(Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Sdk.TARGET_SDK_VERSION)

        applicationId = AppCoordinates.APP_ID
        versionCode = AppCoordinates.APP_VERSION_CODE
        versionName = AppCoordinates.APP_VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        renderscriptTargetApi = 19
        renderscriptSupportModeEnabled = true
        multiDexEnabled = true
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName("release") {
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
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }

    kapt {
        generateStubs = true
    }

}

repositories {
    maven(url = "https://jitpack.io")
    maven(url = "https://maven.fabric.io/public")
}

dependencies {

    implementation(SupportLibs.ANDROIDX_APPCOMPAT)
    implementation(SupportLibs.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(SupportLibs.ANDROIDX_CORE_KTX)
    implementation(SupportLibs.ANDROIDX_LEGACY_SUPPORT_V4)
    implementation(SupportLibs.ANDROIDX_VECTOR_DRAWABLE)
    implementation(SupportLibs.ANDROIDX_PERCENT_LAYOUT)
    implementation(SupportLibs.ANDROIDX_LEGACY_PREFERENCE_V14)
    implementation(SupportLibs.ANDROIDX_PALETTE)
    implementation(SupportLibs.ANDROIDX_RECYCLERVIEW)
    implementation(SupportLibs.ANDROID_MATERIAL)
    implementation(SupportLibs.ANDROIDX_FRAGMENT_KTX)
    implementation(SupportLibs.ANDROIDX_LIFECYCLE_EXTENSIONS)
    implementation(SupportLibs.ANDROIDX_LIFECYCLE_VIEWMODEL_KTX)
    implementation(SupportLibs.ANDROIDX_LIFECYCLE_VIEWMODEL_SAVEDSTATE)
    implementation(SupportLibs.ANDROIDX_LIFECYCLE_RUNTIME_KTX)
    implementation(SupportLibs.ANDROIDX_LIFECYCLE_LIVEDATA_KTX)
    implementation(SupportLibs.ANDROIDX_HILT_COMMON)
    implementation(SupportLibs.ANDROIDX_HILT_VIEWMODEL)
    kapt(SupportLibs.ANDROIDX_HILT_COMPILER)

    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_RULES)
    androidTestImplementation(AndroidTestingLib.ESPRESSO_CORE)
    androidTestImplementation(OtherLibs.DAGGER_HILT_TESTING)

    implementation(ThirdPartyLibs.JSOUP)
    implementation(ThirdPartyLibs.MATERIAL_DIALOGS_CORE)
    implementation(ThirdPartyLibs.MATERIAL_DIALOGS_INPUT)
    implementation(ThirdPartyLibs.MATERIAL_DIALOGS_COLOR)
    implementation(ThirdPartyLibs.RECYCLERVIEW_FASTSCROLL)
    implementation(ThirdPartyLibs.BUTTERKNIFE)
    annotationProcessor(ThirdPartyLibs.BUTTERKNIFE_COMPILER)
    implementation(ThirdPartyLibs.APACHE_COMMONS_IO)
    implementation(ThirdPartyLibs.RXBUS)
    implementation(ThirdPartyLibs.SLIDINGUPPANEL)
    implementation(ThirdPartyLibs.MATERIALSEARCHVIEW)
    implementation(ThirdPartyLibs.PICASSO)
    implementation(ThirdPartyLibs.SEEKARC)
    implementation(ThirdPartyLibs.SDP)
    implementation(ThirdPartyLibs.SANDWICH)
    implementation(ThirdPartyLibs.WHATIF)
    implementation(OtherLibs.RETROFIT)
    implementation(OtherLibs.RXJAVA2_ANDROID)
    implementation(OtherLibs.RXJAVA2)
    implementation(OtherLibs.RXANDROID)
    implementation(OtherLibs.LIFECYCLE_EXTENSIONS)
    implementation(OtherLibs.ROOM)
    implementation(OtherLibs.ROOM_KTX)
    kapt(OtherLibs.ROOM_COMPILER)
    implementation(OtherLibs.DAGGER_HILT)
    kapt(OtherLibs.DAGGER_HILT_COMPILER)
}

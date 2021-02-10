object Versions {
    const val ANDROIDX_TEST_EXT = "1.1.2"
    const val ANDROIDX_TEST = "1.3.0"
    const val APPCOMPAT = "1.2.0"
    const val CONSTRAINT_LAYOUT = "2.0.4"
    const val CORE_KTX = "1.3.2"
    const val ESPRESSO_CORE = "3.3.0"
    const val JUNIT = "4.13.1"
    const val KTLINT = "0.40.0"
    const val LEGACY_SUPPORT_V4 = "1.0.0"
    const val VECTOR_DRAWABLE = "1.1.0"
    const val PERCENT_LAYOUT = "1.0.0"
    const val LEGACY_PREFERENCE_V14 = "1.0.0"
    const val PALETTE = "1.0.0"
    const val RECYCLERVIEW = "1.1.0"
    const val MATERIAL = "1.2.1"
    const val JSOUP = "1.11.2"
    const val MATERIAL_DIALOGS = "3.3.0"
    const val RECYCLERVIEW_FASTSCROLL = "1.0.17"
    const val BUTTERKNIFE = "10.1.0"
    const val COMMONS_IO = "1.3.2"
    const val RXBUS = "2.0.0"
    const val SLIDINGUPPANEL = "3.4.0"
    const val MATERIALSEARCHVIEW = "1.4.0"
    const val PICASSO = "2.71828"
    const val SEEKARC = "1.2-kmod"
    const val SDP = "1.0.6"
    const val RETROFIT = "2.9.0"
    const val RX_JAVA_2 = "2.2.8"
    const val RX_JAVA_2_RX_ANDROID = "2.1.0"
    const val RX_ANDROID = "1.2.1"
    const val DAGGER = "2.31.1"
    const val DAGGER_COMPILER = "2.29.1"
    const val DAGGER_ANDROID_SUPPORT = "2.21"
    const val LIFECYCLE_EXTENSIONS = "1.1.1"
    const val ROOM = "2.2.6"
}

object BuildPluginsVersion {
    const val AGP = "4.1.1"
    const val DETEKT = "1.14.2"
    const val KOTLIN = "1.4.21"
    const val KTLINT = "9.4.1"
    const val VERSIONS_PLUGIN = "0.33.0"
}

object SupportLibs {
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val ANDROIDX_CONSTRAINT_LAYOUT =
        "com.android.support.constraint:constraint-layout:${Versions.CONSTRAINT_LAYOUT}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val ANDROIDX_LEGACY_SUPPORT_V4 =
        "androidx.legacy:legacy-support-v4:${Versions.LEGACY_SUPPORT_V4}"
    const val ANDROIDX_VECTOR_DRAWABLE =
        "androidx.vectordrawable:vectordrawable:${Versions.VECTOR_DRAWABLE}"
    const val ANDROIDX_PERCENT_LAYOUT =
        "androidx.percentlayout:percentlayout:${Versions.PERCENT_LAYOUT}"
    const val ANDROIDX_LEGACY_PREFERENCE_V14 =
        "androidx.legacy:legacy-preference-v14:${Versions.LEGACY_PREFERENCE_V14}"
    const val ANDROIDX_PALETTE = "androidx.palette:palette:${Versions.PALETTE}"
    const val ANDROIDX_RECYCLERVIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLERVIEW}"
    const val ANDROID_MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
}

object TestingLib {
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
}

object AndroidTestingLib {
    const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
}

object ThirdPartyLibs {
    const val JSOUP = "org.jsoup:jsoup:${Versions.JSOUP}"
    const val MATERIAL_DIALOGS_CORE =
        "com.afollestad.material-dialogs:core:${Versions.MATERIAL_DIALOGS}"
    const val MATERIAL_DIALOGS_INPUT =
        "com.afollestad.material-dialogs:input:${Versions.MATERIAL_DIALOGS}"
    const val MATERIAL_DIALOGS_COLOR =
        "com.afollestad.material-dialogs:color:${Versions.MATERIAL_DIALOGS}"
    const val RECYCLERVIEW_FASTSCROLL =
        "com.simplecityapps:recyclerview-fastscroll:${Versions.RECYCLERVIEW_FASTSCROLL}"
    const val BUTTERKNIFE = "com.jakewharton:butterknife:${Versions.BUTTERKNIFE}"
    const val BUTTERKNIFE_COMPILER = "com.jakewharton:butterknife-compiler:${Versions.BUTTERKNIFE}"
    const val APACHE_COMMONS_IO = "org.apache.commons:commons-io:${Versions.COMMONS_IO}"
    const val RXBUS = "com.hwangjr.rxbus:rxbus:${Versions.RXBUS}"
    const val SLIDINGUPPANEL = "com.sothree.slidinguppanel:library:${Versions.SLIDINGUPPANEL}"
    const val MATERIALSEARCHVIEW =
        "com.miguelcatalan:materialsearchview:${Versions.MATERIALSEARCHVIEW}"
    const val PICASSO = "com.squareup.picasso:picasso:${Versions.PICASSO}"
    const val SEEKARC = "com.github.kabouzeid:SeekArc:${Versions.SEEKARC}"
    const val SDP = "com.intuit.sdp:sdp-android:${Versions.SDP}"
}

object OtherLibs {
    const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
    const val RXJAVA2_ANDROID = "io.reactivex.rxjava2:rxandroid:${Versions.RX_JAVA_2_RX_ANDROID}"
    const val RXJAVA2 = "io.reactivex.rxjava2:rxjava:${Versions.RX_JAVA_2}"
    const val RXANDROID = "io.reactivex:rxandroid:${Versions.RX_ANDROID}"
    const val DAGGER = "com.google.dagger:dagger:${Versions.DAGGER}"
    const val DAGGER_COMPILER = "com.google.dagger:dagger-compiler:${Versions.DAGGER_COMPILER}"
    const val DAGGER_ANDROID_SUPPORT =
        "com.google.dagger:dagger-android-support:${Versions.DAGGER_ANDROID_SUPPORT}"
    const val LIFECYCLE_EXTENSIONS =
        "android.arch.lifecycle:extensions:${Versions.LIFECYCLE_EXTENSIONS}"
    const val ROOM = "androidx.room:room-runtime:${Versions.ROOM}"
    const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
}
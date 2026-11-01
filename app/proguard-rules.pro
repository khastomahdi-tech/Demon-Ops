# Demon Ops — proguard
-keep class com.demon.ops.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-dontwarn org.json.**

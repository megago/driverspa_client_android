# =====================================================================
# DriverSpa Client — R8 / ProGuard rules (release obfuscation)
# Legacy reflection-heavy stack: Retrofit 1.x, Gson, Otto, OrmLite,
# Picasso, ButterKnife, Splunk Mint.
# =====================================================================

# ---- Attributes (needed for Gson generics, annotations, crash readability) ----
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
# Keep line numbers but hide the original source file name in stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# =====================================================================
# App models — serialized by Gson (@SerializedName), persisted by OrmLite
# (@DatabaseField) and passed as Parcelables. Keep them wholesale.
# =====================================================================
-keep class com.driverspa.model.** { *; }
-keep class com.driverspa.Reference { *; }
-keep class com.driverspa.Reference$** { *; }

# Otto events are matched by type across the bus — keep them intact.
-keep class com.driverspa.util.otto.** { *; }

# Custom views inflated from XML (need their (Context, AttributeSet) ctor).
-keep class com.driverspa.view.** { *; }

# Parcelable CREATOR fields.
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Enums (Gson uses values()/valueOf()).
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# WebView JavaScript bridge (About Us / оферта screens).
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Native methods.
-keepclasseswithmembernames class * {
    native <methods>;
}

# =====================================================================
# Otto event bus — handlers are invoked via reflection on @Subscribe /
# @Produce annotations; R8 must not strip or rename those methods.
# =====================================================================
-keep class com.squareup.otto.** { *; }
-dontwarn com.squareup.otto.**
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe <methods>;
    @com.squareup.otto.Produce <methods>;
}

# =====================================================================
# Retrofit 1.x (square retrofit) + the API interface it reflects over.
# =====================================================================
-keep class retrofit.** { *; }
-keep interface retrofit.** { *; }
-dontwarn retrofit.**
-dontwarn rx.**
-keep interface com.driverspa.util.RetrofitClient$** { *; }
-keepclasseswithmembers interface * {
    @retrofit.http.** <methods>;
}

# ---- OkHttp 1.x / Okio ----
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

# =====================================================================
# Gson
# =====================================================================
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# =====================================================================
# OrmLite — model fields/tables read via reflection.
# =====================================================================
-keep class com.j256.ormlite.** { *; }
-keep interface com.j256.ormlite.** { *; }
-dontwarn com.j256.ormlite.**
-keepclassmembers class * {
    @com.j256.ormlite.field.DatabaseField <fields>;
    @com.j256.ormlite.field.ForeignCollectionField <fields>;
}
-keep @com.j256.ormlite.table.DatabaseTable class * { *; }

# =====================================================================
# Picasso
# =====================================================================
-keep class com.squareup.picasso.** { *; }
-dontwarn com.squareup.picasso.**

# =====================================================================
# ButterKnife — generated *_ViewBinding classes + annotated members.
# =====================================================================
-keep class butterknife.** { *; }
-dontwarn butterknife.**
-keep class **_ViewBinding { *; }
-keepclasseswithmembers class * {
    @butterknife.* <methods>;
}
-keepclasseswithmembers class * {
    @butterknife.* <fields>;
}

# =====================================================================
# Splunk Mint (legacy crash/analytics) + websocket.
# =====================================================================
-keep class com.splunk.mint.** { *; }
-dontwarn com.splunk.mint.**
-keep class org.java_websocket.** { *; }
-dontwarn org.java_websocket.**

# =====================================================================
# Firebase / Play services / misc — silence warnings (they ship their
# own consumer keep rules).
# =====================================================================
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn org.apache.http.**
-dontwarn javax.inject.**
-dontwarn com.squareup.javawriter.**

# =====================================================================
# PrettyTime — its per-locale i18n classes (org.ocpsoft.prettytime.i18n.
# Resources, Resources_ru, …) are loaded by fully-qualified name via
# ResourceBundle.getBundle(). R8 must not rename or strip them or the
# lookup throws MissingResourceException at runtime.
# =====================================================================
-keep class org.ocpsoft.prettytime.** { *; }
-keep class org.ocpsoft.prettytime.i18n.** { *; }
-dontwarn org.ocpsoft.prettytime.**

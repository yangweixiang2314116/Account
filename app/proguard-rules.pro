# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn android.support.**
-dontwarn android.os.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-keepattributes *Annotation*
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-keep class com.baoyz.swipemenulistview.** {*;}
-dontwarn com.baoyz.swipemenulistview.**

-keep class me.nereo.multi_image_selector.** {*;}
-dontwarn me.nereo.multi_image_selector.**

-keep class com.activeandroid.** { *; }
-keepattributes *Annotation*

-keep class cn.smssdk.** {*;}
-dontwarn cn.smssdk.**

-keep class com.jeremyfeinstein.slidingmenu.lib.** {*;}
-dontwarn com.jeremyfeinstein.slidingmenu.lib.**

-keep class me.relex.circleindicator.** {*;}
-dontwarn me.relex.circleindicator.**

-keep class com.miguelcatalan.materialsearchview.** {*;}
-dontwarn com.miguelcatalan.materialsearchview.**

-keep class com.mob.** {*;}
-dontwarn com.mob.**

-keep class cz.msebera.android.httpclient.** {*;}
-dontwarn cz.msebera.android.httpclient.**

-keep class com.tencent.wxop.** {*;}
-dontwarn com.tencent.wxop.**

-keep class com.melnykov.fab.** {*;}
-dontwarn com.melnykov.fab.**

-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.picasso.**

-keep class com.loopj.android.http.** {*;}
-dontwarn com.loopj.android.http.**

-keep class com.nineoldandroids.** {*;}
-dontwarn com.nineoldandroids.**

-keep class android.support.design.widget.** {*;}
-dontwarn android.support.design.widget.**

-dontwarn com.umeng.analytics.**
-keep class com.umeng.analytics.** {*;}

-dontwarn u.aly.**
-keep class u.aly.** {*;}

-keepattributes SourceFile,LineNumberTable
-keep class com.parse.*{ *; }
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**
-keepclasseswithmembernames class * {
    native <methods>;
}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.widget
-keep public class * extends android.support.v4.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-assumenosideeffects class android.util.Log{ public static *** d(...); public static *** i(...); }

-keepattributes EnclosingMethod
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program Files\MySDK\android-sdk-windows/tools/proguard/proguard-android.txt
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
#-optimizations !code/simplification/cast,!field/*,!class/merging/*,!class/unboxing/enum,!code/allocation/variable,!method/marking/private
#-optimizationpasses 5
#-allowaccessmodification
#-dontpreverify
#
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-verbose
#-dontwarn com.google.**
#-dontwarn org.spongycastle.**
#-dontwarn com.csr.**
#-dontwarn javax.annotation.Nullable
#
## ADDED
#-dontshrink
#-dontobfuscate
#
##使用注解需要添加
#-keepattributes *Annotation*
#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService
#
##指定不混淆所有的JNI方法
#-keepclasseswithmembernames class * {
#    native <methods>;
#}
#
##所有View的子类及其子类的get、set方法都不进行混淆
#-keepclassmembers public class * extends android.view.View {
#   void set*(***);
#   *** get*();
#}
#
##不混淆Activity中参数类型为View的所有方法
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}
#
##不混淆Enum类型的指定方法
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
##不混淆Parcelable和它的子类，还有Creator成员变量
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}
##不混淆R类里及其所有内部static类中的所有static变量字段
#-keepclassmembers class **.R$* {
#    public static <fields>;
#}
#
##不提示兼容库的错误警告
#-dontwarn android.support.**
#
##不混淆所有的com.axalent.model包下的类和这些类的所有成员变量
#-keep class com.axalent.model.**{*;}
#-keep class com.csr.csrmeshdemo2.data.model.**{*;}
#-keep class com.csr.csrmeshdemo2.data.model.device.**{*;}
#-keep class com.csr.csrmeshdemo2.data.model.device.states.**{*;}
#-keep class com.csr.csrmeshdemo2.data.model.places.**{*;}
#-keep class com.csr.csrmeshdemo2.data.model.status.**{*;}
#
##不混淆Serializable接口的子类中指定的某些成员变量和方法
#-keepclassmembers class * implements java.io.Serializable {
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}

-dontwarn
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
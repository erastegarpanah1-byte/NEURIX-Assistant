# Neurix ProGuard Rules
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepattributes *Annotation*
-keepattributes InnerClasses
-keep class kotlin.Metadata { *; }
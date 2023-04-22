
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items). 
-keep,allowobfuscation,allowshrinking interface retrofit2.Call 
-keep,allowobfuscation,allowshrinking class retrofit2.Response 

# With R8 full mode generic signatures are stripped for classes that are not 
# kept. Suspend functions are wrapped in continuations where the type argument 
# is used. 
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation 

#This rule will instruct ProGuard to keep all classes in the 
#dev.haqim.productdummy.core.data.remote.response package and 
#its sub-packages, including the ProductsResponse and ProductsItemResponse classes.
-keep class dev.haqim.productdummy.core.data.remote.response.** { *; }
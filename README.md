on-task
=======
This is interactive watch face application for android wear.

Please install from following link.
https://play.google.com/store/apps/details?id=com.btrax.on_task

This code does not contain .config.AppConsts.java, so please create by yourself.

for wear .config.AppConsts.java
```
public class AppConsts {
    public static final String CRITTERCISM_APP_ID = "********";

    public static boolean isDebug() {
        return false;
    }
}
```
for mobile, .AppConsts.java
```
public class AppConsts {
    public static final String FLURRY_API_KEY = "*********";
    public static final String CRITTERCISM_APP_ID = "***********";

    public static final String GUIDE_URL = "http://*********";
    
    public static boolean isDebug() {
        return false;
    }
}
```

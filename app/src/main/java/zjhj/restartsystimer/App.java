package zjhj.restartsystimer;

import android.app.Application;
import android.content.Context;

/**
 * CreateTime 2018/12/11 17:38
 * Author LiuShiHua
 * Description：
 */

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}

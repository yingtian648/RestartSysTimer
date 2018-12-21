package zjhj.restartsystimer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * CreateTime 2018/12/12 09:09
 * Author LiuShiHua
 * Description：在本地存储开始时间【防止app运行时被杀死后又被重启了，丢失上次重启时间】
 */

public class SharedUtil {
    private static final String SHARED_FILE_NAME = "ReStartSysTimer";
    private static SharedPreferences sp;

    public static void setStartTime(long startTime) {
        if (sp == null)
            sp = App.getContext().getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong("startTime", startTime).commit();
    }

    public static long getStartTime() {
        if (sp == null)
            sp = App.getContext().getSharedPreferences(SHARED_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getLong("startTime", 0);
    }
}

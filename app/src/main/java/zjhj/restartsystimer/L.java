package zjhj.restartsystimer;

import android.util.Log;


/**
 * CreateTime 2017/7/14 09:10
 * Author LiuShiHua
 * Description：
 */

public class L {
    private static String TAG = "RESTARTSYS----------->";

    public static void d(String msg) {
        if (msg == null) {
            msg = "null";
        }
        Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (msg == null) {
            msg = "null";
        }
        Log.e(TAG, msg);
    }

    private static String lineNum(Throwable throwable) {
        StackTraceElement[] trace = throwable.getStackTrace();
        // 下标为0的元素是上一行语句的信息, 下标为1的才是调用printLine的地方的信息
        StackTraceElement tmp = trace[0];
        return tmp.getClassName() + "." + tmp.getMethodName()
                + "(" + tmp.getFileName() + ":" + tmp.getLineNumber() + ")";
    }
}

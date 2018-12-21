package zjhj.restartsystimer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

public class MainActivity extends Activity {
    private String TAG = "RestartSys------>";
    private Date curDate;
    private long timeFirst;
    private SimpleDateFormat formatter;
    private String startTimeStr;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置activity背景色透明
//        startMainTimer();
//        startRestartTimerService();
        openDialogWindow();
        finish();
    }

    private void startMainTimer() {
        timeFirst = System.currentTimeMillis();
        formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        curDate = new Date(timeFirst);
        startTimeStr = formatter.format(curDate);
        L.d("MainActivity --- timer start --- " + formatter.format(curDate));
//        startActivityTimer();
    }

    private void startActivityTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                long tNow = System.currentTimeMillis();
                curDate = new Date(tNow);
                L.d("MainActivity --- timer is running " + startTimeStr + " --- " + formatter.format(curDate));
                if ((tNow - timeFirst) / (1000 * 60 * 60) >= 1) {
                    rebootAction();
                } else {
                    startActivityTimer();
                }
            }
        }, 60 * 1000);
    }
	
	//开启悬浮窗【悬浮窗只有宽高紧为1px,肉眼开不见】
    private void openDialogWindow() {
        Intent intent = new Intent(this, MWindowService.class);
        startService(intent);
        finish();
    }

    private void startRestartTimerService() {
        Intent intent = new Intent(this, ReStartService.class);
        startService(intent);
        finish();
    }
	
	//重启方法 1
    private void restartSys() {
        Log.e(TAG, "restartSys");
        try {
            //获得ServiceManager类
            Class ServiceManager = Class.forName("android.os.ServiceManager");
            //获得ServiceManager的getService方法
            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
            //调用getService获取RemoteService
            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);
            //获得IPowerManager.Stub类
            Class cStub = Class.forName("android.os.IPowerManager$Stub");
            //获得asInterface方法
            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
            //调用asInterface方法获取IPowerManager对象
            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
            //获得shutdown()方法
            Method shutdown = oIPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
            //调用shutdown()方法
            shutdown.invoke(oIPowerManager, false, true);
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

	//重启方法 2
    private void restartSysBroadcast() {
        Log.e(TAG, "restartSysBroadcast");
        Intent reboot = new Intent(Intent.ACTION_REBOOT);
        reboot.setAction(Intent.ACTION_REBOOT);
        reboot.putExtra("nowait", 1);
        reboot.putExtra("interval", 1);
        reboot.putExtra("window", 0);
        sendBroadcast(reboot);
    }

    //重启方法 3 root之后可以重启成功
    public void rebootAction() {
        String cmd = "su -c reboot";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error! Fail to reboot.", Toast.LENGTH_SHORT).show();
        }
    }
}

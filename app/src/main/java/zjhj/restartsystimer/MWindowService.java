package zjhj.restartsystimer;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

/**
 * CreateTime 2018/12/11 14:15
 * Author LiuShiHua
 * Description：悬浮窗【允许存在于其他app的上面——看manifest中的权限】
 */

public class MWindowService extends Service {
    private Context context;
    private LinearLayout linearLayout;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;
    private ImageButton imageButton1;
    private long timeFirst = 0;
    private SimpleDateFormat formatter;
    private Date curDate;
    private String startTimeStr;
    private AlertDialog dialog;
    // 重启时间间隔（小时）
    private final int REBOOT_HOUR_SET = 4;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int statusBarHeight = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        createToucher();
        formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        timeFirst = SharedUtil.getStartTime();
        if (timeFirst == 0) {
            timeFirst = System.currentTimeMillis();
            curDate = new Date(timeFirst);
            startTimeStr = formatter.format(curDate);
            SharedUtil.setStartTime(timeFirst);
        } else {
            curDate = new Date(timeFirst);
            startTimeStr = formatter.format(curDate);
        }
        L.d("MWindowService --- onCreate");
        startTimer();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    private void startTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                long tNow = System.currentTimeMillis();
                curDate = new Date(tNow);
                L.d("MWindowService --- timer is running(rebootHour " + REBOOT_HOUR_SET + ") " + startTimeStr + " --- " + formatter.format(curDate));
                if ((tNow - timeFirst) / (1000 * 60 * 60) >= REBOOT_HOUR_SET) { //重启判断
                    SharedUtil.setStartTime(0);
                    rebootAction();
                } else {
                    stopSelf();
                    handler.sendEmptyMessage(0);
                }
            }
        }, 60 * 1000);
    }

	//重启系统方法
    public void rebootAction() {
        L.d("MWindowService --- rebootAction");
        String cmd = "su -c reboot";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error! Fail to reboot.", Toast.LENGTH_SHORT).show();
        }
    }

	//创建悬浮窗
    private void createToucher() {
        params = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        params.type = TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        params.width = 1;
        params.height = 1;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_window, null);
        windowManager.addView(linearLayout, params);

        linearLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        imageButton1 = (ImageButton) linearLayout.findViewById(R.id.image);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            //双击退出
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(MWindowService.this).setTitle("是否确认退出？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopSelf();
                    }
                }).create();
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));//调用系统dialog
                dialog.show();
            }
        });
        linearLayout.setOnTouchListener(new View.OnTouchListener() {//拖动布局
            @Override
            public boolean onTouch(View v, MotionEvent event) { //ImageButton我放在了布局中心，布局一共300dp
                params.x = (int) event.getRawX() - 300; //这就是状态栏偏移量用的地方
                params.y = (int) event.getRawY() - 150 - statusBarHeight;
                windowManager.updateViewLayout(linearLayout, params);
                return false;
            }
        });

    }

    @Override
    public void onDestroy() { //用imageButton检查悬浮窗还在不在，这里可以不要。优化悬浮窗时要用到。
        L.d("MWindowService --- onDestroy");
        if (imageButton1 != null) {
            windowManager.removeView(linearLayout);
        }
        super.onDestroy();
    }
}

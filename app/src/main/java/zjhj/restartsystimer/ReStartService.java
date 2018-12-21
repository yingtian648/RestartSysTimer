package zjhj.restartsystimer;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * CreateTime 2018/12/10 15:34
 * Author LiuShiHua
 * Description：重启服务，容易被系统杀死，之后改用了悬浮窗之后没那么容易被杀死了
 */

public class ReStartService extends IntentService {

    private long timeFirst;
    private SimpleDateFormat formatter;
    private Date curDate;

    public ReStartService() {
        super("ReStartService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        L.d("ReStartService --- onHandleIntent");
        timeFirst = System.currentTimeMillis();
        formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        curDate = new Date(timeFirst);
        startTimer();
    }

    private void startTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                long tNow = System.currentTimeMillis();
                curDate = new Date(tNow);
                L.d("ReStartService --- timer is running --- " + formatter.format(curDate));
                if ((tNow - timeFirst) / (1000 * 60 * 65) >= 4) {
                    rebootAction();
                } else {
                    startTimer();
                }
            }
        }, 60 * 1000);
    }

    public void rebootAction() {
        L.d("ReStartService --- rebootAction");
        String cmd = "su -c reboot";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error! Fail to reboot.", Toast.LENGTH_SHORT).show();
        }
    }
}

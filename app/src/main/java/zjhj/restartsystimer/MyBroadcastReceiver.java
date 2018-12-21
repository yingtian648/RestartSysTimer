package zjhj.restartsystimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * CreateTime 2018/12/10 15:28
 * Author LiuShiHua
 * Description：
 */

public class MyBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent it=new Intent(context,MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
    }
}

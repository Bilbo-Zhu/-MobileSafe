package mobilesafe.zjn.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.db.AddressDao;

/**
 * Created by zjn on 2015/12/13.
 */
public class AddressService extends Service {
    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager mWM;
    private View view;

    private int startX;
    private int startY;
    private int winWidth;
    private int winHeight;

    private SharedPreferences mPrefs;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPrefs = getSharedPreferences("config", MODE_PRIVATE);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new OutCallReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = AddressDao.getAddress(incomingNumber);

                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mWM != null && view != null) {
                        mWM.removeView(view);// 从window中移除view
                        view = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();

            String address = AddressDao.getAddress(number);

            showToast(address);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        tm.listen(listener, PhoneStateListener.LISTEN_NONE);// 停止来电监听

        unregisterReceiver(receiver);// 注销广播
    }

    private void showToast(String text) {
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        // 获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.gravity = Gravity.LEFT + Gravity.TOP;

        int lastX = mPrefs.getInt("lastX", 0);
        int lastY = mPrefs.getInt("lastY", 0);

        params.x = lastX;
        params.y = lastY;

        // view = new TextView(this);
        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[]{R.drawable.white1,
                R.drawable.orange1, R.drawable.blue,
                R.drawable.gray, R.drawable.green};
        int style = mPrefs.getInt("address_style", 0);

        view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

        TextView tvText = (TextView) view.findViewById(R.id.tv_number);
        tvText.setText(text);
        mWM.addView(view, params);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;

                        params.x += dx;
                        params.y += dy;

                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > winWidth - view.getWidth()) {
                            params.x = winWidth - view.getWidth();
                        }
                        if (params.y > winHeight - view.getHeight()) {
                            params.y = winHeight - view.getHeight();
                        }

                        mWM.updateViewLayout(view, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_UP:

                        SharedPreferences.Editor edit = mPrefs.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.commit();

                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }
}

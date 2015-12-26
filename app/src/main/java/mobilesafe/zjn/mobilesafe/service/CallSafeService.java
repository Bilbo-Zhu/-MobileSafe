package mobilesafe.zjn.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import mobilesafe.zjn.mobilesafe.db.BlackNumberDao;

/**
 * Created by zjn on 2015/12/18.
 */
public class CallSafeService extends Service {

    private BlackNumberDao dao;
    private InnerReceiver receiver;
    private TelephonyManager tm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackNumberDao(this);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listen = new MyPhoneStateListener();
        tm.listen(listen, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver,filter);
    }

    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {// 短信最多140字节,
                // 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();// 短信来源号码
                String messageBody = message.getMessageBody();// 短信内容
                //通过短信的电话号码查询拦截的模式
                String mode = dao.findNumber(originatingAddress);
                /**
                 * 黑名单拦截模式
                 * 1 全部拦截 电话拦截 + 短信拦截
                 * 2 电话拦截
                 * 3 短信拦截
                 */
                if(mode.equals("1")){
                    abortBroadcast();
                }else if(mode.equals("3")){
                    abortBroadcast();
                }
                //智能拦截模式 发票  你的头发漂亮 分词
                if(messageBody.contains("fapiao")){
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state){
                //电话铃响的状态
                case TelephonyManager.CALL_STATE_RINGING:

                    String mode = dao.findNumber(incomingNumber);
                    /**
                     * 黑名单拦截模式
                     * 1 全部拦截 电话拦截 + 短信拦截
                     * 2 电话拦截
                     * 3 短信拦截
                     */
                    if(mode.equals("1")|| mode.equals("2")){
                        System.out.println("挂断黑名单电话号码");

                        Uri uri = Uri.parse("content://call_log/calls");

                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));

                        //挂断电话
                        endCall();

                    }
                    break;
            }
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {

        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class MyContentObserver extends ContentObserver {
        String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //当数据改变的时候调用的方法
        @Override
        public void onChange(boolean selfChange) {

            getContentResolver().unregisterContentObserver(this);

            deleteCallLog(incomingNumber);

            super.onChange(selfChange);
        }
    }

    //删掉电话号码
    private void deleteCallLog(String incomingNumber) {

        Uri uri = Uri.parse("content://call_log/calls");

        getContentResolver().delete(uri,"number=?",new String[]{incomingNumber});

    }
}

package mobilesafe.zjn.mobilesafe.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.bean.AppInfo;
import mobilesafe.zjn.mobilesafe.engine.AppInfos;

/**
 * Created by zjn on 2015/12/19.
 */
public class AppOperationActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivReturn;
    private TextView tv_appname;
    private RelativeLayout re_running;
    private RelativeLayout re_uninstall;
    private RelativeLayout re_share;
    private RelativeLayout re_details;
    private List<AppInfo> packageInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private int position;
    private String appName;
    private String apkPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_operation);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 1);
        appName = intent.getStringExtra("appname");

        ivReturn = (ImageView) findViewById(R.id.topbarturn_app_operation);
        tv_appname = (TextView) findViewById(R.id.tv_appname);
        re_running = (RelativeLayout) findViewById(R.id.re_running);
        re_uninstall = (RelativeLayout) findViewById(R.id.re_uninstall);
        re_share = (RelativeLayout) findViewById(R.id.re_share);
        re_details = (RelativeLayout) findViewById(R.id.re_details);

        initData();
        //initUI();

        tv_appname.setText(appName);
        ivReturn.setOnClickListener(this);
        re_running.setOnClickListener(this);
        re_uninstall.setOnClickListener(this);
        re_share.setOnClickListener(this);
        re_details.setOnClickListener(this);
    }

    private void initUI() {
        AppInfo appInfo;

        if(position < userAppInfos.size() + 1){
            appInfo = userAppInfos.get(position - 1);
        }else{
            appInfo = systemAppInfos.get(position - 1);
        }
        appName = appInfo.getApkName();
        apkPackageName = appInfo.getApkPackageName();

        //tv_appname.setText(appName);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            initUI();
        }
    };

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                packageInfos = AppInfos.getAppInfo(AppOperationActivity.this);

                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();

                for (AppInfo appInfo:packageInfos) {
                    //System.out.println(appInfo.getApkName());
                    if(appInfo.getUserApp()){
                        userAppInfos.add(appInfo);
                    }else{
                        systemAppInfos.add(appInfo);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(this,AppManagerActivity.class));
        finish();

        overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);
    }

    @Override
    protected void showNextPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topbarturn_app_operation:
                showPreviousPage();

                break;

            case R.id.re_running:
               //showPreviousPage();
                Intent start_localIntent = this.getPackageManager().getLaunchIntentForPackage(apkPackageName);
                this.startActivity(start_localIntent);

                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

                break;

            case R.id.re_uninstall:
                //showPreviousPage();
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE",
                        Uri.parse("package:" + apkPackageName));
                startActivity(uninstall_localIntent);

                break;

            case R.id.re_share:
                //showPreviousPage();

                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + appName+"下载地址:"+"https://play.google.com/store/apps/details?id="+apkPackageName);
                this.startActivity(Intent.createChooser(share_localIntent, "分享"));

                break;

            case R.id.re_details:
                //showPreviousPage();
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + apkPackageName));
                startActivity(detail_intent);

                overridePendingTransition(R.anim.tran_in,R.anim.tran_out);

                break;

            default:
                break;
        }
    }
}

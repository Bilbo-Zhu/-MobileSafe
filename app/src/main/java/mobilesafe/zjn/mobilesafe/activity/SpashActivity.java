package mobilesafe.zjn.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.service.CallSafeService;
import mobilesafe.zjn.mobilesafe.service.WatchDogService;
import mobilesafe.zjn.mobilesafe.utils.StreamUtils;

public class SpashActivity extends AppCompatActivity {
    protected static final int CODE_UPDATE_DIALOG = 0;
    protected static final int CODE_URL_ERROR = 1;
    protected static final int CODE_NET_ERROR = 2;
    protected static final int CODE_JSON_ERROR = 3;
    protected static final int CODE_ENTER_HOME = 4;// 进入主页面

    private TextView tvVersion;
    private RelativeLayout rlRoot;

    // 服务器返回的信息
    private String mVersionName;// 版本名
    private int mVersionCode;// 版本号
    private String mDesc;// 版本描述
    private String mDownloadUrl;// 下载地址

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDailog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SpashActivity.this, "url错误", Toast.LENGTH_SHORT)
                            .show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SpashActivity.this, "网络错误", Toast.LENGTH_SHORT)
                            .show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SpashActivity.this, "数据解析错误",
                            Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);

        copyDB("address.db");// 拷贝归属地查询数据库

        //拷贝资产目录下的病毒数据库文件
        copyDB("antivirus.db");

        startService(new Intent(this, CallSafeService.class));

        //Intent watchDogIntent = new Intent(this, WatchDogService.class);
        //startService(watchDogIntent);

        initView();
        opEvents();
        checkVersion();
    }

    private void opEvents() {
        tvVersion.setText("版本名：" + getVersionName());

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1f);
        alphaAnimation.setDuration(2000);
        rlRoot.startAnimation(alphaAnimation);
    }

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                                            getPackageName(),0);
            String  versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取本地app的版本号
     *
     * @return
     */
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);// 获取包的信息

            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // 没有找到包名的时候会走此异常
            e.printStackTrace();
        }

        return -1;
    }

    private void initView() {
        tvVersion = (TextView) findViewById(R.id.tv_version);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
    }

    private void checkVersion() {
        final long startTime = System.currentTimeMillis();
        // 启动子线程异步加载数据
        new Thread() {

            @Override
            public void run() {
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                try {
                    // 本机地址用localhost, 但是如果用模拟器加载本机的地址时,可以用ip(10.0.2.2)来替换
                    URL url = new URL("http://10.0.2.2:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");// 设置请求方法
                    conn.setConnectTimeout(5000);// 设置连接超时
                    conn.setReadTimeout(5000);// 设置响应超时, 连接上了,但服务器迟迟不给响应
                    conn.connect();// 连接服务器

                    int responseCode = conn.getResponseCode();// 获取响应码
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);
                        // System.out.println("网络返回:" + result);

                        // 解析json
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDesc = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");
                        // System.out.println("版本描述:" + mDesc);

                        if (mVersionCode > getVersionCode()) {// 判断是否有更新
                            // 服务器的VersionCode大于本地的VersionCode
                            // 说明有更新, 弹出升级对话框
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            // 没有版本更新
                            msg.what = CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    // url错误的异常
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    // 网络错误异常
                    msg.what = CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    // json解析失败
                    msg.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;// 访问网络花费的时间
                    if (timeUsed < 2000) {
                        // 强制休眠一段时间,保证闪屏页展示2秒钟
                        try {
                            Thread.sleep(2000 - timeUsed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    mHandler.sendMessage(msg);
                    if (conn != null) {
                        conn.disconnect();// 关闭网络连接
                    }
                }
            }
        }.start();
    }

    /**
     * 升级对话框
     */
    protected void showUpdateDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本:" + mVersionName);
        builder.setMessage(mDesc);
        // builder.setCancelable(false);//不让用户取消对话框, 用户体验太差,尽量不要用
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("立即更新");
                download();
            }
        });

        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        // 设置取消的监听, 用户点击返回键时会触发
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });

        builder.show();
    }

    /**
     * 下载apk文件
     */
    protected void download() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            //tvProgress.setVisibility(View.VISIBLE);// 显示进度

            String target = Environment.getExternalStorageDirectory()
                    + "/update.apk";
            // XUtils
            HttpUtils utils = new HttpUtils();
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

                // 下载文件的进度
                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                   // System.out.println("下载进度:" + current + "/" + total);
                   // tvProgress.setText("下载进度:" + current * 100 / total + "%");
                }

                // 下载成功
                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    System.out.println("下载成功");
                    // 跳转到系统下载页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(arg0.result),
                            "application/vnd.android.package-archive");
                    // startActivity(intent);
                    startActivityForResult(intent, 0);// 如果用户取消安装的话,
                    // 会返回结果,回调方法onActivityResult
                }

                // 下载失败
                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Toast.makeText(SpashActivity.this, "下载失败!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SpashActivity.this, "没有找到sdcard!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 如果用户取消安装的话,回调此方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 进入主页面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 拷贝数据库
     *
     * @param dbName
     */
    private void copyDB(String dbName) {
        File filesDir = getFilesDir();
        System.out.println("路径:" + filesDir.getAbsolutePath());
        File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址

       if (destFile.exists()) {
            System.out.println("数据库" + dbName + "已存在!");
            return;
        }

        FileOutputStream out = null;
        InputStream in = null;

        try {
            //in = getAssets().open(dbName);
            in = getClassLoader().getResourceAsStream("assets/" + dbName);
            int length = in.available();
            //System.out.println("databasesize" + length);
            out = new FileOutputStream(destFile);

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

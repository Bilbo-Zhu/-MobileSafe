package mobilesafe.zjn.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.utils.MD5Utils;

/**
 * Created by zjn on 2015/12/8.
 */
public class AToolsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView atools_return;
    private RelativeLayout atools_phone_address;
    private RelativeLayout settingLock;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atool);

        mPrefs = getSharedPreferences("config",MODE_PRIVATE);

        atools_return = (ImageView) findViewById(R.id.topbarturn_atools);
        atools_phone_address = (RelativeLayout) findViewById(R.id.re_phone_address);
        settingLock = (RelativeLayout) findViewById(R.id.re_settinglock);

        atools_return.setOnClickListener(this);
        atools_phone_address.setOnClickListener(this);
        settingLock.setOnClickListener(this);

    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(AToolsActivity.this, HomeActivity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,
                R.anim.tran_previous_out);// 进入动画和退出动画
    }

    @Override
    protected void showNextPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topbarturn_atools:
                showPreviousPage();
                break;
            case R.id.re_phone_address:
                startActivity(new Intent(AToolsActivity.this, SearchPhoneAddress.class));
                finish();

                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
                break;

            case R.id.re_settinglock:
                showPasswordDialog();

                break;

            default:
                break;
        }
    }

    private void showPasswordDialog() {
        // 判断是否设置密码
        String savedPassword = mPrefs.getString("applockpassword", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            // 输入密码弹窗
            showPasswordInputDialog();
        } else {
            // 如果没有设置过, 弹出设置密码的弹窗
            showPasswordSetDailog();
        }
    }

    private void showPasswordSetDailog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_set_apppassword, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPassword.getText().toString();
                // password!=null && !password.equals("")
                if (!TextUtils.isEmpty(password) && !passwordConfirm.isEmpty()) {
                    if (password.equals(passwordConfirm)) {
                        // Toast.makeText(HomeActivity.this, "登录成功!",
                        // Toast.LENGTH_SHORT).show();

                        // 将密码保存起来
                        mPrefs.edit().putString("applockpassword",
                                MD5Utils.encode(password)).commit();

                        dialog.dismiss();

                        // 跳转到手机防盗页
                        startActivity(new Intent(AToolsActivity.this,
                                AppLockActivity.class));
                    } else {
                        Toast.makeText(AToolsActivity.this, "两次密码不一致!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AToolsActivity.this, "输入框内容不能为空!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });

        dialog.show();

    }

    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dialog_inputapp_password, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_apppassword);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = mPrefs.getString("applockpassword", null);

                    if (MD5Utils.encode(password).equals(savedPassword)) {
                        // Toast.makeText(HomeActivity.this, "登录成功!",
                        // Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        // 跳转到手机防盗页
                        startActivity(new Intent(AToolsActivity.this,
                                AppLockActivity.class));
                    } else {
                        Toast.makeText(AToolsActivity.this, "密码错误!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AToolsActivity.this, "输入框内容不能为空!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });


        dialog.show();
    }
}

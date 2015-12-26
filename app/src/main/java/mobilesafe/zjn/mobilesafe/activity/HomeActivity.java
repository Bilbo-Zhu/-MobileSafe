package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.utils.MD5Utils;
import mobilesafe.zjn.mobilesafe.utils.ToastUtils;

/**
 * Created by zjn on 2015/11/28.
 */
public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {
    private GridView gvHome;
    private PopupWindow mPopupWindow;
    private View mPopView;

    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "工具箱"};

    private int[] mPics = new int[]{R.mipmap.home_safe,
            R.mipmap.home_callmsgsafe, R.mipmap.home_apps,
            R.mipmap.home_taskmanager, R.mipmap.home_netmanager,
            R.mipmap.other_home};

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();
        initEvents();
    }

    private void initEvents() {
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(this);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
    }

    private void initView() {
        gvHome = (GridView) findViewById(R.id.gv_home);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //手机防盗
                showPasswordDialog();
                break;
            case 1:
                //通讯卫士
                startActivity(new Intent(this, CallSafeActivity.class));
                //overridePendingTransition(R.anim.tran_in,R.anim.tran_out);

                break;

            case 2:
                startActivity(new Intent(this, AppManagerActivity.class));

                break;

            case 3:
                startActivity(new Intent(this,TaskManagerActivity.class));

                break;

            case 4:
                //startActivity(new Intent(this,AntivirusActivity.class));

                break;

            case 5:
                //弹出底部对话框
                showDownloadDialog();
                break;

            default:
                break;
        }
    }

    /**
     * 显示密码弹窗
     */
    protected void showPasswordDialog() {
        // 判断是否设置密码
        String savedPassword = mPref.getString("password", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            // 输入密码弹窗
            showPasswordInputDialog();
        } else {
            // 如果没有设置过, 弹出设置密码的弹窗
            showPasswordSetDailog();
        }
    }

    /**
     * 输入密码弹窗
     */
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dialog_input_password, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = mPref.getString("password", null);

                    if (MD5Utils.encode(password).equals(savedPassword)) {
                        // Toast.makeText(HomeActivity.this, "登录成功!",
                        // Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        // 跳转到手机防盗页
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });


        dialog.show();
    }

    /**
     * 设置密码的弹窗
     */
    private void showPasswordSetDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_set_password, null);
        // dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new OnClickListener() {

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
                        mPref.edit().putString("password",
                                MD5Utils.encode(password)).commit();

                        dialog.dismiss();

                        // 跳转到手机防盗页
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });

        dialog.show();
    }

    /**
     * 底部对话框
     *
     * @param position
     */
    private void showDownloadDialog() {
        if (mPopView != null) {
            onPopupWindowShown();
        } else {
            mPopView = View.inflate(HomeActivity.this, R.layout.home_popup,
                    null);

            mPopupWindow = new PopupWindow(mPopView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(
                    Color.TRANSPARENT));
            mPopupWindow.setAnimationStyle(R.style.popwin_anim);
            mPopupWindow.setFocusable(true);

            backgroundAlpha(0.3f);

            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    onPopupWindowDismiss();
                    backgroundAlpha(1f);
                }
            });
        }

        mPopView.findViewById(R.id.re_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });

        mPopView.findViewById(R.id.re_trojan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtils.showToast(HomeActivity.this, "trojan");

                startActivity(new Intent(HomeActivity.this,AntivirusActivity.class));

                dismissDialog();
            }
        });

        mPopView.findViewById(R.id.re_sysoptimize).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(HomeActivity.this, "sysoptimize");

                dismissDialog();
            }
        });

        mPopView.findViewById(R.id.re_tools).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,
                        AToolsActivity.class));

                // 两个界面切换的动画
                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画

                dismissDialog();
            }
        });

        mPopView.findViewById(R.id.re_settings).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtils.showToast(HomeActivity.this,"settings");
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));//内部类用this直接指向onclicklistener

                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

                dismissDialog();
            }
        });

        mPopupWindow.showAtLocation(getWindow().getDecorView(),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    public void onPopupWindowShown() {
        mPopView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.layer_show_anim));
        mPopView.setVisibility(View.VISIBLE);
        backgroundAlpha(0.3f);
    }

    public void onPopupWindowDismiss() {
        mPopView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.layer_gone_anim));
        mPopView.setVisibility(View.GONE);
    }

    private void dismissDialog() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(HomeActivity.this,
                        R.layout.home_list_item, null);
                holder.ivItem = (ImageView) convertView.findViewById(R.id.iv_item);
                holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvItem.setText(mItems[position]);
            holder.ivItem.setImageResource(mPics[position]);
            return convertView;
        }
    }

    public final class ViewHolder {
        public ImageView ivItem;
        public TextView tvItem;
    }
}

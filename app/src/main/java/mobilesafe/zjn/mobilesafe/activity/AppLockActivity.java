package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;

import mobilesafe.zjn.mobilesafe.fragment.LockFragment;
import mobilesafe.zjn.mobilesafe.fragment.unLockFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import mobilesafe.zjn.mobilesafe.R;

/**
 * Created by zjn on 2015/12/25.
 */
public class AppLockActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView appLock;
    private TextView tv_unlock;
    private TextView tv_lock;
    private FrameLayout fl_content;
    private FragmentManager fragmentManager;
    private unLockFragment munLockFragment;
    private LockFragment lockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_applock);

        initUI();
        initData();
    }

    private void initData() {

    }

    private void initUI() {
        appLock = (ImageView) findViewById(R.id.topbarturn_applock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);

        fragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction = fragmentManager.beginTransaction();

        munLockFragment = new unLockFragment();

        lockFragment = new LockFragment();
        /**
         * 替换界面
         * 1 需要替换的界面的id
         * 2具体指某一个fragment的对象
         */
        mTransaction.replace(R.id.fl_content, munLockFragment).commit();

        appLock.setOnClickListener(this);
        tv_unlock.setOnClickListener(this);
        tv_lock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        switch (v.getId()){
            case R.id.topbarturn_applock:
                startActivity(new Intent(this,AToolsActivity.class));
                finish();

                overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);

                break;

            case R.id.tv_unlock:

                //没有加锁
                tv_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);
                tv_lock.setBackgroundResource(R.mipmap.tab_right_default);

                ft.replace(R.id.fl_content, munLockFragment).commit();
                System.out.println("切换到lockFragment");

                break;

            case R.id.tv_lock:

                //没有加锁
                tv_unlock.setBackgroundResource(R.mipmap.tab_left_default);
                tv_lock.setBackgroundResource(R.mipmap.tab_right_pressed);

                ft.replace(R.id.fl_content, lockFragment).commit();
                System.out.println("切换到unlockFragment");

                break;

            default:
                break;
        }
    }
}

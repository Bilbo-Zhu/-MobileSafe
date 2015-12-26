package mobilesafe.zjn.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import mobilesafe.zjn.mobilesafe.R;

/**
 * Created by zjn on 2015/12/3.
 */
public class LostFindSettingsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivturn;
    private ImageView ivSimCard;
    private Boolean simconfiged;
    private Boolean protect;
    private RelativeLayout reSafePhone;
    private ImageView ivProtectSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lostfind_setting);

        ivturn = (ImageView)findViewById(R.id.topbarturn_lostfindsettings);
        ivSimCard = (ImageView)findViewById(R.id.select2_lostfind);
        reSafePhone = (RelativeLayout)findViewById(R.id.re_safephone);
        ivProtectSetting = (ImageView)findViewById(R.id.select1_lostfindsetting);

        String sim = mPrefs.getString("sim",null);
        if(!TextUtils.isEmpty(sim)){
            setSimCardSelected();
        }else{
            setSimCardSelected_no();
        }

        protect = mPrefs.getBoolean("protect",false);
        if(protect){
            ivProtectSetting.setImageResource(R.drawable.select_yess);
        }else{
            ivProtectSetting.setImageResource(R.drawable.selsect_noo);
        }

        ivSimCard.setOnClickListener(this);
        ivturn.setOnClickListener(this);
        reSafePhone.setOnClickListener(this);
        ivProtectSetting.setOnClickListener(this);
    }

    /**
     * set SIMCard status
     */
    private void setSimCardSelected_no() {
        ivSimCard.setImageResource(R.drawable.selsect_noo);
    }

    private void setSimCardSelected() {
        ivSimCard.setImageResource(R.drawable.select_yess);
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(LostFindSettingsActivity.this, LostFindActivity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,
                R.anim.tran_previous_out);// 进入动画和退出动画
    }

    @Override
    protected void showNextPage() {
        //ToastUtils.showToast(this,"哈哈，向左滑试试");
    }

    @Override
    public void onClick(View v) {
        simconfiged = mPrefs.getBoolean("simconfiged",false);
        protect = mPrefs.getBoolean("protect",false);

        switch (v.getId()){
            case R.id.select2_lostfind:
                if(simconfiged){
                    setSimCardSelected_no();

                    mPrefs.edit().putBoolean("simconfiged",false).commit();
                    mPrefs.edit().remove("sim").commit();// 删除已绑定的sim卡
                }else if(!simconfiged){
                    setSimCardSelected();

                    mPrefs.edit().putBoolean("simconfiged",true).commit();

                    // 保存sim卡信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();// 获取sim卡序列号
                    //System.out.println("sim卡序列号:" + simSerialNumber);

                    mPrefs.edit().putString("sim", simSerialNumber).commit();// 将sim卡序列号保存在sp中
                }
                break;
            case R.id.topbarturn_lostfindsettings:
                showPreviousPage();
                mPrefs.edit().putBoolean("configed",true).commit();
                break;
            case R.id.re_safephone:
                startActivity(new Intent(LostFindSettingsActivity.this, SetSafePhoneActivity.class));
                finish();

                // 两个界面切换的动画
                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
                break;
            case R.id.select1_lostfindsetting:
                if(protect){
                    ivProtectSetting.setImageResource(R.drawable.selsect_noo);
                    mPrefs.edit().putBoolean("protect",false).commit();
                }else{
                    ivProtectSetting.setImageResource(R.drawable.select_yess);
                    mPrefs.edit().putBoolean("protect",true).commit();
                }
                break;
            default:
                break;
        }
    }
}

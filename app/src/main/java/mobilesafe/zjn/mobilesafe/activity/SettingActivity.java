package mobilesafe.zjn.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.service.AddressService;
import mobilesafe.zjn.mobilesafe.service.CallSafeService;
import mobilesafe.zjn.mobilesafe.utils.ServiceStatusUtils;

/**
 * Created by zjn on 2015/12/13.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivReturn;
    private ImageView ivselect;
    private RelativeLayout reSelStyle;
    private TextView tvStyle;
    private RelativeLayout reAddrLocation;
    private ImageView black_setting;

    private Boolean addressstatue;

    final String[] items = new String[] { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
    private Boolean serviceRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        ivReturn = (ImageView)findViewById(R.id.topbarturn_setting);
        ivselect = (ImageView)findViewById(R.id.select2_setting);
        reSelStyle = (RelativeLayout)findViewById(R.id.re_selstyle_setting);
        tvStyle = (TextView)findViewById(R.id.tv_style);
        reAddrLocation = (RelativeLayout)findViewById(R.id.re_addresslocation);
        black_setting = (ImageView) findViewById(R.id.black_setting);

        ivReturn.setOnClickListener(this);
        ivselect.setOnClickListener(this);
        reSelStyle.setOnClickListener(this);
        reAddrLocation.setOnClickListener(this);
        black_setting.setOnClickListener(this);

        serviceRunning = ServiceStatusUtils.isServiceRunning(this
                , "mobilesafe.zjn.mobilesafe.service.CallSafeService");
        if(serviceRunning){
            black_setting.setImageResource(R.drawable.select_yess);
        }else{
            black_setting.setImageResource(R.drawable.selsect_noo);
        }

        checkExitsService();
        addressstatue = mPrefs.getBoolean("addressstatue", false);
        if(addressstatue){
            startService();
        }else{
            stopService();
        }

        int style = mPrefs.getInt("address_style",0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceRunning = ServiceStatusUtils.isServiceRunning(this
                , "mobilesafe.zjn.mobilesafe.service.CallSafeService");
        if(serviceRunning){
            black_setting.setImageResource(R.drawable.select_yess);
        }else{
            black_setting.setImageResource(R.drawable.selsect_noo);
        }
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();

        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    protected void showNextPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topbarturn_setting:
                showPreviousPage();
                break;

            case R.id.select2_setting:
                addressstatue = mPrefs.getBoolean("addressstatue", false);
                if(addressstatue){
                    stopService();
                }else{
                    startService();
                }
                break;

            case R.id.re_selstyle_setting:
                showSingleChooseDailog();
                break;

            case R.id.re_addresslocation:
                startActivity(new Intent(this,DragViewActivity.class));
                //finish();

                //overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
                break;

            case R.id.black_setting:
                serviceRunning = ServiceStatusUtils.isServiceRunning(this
                        , "mobilesafe.zjn.mobilesafe.service.CallSafeService");

                if(serviceRunning){
                    black_setting.setImageResource(R.drawable.selsect_noo);

                    stopService(new Intent(this, CallSafeService.class));
                }else{
                    black_setting.setImageResource(R.drawable.select_yess);

                    startService(new Intent(this,CallSafeService.class));
                }

                break;

            default:
                break;
        }
    }

    private void showSingleChooseDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");

        int style = mPrefs.getInt("address_style",0);

        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPrefs.edit().putInt("address_style",which).commit();

                dialog.dismiss();

                tvStyle.setText(items[which]);
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void checkExitsService(){
        if(ServiceStatusUtils.isServiceRunning(this,"mobilesafe.zjn.mobilesafe.service.AddressService")){
            mPrefs.edit().putBoolean("addressstatue",true).commit();
        }else{
            mPrefs.edit().putBoolean("addressstatue",false).commit();
        }
    }

    private void startService(){
        ivselect.setImageResource(R.drawable.select_yess);
        mPrefs.edit().putBoolean("addressstatue",true).commit();
        startService(new Intent(SettingActivity.this,AddressService.class));
    }

    private void stopService(){
        ivselect.setImageResource(R.drawable.selsect_noo);
        mPrefs.edit().putBoolean("addressstatue",false).commit();
        stopService(new Intent(SettingActivity.this, AddressService.class));
    }
}

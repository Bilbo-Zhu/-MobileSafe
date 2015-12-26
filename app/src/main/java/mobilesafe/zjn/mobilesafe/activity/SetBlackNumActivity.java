package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.db.BlackNumberDao;
import mobilesafe.zjn.mobilesafe.utils.ToastUtils;

/**
 * Created by zjn on 2015/12/17.
 */
public class SetBlackNumActivity extends BaseActivity implements View.OnClickListener {

    private Button selsectBlackNum;
    private EditText etReturnBlackNum;
    private Button btCompleted;
    private BlackNumberDao dao;
    private String phone;
    private ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setblacknum);

        selsectBlackNum = (Button) findViewById(R.id.bt_setblacknum);
        etReturnBlackNum = (EditText) findViewById(R.id.et_setblacknum);
        btCompleted = (Button) findViewById(R.id.completed_setblacknum);
        ivReturn = (ImageView) findViewById(R.id.iv_return_setblacknum);

        selsectBlackNum.setOnClickListener(this);
        btCompleted.setOnClickListener(this);
        ivReturn.setOnClickListener(this);
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(this, MyBlackNumberActivity.class));
        finish();

        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    protected void showNextPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_setblacknum:
                startActivityForResult(new Intent(this, ContactActivity.class), 2);
                mPrefs.edit().putBoolean("blacknum",true).commit();
                //finish();

                break;

            case R.id.completed_setblacknum:
                getEdittextNum();

                break;

            case R.id.iv_return_setblacknum:
                showPreviousPage();

                break;

            default:
                break;
        }
    }

    private void getEdittextNum() {
        phone = etReturnBlackNum.getText().toString().trim();
        if(TextUtils.isEmpty(phone)){
            ToastUtils.showToast(this, "输入不能为空");
        }else {
            //mPrefs.edit().putString("safephone", phone).commit();
            //showPreviousPage();

            setBlackNumMode();

            //ToastUtils.showToast(this, "设置成功");
        }
    }

    private void setBlackNumMode() {
        dao = new BlackNumberDao(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialog_view = View.inflate(this, R.layout.dialog_add_black_number, null);

        Button btn_ok = (Button) dialog_view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialog_view.findViewById(R.id.btn_cancel);
        final CheckBox cb_phone = (CheckBox) dialog_view.findViewById(R.id.cb_phone);
        final CheckBox cb_sms = (CheckBox) dialog_view.findViewById(R.id.cb_sms);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String str_number = etReturnBlackNum.getText().toString().trim();
                //if(TextUtils.isEmpty(str_number)){
                 //   Toast.makeText(SetBlackNumActivity.this,"请输入黑名单号码",Toast.LENGTH_SHORT).show();
                   // return;
               // }

                String mode = "";

                if(cb_phone.isChecked()&& cb_sms.isChecked()){
                    mode = "1";
                }else if(cb_phone.isChecked()){
                    mode = "2";
                }else if(cb_sms.isChecked()){
                    mode = "3";
                }else{
                    Toast.makeText(SetBlackNumActivity.this, "请勾选拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                String modetrue = dao.findNumber(phone);
                if(modetrue.equals("1")  || modetrue.equals("2")  || modetrue.equals("3")){
                    ToastUtils.showToast(SetBlackNumActivity.this,"已经在黑名单中");
                }else{
                    dao.add(phone,mode);
                    showPreviousPage();
                }
                dialog.dismiss();
            }
        });
        dialog.setView(dialog_view);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // System.out.println("resultCode:" + resultCode);
        // System.out.println("requestCode:" + requestCode);

        if (resultCode == 2) {
            String phone = data.getStringExtra("phone");
            phone = phone.replaceAll("-", "").replaceAll(" ", "");// 替换-和空格

            etReturnBlackNum.setText(phone);// 把电话号码设置给输入框
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}

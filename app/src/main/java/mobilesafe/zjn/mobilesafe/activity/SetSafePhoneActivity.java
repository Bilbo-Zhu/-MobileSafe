package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.utils.ToastUtils;

/**
 * Created by zjn on 2015/12/5.
 */
public class SetSafePhoneActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private Button btCompleted;
    private Button btSelectContact;
    private EditText etPhone;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setsafephone);

        ivBack = (ImageView)findViewById(R.id.iv_return_setsafephone);
        btCompleted = (Button)findViewById(R.id.completed_setsafephone);
        etPhone = (EditText)findViewById(R.id.et_setsafephone);
        btSelectContact = (Button)findViewById(R.id.bt_setsafephone);

        phone = mPrefs.getString("safephone","");
        if(!TextUtils.isEmpty(phone)){
            etPhone.setText(phone);
        }

        ivBack.setOnClickListener(this);
        btCompleted.setOnClickListener(this);
        btSelectContact.setOnClickListener(this);
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(SetSafePhoneActivity.this, LostFindSettingsActivity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,
                R.anim.tran_previous_out);// 进入动画和退出动画
    }

    @Override
    protected void showNextPage() {
        //ToastUtils.showToast(this,"我累啦");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_return_setsafephone:
                showPreviousPage();
                break;
            case R.id.completed_setsafephone:
                phone = etPhone.getText().toString().trim();//获取输入号码并且过滤空格
                if(TextUtils.isEmpty(phone)){
                    ToastUtils.showToast(this,"输入不能为空");
                }else {
                    mPrefs.edit().putString("safephone", phone).commit();
                    showPreviousPage();
                    ToastUtils.showToast(this, "设置成功");
                }
                break;
            case R.id.bt_setsafephone:
                selectContact();
                break;
            default:
                break;
        }
    }

    /**
     * 选择联系人
     *
     * @param view
     */
    public void selectContact() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // System.out.println("resultCode:" + resultCode);
        // System.out.println("requestCode:" + requestCode);

        if (resultCode == Activity.RESULT_OK) {
            String phone = data.getStringExtra("phone");
            phone = phone.replaceAll("-", "").replaceAll(" ", "");// 替换-和空格

            etPhone.setText(phone);// 把电话号码设置给输入框
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

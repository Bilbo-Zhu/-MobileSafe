package mobilesafe.zjn.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import mobilesafe.zjn.mobilesafe.R;

/**
 * Created by zjn on 2015/12/12.
 */
public class SearchPhoneAddress extends BaseActivity implements View.OnClickListener {
    private ImageView topbarturn_spa;
    private ImageButton search_btn;
    private EditText etNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchphoneaddress);

        topbarturn_spa = (ImageView)findViewById(R.id.topbarturn_atools);
        search_btn = (ImageButton)findViewById(R.id.ib_search_btn);
        etNumber = (EditText)findViewById(R.id.et_search_content);

        topbarturn_spa.setOnClickListener(this);
        search_btn.setOnClickListener(this);
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(SearchPhoneAddress.this,AToolsActivity.class));
        finish();

        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }

    @Override
    protected void showNextPage() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topbarturn_atools:
                showPreviousPage();
                break;
            case R.id.ib_search_btn:
                String number = etNumber.getText().toString().trim();
                if(!TextUtils.isEmpty(number)){
                    mPrefs.edit().putString("searchnumber",number).commit();

                    startActivity(new Intent(SearchPhoneAddress.this,SearchAddressResultActivity.class));
                    finish();

                    overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
                }else{
                    Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

                    etNumber.startAnimation(shake);
                    vibrate();
                }
                break;
            default:
                break;
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // vibrator.vibrate(2000);震动两秒
        vibrator.vibrate(2000);// 先等待1秒,再震动2秒,再等待1秒,再震动3秒,
        // 参2等于-1表示只执行一次,不循环,
        // 参2等于0表示从头循环,
        // 参2表示从第几个位置开始循环
    }
}

package mobilesafe.zjn.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.db.AddressDao;

/**
 * Created by zjn on 2015/12/12.
 */
public class SearchAddressResultActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvNumber;
    private ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sar);

        tvNumber = (TextView)findViewById(R.id.tv_numaddress);
        ivReturn = (ImageView)findViewById(R.id.topbarturn_atools);

        ivReturn.setOnClickListener(this);

        String number = mPrefs.getString("searchnumber","");
        System.out.println("number = "+ number);

        if (!TextUtils.isEmpty(number)){
            String address = AddressDao.getAddress(number);

            tvNumber.setText(address);
        }else{
            tvNumber.setText("位置号码");
        }
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(this,SearchPhoneAddress.class));
        finish();

        overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);
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
            default:
                break;
        }
    }
}

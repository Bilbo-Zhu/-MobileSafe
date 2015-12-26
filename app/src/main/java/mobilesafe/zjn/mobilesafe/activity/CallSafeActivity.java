package mobilesafe.zjn.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.db.BlackNumberDao;
import mobilesafe.zjn.mobilesafe.utils.ToastUtils;

/**
 * Created by zjn on 2015/12/16.
 */
public class CallSafeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView reTrun;
    private ImageButton search;
    private RelativeLayout viewById;
    private RelativeLayout blackNumberItem;
    private EditText etSearchBlackNum;
    private BlackNumberDao dao;
    private TextView tv_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_callsafe);

        dao = new BlackNumberDao(this);

        reTrun = (ImageView) findViewById(R.id.topbarturn_callsafe);
        search = (ImageButton) findViewById(R.id.ib_search_btn);
        blackNumberItem = (RelativeLayout) findViewById(R.id.re_callsafe);
        etSearchBlackNum = (EditText) findViewById(R.id.et_search_content);
        tv_num = (TextView) findViewById(R.id.tv_num1);

        reTrun.setOnClickListener(this);
        search.setOnClickListener(this);
        blackNumberItem.setOnClickListener(this);
        setInVisibility();
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
        switch (v.getId()) {
            case R.id.topbarturn_callsafe:
                showPreviousPage();

                break;

            case R.id.ib_search_btn:
                String blackNumber = etSearchBlackNum.getText().toString().trim();
                if(TextUtils.isEmpty(blackNumber)){
                    ToastUtils.showToast(this,"输入不能为空");
                }else{
                    String mode = dao.findNumber(blackNumber);
                    if(mode.equals("1") || mode.equals("2") || mode.equals("3")){
                        tv_num.setVisibility(View.VISIBLE);
                        tv_num.setText("不安全号码");
                    }else{
                        tv_num.setVisibility(View.VISIBLE);
                        tv_num.setText("安全号码");
                    }
                }

                break;

            case R.id.re_callsafe:
                //showPreviousPage();
                startActivity(new Intent(this, MyBlackNumberActivity.class));
                finish();

                overridePendingTransition(R.anim.tran_in,R.anim.tran_out);

                break;

            default:
                break;
        }
    }

    private void setInVisibility(){
        tv_num.setVisibility(View.INVISIBLE);
    }
}

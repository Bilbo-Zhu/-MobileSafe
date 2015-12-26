package mobilesafe.zjn.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.utils.ToastUtils;

/**
 * Created by zjn on 2015/12/2.
 */
public class LostFindActivity extends BaseActivity {
    private ImageView ivturn;
    private RelativeLayout re_turn;
    private TextView tvSafeNum;
    private ImageView ivIsLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Boolean configed = mPrefs.getBoolean("configed", false);

        if (configed) {
            setContentView(R.layout.activity_lostfind);
        } else {
            startActivity(new Intent(this, LostFindSettingsActivity.class));
            finish();
        }

        ivturn = (ImageView) findViewById(R.id.topbarturn_lostfind);
        re_turn = (RelativeLayout) findViewById(R.id.return_lostfind);
        tvSafeNum = (TextView) findViewById(R.id.tv_num);
        ivIsLock = (ImageView) findViewById(R.id.lock_lostfind);

        String safeNum = mPrefs.getString("safephone","");
        if(!TextUtils.isEmpty(safeNum)){
            tvSafeNum.setText(safeNum);
        }

        Boolean protect = mPrefs.getBoolean("protect",false);
        if(protect){
            ivIsLock.setImageResource(R.mipmap.lostfind_lock);
        }else{
            ivIsLock.setImageResource(R.mipmap.lostfind_unlock);
        }

        ivturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousPage();
            }
        });
        re_turn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LostFindActivity.this, LostFindSettingsActivity.class));
                finish();

                // 两个界面切换的动画
                overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
            }
        });
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(LostFindActivity.this, HomeActivity.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,
                R.anim.tran_previous_out);// 进入动画和退出动画
    }

    @Override
    protected void showNextPage() {
        //ToastUtils.showToast(this,"我累了，滑不动了！主人");
    }
}


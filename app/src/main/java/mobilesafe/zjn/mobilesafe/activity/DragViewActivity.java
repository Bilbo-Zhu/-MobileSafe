package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mobilesafe.zjn.mobilesafe.R;

/**
 * Created by zjn on 2015/12/15.
 */
public class DragViewActivity extends Activity {
    private TextView tvTop;
    private TextView tvBottom;
    private ImageView ivDrag;

    private SharedPreferences mPrefs;

    private int startX;
    private int startY;

    long[] mHits = new long[2];// 数组长度表示要点击的次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drag_view);

        mPrefs = getSharedPreferences("config",MODE_PRIVATE);

        tvTop = (TextView)findViewById(R.id.tv_top);
        tvBottom = (TextView)findViewById(R.id.tv_bottom);
        ivDrag = (ImageView)findViewById(R.id.iv_drag);

        int lastX = mPrefs.getInt("lastX",0);
        int lastY = mPrefs.getInt("lastY",0);

        final int width = getWindowManager().getDefaultDisplay().getWidth();
        final int height = getWindowManager().getDefaultDisplay().getHeight();

        if (lastY > height / 2) {// 上边显示,下边隐藏
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        layoutParams.leftMargin = lastX;
        layoutParams.topMargin = lastY;

        ivDrag.setLayoutParams(layoutParams);

        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    // 把图片居中
                    ivDrag.layout(width / 2 - ivDrag.getWidth() / 2,
                            ivDrag.getTop(), width / 2 + ivDrag.getWidth()
                                    / 2, ivDrag.getBottom());
                }
            }
        });

        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int dx = endX - startX;
                        int dy = endY - startY;

                        int l = ivDrag.getLeft() + dx;
                        int r = ivDrag.getRight() + dx;
                        int t = ivDrag.getTop() + dy;
                        int b = ivDrag.getBottom() + dy;

                        if(l < 0 || r > width || t < 0 || b > height - 20){
                            break;
                        }

                        if(t > height/2){
                            tvTop.setVisibility(v.VISIBLE);
                            tvBottom.setVisibility(v.INVISIBLE);
                        }else{
                            tvTop.setVisibility(v.INVISIBLE);
                            tvBottom.setVisibility(v.VISIBLE);
                        }

                        ivDrag.layout(l,t,r,b);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;

                    case MotionEvent.ACTION_UP:
                        SharedPreferences.Editor edit = mPrefs.edit();
                        edit.putInt("lastX",ivDrag.getLeft());
                        edit.putInt("lastY",ivDrag.getTop());
                        edit.commit();

                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }
}

package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by zjn on 2015/12/5.
 */
public abstract class BaseActivity extends Activity{
    private GestureDetector gestureDetector;
    public SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("config",MODE_PRIVATE);

        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getRawX() - e2.getRawX() > 200){
                    showNextPage();
                    return true;
                }else if(e2.getRawX() - e1.getRawX() > 200)
                {
                    showPreviousPage();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    protected abstract void showPreviousPage();

    protected abstract void showNextPage();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);// 委托手势识别器处理触摸事件
        return super.onTouchEvent(event);
    }
}

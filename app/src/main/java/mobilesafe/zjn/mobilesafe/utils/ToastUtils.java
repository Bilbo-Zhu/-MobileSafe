package mobilesafe.zjn.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zjn on 2015/12/5.
 */
public class ToastUtils {
    public static void showToast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
}

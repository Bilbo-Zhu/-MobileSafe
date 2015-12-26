package mobilesafe.zjn.mobilesafe.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.bean.TaskInfo;

/**
 * Created by zjn on 2015/12/22.
 */
public class SwipeAdapter extends BaseAdapter {
    public List<TaskInfo> lists;

    public Context mContext;

    private int mRightWidth = 0;

    /**
     * 单击事件监听器
     */
    private IOnItemRightClickListener mListener = null;

    public interface IOnItemRightClickListener {
        void onRightClick(View v, int position);
    }

    /**
     * @param mainActivity
     */
    public SwipeAdapter(Context ctx, List<TaskInfo> lists,int rightWidth, IOnItemRightClickListener l) {
        mContext = ctx;
        this.lists = lists;
        mRightWidth = rightWidth;
        mListener = l;
    }

    protected SwipeAdapter() {
    }


    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder item;
        final int thisPosition = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.swipe_list_item, parent, false);
            item = new ViewHolder();
            item.item_left = convertView.findViewById(R.id.item_left);
            item.item_right = convertView.findViewById(R.id.item_right);
            item.iv = (ImageView)convertView.findViewById(R.id.iv);
            item.item_left_txt = (TextView)convertView.findViewById(R.id.item_left_txt);
            item.item_right_txt = (TextView)convertView.findViewById(R.id.item_right_txt);
            convertView.setTag(item);
        } else {// 有直接获得ViewHolder
            item = (ViewHolder)convertView.getTag();
        }
        LinearLayout.LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        item.item_left.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = new LayoutParams(mRightWidth, LayoutParams.MATCH_PARENT);
        item.item_right.setLayoutParams(lp2);
        item.iv.setImageDrawable(lists.get(thisPosition).getIcon());
        item.item_left_txt.setText(lists.get(thisPosition).getAppName());
        item.item_right_txt.setText("delete ");
        item.item_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightClick(v, thisPosition);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        View item_left;

        View item_right;

        ImageView iv;

        TextView item_left_txt;

        TextView item_right_txt;
    }
}

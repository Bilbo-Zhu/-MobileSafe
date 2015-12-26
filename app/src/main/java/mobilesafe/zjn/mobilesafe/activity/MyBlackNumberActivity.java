package mobilesafe.zjn.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mobilesafe.zjn.mobilesafe.Adapter.MyBaseAdapter;
import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.bean.BlackNumberInfo;
import mobilesafe.zjn.mobilesafe.db.BlackNumberDao;

/**
 * Created by zjn on 2015/12/17.
 */
public class MyBlackNumberActivity extends BaseActivity implements View.OnClickListener, AbsListView.OnScrollListener {

    private ImageView reTrun;
    private Button addBlackNum;
    private LinearLayout ll_pb;
    private ListView list_view;
    private List<BlackNumberInfo> blackNumberInfos;
    private CallSafeAdapter adapter;

    /**
     * 开始的位置
     */
    private int mStartIndex = 0;
    /**
     * 每页展示20条数据
     */
    private int maxCount = 20;
    private BlackNumberDao dao;

    /**
     * 一共有多少页面
     */
    private int totalPage;
    private int totalNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.acivity_blacknumberitem);

        reTrun = (ImageView) findViewById(R.id.iv_return_blacknumberitem);
        addBlackNum = (Button) findViewById(R.id.add_blacknumberitem);
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        list_view = (ListView) findViewById(R.id.list_view);

        reTrun.setOnClickListener(this);
        addBlackNum.setOnClickListener(this);
        list_view.setOnScrollListener(this);

        initData();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_pb.setVisibility(View.INVISIBLE);
            if(adapter == null){
                adapter = new CallSafeAdapter(blackNumberInfos, MyBlackNumberActivity.this);
                list_view.setAdapter(adapter);
            }else{
                adapter.notifyDataSetChanged();
            }

        }
    };

    private void initData() {
        dao = new BlackNumberDao(this);

        totalNumber = dao.getTotalNumber();

        new Thread(){
            @Override
            public void run() {
                if(blackNumberInfos == null){
                    blackNumberInfos = dao.findPar2(mStartIndex,maxCount);
                }else{
                    blackNumberInfos.addAll(dao.findPar2(mStartIndex,maxCount));
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    protected void showPreviousPage() {
        startActivity(new Intent(this,CallSafeActivity.class));
        finish();

        overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);
    }

    @Override
    protected void showNextPage() {

    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.iv_return_blacknumberitem:
             showPreviousPage();

             break;

         case R.id.add_blacknumberitem:
             startActivity(new Intent(this,SetBlackNumActivity.class));
             finish();

             break;

         default:
             break;
     }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                //获取到最后一条显示的数据
                int lastVisiblePosition = list_view.getLastVisiblePosition();
                //System.out.println("lastVisiblePosition==========" + lastVisiblePosition);
                if(lastVisiblePosition == blackNumberInfos.size() - 1){
                    // 加载更多的数据。 更改加载数据的开始位置
                    mStartIndex += maxCount;
                    if (mStartIndex >= totalNumber) {
                        Toast.makeText(getApplicationContext(),
                                "没有更多的数据了。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    initData();
                }


                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {


        private CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(MyBlackNumberActivity.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_number.setText(lists.get(position).getNumber());
            String mode = lists.get(position).getMode();
            if (mode.equals("1")) {
                holder.tv_mode.setText("来电拦截+短信");
            } else if (mode.equals("2")) {
                holder.tv_mode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tv_mode.setText("短信拦截");
            }
            final BlackNumberInfo info = lists.get(position);
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = info.getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        Toast.makeText(MyBlackNumberActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        lists.remove(info);
                        //刷新界面
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MyBlackNumberActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }


    }

    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }
}

package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.bean.AppInfo;
import mobilesafe.zjn.mobilesafe.engine.AppInfos;
import mobilesafe.zjn.mobilesafe.utils.ToastUtils;

/**
 * Created by zjn on 2015/12/19.
 */
public class AppManagerActivity extends Activity implements AdapterView.OnItemClickListener ,AbsListView.OnScrollListener, View.OnClickListener {

    private ListView list_view;
    private List<AppInfo> packageInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private TextView tv_app;
    private AppInfo clickAppInfo;
    private ImageView ivReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appmanager);

        list_view = (ListView) findViewById(R.id.listview_appmanager);
        tv_app = (TextView) findViewById(R.id.tv_app);
        ivReturn = (ImageView) findViewById(R.id.topbarturn_appmanager);

        initData();

        list_view.setOnItemClickListener(this);
        list_view.setOnScrollListener(this);
        ivReturn.setOnClickListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AppManagerAdapter adapter = new AppManagerAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                packageInfos = AppInfos.getAppInfo(AppManagerActivity.this);

                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();

                for (AppInfo appInfo:packageInfos) {
                    if(appInfo.getUserApp()){
                        userAppInfos.add(appInfo);
                    }else{
                        systemAppInfos.add(appInfo);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(userAppInfos != null && systemAppInfos != null){
            if(firstVisibleItem < userAppInfos.size() + 1){
                tv_app.setText("用户应用（"+userAppInfos.size()+")个");
            }else{
                tv_app.setText("系统应用（"+systemAppInfos.size()+")个");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object obj = list_view.getItemAtPosition(position);
        String appName;
        if (obj != null && obj instanceof AppInfo) {
            clickAppInfo = (AppInfo) obj;
            appName = clickAppInfo.getApkName();
            if (position == 0 || position == userAppInfos.size() + 1){
                return;
            }else {
                Intent intent = new Intent(this,AppOperationActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("appname",appName);
                startActivity(intent);
                finish();

                overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
                //System.out.println("position:" + position);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topbarturn_appmanager:
                startActivity(new Intent(this,HomeActivity.class));
                finish();
                overridePendingTransition(R.anim.tran_previous_in,R.anim.tran_previous_out);

                break;
        }
    }

    private class AppManagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            AppInfo appInfo;
            if(position == 0){
                return null;
            }else if(position == userAppInfos.size() + 1){
                return null;
            }else if(position < userAppInfos.size() + 1){
                appInfo = userAppInfos.get(position - 1);
            }else{
                appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == 0){
                TextView textView = new TextView(AppManagerActivity.this);

                textView.setTextColor(Color.WHITE);

                textView.setBackgroundColor(Color.GRAY);

                textView.setText("用户程序("+userAppInfos.size()+")");

                return textView;
                //表示系统程序
            }else if(position == userAppInfos.size() + 1){
                TextView textView = new TextView(AppManagerActivity.this);


                textView.setTextColor(Color.WHITE);

                textView.setBackgroundColor(Color.GRAY);

                textView.setText("系统程序("+systemAppInfos.size()+")");

                return textView;
            }

            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                //把多出来的特殊的条目减掉
                appInfo = userAppInfos.get(position - 1);

            } else {

                int location = userAppInfos.size() + 2;

                appInfo = systemAppInfos.get(position - location);
            }

            View view = null;
            ViewHolder holder;
            if(convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(AppManagerActivity.this,R.layout.item_listview_appmanager,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView)view.findViewById(R.id.iv_appicon);
                holder.tv_name = (TextView)view.findViewById(R.id.tv_apkname);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_name.setText(appInfo.getApkName());

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
    }
}

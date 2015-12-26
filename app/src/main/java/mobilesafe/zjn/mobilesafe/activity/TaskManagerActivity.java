package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import mobilesafe.zjn.mobilesafe.Adapter.SwipeAdapter;
import mobilesafe.zjn.mobilesafe.R;
import mobilesafe.zjn.mobilesafe.bean.TaskInfo;
import mobilesafe.zjn.mobilesafe.engine.TaskInfoParser;
import mobilesafe.zjn.mobilesafe.ui.SwipeListView;
import mobilesafe.zjn.mobilesafe.utils.ToastUtils;

/**
 * Created by zjn on 2015/12/22.
 */
public class TaskManagerActivity extends Activity implements View.OnClickListener {

    private SwipeListView list_view;
    private List<TaskInfo> taskInfos;
    private ActivityManager activityManager;
    private SwipeAdapter adapter;
    private ImageView ivReturn;
    private Button clearAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_manager);

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        list_view = (SwipeListView) findViewById(R.id.listview);
        ivReturn = (ImageView) findViewById(R.id.iv_return_taskmanager);
        clearAll = (Button) findViewById(R.id.completed_taskmanager);

        initData();

        ivReturn.setOnClickListener(this);
        clearAll.setOnClickListener(this);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new SwipeAdapter(TaskManagerActivity.this, taskInfos,
                    list_view.getRightViewWidth(), new SwipeAdapter.IOnItemRightClickListener() {
                @Override
                public void onRightClick(View v, int position) {
                    if (taskInfos.get(position).getPackageName().equals(getPackageName())) {
                        ToastUtils.showToast(TaskManagerActivity.this, "不能清理自己哦");
                    } else if (position == taskInfos.size()-1) {
                        ToastUtils.showToast(TaskManagerActivity.this, "系统应用不能清理");
                    } else {
                        taskInfos.remove(taskInfos.get(position));
                        ToastUtils.showToast(TaskManagerActivity.this, "为您清理"
                                + Formatter.formatFileSize(TaskManagerActivity.this, taskInfos.get(position).getMemorySize()) + "内存");
                        activityManager.killBackgroundProcesses(taskInfos.get(position)
                                .getPackageName());
                    }
                    adapter.notifyDataSetChanged();
                }
            });
            list_view.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                taskInfos = TaskInfoParser
                        .getTaskInfos(TaskManagerActivity.this);

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return_taskmanager:
                startActivity(new Intent(this, HomeActivity.class));
                finish();

                overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
                break;

            case R.id.completed_taskmanager:
                killAllProcess();
                break;

            default:
                break;
        }
    }

    private void killAllProcess() {
        List<TaskInfo> killLists = new ArrayList<TaskInfo>();
        int totalCount = 0;
        // 清理的进程的大小
        long killMem = 0;

        for (TaskInfo taskinfo : taskInfos) {
            if (taskinfo.getPackageName().equals(getPackageName())) {
                continue;
            } else {
                killLists.add(taskinfo);
            }
        }
        for (TaskInfo taskinfo : killLists) {
            if (taskinfo.getPackageName().equals(getPackageName())) {
                continue;
            } else {
                totalCount++;
                killMem += taskinfo.getMemorySize();

                taskInfos.remove(taskinfo);

                activityManager.killBackgroundProcesses(taskinfo
                        .getPackageName());
            }

        }
        ToastUtils.showToast(this, "共杀死" + totalCount + "内存" + "为您清理" +
                Formatter.formatFileSize(TaskManagerActivity.this, killMem) + "内存");

        adapter.notifyDataSetChanged();
    }
}

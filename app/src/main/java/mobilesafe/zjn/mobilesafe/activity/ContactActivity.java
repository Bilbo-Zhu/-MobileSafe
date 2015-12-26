package mobilesafe.zjn.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import mobilesafe.zjn.mobilesafe.R;

/**
 * Created by zjn on 2015/12/6.
 */
public class ContactActivity extends Activity{
    private ListView lvList;
    private ArrayList<HashMap<String, String>> readContact;
    private ImageView ivreturn;
    private SharedPreferences mPrefs;
    private boolean aBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);

        mPrefs = getSharedPreferences("config",MODE_PRIVATE);

        lvList = (ListView)findViewById(R.id.lv_list);
        ivreturn = (ImageView)findViewById(R.id.topbarturn_contact);

        readContact = readContact();
        lvList.setAdapter(new SimpleAdapter(this, readContact,
                R.layout.contact_list_item, new String[] { "name", "phone" },
                new int[] { R.id.tv_name, R.id.tv_phone }));

        lvList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String phone = readContact.get(position).get("phone");// 读取当前item的电话号码
                Intent intent = new Intent();
                intent.putExtra("phone", phone);

                aBoolean = mPrefs.getBoolean("blacknum", false);
                if (aBoolean) {
                    setResult(2, intent);
                    mPrefs.edit().putBoolean("blacknum", false).commit();
                } else {
                    setResult(Activity.RESULT_OK, intent);// 将数据放在intent中返回给上一个页面
                }

                finish();
            }
        });

        ivreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aBoolean = mPrefs.getBoolean("blacknum", false);
                if(aBoolean){
                    startActivity(new Intent(ContactActivity.this,SetBlackNumActivity.class));
                    mPrefs.edit().putBoolean("blacknum", false).commit();
                    finish();

                    // 两个界面切换的动画
                    overridePendingTransition(R.anim.tran_previous_in,
                            R.anim.tran_previous_out);// 进入动画和退出动画
                }else{
                    startActivity(new Intent(ContactActivity.this,SetSafePhoneActivity.class));
                    finish();

                    // 两个界面切换的动画
                    overridePendingTransition(R.anim.tran_previous_in,
                            R.anim.tran_previous_out);// 进入动画和退出动画
                }
            }
        });
    }

    private ArrayList<HashMap<String, String>> readContact() {
        // 首先,从raw_contacts中读取联系人的id("contact_id")
        // 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
        // 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
        Uri rawContactsUri = Uri
                .parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        // 从raw_contacts中读取联系人的id("contact_id")
        Cursor rawContactsCursor = getContentResolver().query(rawContactsUri,
                new String[] { "contact_id" }, null, null, null);
        if (rawContactsCursor != null) {
            while (rawContactsCursor.moveToNext()) {
                String contactId = rawContactsCursor.getString(0);
                // System.out.println(contactId);

                // 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
                Cursor dataCursor = getContentResolver().query(dataUri,
                        new String[] { "data1", "mimetype" }, "contact_id=?",
                        new String[] { contactId }, null);

                if (dataCursor != null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        // System.out.println(contactId + ";" + data1 + ";"
                        // + mimetype);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name"
                                .equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawContactsCursor.close();
        }
        return list;
    }
}

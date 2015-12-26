package mobilesafe.zjn.mobilesafe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import mobilesafe.zjn.mobilesafe.bean.BlackNumberInfo;

/**
 * Created by zjn on 2015/12/16.
 */
public class BlackNumberDao {
    private  BlackNumberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberOpenHelper(context);
    }

    public Boolean add(String number,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentvalues = new ContentValues();
        contentvalues.put("number",number);
        contentvalues.put("mode",mode);
        long rowid = db.insert("blacknumber", null, contentvalues);

        return rowid != -1;
    }

    public Boolean delete(String number){
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowNum = db.delete("blacknumber", "number=?", new String[]{number});
        if(rowNum == 0){
            return false;
        }else{
            return true;
        }
    }

    public Boolean changeNumberMode(String number,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);
        int rowNum = db.update("blacknumber", contentValues, "number=?", new String[]{number});
        if(rowNum == 0){
            return false;
        }else{
            return true;
        }
    }

    public String findNumber(String number){
        String mode = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        if(cursor.moveToNext()){
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    public List<BlackNumberInfo> findAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        Cursor cursor = db.query("blacknumber", new String[]{"number", "mode"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            blackNumberInfos.add(info);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(3000);

        return blackNumberInfos;
    }

    /**
     * 分页加载
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public List<BlackNumberInfo> findPar(int pageNumber,int pageSize){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(pageSize),
                String.valueOf(pageSize * pageNumber)});//offset表示跳过多少条数据
        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            blackNumberInfos.add(info);
        }
        cursor.close();
        db.close();

        return blackNumberInfos;
    }

    /**
     * 分批加载
     */
    public List<BlackNumberInfo> findPar2(int startIndex,int maxCount){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?", new String[]{String.valueOf(maxCount),
                String.valueOf(startIndex)});
        while (cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getString(1));
            blackNumberInfos.add(info);
        }
        cursor.close();
        db.close();

        return blackNumberInfos;
    }

    /**
     * 获取总的记录数
     * @return
     */
    public int getTotalNumber(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}

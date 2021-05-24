package com.example.ttstest.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ttstest.db.DBHelper;
import com.example.ttstest.info.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Created by Administrator on 2020/11/24.
 * 邮箱：
 */
public class PersonService {
    private DBHelper dbHelper;


    public PersonService(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    //插入数据
    public void save(Person person) {
        SQLiteDatabase dbWrit = dbHelper.getWritableDatabase();
        dbWrit.execSQL("insert into person (Titie,Mater,Time,bundleid) values(?,?,?,?)", new Object[]{person.getTitie(), person.getMatter(), person.getTime(), person.getBundleid()});
    }

    //查询所有
    public List<Person> selectPerson() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //创建集合
        List<Person> persons = new ArrayList<Person>();
        //创建Cursor对象
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from person", null);
            while (cursor.moveToNext()) {
                String Titie = cursor.getString(cursor.getColumnIndex("Titie"));
                String Matter = cursor.getString(cursor.getColumnIndex("Mater"));
                String Time = cursor.getString(cursor.getColumnIndex("Time"));
                String bundleid = cursor.getString(cursor.getColumnIndex("bundleid"));

                //创建Person对象
                Person p = new Person(Titie, Matter, Time, bundleid);
                //将创建出来的Person对象添加到集合中去
                persons.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭相应的资源
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return persons;
    }

    //根据姓名条件查询
    public List<Person> vague(String id, List<Person> persons) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //创建Cursor对象
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from person where Titie=?", new String[]{id.toString()});
            while (cursor.moveToNext()) {
                String Titie = cursor.getString(cursor.getColumnIndex("Titie"));
                String Matter = cursor.getString(cursor.getColumnIndex("Mater"));
                String Time = cursor.getString(cursor.getColumnIndex("Time"));
                String bundleid = cursor.getString(cursor.getColumnIndex("bundleid"));
                //将创建出来的Person对象添加到集合中去

                Person p = new Person(Titie, Matter, Time, bundleid);
                persons.add(p);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭相应的资源
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return persons;
    }

    //模糊查询
    public List<Person> queryLike(String str, List<Person> persons) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //创建Cursor对象
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + "person"
                    + " where " + "Titie" + " like " + "'%" + str + "%" + "'", null);
            while (cursor.moveToNext()) {
                String Titie = cursor.getString(cursor.getColumnIndex("Titie"));
                String Matter = cursor.getString(cursor.getColumnIndex("Mater"));
                String Time = cursor.getString(cursor.getColumnIndex("Time"));
                String bundleid = cursor.getString(cursor.getColumnIndex("bundleid"));
                //将创建出来的Person对象添加到集合中去

                Person p = new Person(Titie, Matter, Time, bundleid);
                persons.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭相应的资源
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return persons;
    }

    //删除所有数据
    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "delete from " + "person";
        db.execSQL(sql);
        db.close();
    }

}


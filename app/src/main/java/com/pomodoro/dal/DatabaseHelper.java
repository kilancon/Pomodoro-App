package com.pomodoro.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.pomodoro.model.BlockedApp;
import com.pomodoro.model.Task;
import com.pomodoro.model.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Pomodoro.db";
    private static final String TABLE_TASK = "task";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_EST = "est";
    private static final String COLUMN_USERID = "user_id";

    private static final String TABLE_USER = "user";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PWD = "password";

    private static final String TABLE_BLOCKEDAPP = "blockedapp";
    private static final String COLUMN_PKGNAME = "pkgname";
    private static final String COLUMN_LOCKED = "locked";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.d("DB Manager","DB Manager");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery1 = "CREATE TABLE "+TABLE_TASK +" (" +
                COLUMN_ID +" integer primary key, "+
                COLUMN_NAME + " TEXT, "+
                COLUMN_STATE +" BOOLEAN, "+
                COLUMN_COUNT + " INTEGER DEFAULT 0," +
                COLUMN_EST + " INTEGER ," +
                COLUMN_USERID +" INTEGER DEFAULT 0)"; //userid 0 ~ user local

        String sqlQuery2 =  "CREATE TABLE user (" +
                COLUMN_ID +" integer primary key autoincrement, "+
                COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, "+
                COLUMN_PWD +" TEXT NOT NULL)";
        String sqlQuery3 =  "CREATE TABLE " +TABLE_BLOCKEDAPP + " (" +
                COLUMN_PKGNAME +" TEXT primary key, "+
                COLUMN_LOCKED +" INTEGER DEFAULT 0)";

        db.execSQL(sqlQuery1);
        db.execSQL(sqlQuery2);
        db.execSQL(sqlQuery3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_BLOCKEDAPP);
        onCreate(db);
    }

    public ArrayList<Task> getAllTaskByState(int state, int user_id) { //state=0 for unfinished task and 1 for finished.
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Task> dsTask = new ArrayList();
        Cursor cursor = db.query(TABLE_TASK, null, COLUMN_STATE + "=? and " + COLUMN_USERID + "=?", new String[]{""+state, ""+user_id}, null, null, null);
        while (cursor.moveToNext()){
            Task task = new Task(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)>0, cursor.getInt(3), cursor.getInt(4));
            dsTask.add(task);
        }
        cursor.close();
        return dsTask;
    }

    public Task getTaskById(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //columns = null : select *
        Cursor cursor = db.query(TABLE_TASK, null, COLUMN_ID + "=?", new String[]{""+id}, null, null, null);
        while (cursor.moveToNext()) {
            Task task = new Task(cursor.getInt(0), cursor.getString(1), cursor.getInt(2) > 0, cursor.getInt(3), cursor.getInt(4));
            cursor.close();
            return task;
        }
        cursor.close();
        return null;
    }

    public void addTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_STATE, task.getState());
        values.put(COLUMN_COUNT, task.getCount());
        values.put(COLUMN_EST, task.getEst());
        values.put(COLUMN_USERID, task.getUserid());
        //Neu de null thi id tu tang
        Long id = db.insert(TABLE_TASK,null,values);
        Log.e("Insert ID", ""+id);
    }

    public int updateTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, task.getName());
        values.put(COLUMN_STATE, task.getState());
        values.put(COLUMN_COUNT, task.getCount());
        values.put(COLUMN_EST, task.getEst());
        // return rows affected
        return db.update(TABLE_TASK, values, COLUMN_ID+"=?", new String[]{""+task.getId()});
    }

    public int deleteTaskById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        //return rows deleted
        return db.delete(TABLE_TASK, COLUMN_ID+"=?", new String[]{""+id});
    }

    //TABLE_USER
    public User getUser(String email, String pwd) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_EMAIL + "=? and " +  COLUMN_PWD + "=?", new String[]{email, pwd}, null, null, null);
        while (cursor.moveToNext()) {
            User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            cursor.close();
            return user;
        }
        cursor.close();
        db.close();
        return null;
    }

    public void addUser(User user) throws Exception{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PWD, user.getPassword());
        Long id = db.insert(TABLE_USER,null,values);
        db.close();
        Log.e("Insert ID", ""+id); //co the return id de sign in luon
    }

    public ArrayList<BlockedApp> getAllBlockedApp(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<BlockedApp> ds = new ArrayList<>();
        Cursor cursor = db.query(TABLE_BLOCKEDAPP, null, COLUMN_LOCKED + "= 1", null, null,null,null);
        while (cursor.moveToNext()){
            BlockedApp app = new BlockedApp(cursor.getString(0), cursor.getInt(1)>0);
            ds.add(app);
        }
        cursor.close();
        db.close();
        return ds;
    }

    public BlockedApp getBlockedApp(String pkgname){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BLOCKEDAPP, null, COLUMN_PKGNAME + "=?", new String[]{pkgname}, null, null, null);
        while (cursor.moveToNext()) {
            BlockedApp app = new BlockedApp(cursor.getString(0), cursor.getInt(1)>0);
            cursor.close(); db.close();
            return app;
        }
        cursor.close();
        db.close();
        return null;
    }

    public void addOrUpdBlockedApp(BlockedApp app, boolean isAdd){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PKGNAME, app.getPackageName());
        values.put(COLUMN_LOCKED, app.isLocked());
        if(isAdd){
            db.insert(TABLE_BLOCKEDAPP, null, values);
            Log.e("Insert", "ins thanh cong");
        }
        else{
            db.update(TABLE_BLOCKEDAPP, values, COLUMN_PKGNAME+"=?", new String[]{app.getPackageName()});
            Log.e("Update", "upd thanh cong");
        }
    }
}

package com.example.androidtvremote.logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.androidtvremote.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ATVHelper extends SQLiteOpenHelper {
    private static final String APPS_TABLE = "applications";
    private static final String TASKS_TABLE = "tasks";

    public ATVHelper(@Nullable Context context) {
        super(context, "ATVDatabase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS \"" + APPS_TABLE + "\" (\"name\" TEXT NOT NULL,\"package\" TEXT,\"img\" INTEGER,PRIMARY KEY(\"package\"))");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS \"" + TASKS_TABLE + "\" (\"actionName\" TEXT,\"id\" TEXT,\"actionId\" INTEGER,\"actionTime\" INTEGER,PRIMARY KEY(\"id\"))");
        App[] apps = {
                new App(R.drawable.netflix, "netflix", "https://www.netflix.com/title.*"),
                new App(R.drawable.youtube, "youtube","https://www.youtube.com"),
                new App(R.drawable.spotify,"spotify","https://spotify.app.link/?product=open")
        };
        for (App app : apps) {
            ContentValues values = new ContentValues();
            values.put("name", app.getName());
            values.put("package", app.getContent());
            values.put("img", app.getImage());
            sqLiteDatabase.insert(APPS_TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void write(String table, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(table, null, values);
        db.close();
    }

    public void addApplication(App app) {
        ContentValues values = new ContentValues();
        values.put("name", app.getName());
        values.put("package", app.getContent());
        values.put("img", app.getImage());
        write(APPS_TABLE, values);
    }

    public void addTask(Task task) {
        ContentValues values = new ContentValues();
        values.put("id", task.getId());
        values.put("actionId", task.getActionId());
        values.put("actionTime", task.getTime().getTimeInMillis());
        values.put("actionName", task.getActionName());
        write(TASKS_TABLE, values);
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TASKS_TABLE, "id = ?", new String[]{task.getId()});
        db.close();
    }

    public void deleteApplication(App app) {
        delete("package", app.getContent());
    }

    private void delete(String primaryKey, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = primaryKey + " = ?";
        String[] selectionArgs = {value};
        db.delete("users", selection, selectionArgs);
        db.close();
    }

    public Task getTask(String id) {
        String[] projection = {
                "actionId",
                "actionName",
                "actionTime"
        };
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TASKS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            byte actionId = (byte) cursor.getInt(cursor.getColumnIndexOrThrow("actionId"));
            long actionTime = cursor.getLong(cursor.getColumnIndexOrThrow("actionTime"));
            String actionName = cursor.getString(cursor.getColumnIndexOrThrow("actionName"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(actionTime);
            cursor.close();
            db.close();
            return new Task(actionName, calendar, actionId, id);
        }
        return null;
    }

    public App getApp(String appPackage) {
        String[] projection = {
                "name",
                "img"
        };
        String selection = "package = ?";
        String[] selectionArgs = {String.valueOf(appPackage)};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(APPS_TABLE, projection, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int img = cursor.getInt(cursor.getColumnIndexOrThrow("img"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            cursor.close();
            db.close();
            return new App(img, name, appPackage);
        }
        return null;
    }

    public App[] getApps() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<App> apps = new ArrayList<>();
        String[] projection = {
                "name",
                "package",
                "img"
        };
        Cursor cursor = db.query(APPS_TABLE, projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String appPackage = cursor.getString(cursor.getColumnIndexOrThrow("package"));
            int img = cursor.getInt(cursor.getColumnIndexOrThrow("img"));
            apps.add(new App(img, name, appPackage));
        }
        App[] arr = new App[apps.size()];
        apps.toArray(arr);
        cursor.close();
        db.close();
        return arr;
    }

    public ArrayList<Task> getTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Task> tasks = new ArrayList<>();
        String[] projection = {
                "actionName",
                "actionId",
                "actionTime",
                "id"
        };
        Cursor cursor = db.query(TASKS_TABLE, projection, null, null, null, null, null);
        while (cursor.moveToNext()) {
            byte actionId = (byte) cursor.getInt(cursor.getColumnIndexOrThrow("actionId"));
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String actionName = cursor.getString(cursor.getColumnIndexOrThrow("actionName"));
            long actionTime = cursor.getLong(cursor.getColumnIndexOrThrow("actionTime"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(actionTime);
            tasks.add(new Task(actionName, calendar, actionId, id));
        }
        cursor.close();
        db.close();
        return tasks;
    }

}

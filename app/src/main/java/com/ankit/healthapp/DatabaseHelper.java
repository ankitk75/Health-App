package com.ankit.healthapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthApp.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS users (" +
            "email TEXT PRIMARY KEY," +
            "password TEXT NOT NULL)"
        );

        // Create profile table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS profiles (" +
            "email TEXT PRIMARY KEY," +
            "name TEXT," +
            "age TEXT," +
            "gender TEXT," +
            "height TEXT," +
            "weight TEXT," +
            "birthday TEXT," +
            "emergency_contact TEXT," +
            "FOREIGN KEY(email) REFERENCES users(email))"
        );

        // Create diet_entries table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS diet_entries (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT NOT NULL," +
            "meal_type TEXT NOT NULL," +
            "calories INTEGER NOT NULL," +
            "entry_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(email) REFERENCES users(email))"
        );

        // Create nicotine_entries table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS nicotine_entries (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT NOT NULL," +
            "intake_type TEXT NOT NULL," +
            "quantity INTEGER NOT NULL," +
            "entry_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(email) REFERENCES users(email))"
        );

        // Add proper assessments table creation
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS assessments (" +
            "email TEXT PRIMARY KEY," +
            "fitness_level TEXT NOT NULL," +
            "plan_duration TEXT NOT NULL," +
            "assessment_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(email) REFERENCES users(email))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS diet_entries");
        db.execSQL("DROP TABLE IF EXISTS profiles");
        db.execSQL("DROP TABLE IF EXISTS users");
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS assessments");
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS assessments (" +
                "email TEXT PRIMARY KEY," +
                "fitness_level TEXT NOT NULL," +
                "plan_duration TEXT NOT NULL," +
                "assessment_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(email) REFERENCES users(email))"
            );
        }
        onCreate(db);
    }

    // User authentication methods
    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, 
            "email = ? AND password = ?", 
            new String[]{email, password}, 
            null, null, null);
        
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", null, 
            "email = ?", 
            new String[]{email}, 
            null, null, null);
        
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        
        long result = db.insert("users", null, values);
        return result != -1;
    }

    // Profile methods
    public long insertOrUpdateProfile(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        String email = values.getAsString("email");
        
        // Try to update first
        int updated = db.update("profiles", values, "email = ?", new String[]{email});
        
        if (updated > 0) {
            return updated;
        } else {
            // If update failed, insert new record
            return db.insert("profiles", null, values);
        }
    }

    public Cursor getProfile(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("profiles", null, 
            "email = ?", 
            new String[]{email}, 
            null, null, null);
    }

    // Diet tracking methods
    public long addDietEntry(String email, String mealType, int calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("meal_type", mealType);
        values.put("calories", calories);
        return db.insert("diet_entries", null, values);
    }

    public Cursor getDietEntries(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("diet_entries", null,
            "email = ?",
            new String[]{email},
            null, null, "entry_date DESC");
    }

    // Nicotine tracking methods
    public long addNicotineEntry(String email, String intakeType, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("intake_type", intakeType);
        values.put("quantity", quantity);
        return db.insert("nicotine_entries", null, values);
    }

    public Cursor getNicotineEntries(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("nicotine_entries", null,
            "email = ?",
            new String[]{email},
            null, null, "entry_date DESC");
    }

    // Assessment methods
    public long insertOrUpdateAssessment(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        String email = values.getAsString("email");
        
        int updated = db.update("assessments", values, "email = ?", new String[]{email});
        return updated > 0 ? updated : db.insert("assessments", null, values);
    }
} 
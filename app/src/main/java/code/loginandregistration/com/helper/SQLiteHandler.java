package code.loginandregistration.com.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by 43065 on 2/21/2017.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LoginRegistration";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERID = "userid";
    private static final String PROFILE_PIC = "profilePic";
    private static final String KEY_NAME = "name";
    private static final String KEY_LNAME = "lname";
    private static final String STATUS = "status";
    private static final String KEY_MOBILE = "mobile";
    private static final String ISADMIN = "isAdmin";
    private static final String TOKEN = "token";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                //+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERID + " TEXT," + KEY_NAME + " TEXT," + KEY_LNAME + " TEXT," + KEY_MOBILE + " TEXT," + PROFILE_PIC + " TEXT," + STATUS + " TEXT," + ISADMIN + " TEXT" +")";
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USERID + " TEXT," + KEY_NAME + " TEXT," + KEY_LNAME + " TEXT," + KEY_MOBILE + " TEXT," + PROFILE_PIC + " TEXT," + STATUS + " TEXT," + ISADMIN + " TEXT," + TOKEN + " TEXT" +")";

        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String userId, String name, String lName, String mobile, String profilePic, String status, String isAdmin, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userId); // Password
        values.put(KEY_NAME, name); // Name
        values.put(KEY_LNAME, lName); // Emp No
        values.put(KEY_MOBILE, mobile); // Mobile
        values.put(PROFILE_PIC, profilePic); // Mobile
        values.put(STATUS, status); // Mobile
        values.put(ISADMIN, isAdmin); // Mobile
        values.put(TOKEN, token); // Mobile

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(1));
            user.put("name", cursor.getString(2));
            user.put("lName", cursor.getString(3));
            user.put("mobile", cursor.getString(4));
            user.put("profilePic", cursor.getString(5));
            user.put("status", cursor.getString(6));
            user.put("isAdmin", cursor.getString(7));
            user.put("token", cursor.getString(8));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}

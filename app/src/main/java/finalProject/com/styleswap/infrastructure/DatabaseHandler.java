package finalProject.com.styleswap.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * DatabasHandler:
 *
 *              DatabaseHandler stores user login info when the user selects the "Remember me"
 *              option in the login screen. 
 *
 *              Created by gary on 17/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "debug_db";
    private static final String DATABASE_NAME = "StyleSwap";
    private static final int DATABASE_VERSION = 7;

    private static final String TABLE_REMEMBER_ME = "userRemember";
    private static final String R_ID = "id_remember";
    private static final String KEY_REMEMBER_EMAIL = "remember_mail";
    private static final String KEY_REMEMBER_PASSWORD = "remember_pass";
    private static final String KEY_REMEMBER_ME = "remember_me";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Db created :)");
        String CREATE_REMEMBER_TABLE = createRememberMe();

        sqLiteDatabase.execSQL(CREATE_REMEMBER_TABLE);
        //writeDummyInfo();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REMEMBER_ME);
        onCreate(sqLiteDatabase);
    }

    private String createRememberMe() {
        return "CREATE TABLE " + TABLE_REMEMBER_ME + " ( " +
            R_ID + " INTEGER PRIMARY KEY, " +
            KEY_REMEMBER_EMAIL + " TEXT, " +
            KEY_REMEMBER_PASSWORD + " TEXT, " +
            KEY_REMEMBER_ME + " INTEGER )";
    }

    private void writeDummyInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_REMEMBER_EMAIL, "");
        values.put(KEY_REMEMBER_PASSWORD, "");
        values.put(KEY_REMEMBER_ME, 0);
        db.insert(TABLE_REMEMBER_ME, null, values);
        Log.d(TAG, "put in: " + values);

        db.close();
    }

    public void addDetails(String email, String password, int rememberMe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_REMEMBER_EMAIL, email);
        values.put(KEY_REMEMBER_PASSWORD, password);
        values.put(KEY_REMEMBER_ME, rememberMe);
        db.insert(TABLE_REMEMBER_ME, null, values);
        Log.d(TAG, "put in: " + values);

        db.close();
    }

    public void updateEmail(String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_REMEMBER_EMAIL, newEmail);
        db.update(TABLE_REMEMBER_ME, values, R_ID + "=\'" + 1 + "\'", null);
        Log.d(TAG, "updated table in: " + values);
        db.close();
    }

    public void updateRememberMe(int rememberMe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_REMEMBER_ME, rememberMe);
        db.update(TABLE_REMEMBER_ME, values, R_ID + "=\'" + 1 + "\'", null);
        Log.d(TAG, "updated table in: " + values);
        db.close();
    }

    public void updatePassword(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_REMEMBER_PASSWORD, password);
        db.update(TABLE_REMEMBER_ME, values, R_ID + "=\'" + 1 + "\'", null);
        Log.d(TAG, "updated table in: " + values);
        db.close();
    }

    public String readEmail() {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_REMEMBER_ME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        String email = null;
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndex(KEY_REMEMBER_EMAIL));
            //usr.setImg(cursor.getBlob(cursor.getColumnIndex()));
            Log.d(TAG, "read user successfully with email");
        }

        return email;
    }

    public String readPassword() {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_REMEMBER_ME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        String password = null;
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(KEY_REMEMBER_PASSWORD));
            //usr.setImg(cursor.getBlob(cursor.getColumnIndex()));
            Log.d(TAG, "read user successfully with email");
        }

        return password;
    }

    public boolean readRememberMe() {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_REMEMBER_ME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        int rememberMe = 0;
        if (cursor.moveToFirst()) {
            rememberMe = cursor.getInt(cursor.getColumnIndex(KEY_REMEMBER_ME));
            //usr.setImg(cursor.getBlob(cursor.getColumnIndex()));
            Log.d(TAG, "read user successfully with email");
        }

        if (rememberMe == 1)
            return true;
        else
            return false;
    }

    public void deleteEntry() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_REMEMBER_ME, R_ID + "=\'" + 1 + "\'", null);
        Log.d(TAG, "deleted entry in Table");
        db.close();
    }

    public static byte[] createByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap getBitmapFromBlob(byte[] bytes) {
        return BitmapFactory.decodeByteArray(
            bytes, 0,
            bytes.length);
    }

    //Convert list to byte stream
    private byte[] createByteArray(Object obj) {
        byte[] bArray = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objOstream = new ObjectOutputStream(baos);
            objOstream.writeObject(obj);
            bArray = baos.toByteArray();

        } catch (IOException e) {
            Log.d(null, "Problem in createByteArray");
        }

        return bArray;
    }

    //decode byte stream to list
    private ArrayList<Integer> decodeByteArray(byte[] bytes) {
        ArrayList<Integer> list = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            list = (ArrayList<Integer>) ois.readObject();
        } catch (IOException e) {
            Log.d(null, "Problem in decodeByteArray");
        } catch (ClassNotFoundException e) {
            Log.d(null, "Problem in decodeByteArray");
        }

        return list;
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(openDb) {
            if(db == null || !db.isOpen()) {
                db = getReadableDatabase();
            }

            if(!db.isReadOnly()) {
                db.close();
                db = getReadableDatabase();
            }
        }

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}
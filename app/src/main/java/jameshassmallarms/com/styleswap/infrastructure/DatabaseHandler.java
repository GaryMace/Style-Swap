package jameshassmallarms.com.styleswap.infrastructure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import jameshassmallarms.com.styleswap.impl.User;

/**
 * Created by gary on 17/10/16.
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

    private static final String TABLE_USER = "userTable";
    private static final String U_ID = "id_user";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_SIZE = "user_size";
    private static final String KEY_USER_BIO = "user_bio";
    private static final String KEY_USER_PIC_ID = "user_pic_id";
    private static final String KEY_USER_LOCATION_LAT = "user_location_lat";
    private static final String KEY_USER_LOCATION_LON = "user_location_lon";

    private static final String TABLE_IMAGE = "imageTable";
    private static final String I_ID = "id_image";
    private static final String KEY_IMAGE_KEY = "image_keyimg";
    private static final String KEY_IMAGE = "image_img";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //String CREATE_USER_TABLE = createUserTable();
        //String CREATE_IMAGE_TABLE = createImageTable();
        String CREATE_REMEBER_TABLE = createRememberMe();

        sqLiteDatabase.execSQL(CREATE_REMEBER_TABLE);
        //sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
        writeDummyInfo();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REMEMBER_ME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        onCreate(sqLiteDatabase);
    }

    private String createRememberMe() {
        return "CREATE TABLE " + TABLE_REMEMBER_ME + " ( " +
            R_ID + " INTEGER PRIMARY KEY, " +
            KEY_REMEMBER_EMAIL + " TEXT, " +
            KEY_REMEMBER_PASSWORD + " TEXT, " +
            KEY_REMEMBER_ME + " INTEGER )";
    }

    private String createUserTable() {
        return "CREATE TABLE " + TABLE_USER + " ( " +
            U_ID + " INTEGER PRIMARY KEY, " +
            KEY_USER_EMAIL + " TEXT, " +
            KEY_USER_NAME + " TEXT, " +
            KEY_USER_PHONE + " TEXT, " +
            KEY_USER_SIZE + " INTEGER, " +
            KEY_USER_LOCATION_LAT + " REAL, " +
            KEY_USER_LOCATION_LON + " REAL, " +
            KEY_USER_BIO + " TEXT, " +
            KEY_USER_PIC_ID + " INTEGER )";
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

///////////////////////////////////////////////////////////////////////////////

    private String createImageTable() {
        return "CREATE TABLE " + TABLE_IMAGE + " ( " +
            I_ID + " INTEGER PRIMARY KEY, " +
            KEY_IMAGE_KEY + " TEXT, " +
            KEY_IMAGE + " BLOB )";
    }

    public void addUserToDatabase(User usr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, usr.getName());
        values.put(KEY_USER_EMAIL, usr.getEmail());
        values.put(KEY_USER_PHONE, usr.getPhoneNum());
        values.put(KEY_USER_SIZE, usr.getDressSize());
        values.put(KEY_USER_BIO, usr.getBio());
        values.put(KEY_USER_PIC_ID, "");
        db.insert(TABLE_USER, null, values);
        Log.d(TAG, "put in: " + values);

        values = new ContentValues();
        values.put(KEY_IMAGE_KEY, usr.getEmail());

        Log.d(TAG, "put in: " + values);

        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }

    public void writeImageForUser(Bitmap img, String emailKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE_KEY, emailKey);
        values.put(KEY_IMAGE, createByteArray(img));
        db.update(TABLE_IMAGE, values, KEY_IMAGE_KEY + "=\'" + emailKey + "\'", null);
        Log.d(TAG, "updated in: " + values);
        db.close();
    }

    public User readUserFromDatabase(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER +
            " WHERE " + KEY_USER_EMAIL + "=\'" + email + "\'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        User usr;
        if (cursor.moveToFirst()) {
            Log.d(TAG, "attempting to read user with email: " + email);
            usr = new User(email);
            usr.setName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
            usr.setDressSize(cursor.getInt(cursor.getColumnIndex(KEY_USER_SIZE)));
            usr.setBio(cursor.getString(cursor.getColumnIndex(KEY_USER_BIO)));

            //TODO: change the below to update based on GPS.
            //usr.setImg(cursor.getBlob(cursor.getColumnIndex()));
            Log.d(TAG, "read user successfully with email: " + email);
            return usr;
        }

        return null;
     }

    public String readNameForUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_USER_NAME + " FROM " + TABLE_USER +
            " WHERE " + KEY_USER_EMAIL + "=\'" + email + "\'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        String name = "";
        if (cursor.moveToFirst()) {
            Log.d("DB", "trying to get name from user: " + email);
            name = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
            Log.d("DB", "Got image from user: " + email);
        }
        db.close();
        return name;
    }

    public Bitmap readImageForUser(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_IMAGE + " FROM " + TABLE_IMAGE +
            " WHERE " + KEY_IMAGE_KEY + "=\'" + userName + "\'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        Bitmap img = null;
        if (cursor.moveToFirst()) {
            Log.d("DB", "trying to get image from user: " + userName);
            img = getBitmapFromBlob(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)));
            Log.d("DB", "Got image from user: " + userName);
        }
        db.close();
        return img;
    }

    public String readBioForUser(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_USER_BIO + " FROM " + TABLE_USER +
            " WHERE " + KEY_USER_EMAIL + "=\'" + userName + "\'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        String bio = "";
        if (cursor.moveToFirst()) {
            Log.d("DB", "trying to get bio from user: " + userName);
            bio = cursor.getString(cursor.getColumnIndex(KEY_USER_BIO));
            Log.d("DB", "Got bio from user: " + userName);
        }
        db.close();
        return bio;
    }

    public int readSizeForUser(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_USER_SIZE + " FROM " + TABLE_USER +
            " WHERE " + KEY_USER_EMAIL + "=\'" + userName + "\'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int size = -1;
        if (cursor.moveToFirst()) {
            Log.d("DB", "trying to get size from user: " + userName);
            size = cursor.getInt(cursor.getColumnIndex(KEY_USER_SIZE));
            Log.d("DB", "Got size from user: " + userName);
        }
        db.close();
        return size;    //handle -1
    }

    public String readPhoneForUser(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_USER_PHONE + " FROM " + TABLE_USER +
            " WHERE " + KEY_USER_EMAIL + "=\'" + userName + "\'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        String phone = "";
        if (cursor.moveToFirst()) {
            Log.d("DB", "trying to get phone number from user: " + userName);
            phone = cursor.getString(cursor.getColumnIndex(KEY_USER_PHONE));
            Log.d("DB", "Got phone number from user: " + userName);
        }
        db.close();
        return phone;
    }

    public LocationTuple readUserLocation(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT " + KEY_USER_LOCATION_LAT + ", " + KEY_USER_LOCATION_LON + " FROM " + TABLE_USER +
            " WHERE " + KEY_USER_EMAIL + "=\'" + userName + "\'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        double lat = 0, lon = 0;
        if (cursor.moveToFirst()) {
            Log.d("DB", "trying to get location from user: " + userName);
            lat = cursor.getDouble(cursor.getColumnIndex(KEY_USER_LOCATION_LAT));
            lon = cursor.getDouble(cursor.getColumnIndex(KEY_USER_LOCATION_LON));
            Log.d("DB", "Gotlocation from user: " + userName);
        }
        db.close();
        return new LocationTuple(lat, lon);
    }

    public void updateUserLocation(String userName, String lat, String lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_LOCATION_LAT, lat);
        values.put(KEY_USER_LOCATION_LON, lon);
        db.update(TABLE_USER, values, KEY_USER_EMAIL + "=\'" + userName + "\'", null);
        Log.d(TAG, "updated table in: " + values);
        db.close();
    }

    public void updateBioForUser(String userName, String newBio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_BIO, newBio);
        db.update(TABLE_USER, values, KEY_USER_EMAIL + "=\'" + userName + "\'", null);
        Log.d(TAG, "updated table in: " + values);
        db.close();
    }

    public void updateNameForUser(String userName, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_NAME, newName);
        db.update(TABLE_USER, values, KEY_USER_EMAIL + "=\'" + userName + "\'", null);
        Log.d(TAG, "updated table in: " + values);
        db.close();
    }

    public void updatePhoneForUser(String userName, String newNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_PHONE, newNumber);
        db.update(TABLE_USER, values, KEY_USER_EMAIL + "=\'" + userName + "\'", null);
        Log.d(TAG, "updated table in: " + values);
        db.close();
    }

    public void updateSizeForUser(String userName, int size) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_SIZE, size);
        db.update(TABLE_USER, values, KEY_USER_EMAIL + "=\'" + userName + "\'", null);
        Log.d(TAG, "updated table in: " + values);
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
}
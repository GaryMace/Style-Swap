package jameshassmallarms.com.styleswap.infrastructure;

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
import java.util.List;

import jameshassmallarms.com.styleswap.gui.im.Match;

/**
 * Created by gary on 17/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "StyleSwap";
        private static final int DATABASE_VERSION = 7;

        private static final String TABLE_USER = "userTable";
        private static final String U_ID = "id_user";
        private static final String KEY_USER_NAME = "user_name";
        private static final String KEY_USER_EMAIL = "user_email";
        private static final String KEY_USER_PASSWORD = "user_password";
        private static final String KEY_USER_PHONE = "user_phone";
        private static final String KEY_USER_SIZE = "user_size";
        private static final String KEY_USER_BIO = "user_bio";
        private static final String KEY_USER_PIC_ID = "user_pic_id";
        private static final String KEY_USER_MATCH_ID = "user_match_id";

        private static final String TABLE_IMAGE = "imageTable";
        private static final String I_ID = "id_image";
        private static final String KEY_IMAGE = "image_img";

        private static final String TABLE_MATCH = "matchTable";
        private static final String M_ID = "id_match";
        private static final String KEY_MATCH_LIKED = "match_likes";
        private static final String KEY_MATCH_MATCHES = "match_matches";

        public DatabaseHandler(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
                String CREATE_USER_TABLE = createUserTable();
                String CREATE_IMAGE_TABLE = createImageTable();
                String CREATE_MATCH_TABLE = createMatchTable();

                sqLiteDatabase.execSQL(CREATE_USER_TABLE);
                sqLiteDatabase.execSQL(CREATE_MATCH_TABLE);
                sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH);
                sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
                onCreate(sqLiteDatabase);
        }

        private String createUserTable() {
                return "CREATE TABLE" + TABLE_USER + " (" +
                        U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_USER_NAME + "TEXT, " +
                        KEY_USER_EMAIL + "TEXT, " +
                        KEY_USER_PASSWORD + "TEXT, " +
                        KEY_USER_PHONE + "TEXT, " +
                        KEY_USER_SIZE + "INTEGER, " +
                        KEY_USER_BIO + "TEXT, " +
                        KEY_USER_PIC_ID + "INTEGER, " +
                        KEY_USER_MATCH_ID + "INTEGER )";
        }

        private String createMatchTable() {
                return "CREATE TABLE" + TABLE_MATCH + " (" +
                        M_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_MATCH_MATCHES + "BLOB, " +
                        KEY_MATCH_LIKED + "BLOB )";
        }

        private String createImageTable() {
                return "CREATE TABLE" + TABLE_IMAGE + " (" +
                        I_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        KEY_IMAGE + "BLOB )";
        }

        public void addUser(String name, String email, String password) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_USER_NAME, name);
                values.put(KEY_USER_EMAIL, email);
                values.put(KEY_USER_PASSWORD, password);
                db.insert(TABLE_USER, null, values);
                Log.d("DB", "put in: " + values);

                values = new ContentValues();
                //values.put(KEY_IMAGEKEY, name);
                //values.put(KEY_IMAGE, createByteArray(image));
                //db.insert(TABLE_PICTURES, null, values);
                Log.d("DB", "put in image with key: " + name);
                db.close();
        }

        public List<Match> getMatches() {
                SQLiteDatabase db = this.getReadableDatabase();
                List<Match> matches = new ArrayList<>();
                String selectQuery = "SELECT * FROM " + TABLE_MATCH;
                Cursor cursor = db.rawQuery(selectQuery, null);

                do {
                        if (cursor != null) {
                                cursor.moveToFirst();
                                String name, number;
                                int imgKey;
                                ArrayList<Integer> mIds = decodeByteArray(cursor.getBlob(cursor.getColumnIndex(KEY_MATCH_MATCHES)));
                                for (Integer mId : mIds) {
                                        Match m = getUserFromMatchID(mId);

                                        name = m.getMatchName();
                                        imgKey = m.getMatchImageKey();
                                }
                                matches.add(new Match());
                        } else {
                                return null;
                        }

                } while (cursor.moveToNext());

                db.close();
                cursor.close();
                return matches;
        }

        private Match getUserFromMatchID(int mId) {
                SQLiteDatabase db = this.getReadableDatabase();
                String selectQuery = "SELECT DISTINCT FROM " + TABLE_USER + "" +
                        " WHERE " + U_ID + " = \"" + mId + "\"";
                Cursor cursor = db.rawQuery(selectQuery, null);

                Match m = new Match();
                if (cursor.moveToFirst()) {
                        Log.d("DB", "trying to get user with id: " + mId);
                        m.setMatchName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                        m.setMatchNumber(cursor.getString(cursor.getColumnIndex(KEY_USER_PHONE)));
                        m.setMatchImageKey(cursor.getInt(cursor.getColumnIndex(KEY_USER_PIC_ID)));

                        Log.d("DB", "Got user with id: " + mId);

                }
                db.close();

                return m;
        }

        private byte[] createByteArray(Bitmap bitmap) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                return stream.toByteArray();
        }

        private Bitmap getBitmapFromBlob(byte[] bytes) {
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
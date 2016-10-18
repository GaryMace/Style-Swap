package jameshassmallarms.com.styleswap.infrastructure;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gary on 17/10/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "StyleSwap";
        private static final int DATABASE_VERSION = 7;

        public DatabaseHandler(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        }
}

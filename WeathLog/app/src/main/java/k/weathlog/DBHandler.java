package k.weathlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.apache.commons.lang3.*;
/**
 * Created by konra on 05.03.2017.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DB_NAME = "weatherLog";

    public static final String WEATHER_TABLE = "weatherTable";

    public static final String KEY_WEATHER = "weather";
    public static final String KEY_COMMENT = "comment";

    public DBHandler(Context context){
        super(context,DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + WEATHER_TABLE + "(" + KEY_WEATHER + " BLOB, " + KEY_COMMENT + " TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        db.execSQL("DROP TABLE IF EXISTS " + WEATHER_TABLE);

        onCreate(db);
    }

    public void addEntry(Weather weather, String comment){

        byte[] serializedWeather = SerializationUtils.serialize(weather);

        ContentValues cv = new ContentValues();
        cv.put(KEY_WEATHER, serializedWeather);
        cv.put(KEY_COMMENT, comment);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(WEATHER_TABLE,null, cv);
        db.close();
    }

    public void deleteEntry(int id){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(WEATHER_TABLE, "rowid = ?", new String[]{Integer.toString(id)});
        db.close();
    }

    public Cursor getAllEntries(){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT rowid _id, * FROM " + WEATHER_TABLE,null);


        return cursor;
    }

    public void clearDB(){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(WEATHER_TABLE, null, null);
        db.close();
    }

    public Cursor getEntry(int id){

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WEATHER_TABLE + " WHERE rowid = ?", new String[]{Integer.toString(id)});

        return cursor;
    }

}

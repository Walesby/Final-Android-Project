package davidwalesby.mohawk.mohawkcoursebrowser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE = "CREATE TABLE courseTable ( _id INTEGER PRIMARY KEY, program INTEGER, " +
            "semesterNum INTEGER, courseCode TEXT, courseTitle TEXT, courseDescription TEXT, courseOwner TEXT, optional INTEGER, hours INTEGER)";

    private static final String DATABASE_NAME = "MyDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

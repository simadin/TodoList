package il.ac.huji.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context) {
        super(context, "todo_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE todo ( " +
                        " _id integer primary key autoincrement, " +
                        " title text, " +
                        " due long );"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(db);
    }

    public boolean insertItem(String title, long due){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues item = new ContentValues();

        item.put("title", title);
        item.put("due", due);

        db.insert("todo", null, item);
        return true;
    }

    public Cursor getAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("todo", new String[] {"_id", "title", "due"}, null, null, null, null, "_id");
    }

    public Integer deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("todo", "_id = ? ", new String[] { Integer.toString(id) });
    }

}
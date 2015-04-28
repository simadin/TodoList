package il.ac.huji.todolist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TodoListManagerActivity extends ActionBarActivity {
    final int ADD_ITEM_CODE = 1;
    ListView todoView;
    ResourceCursorAdapter todoAdapter;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        final DateFormat format = DateFormat.getDateInstance();

        helper = new DBHelper(this);

        Cursor cursor = helper.getAll();
        todoAdapter = new ResourceCursorAdapter(this, R.layout.row, cursor, 1) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String titleText = cursor.getString(cursor.getColumnIndex("title"));
                long dateValue = cursor.getLong(cursor.getColumnIndex("due"));
                TextView title = (TextView) view.findViewById(R.id.txtTodoTitle);
                TextView date = (TextView) view.findViewById(R.id.txtTodoDueDate);
                if (title != null && titleText != null) {
                    title.setText(titleText);
                }
                Date item_date = new Date(dateValue);
                if (date != null && dateValue != 0) {
                    date.setText(format.format(new Date(dateValue)));
                }
                Date cur_date = new Date();
                Calendar cur_calendar = Calendar.getInstance();
                cur_calendar.setTime(cur_date);
                int cur_day = cur_calendar.get(Calendar.DAY_OF_YEAR);
                int cur_year = cur_calendar.get(Calendar.YEAR);
                Calendar item_calendar = Calendar.getInstance();
                item_calendar.setTime(item_date);
                int item_day = item_calendar.get(Calendar.DAY_OF_YEAR);
                int item_year = item_calendar.get(Calendar.YEAR);
                int color = item_year < cur_year || (item_year == cur_year && item_day < cur_day) ? Color.RED : Color.BLACK;
                if (title != null) {
                    title.setTextColor(color);
                }
                if (date != null) {
                    date.setTextColor(color);
                }
            }
        };

        todoView = (ListView)findViewById(R.id.lstTodoItems);
        todoView.setAdapter(todoAdapter);
        registerForContextMenu(todoView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menuItemAdd) {
            Intent newItemIntent = new Intent(this, AddNewTodoItemActivity.class);
            startActivityForResult(newItemIntent, ADD_ITEM_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lstTodoItems) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            getMenuInflater().inflate(R.menu.context_menu_todo_list_manager, menu);
            TextView t = (TextView) todoView.getChildAt(info.position).findViewById(R.id.txtTodoTitle);
            String title = t.getText().toString();
            menu.setHeaderTitle(title);
            if (title.startsWith("Call ")) {
                menu.findItem(R.id.menuItemCall).setVisible(true).setTitle(title);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menuItemDelete:
                Cursor cursor = (Cursor) todoAdapter.getItem(info.position);
                helper.deleteItem(cursor.getInt(cursor.getColumnIndex("_id")));
                todoAdapter.changeCursor(helper.getAll());
                return true;
            case R.id.menuItemCall:
                String phone_num = item.getTitle().toString().substring(5);
                Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_num));
                startActivity(dial);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_ITEM_CODE:
                    helper.insertItem(data.getStringExtra("title"), data.getLongExtra("dueDate", 0));
                    todoAdapter.changeCursor(helper.getAll());
            }
        }
    }
}

package il.ac.huji.todolist;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class ListRow {
    public String title;
    public long date = 0;
}

public class TodoListManagerActivity extends ActionBarActivity {
    final int ADD_ITEM_CODE = 1;
    ArrayList<ListRow> myList = new ArrayList<>();
    ListView todosView;
    ArrayAdapter<ListRow> todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        todosView = (ListView)findViewById(R.id.lstTodoItems);
        final DateFormat format = DateFormat.getDateInstance();
        todoAdapter = new ArrayAdapter<ListRow>(
                this, R.layout.row, myList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View view = inflater.inflate(R.layout.row, null);
                ListRow item = getItem(position);
                if (item != null) {
                    TextView title = (TextView) view.findViewById(R.id.txtTodoTitle);
                    TextView date = (TextView) view.findViewById(R.id.txtTodoDueDate);
                    Date item_date = new Date(item.date);
                    if (title != null && item.title != null) {
                        title.setText(item.title);
                    }
                    if (date != null && item.date != 0) {
                        date.setText(format.format(new Date(item.date)));
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
                    title.setTextColor(color);
                    date.setTextColor(color);
                }
                return view;
            }
        };
        todosView.setAdapter(todoAdapter);
        registerForContextMenu(todosView);
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
            String title = myList.get(info.position).title;
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
                myList.remove(info.position);
                todoAdapter.notifyDataSetChanged();
                return true;
            case R.id.menuItemCall:
                String phone_num = myList.get(info.position).title.substring(5);
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
                    ListRow item = new ListRow();
                    item.title = data.getStringExtra("title");
                    item.date = data.getLongExtra("dueDate", 0);
                    myList.add(item);
                    todoAdapter.notifyDataSetChanged();
            }
        }
    }
}

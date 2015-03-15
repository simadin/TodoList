package il.ac.huji.todolist;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class TodoListManagerActivity extends ActionBarActivity {
    ArrayList<String> myList;
    EditText newItem;
    ListView todosView;
    ArrayAdapter<String> todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        myList = new ArrayList<>();
        newItem = (EditText)findViewById(R.id.edtNewItem);
        todosView = (ListView)findViewById(R.id.lstTodoItems);
        todoAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, myList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView myView = (TextView) super.getView(position, convertView, parent);
                myView.setTextColor(position % 2 == 0 ? Color.RED : Color.BLUE);
                return myView;
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
            myList.add(newItem.getText().toString());
            newItem.setText("");
            todoAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lstTodoItems) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(myList.get(info.position));
            menu.add(getResources().getString(R.string.delete_string));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // We have only one option in the menu. but we want to check it anyway
        if (item.getItemId() == 0) {
            myList.remove(info.position);
            todoAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }
}

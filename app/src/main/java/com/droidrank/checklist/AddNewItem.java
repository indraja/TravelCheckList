package com.droidrank.checklist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewItem extends AppCompatActivity {

    // To take the user input for the new item
    EditText itemName;
    // To add the new item to the list
    Button addItem;
    // To cancel this task
    Button cancel;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        itemName = (EditText) findViewById(R.id.et_item_name);
        cancel = (Button) findViewById(R.id.bt_cancel);
        addItem = (Button) findViewById(R.id.bt_ok);

        DBhelp dbhelp = new DBhelp(this);
        db = dbhelp.getWritableDatabase();

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *Save the new item, if it does not exist in the list
                 *else display appropriate error message
                 */
                if (!itemName.getText().toString().equals("")) {
                            ContentValues insertValues = new ContentValues();
                            insertValues.put(ChecklistEntry.COLUMN_NAME, itemName.getText().toString());
                            insertValues.put(ChecklistEntry.COLUMN_NAME_ISDONE, "0");
                            db.insert(ChecklistEntry.TABLE_NAME, null, insertValues);
                        }

                // finish the current activity
                Intent intent = getIntent();

                setResult(RESULT_OK, intent);
                finish();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Do nothing and close this activity
                 */
                finish();
            }
        });
    }
}

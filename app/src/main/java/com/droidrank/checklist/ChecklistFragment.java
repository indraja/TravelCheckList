package com.droidrank.checklist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChecklistFragment extends Fragment {

    private CheckListAdapter adapter;
    private SQLiteDatabase db;
    private Activity activity;
    private static final int ADD_ITEM = 1;
    private ArrayList<ChecklistItem> items = new ArrayList<>();

    private ListView lv;
    private ArrayList<ChecklistItem> mCheckedListItems = new ArrayList<>();
    private ArrayList<ChecklistItem> mUnCheckedListItems = new ArrayList<>();

    public ChecklistFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_check_list, container, false);

        DBhelp dbhelp = new DBhelp(getContext());
        db = dbhelp.getWritableDatabase();

        lv = (ListView) view.findViewById(R.id.lv);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //First time users
        String isAlreadyAdded = sharedPreferences.getString(Constants.ID_ADDED_INDB, "null");
        if (isAlreadyAdded.equals("null")) {

            for (int i = 0; i < Constants.baseTask.size(); i++) {
                ContentValues insertValues = new ContentValues();
                insertValues.put(ChecklistEntry.COLUMN_NAME, Constants.baseTask.get(i));
                insertValues.put(ChecklistEntry.COLUMN_NAME_ISDONE, "0");
                db.insert(ChecklistEntry.TABLE_NAME, null, insertValues);
            }

            editor.putString(Constants.ID_ADDED_INDB, "yes");
            editor.apply();
        }

        adapter = new CheckListAdapter(activity, items);
        lv.setAdapter(adapter);

        refresh();

        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddButtonClick();
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        refresh();
    }

    void onAddButtonClick() {
        Intent intent = new Intent(getActivity(), AddNewItem.class);
        startActivityForResult(intent, ADD_ITEM);
    }

    private void refresh() {
        items.clear();
        adapter.notifyDataSetChanged();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ChecklistEntry.TABLE_NAME + " ORDER BY " +
                ChecklistEntry.COLUMN_NAME_ISDONE, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ChecklistEntry.COLUMN_NAME_ID));
                String task = cursor.getString(cursor.getColumnIndex(ChecklistEntry.COLUMN_NAME));
                String isdone = cursor.getString(cursor.getColumnIndex(ChecklistEntry.COLUMN_NAME_ISDONE));
                items.add(new ChecklistItem(id, task, isdone));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        mCheckedListItems.clear();
        mUnCheckedListItems.clear();
        for (ChecklistItem checklistItem : items) {
            if (checklistItem.getIsDone().equals("1")) {
                mCheckedListItems.add(checklistItem);
            } else {
                mUnCheckedListItems.add(checklistItem);
            }
        }

        sortListInCaseSensitive(mCheckedListItems);
        sortListInCaseSensitive(mUnCheckedListItems);

        items.clear();
        items.addAll(mUnCheckedListItems);
        items.addAll(mCheckedListItems);
        adapter.notifyDataSetChanged();
    }

    void sortListInCaseSensitive(List<ChecklistItem> items) {
        Collections.sort(items, new Comparator<ChecklistItem>() {
            @Override
            public int compare(ChecklistItem item1, ChecklistItem item2) {
                return item1.getName().compareToIgnoreCase(item2.getName());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    class CheckListAdapter extends ArrayAdapter<ChecklistItem> {

        private final Activity context;
        private final List<ChecklistItem> items;
        DBhelp dbhelp;
        SQLiteDatabase db;

        CheckListAdapter(Activity context, List<ChecklistItem> items) {
            super(context, R.layout.checklist_item, items);
            this.context = context;
            this.items = items;
        }

        class ViewHolder {
            CheckBox c;
            Button btnDel;
        }

        @NonNull
        @Override
        public View getView(final int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();

            View vi = view;             //trying to reuse a recycled view
            ViewHolder holder;
            if (vi == null) {
                vi = inflater.inflate(R.layout.checklist_item, parent, false);
                holder = new ViewHolder();
                holder.c = (CheckBox) vi.findViewById(R.id.cb1);
                holder.btnDel = (Button) vi.findViewById(R.id.btn_del);
                vi.setTag(holder);

            } else {
                holder = (ViewHolder) vi.getTag();
            }

            dbhelp = new DBhelp(context);
            db = dbhelp.getWritableDatabase();

            if (items.get(position).getIsDone().equals("1")) {
//                holder.c.setPaintFlags(holder.c.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.c.setChecked(true);
            } else {
//                holder.c.setPaintFlags(holder.c.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.c.setChecked(false);
            }
            holder.c.setText(items.get(position).getName());

            holder.c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    CheckBox c2 = (CheckBox) view1;
                    if (c2.isChecked()) {
                        String query = "UPDATE " + ChecklistEntry.TABLE_NAME +
                                " SET " + ChecklistEntry.COLUMN_NAME_ISDONE + " = 1 WHERE " +
                                ChecklistEntry.COLUMN_NAME_ID + " IS " + items.get(position).getId();

                        db.execSQL(query);
                        Log.e("EXECUTED : ", query);
                    } else {
                        String query = "UPDATE " + ChecklistEntry.TABLE_NAME +
                                " SET " + ChecklistEntry.COLUMN_NAME_ISDONE + " = 0 WHERE " +
                                ChecklistEntry.COLUMN_NAME_ID + " IS " + items.get(position).getId();
                        db.execSQL(query);
                        Log.e("EXECUTED : ", query);
                    }
                    refresh();
                }
            });

            // delete element
            holder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues insertValues = new ContentValues();
                    db.delete(ChecklistEntry.TABLE_NAME,
                            ChecklistEntry.COLUMN_NAME_ID + "=" + items.get(position).getId(), null);
                    refresh();
                }
            });
            return vi;
        }
    }
}
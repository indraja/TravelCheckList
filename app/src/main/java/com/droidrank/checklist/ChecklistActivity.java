package com.droidrank.checklist;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.droidrank.checklist.ChecklistFragment;

import java.util.Objects;

public class ChecklistActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = new ChecklistFragment();
        fragmentManager.beginTransaction().replace(R.id.inc, fragment).commit();

        setTitle("TravelMate Checklist");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            ChecklistFragment checklistFragment = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.inc);
            if (checklistFragment != null) {
                checklistFragment.onActivityResult(requestCode, resultCode, intent);
            }
        }
    }

}

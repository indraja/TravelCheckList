package com.droidrank.checklist;

import android.provider.BaseColumns;

/**
 * Constants for database column names
 */
public abstract class ChecklistEntry implements BaseColumns {
    public static final String TABLE_NAME = "events_new";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NAME_ISDONE = "isdone";
}


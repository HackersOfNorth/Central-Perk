package com.hackinthenorth.centralperk.entity;

/**
 * Created by Deepankar on 12-01-2016.
 */
public class DBConstants {
    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "centralperk";

    // Table names
    public static final String TABLE_USER = "user";

    // Users Table Columns names
    public static final String KEY_UUID = "uuid";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phoneno";
    public static final String KEY_EMAIL = "email";

    // Queries for creation of user table
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE " + TABLE_USER + "("
                    + KEY_UUID + " TEXT PRIMARY KEY NOT NULL,"
                    + KEY_NAME + " TEXT NOT NULL,"
                    + KEY_EMAIL + " TEXT NOT NULL,"
                    + KEY_PHONE + " INTEGER NOT NULL "
                    + ")";

}

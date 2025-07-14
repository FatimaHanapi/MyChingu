package com.example.mychingu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Added for logging database operations
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "MyChingu.db";
    // IMPORTANT: Increment this version if you change table schema (e.g., adding columns)
    // You must uninstall the app from the device/emulator for onUpgrade to be called.
    private static final int DATABASE_VERSION = 2; // CHANGED TO 2 (or higher if you changed it before)

    // Friends Table
    private static final String TABLE_FRIENDS = "friends";
    private static final String COLUMN_FRIEND_ID = "_id"; // Primary key for friends table
    private static final String COLUMN_USER_ID_FK = "user_id"; // Foreign key to users table
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_NAME = "friend_name";
    private static final String COLUMN_DOB = "date_of_birth";
    private static final String COLUMN_PHONE = "phone_number";
    private static final String COLUMN_EMAIL = "email";

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_PRIMARY_ID = "_id"; // Primary key for users table (e.g., 1, 2, 3...)
    private static final String COLUMN_STUDENT_ID = "student_id"; // Unique ID for login (e.g., A001, B002)
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_PASSWORD = "password";

    DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STUDENT_ID + " TEXT UNIQUE, " // Student ID must be unique
                + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_EMAIL + " TEXT, "
                + COLUMN_PASSWORD + " TEXT);"; // Store password (consider hashing in a real app)
        db.execSQL(CREATE_USERS_TABLE);
        Log.d("DatabaseHelper", "Users table created.");

        // Create Friends Table (with foreign key to Users table)
        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + " ("
                + COLUMN_FRIEND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID_FK + " INTEGER, " // This is the new column
                + COLUMN_GENDER + " TEXT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_DOB + " TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_PRIMARY_ID + ") ON DELETE CASCADE);"; // Added ON DELETE CASCADE
        db.execSQL(CREATE_FRIENDS_TABLE);
        Log.d("DatabaseHelper", "Friends table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop both tables and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        Log.d("DatabaseHelper", "Database upgraded. Tables dropped and recreated.");
    }

    //region User Management Methods

    /**
     * Adds a new user to the database.
     * @param studentId The unique student ID for the user.
     * @param name The user's full name.
     * @param email The user's email address.
     * @param password The user's password.
     * @return true if registration is successful, false otherwise.
     */
    public boolean addUser(String studentId, String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_STUDENT_ID, studentId);
        cv.put(COLUMN_USER_NAME, name);
        cv.put(COLUMN_USER_EMAIL, email);
        cv.put(COLUMN_PASSWORD, password); // In a real app, hash this password!

        long result = db.insert(TABLE_USERS, null, cv);
        db.close();
        return result != -1; // returns true if insertion was successful
    }

    /**
     * Checks if a student ID already exists in the database.
     * @param studentId The student ID to check.
     * @return true if student ID exists, false otherwise.
     */
    public boolean checkStudentIdExists(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT " + COLUMN_STUDENT_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_STUDENT_ID + " = ?";
        try {
            cursor = db.rawQuery(query, new String[]{studentId});
            boolean exists = (cursor != null && cursor.getCount() > 0);
            return exists;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close(); // Close DB connection
        }
    }

    /**
     * Authenticates a user based on student ID and password.
     * @param studentId The student ID for login.
     * @param password The password for login.
     * @return The internal user_id (primary key) if credentials are valid, or -1 otherwise.
     */
    public int checkUser(String studentId, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int userId = -1; // Default to -1 (not found)

        try {
            String[] columns = {COLUMN_USER_PRIMARY_ID}; // Corrected to COLUMN_USER_PRIMARY_ID
            String selection = COLUMN_STUDENT_ID + " = ? AND " + COLUMN_PASSWORD + " = ?";
            String[] selectionArgs = {studentId, password};

            cursor = db.query(
                    TABLE_USERS,       // Table to query
                    columns,           // The columns to return
                    selection,         // The columns for the WHERE clause
                    selectionArgs,     // The values for the WHERE clause
                    null,              // don't group the rows
                    null,              // don't filter by row groups
                    null               // The sort order
            );

            if (cursor != null && cursor.moveToFirst()) {
                // User found, get their ID
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_PRIMARY_ID)); // Corrected column name
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking user: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close(); // Close DB connection
        }
        return userId;
    }

    /**
     * Retrieves the internal user_id (primary key) for a given student ID.
     * This method is kept separate for cases where you only need the ID, not full authentication.
     * @param studentId The student ID of the user.
     * @return The internal user_id, or -1 if not found.
     */
    public int getUserId(String studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int userId = -1;
        String query = "SELECT " + COLUMN_USER_PRIMARY_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_STUDENT_ID + " = ?";
        try {
            cursor = db.rawQuery(query, new String[]{studentId});
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_PRIMARY_ID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close(); // Close DB connection
        }
        return userId;
    }

    //endregion User Management Methods

    //region Friend Management Methods (Modified to be user-aware)

    /**
     * Adds a new friend associated with a specific user.
     * @param userId The internal ID of the user adding the friend.
     * @param gender Friend's gender.
     * @param name Friend's name.
     * @param dob Friend's date of birth.
     * @param phone Friend's phone number.
     * @param email Friend's email.
     */
    public void addFriend(int userId, String gender, String name, String dob, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USER_ID_FK, userId); // Store the user ID
        cv.put(COLUMN_GENDER, gender);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DOB, dob);
        cv.put(COLUMN_PHONE, phone);
        cv.put(COLUMN_EMAIL, email);

        long result = db.insert(TABLE_FRIENDS, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to add friend", Toast.LENGTH_SHORT).show();
            Log.e("DatabaseHelper", "Failed to add friend for user ID: " + userId);
        } else {
            Toast.makeText(context, "Friend added successfully!", Toast.LENGTH_SHORT).show();
            Log.d("DatabaseHelper", "Friend added for user ID: " + userId);
        }
        db.close();
    }

    /**
     * Reads all friends data for a specific user.
     * @param userId The internal ID of the user whose friends to retrieve.
     * @return A Cursor containing the friends' data.
     */
    public Cursor readAllData(int userId) {
        String query = "SELECT * FROM " + TABLE_FRIENDS + " WHERE " + COLUMN_USER_ID_FK + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        }
        // No db.close() here as cursor needs to be read outside this method
        return cursor;
    }

    /**
     * Updates a friend's information.
     * (No change needed here for user_id, as friend_id is unique and retrieved for current user)
     */
    public void updateFriend(String row_id, String gender, String name, String dob, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_GENDER, gender);
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DOB, dob);
        cv.put(COLUMN_PHONE, phone);
        cv.put(COLUMN_EMAIL, email);

        long result = db.update(TABLE_FRIENDS, cv, COLUMN_FRIEND_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to update friend", Toast.LENGTH_SHORT).show();
            Log.e("DatabaseHelper", "Failed to update friend with ID: " + row_id);
        } else {
            Toast.makeText(context, "Friend updated successfully!", Toast.LENGTH_SHORT).show();
            Log.d("DatabaseHelper", "Friend updated with ID: " + row_id);
        }
        db.close();
    }

    /**
     * Deletes one friend by their unique ID.
     * (No change needed here for user_id, as friend_id is unique and retrieved for current user)
     */
    public void deleteOneFriend(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_FRIENDS, COLUMN_FRIEND_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to delete friend", Toast.LENGTH_SHORT).show();
            Log.e("DatabaseHelper", "Failed to delete friend with ID: " + row_id);
        } else {
            Toast.makeText(context, "Friend deleted successfully!", Toast.LENGTH_SHORT).show();
            Log.d("DatabaseHelper", "Friend deleted with ID: " + row_id);
        }
        db.close();
    }

    /**
     * Deletes all friends for a specific user.
     * This replaces your previous deleteAllData() which deleted all friends from the entire database.
     * @param userId The internal ID of the user whose friends to delete.
     */
    public void deleteAllFriendsForUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_FRIENDS, COLUMN_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)});
        if (result == -1) {
            Toast.makeText(context, "Failed to delete all friends for user.", Toast.LENGTH_SHORT).show();
            Log.e("DatabaseHelper", "Failed to delete all friends for user ID: " + userId);
        } else if (result == 0) {
            Toast.makeText(context, "No friends to delete for this user.", Toast.LENGTH_SHORT).show();
            Log.d("DatabaseHelper", "No friends to delete for user ID: " + userId);
        } else {
            Toast.makeText(context, "All friends for user deleted successfully!", Toast.LENGTH_SHORT).show();
            Log.d("DatabaseHelper", "All friends deleted for user ID: " + userId + ". Count: " + result);
        }
        db.close();
    }


    /**
     * Searches for friends by name for a specific user.
     * @param userId The internal ID of the user whose friends to search.
     * @param query The search query (friend's name or part of it).
     * @return A Cursor containing the friends' data matching the query.
     */
    public Cursor searchFriends(int userId, String query) {
        String selection = COLUMN_USER_ID_FK + " = ? AND " + COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {String.valueOf(userId), "%" + query + "%"}; // % for wildcard search
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.query(
                    TABLE_FRIENDS,
                    null, // All columns
                    selection,
                    selectionArgs,
                    null, null, null // groupBy, having, orderBy
            );
        }
        // No db.close() here as cursor needs to be read outside this method
        return cursor;
    }

    // Your old deleteAllData method, keeping it just in case for a full clear, but recommend using deleteAllFriendsForUser
    // for user-specific operations.
    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FRIENDS);
        db.close();
        Log.w("DatabaseHelper", "WARNING: All friends data deleted from database (not user specific).");
    }

    //endregion Friend Management Methods

    public Map<String, Integer> getGenderCounts(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT gender, COUNT(*) FROM friends WHERE user_id = ? GROUP BY gender", new String[]{String.valueOf(userId)});
        Map<String, Integer> result = new HashMap<>();
        while (cursor.moveToNext()) {
            String gender = cursor.getString(0);
            int count = cursor.getInt(1);
            result.put(gender, count);
        }
        cursor.close();
        return result;
    }

    public int[] getBirthdayMonthCounts(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT strftime('%m', date_of_birth) AS month, COUNT(*) FROM friends WHERE user_id = ? GROUP BY month", new String[]{String.valueOf(userId)});
        int[] monthCounts = new int[12];
        while (cursor.moveToNext()) {
            int month = Integer.parseInt(cursor.getString(0)) - 1;
            int count = cursor.getInt(1);
            monthCounts[month] = count;
        }
        cursor.close();
        return monthCounts;
    }
}
package edu.fatec.profg.contactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ContactDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "contact.db";
    public static final int DATABASE_VERSION = 1;

    // Definição da tabela CONTACT
    public static final String CONTACT_TB = "contact";
    public static final String CONTACT_CODE = "code";
    public static final String CONTACT_NAME = "name";
    public static final String CONTACT_INITIALS = "initials_name";
    public static final String CONTACT_PHONE = "phone";
    public static final String CONTACT_NICKNAME = "nickname";
    public static final String CONTACT_IMAGE = "image";

    private String dbBuildScript;

    private SQLiteDatabase wDb;
    private SQLiteDatabase rDb;

    public ContactDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(CONTACT_TB); sb.append(" (");
        sb.append(CONTACT_CODE); sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(CONTACT_NAME); sb.append(" TEXT NOT NULL, ");
        sb.append(CONTACT_INITIALS); sb.append(" TEXT NOT NULL, ");
        sb.append(CONTACT_PHONE); sb.append(" TEXT NOT NULL, ");
        sb.append(CONTACT_NICKNAME); sb.append(" TEXT NOT NULL, ");
        sb.append(CONTACT_IMAGE); sb.append(" BLOB");
        sb.append(")");

        dbBuildScript = sb.toString();

        wDb = this.getWritableDatabase();
        rDb = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(dbBuildScript);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // "re-criar" o banco
        db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TB);
        onCreate(db);
    }

    // Métodos CRUD
    public boolean addContact(Contact ct) {
        ContentValues cv = new ContentValues();
        cv.put(CONTACT_NAME, ct.getName());
        cv.put(CONTACT_INITIALS, ct.getInitials_name());
        cv.put(CONTACT_PHONE, ct.getPhone());
        cv.put(CONTACT_NICKNAME, ct.getNickname());
        cv.put(CONTACT_IMAGE, ct.getImage());

        long row = wDb.insert(CONTACT_TB, null, cv);

        if(row != -1) {
            ct.setCode((int)row);
            return true;
        } else {
            return false;
        }
    }

    public Contact getContact(int code) {
        String strCode = String.valueOf(code);
        Cursor c = rDb.query(CONTACT_TB,
                new String[] {
                        CONTACT_CODE,
                        CONTACT_NAME,
                        CONTACT_INITIALS,
                        CONTACT_PHONE,
                        CONTACT_NICKNAME,
                        CONTACT_IMAGE
                },
                CONTACT_CODE + "=?", // WHERE "code=?" <- parâmetro
                new String[] { strCode },
                null,
                null,
                null
        );

        Contact ct = null;
        if(c.moveToFirst()) {
            ct = new Contact(
                    c.getInt(c.getColumnIndex(CONTACT_CODE)),
                    c.getString(c.getColumnIndex(CONTACT_NAME)),
                    c.getString(c.getColumnIndex(CONTACT_INITIALS)),
                    c.getString(c.getColumnIndex(CONTACT_PHONE)),
                    c.getString(c.getColumnIndex(CONTACT_NICKNAME)),
                    c.getBlob(c.getColumnIndex(CONTACT_IMAGE))
            );
        }

        c.close();

        return ct;
    }

    public List<Contact> getAllContacts() {
        String query = "SELECT * FROM " + CONTACT_TB;
        Cursor c = rDb.rawQuery(query, null);

        List<Contact> contactList = new ArrayList<>();
        Contact ct = null;
        while (c.moveToNext()) {
            ct = new Contact(
                    c.getInt(c.getColumnIndex(CONTACT_CODE)),
                    c.getString(c.getColumnIndex(CONTACT_NAME)),
                    c.getString(c.getColumnIndex(CONTACT_INITIALS)),
                    c.getString(c.getColumnIndex(CONTACT_PHONE)),
                    c.getString(c.getColumnIndex(CONTACT_NICKNAME)),
                    c.getBlob(c.getColumnIndex(CONTACT_IMAGE))
            );
            contactList.add(ct);
        }

        c.close();

        return contactList;
    }

    public int deleteContact(int code) {
        String strCode = String.valueOf(code);
        int dRows = wDb.delete(
                CONTACT_TB,
                CONTACT_CODE + " = ?",
                new String[]{strCode}
        );

        return dRows;
    }

    public int updateContact(Contact ct) {
        ContentValues cv = new ContentValues();
        cv.put(CONTACT_CODE, ct.getCode());
        cv.put(CONTACT_NAME, ct.getName());
        cv.put(CONTACT_INITIALS, ct.getName());
        cv.put(CONTACT_PHONE, ct.getPhone());
        cv.put(CONTACT_NICKNAME, ct.getNickname());
        cv.put(CONTACT_IMAGE, ct.getImage());

        String code = String.valueOf(ct.getCode());

        int uRows = wDb.update(
                CONTACT_TB,
                cv,
                CONTACT_CODE + "=?",
                new String[] {code}
        );

        return uRows;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(wDb != null) {
            wDb.close();
        }

        if(rDb != null) {
            rDb.close();
        }
    }
}

package edu.fatec.profg.contactlist;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String createDisplayContactName(Contact ct) {
        return ct.getName() + " - " + ct.getNickname();
    }

    public static final int UPDATED_LIST_REQUEST = 1;
}

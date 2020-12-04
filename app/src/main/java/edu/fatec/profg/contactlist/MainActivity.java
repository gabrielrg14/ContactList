package edu.fatec.profg.contactlist;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button fab;

    // Atributos para controle da lista de contatos
    private List<Contact> contactList;
    private ListView lvContacts;
    private ArrayList<String> formattedContactsNameList;
    private ArrayAdapter lvAdapter;

    // Atributos de banco de dados
    private ContactDatabaseHelper ctDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);

        lvContacts = findViewById(R.id.ct_list);
        ctDBHelper = new ContactDatabaseHelper(this);

        updateContactList();

        // Define onClick nos itens da lista
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int code = contactList.get(position).getCode();
                Intent intent = new Intent(view.getContext(), ContactDetailsActivity.class);
                intent.putExtra("CONTACT_ID", code);
                startActivityForResult(intent, UPDATED_LIST_REQUEST);
            }
        });

        // Define tratadores para os botões
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fab:
                // FAB
                goToNewContactPage();
                break;
        }
    }

    private void goToNewContactPage() {
        Intent intent = new Intent(this, ContactDetailsActivity.class);
        /*intent.putStringArrayListExtra("CONTACT_LIST", formattedContactsNameList);*/
        startActivityForResult(intent, UPDATED_LIST_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATED_LIST_REQUEST) {
            if (resultCode == RESULT_OK) {
                updateContactList();
            }
        }
    }

    public void updateContactList() {
        contactList = ctDBHelper.getAllContacts();

        lvAdapter = new ContactAdapter(this, contactList);
        lvContacts.setAdapter(lvAdapter);

        // Carrega o ListView
        /*formattedContactsNameList = new ArrayList<>();
        for(Contact ct : contactList) {
            formattedContactsNameList.add(createDisplayContactName(ct));
        }
        lvAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                formattedContactsNameList);

        lvContacts.setAdapter(lvAdapter);*/

        lvAdapter.notifyDataSetChanged();
    }
}
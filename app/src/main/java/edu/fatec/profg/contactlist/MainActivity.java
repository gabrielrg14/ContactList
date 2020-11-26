package edu.fatec.profg.contactlist;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Atributos de layout
    private EditText edTxtCtName;
    private EditText edTxtCtPhone;
    private EditText edTxtCtNickname;
    private Button btnSave;

    // Atributos para controle da lista de contatos
    private List<Contact> contactList;
    private ListView lvContacts;
    private List<String> formattedContactsNameList;
    private ArrayAdapter<String> lvAdapter;

    // Atributos de banco de dados
    private ContactDatabaseHelper ctDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first); // foi trocado de main_activity para testar saveContact()
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        // Snackbar foi comentado pois estava gerando erros ao ter trocado setContentView de main_activity
        /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */

        // Obter referência dos objetos da GUI
        edTxtCtName = findViewById(R.id.ct_name);
        edTxtCtPhone = findViewById(R.id.ct_phone);
        edTxtCtNickname = findViewById(R.id.ct_nickname);

        btnSave = findViewById(R.id.btn_save);

        lvContacts = findViewById(R.id.ct_list);
        ctDBHelper = new ContactDatabaseHelper(this);
        contactList = ctDBHelper.getAllContacts();

        // Carrega o ListView
        formattedContactsNameList = new ArrayList<>();
        for(Contact ct : contactList) {
            formattedContactsNameList.add(createDisplayContactName(ct));
        }
        lvAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                formattedContactsNameList);

        lvContacts.setAdapter(lvAdapter);

        // Define tratadores para os botões
        btnSave.setOnClickListener(this);
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

    private String createDisplayContactName(Contact ct) {
        return ct.getName() + " - " + ct.getNickname();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_save:
                // Botão Salvar
                saveContact();
                break;
        }
    }

    private void saveContact() {
        Contact ct = new Contact(
                edTxtCtName.getText().toString(),
                edTxtCtPhone.getText().toString(),
                edTxtCtNickname.getText().toString(),
                null // TODO
        );

        if(ctDBHelper.addContact(ct)) {
            contactList.add(ct);
            formattedContactsNameList.add(createDisplayContactName(ct));

            // Limpar Campos
            edTxtCtName.setText("");
            edTxtCtPhone.setText("");
            edTxtCtNickname.setText("");
        } else {
            Toast.makeText(this, "Ocorreu um erro durante a inserção do contato.", Toast.LENGTH_LONG).show();
        }
        lvAdapter.notifyDataSetChanged();

        /* Log do contato cadastrado para visualizar - teste
        List<Contact> contatos = ctDBHelper.getAllContacts();
        int tam = contatos.size();
        Contact cadastrado = contatos.get(tam-1);
        Log.i("CONTACT_DB", "Contato cadastrado: " + cadastrado.getCode() + " " + cadastrado.getName() + " " + cadastrado.getPhone() + " " + cadastrado.getNickname());
         */
    }

    private void deleteContact(Contact ct) {
        if(ctDBHelper.deleteContact(ct) == -1) {
            formattedContactsNameList.remove(createDisplayContactName(ct));
            contactList.remove(ct);
        } else {
            Toast.makeText(this, "Erro na remoção do contato", Toast.LENGTH_LONG).show();
        }
        lvAdapter.notifyDataSetChanged();
    }
}
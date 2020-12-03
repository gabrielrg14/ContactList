package edu.fatec.profg.contactlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ContactDetailsActivity extends BaseActivity implements View.OnClickListener {

    // Atributos de layout
    private EditText edTxtCtName;
    private EditText edTxtCtPhone;
    private EditText edTxtCtNickname;
    private Button btnSave;
    public Button btnDelete;
    private Button btnCancel;
    private ArrayList<String> contactList;

    public int contactId;
    public char[] initialsName;

    // Atributos de banco de dados
    private ContactDatabaseHelper ctDBHelper;
    private Contact contact;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        contactId = getIntent().getIntExtra("CONTACT_ID", -1);

        contactList = getIntent().getStringArrayListExtra("CONTACT_LIST");

        // Obter referência dos objetos da GUI
        edTxtCtName = findViewById(R.id.field_name);
        edTxtCtPhone = findViewById(R.id.field_phone);
        edTxtCtNickname = findViewById(R.id.field_nickname);

        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnCancel = findViewById(R.id.btn_cancel);

        ctDBHelper = new ContactDatabaseHelper(this);

        if (contactId == -1) {
            btnDelete.setVisibility(View.GONE);
        } else {
            getContactDetails();
        }

        // Define tratadores para os botões
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_save:
                // Botão Salvar
                saveContact();
                break;
            case R.id.btn_cancel:
                // Botão Cancelar
                finish();
                break;
            case R.id.btn_delete:
                // Botão Cancelar
                deleteContact();
                break;
        }
    }

    private void getContactDetails() {
        contact = ctDBHelper.getContact(contactId);

        if (contact != null) {
            edTxtCtName.setText(contact.getName());
            edTxtCtPhone.setText(contact.getPhone());
            edTxtCtNickname.setText(contact.getNickname());
        }
    }

    private void saveContact() {
        Contact ct = new Contact(
                edTxtCtName.getText().toString(),
                edTxtCtPhone.getText().toString(),
                edTxtCtNickname.getText().toString(),
                null // TODO
        );

        // Caso a imagem não seja cadastrada, gera as iniciais do nome do contato
        if(ct.getImage() == null) {
            String contactName = ct.getName();
            String[] separatedName = contactName.split(" ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < separatedName.length; i++) {
                sb.append(separatedName[i].toUpperCase().charAt(0));
            }
            Log.i("INITIALS", "" + sb.toString());
        }

        // caso não esteja vendo um contato ja criado, faz criacao
        if (contactId == -1) {

            if(ctDBHelper.addContact(ct)) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            } else {
                Toast.makeText(this, "Ocorreu um erro durante a inserção do contato.", Toast.LENGTH_LONG).show();
            }

        } else {
            // caso contrario atualiza contato ja criado
            Contact updatedCt = new Contact(
                    contactId,
                    edTxtCtName.getText().toString(),
                    edTxtCtPhone.getText().toString(),
                    edTxtCtNickname.getText().toString(),
                    null // TODO
            );

            if(ctDBHelper.updateContact(updatedCt) == 1) {
                Toast.makeText(this, "Contato atualizado com sucesso!", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            } else {
                Toast.makeText(this, "Ocorreu um erro durante a atualização do contato.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void deleteContact() {
        if(ctDBHelper.deleteContact(contactId) == 1) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK,returnIntent);
            finish();
        } else {
            Toast.makeText(this, "Erro na remoção do contato", Toast.LENGTH_LONG).show();
        }
    }

    private void resetForm() {
        // Limpar Campos
        edTxtCtName.setText("");
        edTxtCtPhone.setText("");
        edTxtCtNickname.setText("");
    }
}
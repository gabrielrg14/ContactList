package edu.fatec.profg.contactlist;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ContactDetailsActivity extends BaseActivity implements View.OnClickListener {

    // Atributos de layout
    private ImageView imgViewCt;
    private TextView initial_name;
    private TextView txtViewNickname;
    private EditText edTxtCtName;
    private EditText edTxtCtPhone;
    private EditText edTxtCtNickname;
    private Button btnSave;
    public Button btnDelete;
    private Button btnCancel;
    private ArrayList<String> contactList;
    private static int RESULT_LOAD_IMAGE = 0;

    public int contactId;

    // Atributos de banco de dados
    private ContactDatabaseHelper ctDBHelper;
    private Contact contact;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        contactId = getIntent().getIntExtra("CONTACT_ID", -1);

        // Obter referência dos objetos da GUI
        imgViewCt = findViewById(R.id.profile_img);
        initial_name = findViewById(R.id.contact_initials);
        txtViewNickname = findViewById(R.id.txtView_nickname);
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
        imgViewCt.setOnClickListener(this);
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
            case R.id.profile_img:
                accessGallery();
        }
    }

    private void getContactDetails() {
        contact = ctDBHelper.getContact(contactId);

        if (contact != null) {

            if(contact.getImage() != null) {
                byte[] byteArray = contact.getImage();
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imgViewCt.setImageBitmap(bmp);
            } else {
                initial_name.setText(contact.getInitials_name());
            }

            txtViewNickname.setText(contact.getNickname());
            edTxtCtName.setText(contact.getName());
            edTxtCtPhone.setText(contact.getPhone());
            edTxtCtNickname.setText(contact.getNickname());
        }
    }

    private void saveContact() {
        byte[] img = null;
        if(imgViewCt.getDrawable() != null) {
            // Converte a imagem do ImageView em byte array
            Bitmap bitmap = ((BitmapDrawable) imgViewCt.getDrawable()).getBitmap();
            if(bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                img = baos.toByteArray();
            }
        }

        // Gera as iniciais do nome do contato para salvar no Banco de dados
        String contactName = edTxtCtName.getText().toString();
        String[] separatedName = contactName.split(" ");
        StringBuilder initials_name = new StringBuilder();
        for (int i = 0; i < separatedName.length; i++) {
            initials_name.append(separatedName[i].toUpperCase().charAt(0));
        }

        Contact ct = new Contact(
                edTxtCtName.getText().toString(),
                initials_name.toString(),
                edTxtCtPhone.getText().toString(),
                edTxtCtNickname.getText().toString(),
                img
        );

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
                    initials_name.toString(),
                    edTxtCtPhone.getText().toString(),
                    edTxtCtNickname.getText().toString(),
                    img
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

    private void accessGallery() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i.createChooser(i, "Select File"), RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = null;
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = resizedBitmap(selectedImage, imgViewCt.getMeasuredHeight());

                imgViewCt.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap resizedBitmap(Bitmap imageBm, int maxSize) {
        int width = maxSize;
        int height = maxSize;

        return Bitmap.createScaledBitmap(imageBm, width, height, true);
    }
}
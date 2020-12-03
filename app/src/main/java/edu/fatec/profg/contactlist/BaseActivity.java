package edu.fatec.profg.contactlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String createDisplayContactName(Contact ct) {
        // Caso uma imagem tenha sido cadastrada
        if(ct.getImage() != null) {
            byte[] byteArray = ct.getImage();
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            return bmp + " - " + ct.getNickname();
            // TODO: mostrar a imagem na lista, atualmente está sendo mostrado o bitmap
        } else {
            // Caso uma imagem não tenha sido cadastrada, mostra-se as iniciais
            return ct.getInitials_name() + " - " + ct.getNickname();
        }
    }

    public static final int UPDATED_LIST_REQUEST = 1;
}

package edu.fatec.profg.contactlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private final Context context;
    private final List<Contact> contacts;

    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, R.layout.list_view_custom, contacts);
        this.context = context;
        this.contacts = contacts;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_view_custom, parent, false);

        ImageView image = (ImageView) rowView.findViewById(R.id.contact_image);
        TextView contact_initials = (TextView) rowView.findViewById(R.id.contact_initials);
        TextView nickname = (TextView) rowView.findViewById(R.id.contact_nickname);

        byte[] byteArray = contacts.get(position).getImage();
        if(byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            image.setImageBitmap(bmp);
        } else {
            contact_initials.setText(contacts.get(position).getInitials_name());
        }

        nickname.setText(contacts.get(position).getNickname());

        return rowView;
    }
}

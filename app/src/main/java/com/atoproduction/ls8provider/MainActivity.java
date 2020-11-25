package com.atoproduction.ls8provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int MY_PERMISSIONS_REQUEST_READ_CONTACT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // Nếu như trước đây bị từ chối xin quyền, thì lần xin quyền này sẽ không xuất hiện dialog xin quyền nũa
                // lúc này mình sẽ phải tự viết 1 cái thông báo rằng mình yêu cầu xin quyền
            } else {
                // nếu như trước đây chưa từng xin quyền, thì khi xin quyền sẽ có thông báo xin quyền bật lên
                // câu lệnh xin quyền,
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACT);
            }
        }else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "xin quyền thành công", Toast.LENGTH_SHORT).show();
                getContacts();
            } else {
                Toast.makeText(this, "xin quyền thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getContacts(){
        ArrayList<Contact> contacts = readContact();
        for (Contact contact : contacts) {
            String name  = contact.getName();
            StringBuilder phoneNumber = new StringBuilder();
            ArrayList<String> strings = contact.getPhoneNumber();
            for (String string : strings) {
                phoneNumber.append(string +" ");
            }

            Log.d("thinhvh", "name : "+name +" ___ SDT : "+phoneNumber.toString());
        }
    }

    private ArrayList<Contact> readContact() {
        ArrayList<Contact> listContacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                ArrayList<String> phoneList = new ArrayList<>();
                Contact contact = new Contact();
                contact.setId(id);
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (cursorInfo.moveToNext()) {
                     String phoneNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneList.add(phoneNumber);
                    }
                    cursorInfo.close();
                }

                contact.setPhoneNumber(phoneList);
                listContacts.add(contact);
            }
            cursor.close();
        }
        return listContacts;
    }
}
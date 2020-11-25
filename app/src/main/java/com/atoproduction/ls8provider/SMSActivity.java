package com.atoproduction.ls8provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.widget.Toast;

public class SMSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_m_s);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                // Nếu như trước đây bị từ chối xin quyền, thì lần xin quyền này sẽ không xuất hiện dialog xin quyền nũa
                // lúc này mình sẽ phải tự viết 1 cái thông báo rằng mình yêu cầu xin quyền
            } else {
                // nếu như trước đây chưa từng xin quyền, thì khi xin quyền sẽ có thông báo xin quyền bật lên
                // câu lệnh xin quyền,
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 12);
            }
        } else {
            getSMS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "xin quyền thành công", Toast.LENGTH_SHORT).show();
                getSMS();
            } else {
                Toast.makeText(this, "xin quyền thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getSMS() {
//        Cursor cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
//        Uri allMessages = Uri.parse("content://sms/inbox");
//        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            Cursor cursor = this.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.BODY));
                        String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.ADDRESS));
                        Log.d("thinhvh", "address: " + address + " ---  body = " + body);
                    }
                }
            }
        }
    }
}
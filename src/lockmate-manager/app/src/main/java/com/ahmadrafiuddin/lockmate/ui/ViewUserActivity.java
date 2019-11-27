package com.ahmadrafiuddin.lockmate.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmadrafiuddin.lockmate.R;
import com.ahmadrafiuddin.lockmate.model.QrKey;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewUserActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY_INFO = "BUNDLE_KEY_INFO";
    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_MATRIC_NUM = "KEY_MATRIC_NUM";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";
    private Bundle keyInfoBundle;

    private String deleteKeyUrl = "http://gmm-student.fc.utm.my/~arbna/lockmate/delete_key.php";

    private ImageView avatar;
    private ImageView userQrCode;
    private TextView name;
    private TextView matricNum;
    private TextView password;
    private Button deleteUserBtn;
    private Button shareQrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        avatar = (ImageView) findViewById(R.id.img_view_user_avatar);
        name = (TextView) findViewById(R.id.lbl_name);
        matricNum = (TextView) findViewById(R.id.lbl_matric_num);
        password = (TextView) findViewById(R.id.lbl_password);
        deleteUserBtn = (Button) findViewById(R.id.btn_delete_user);
        shareQrBtn = (Button) findViewById(R.id.btn_share_qr);
        userQrCode = (ImageView) findViewById(R.id.img_user_qr_code);

        keyInfoBundle = getIntent().getBundleExtra(BUNDLE_KEY_INFO);
        final QrKey selectedKey = new QrKey(keyInfoBundle.getString(KEY_ID),
                            keyInfoBundle.getString(KEY_NAME),
                            keyInfoBundle.getString(KEY_MATRIC_NUM),
                            keyInfoBundle.getString(KEY_PASSWORD));

        name.setText(selectedKey.getStudentName());
        matricNum.setText(selectedKey.getMatricNo());
        password.setText(selectedKey.getPassword());

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(selectedKey.getPassword(), BarcodeFormat.QR_CODE, 700, 700);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            userQrCode.setImageBitmap(bitmap);
        }
        catch (WriterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        shareQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imagePath = new File(getApplicationContext().getCacheDir(), "images");
                File newFile = new File(imagePath, "image.png");
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.ahmadrafiuddin.lockmate.fileprovider", newFile);

                if (contentUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                }
            }
        });

        deleteUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteKey(selectedKey.getId());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewusermenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_user:
                // User chose the "Edit User" action, go to Edit User Activity
                Intent i = new Intent(this, EditUserActivity.class);
                i.putExtra(BUNDLE_KEY_INFO, keyInfoBundle);
                startActivity(i);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteKey(String keyId) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("id", params[0])
                        .build();
                Request request = new Request.Builder()
                        .url(deleteKeyUrl)
                        .post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(ViewUserActivity.this, s, Toast.LENGTH_LONG).show();
                Intent i = new Intent(ViewUserActivity.this, MainActivity.class);
                startActivity(i);
            }
        };

        task.execute(keyId);
    }

}

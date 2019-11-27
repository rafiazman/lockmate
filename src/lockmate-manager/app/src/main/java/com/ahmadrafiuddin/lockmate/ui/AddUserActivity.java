package com.ahmadrafiuddin.lockmate.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmadrafiuddin.lockmate.R;
import com.ahmadrafiuddin.lockmate.model.QrKey;

import java.io.IOException;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddUserActivity extends AppCompatActivity {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnmABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String addKeyUrl = "http://gmm-student.fc.utm.my/~arbna/lockmate/add_key.php";

    private EditText name;
    private EditText matricNum;
    private EditText password;
    private Button btn_generatePwd;
    private Button btn_addUser;

    private TextInputLayout floatingNameLabel;
    private TextInputLayout floatingMatricNumTextboxLabel;
    private TextInputLayout floatingPasswordLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        name = (EditText) findViewById(R.id.txtbox_name);
        matricNum = (EditText) findViewById(R.id.txtbox_matric_num);
        password = (EditText) findViewById(R.id.txtbox_password);
        btn_generatePwd = (Button) findViewById(R.id.btn_generate_pwd);
        btn_addUser = (Button) findViewById(R.id.btn_add_user);

        floatingNameLabel = (TextInputLayout) findViewById(R.id.name_text_input_layout);
        floatingMatricNumTextboxLabel = (TextInputLayout) findViewById(R.id.matric_num_text_input_layout);
        floatingPasswordLabel = (TextInputLayout) findViewById(R.id.password_text_input_layout);

        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        btn_generatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText(getRandomString(20));
            }
        });

        btn_addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty() && !matricNum.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    // all fields filled
                    QrKey keyToSubmit = new QrKey();
                    keyToSubmit.setStudentName(name.getText().toString());
                    keyToSubmit.setMatricNo(matricNum.getText().toString());
                    keyToSubmit.setPassword(password.getText().toString());

                    submitData(keyToSubmit);
                }
                else {
                    if (name.getText().toString().isEmpty()) {
                        floatingNameLabel.setError("Required field");
                        floatingNameLabel.setErrorEnabled(true);
                    }

                    if (matricNum.getText().toString().isEmpty()) {
                        floatingMatricNumTextboxLabel.setError("Required field");
                        floatingMatricNumTextboxLabel.setErrorEnabled(true);
                    }

                    if (password.getText().toString().isEmpty()) {
                        floatingPasswordLabel.setError("Required field");
                        floatingPasswordLabel.setErrorEnabled(true);
                    }
                }
            }
        });

        setupFloatingLabelError();
    }

    private void submitData(QrKey keyToSubmit) {
        AsyncTask<QrKey, Void, String> task = new AsyncTask<QrKey, Void, String>() {

            @Override
            protected String doInBackground(QrKey... params) {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("studentName", params[0].getStudentName())
                        .add("matricNo", params[0].getMatricNo())
                        .add("password", params[0].getPassword())
                        .build();
                Request request = new Request.Builder()
                        .url(addKeyUrl)
                        .post(formBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "Error";
            }

            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(AddUserActivity.this, s, Toast.LENGTH_LONG).show();
                Intent i = new Intent(AddUserActivity.this, MainActivity.class);
                startActivity(i);
            }
        };

        task.execute(keyToSubmit);
    }

    private void setupFloatingLabelError() {

        floatingNameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    floatingNameLabel.setError("Required field");
                    floatingNameLabel.setErrorEnabled(true);
                }
                else {
                    floatingNameLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        floatingMatricNumTextboxLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    floatingMatricNumTextboxLabel.setError("Required field");
                    floatingMatricNumTextboxLabel.setErrorEnabled(true);
                }
                else {
                    floatingMatricNumTextboxLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        floatingPasswordLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    floatingPasswordLabel.setError("Required field");
                    floatingPasswordLabel.setErrorEnabled(true);
                }
                else {
                    floatingPasswordLabel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private static String getRandomString(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}

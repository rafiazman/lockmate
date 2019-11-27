package com.ahmadrafiuddin.lockmate.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ahmadrafiuddin.lockmate.ItemClickSupport;
import com.ahmadrafiuddin.lockmate.R;
import com.ahmadrafiuddin.lockmate.adapter.CustomAdapter;
import com.ahmadrafiuddin.lockmate.model.QrKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String BUNDLE_KEY_INFO = "BUNDLE_KEY_INFO";
    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_MATRIC_NUM = "KEY_MATRIC_NUM";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";

    private RecyclerView mRecyclerView;
    private CustomAdapter mAdapter;
    private List<QrKey> listOfUsers;

    private static String qrKeysUrl = "http://gmm-student.fc.utm.my/~arbna/lockmate/get_keys.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rec_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(myToolbar);

        listOfUsers = new ArrayList<>();
        loadKeysFromDb();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new CustomAdapter(this, listOfUsers);
        mRecyclerView.setAdapter(mAdapter);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        QrKey selectedQrKey = listOfUsers.get(position);

                        Intent i = new Intent(MainActivity.this, ViewUserActivity.class);

                        Bundle userInfoBundle = new Bundle();
                        userInfoBundle.putString(KEY_ID, selectedQrKey.getId());
                        userInfoBundle.putString(KEY_NAME, selectedQrKey.getStudentName());
                        userInfoBundle.putString(KEY_MATRIC_NUM, selectedQrKey.getMatricNo());
                        userInfoBundle.putString(KEY_PASSWORD, selectedQrKey.getPassword());

                        i.putExtra(BUNDLE_KEY_INFO, userInfoBundle);
                        startActivity(i);
                    }
                }
        );
    }

    private void loadKeysFromDb() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(qrKeysUrl).build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject singleKeyJson = array.getJSONObject(i);

                        QrKey singleKey = new QrKey(singleKeyJson.getString("id"), singleKeyJson.getString("studentName"), singleKeyJson.getString("matricNo"), singleKeyJson.getString("password"));
                        listOfUsers.add(singleKey);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    System.out.println("End of content");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        };

        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user:
                // User chose the "Add QrKey" action, go to Add QrKey Activity
                Intent i = new Intent(this, AddUserActivity.class);
                startActivity(i);
                return true;

            case R.id.action_about:
                // User chose the "About" item, show the app about UI...
                Intent j = new Intent(this, AboutActivity.class);
                startActivity(j);
                return true;

            case R.id.action_view_access_log:
                // User chose the "Access Log" item, show the View Access Log UI
                Intent k = new Intent(this, ViewAccessLogActivity.class);
                startActivity(k);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        promptForExit();
    }

    private void promptForExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.setNegativeButton("No", null);

        alertDialog.setMessage("Do you want to exit?");
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}

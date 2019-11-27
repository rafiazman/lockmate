package com.ahmadrafiuddin.lockmate.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ahmadrafiuddin.lockmate.ItemClickSupport;
import com.ahmadrafiuddin.lockmate.R;
import com.ahmadrafiuddin.lockmate.adapter.CustomAdapter;
import com.ahmadrafiuddin.lockmate.adapter.LogsAdapter;
import com.ahmadrafiuddin.lockmate.model.AccessLog;
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

public class ViewAccessLogActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LogsAdapter mAdapter;
    private List<AccessLog> records;

    private static String access_log_url = "http://gmm-student.fc.utm.my/~arbna/lockmate/get_records.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_access_log);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rec_records_list);
        records = new ArrayList<>();

        downloadRecordsFromDb();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new LogsAdapter(this, records);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void downloadRecordsFromDb() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(access_log_url).build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject singleKeyJson = array.getJSONObject(i);

                        AccessLog singleRecord =
                                new AccessLog(singleKeyJson.getString("studentName"),
                                            singleKeyJson.getString("matricNo"),
                                            singleKeyJson.getString("scan_datetime"));
                        records.add(singleRecord);
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
}

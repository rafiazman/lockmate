package com.ahmadrafiuddin.lockmatescanner2.ui;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmadrafiuddin.lockmatescanner2.R;
import com.ahmadrafiuddin.lockmatescanner2.model.QrKey;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScannerActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    private Button btn_start;
    private Button btn_stop;
    private TextView txt_consoleLog;
    private List<QrKey> validKeys;
    private static String json_url = "http://gmm-student.fc.utm.my/~arbna/lockmate/get_keys.php";
    private static String log_access_url = "http://gmm-student.fc.utm.my/~arbna/lockmate/add_record.php";

    private boolean arduinoConnected = false;

    public final String ACTION_USB_PERMISSION = "com.ahmadrafiuddin.lockmatescanner.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                tvAppend(txt_consoleLog, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            setUiEnabled(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            tvAppend(txt_consoleLog, "Serial Connection Opened!\n");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(btn_start);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(btn_stop);
            }
        }

        ;
    };

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

//            if (result.getText() == null) {
//                // Prevent duplicate scans
//                return;
//            }

            lastText = result.getText();
            boolean isValidCode = false;
            QrKey authenticatedUser = new QrKey();

            for (int i = 0; i < validKeys.size(); i++) {
                if (lastText.equals(validKeys.get(i).getPassword())) {
                    // Scanned QR code is in Database
                    authenticatedUser = validKeys.get(i);
                    isValidCode = true;
                    break;
                }
            }

            if (isValidCode) {
                txt_consoleLog.append("Valid code\n");
                if (arduinoConnected) {
                    serialPort.write("on".getBytes());
                    txt_consoleLog.append("Sent on signal\n");
                    logAccessToDb(authenticatedUser.getId());
                }
            } else {
                txt_consoleLog.append("Invalid code\n");
                if (arduinoConnected) {
                    serialPort.write("off".getBytes());
                    txt_consoleLog.append("Sent off signal\n");
                }
            }

            barcodeView.setStatusText(result.getText());
            txt_consoleLog.append("QR: " + result.getText() + "\n");
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        txt_consoleLog = (TextView) findViewById(R.id.lbl_console);
        setUiEnabled(false);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        //barcodeView.decodeContinuous(callback);

        validKeys = new ArrayList<>();
        downloadValidKeys();
    }

    public void setUiEnabled(boolean bool) {
        btn_start.setEnabled(!bool);
        //sendButton.setEnabled(bool);
        btn_stop.setEnabled(bool);
        txt_consoleLog.setEnabled(bool);
    }

    public void onClickStart(View view) {
        arduinoConnected = true;

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341) // Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }
                if (!keep)
                    break;
            }
        }
    }

    public void onClickStop(View view) {
        setUiEnabled(false);
        serialPort.close();
        tvAppend(txt_consoleLog,"\nSerial Connection Closed! \n");
    }

    private void downloadValidKeys() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                txt_consoleLog.append("Downloading list of valid keys..\n");
            }

            @Override
            protected Void doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(json_url).build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject singleKeyJson = array.getJSONObject(i);

                        QrKey singleKey = new QrKey(singleKeyJson.getString("id"), singleKeyJson.getString("studentName"), singleKeyJson.getString("matricNo"), singleKeyJson.getString("password"));
                        validKeys.add(singleKey);
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
                txt_consoleLog.append("Valid keys downloaded.\n");
                barcodeView.decodeContinuous(callback);
                beepManager = new BeepManager(ScannerActivity.this);
                beepManager.setBeepEnabled(false);
            }
        };

        task.execute();
    }

    private void logAccessToDb(String qr_key_id) {
        AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("qr_key_id", params[0])
                        .build();
                Request request = new Request.Builder()
                        .url(log_access_url)
                        .post(formBody)
                        .build();

                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                txt_consoleLog.append("Logged access.\n");
            }
        };

        task.execute(qr_key_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public void onBackPressed() {
        promptForExit();
    }

    private void promptForExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScannerActivity.this);

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

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }
}

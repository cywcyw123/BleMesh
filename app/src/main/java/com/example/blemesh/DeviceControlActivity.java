/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.blemesh;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airoha.libfota.core.OnAirohaOtaEventListener;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * For a given BLE device, this Activity provides the user interface to connect. The Activity
 * communicates with {@link BluetoothLeService}
 */
public class DeviceControlActivity extends AppCompatActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final String STR_VALID = "valid";
    private static final String STR_INVALID = "invalid";
    private static final String STR_PROGRAMMING = "programming";
    private static final String STR_WACHANGED = "W.A. changed";

    private TextView mConnectionState;
    private TextView mDataField;
    private TextView mViewWorkingArea;
    private TextView mViewArea1Rev;
    private TextView mViewArea2Rev;
    private TextView mViewArea1Stat;
    private TextView mViewArea2Stat;
    private TextView mViewSelFileName;
    private TextView mViewAction;
    private TextView mViewProgress;
    private TextView mViewErrorMsg;
    private TextView mViewThroughput;
    private TextView mViewBatteryLevel;
    private EditText mEditMtu;

    private Button mBtnReadStatus;
    private Button mBtnChangeWa1;
    private Button mBtnChangeWa2;
    private Button mBtnFilePicker;
    private Button mBtnUpdate;
//    private Button mBtnUpdateWa2;
    private Button mBtnRequestMtu;
    private Button mBtnApplyNewFw;

    private Switch mSwitchSmall;

    private String mDeviceName;
    private String mDeviceAddress;

    private String mStrSelectedBinFileName;
    private FilePickerDialog mFilePickerDialog;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    private static final int FILE_SELECT_CODE = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private static final int ACTIVITY_CHOOSE_FILE = 1;

    private String mFilePath;


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if(mBluetoothLeService != null) {
            mBluetoothLeService.getAirohaOtaMgr().setListener(mOnAirohaOtaEventListener);
                mBluetoothLeService.connect(mDeviceAddress);
                updateConnectionState(R.string.connecting);
            }
            if(mFilePath != null) {
                mBluetoothLeService.getAirohaOtaMgr().setOtaBinFileName(mFilePath);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private String mStrErrorReadable;


    private void clearUI() {
        mDataField.setText(R.string.no_data);

        mViewWorkingArea.setText(R.string.no_data);
        mViewArea1Stat.setText(R.string.no_data);
        mViewArea2Stat.setText(R.string.no_data);
        mViewArea1Rev.setText(R.string.no_data);
        mViewArea2Rev.setText(R.string.no_data);

        disableButtons();
    }

    private void disableButtons() {
        mBtnChangeWa1.setEnabled(false);
        mBtnChangeWa2.setEnabled(false);
        mBtnFilePicker.setEnabled(false);
        mBtnUpdate.setEnabled(false);
//        mBtnUpdateWa2.setEnabled(false);
        mSwitchSmall.setEnabled(false);

        mBtnReadStatus.setEnabled(false);

        mBtnApplyNewFw.setEnabled(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_activity);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        if(mDeviceName == null){
            mDeviceName = "Unknown device";
        }
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);
        mViewWorkingArea = (TextView)findViewById(R.id.working_areaNum);
        mViewArea1Rev = (TextView)findViewById(R.id.rev_area1);
        mViewArea2Rev = (TextView)findViewById(R.id.rev_area2);
        mViewArea1Stat = (TextView)findViewById(R.id.status_area1);
        mViewArea2Stat = (TextView)findViewById(R.id.status_area2);
        mViewSelFileName = (TextView)findViewById(R.id.tvFileName);
        mViewAction = (TextView)findViewById(R.id.tvAction);
        mViewProgress = (TextView)findViewById(R.id.tvProgress);
        mViewErrorMsg = (TextView)findViewById(R.id.errorMsg);
        mViewThroughput = findViewById(R.id.throughtPutMsg);
        mViewBatteryLevel = findViewById(R.id.batteryLevel);

        mBtnFilePicker = (Button)findViewById(R.id.btnFilePicker);
        mBtnFilePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
//                mFilePickerDialog.show();
            }
        });

        mBtnReadStatus = findViewById(R.id.btnReadStatus);
        mBtnReadStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.getAirohaOtaMgr().readStatus();
            }
        });

        mBtnChangeWa1 = (Button)findViewById(R.id.btnChangeWorkingArea1);
        mBtnChangeWa1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.getAirohaOtaMgr().changeWorkingArea(1);
            }
        });
        mBtnChangeWa2 = (Button)findViewById(R.id.btnChangeWorkingArea2);
        mBtnChangeWa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.getAirohaOtaMgr().changeWorkingArea(2);
            }
        });
        mBtnUpdate = (Button)findViewById(R.id.btnUpdate);
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOTA();
            }
        });
//        mBtnUpdateWa2 = (Button)findViewById(R.id.btnUpdateWorkingArea2);
//        mBtnUpdateWa2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startOTA();
//            }
//        });

        mSwitchSmall = (Switch)findViewById(R.id.switchSmall);
        mSwitchSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean on = ((Switch) v).isChecked();
//                if(on){
//                    mBluetoothLeService.setUseSmallFlash(true);
//                }else {
//                    mBluetoothLeService.setUseSmallFlash(false);
//                }
            }
        });

        mEditMtu = (EditText)findViewById(R.id.editMtu);
        mBtnRequestMtu = (Button)findViewById(R.id.btnRequestMtu);

        mBtnRequestMtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mtu = Integer.valueOf(mEditMtu.getText().toString());

                mBluetoothLeService.getAirohaLink().requestChangeMtu(mtu);
            }
        });

        mBtnApplyNewFw = findViewById(R.id.btnApplyNewFw);
        mBtnApplyNewFw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.getAirohaOtaMgr().applyNewFw();
            }
        });

        getSupportActionBar().setTitle(mDeviceName);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        initFileDialog();
        checkReadPermission();
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
        requestRnWpermission();
    }

    private void requestRnWpermission(){
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
        }
    }

    private void showFileChooser() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("application/octet-stream");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE:
                Uri uri = data.getData();
                String path = uri.getPath();

                if (path != null && path.length() > 0 && path.endsWith(".bin")) {
                    try {
                        ContentResolver contentResolver = this.getContentResolver();
                        InputStream inputStream = contentResolver.openInputStream(uri);
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                        int nRead;
                        byte[] b = new byte[512];
                        while ((nRead = inputStream.read(b)) != -1) {
                            buffer.write(b, 0, nRead);
                        }
                        final byte[] raw = buffer.toByteArray();

                        mViewSelFileName.setText(path);
                        mViewSelFileName.setTextColor(Color.BLACK);
                        mStrSelectedBinFileName = path;
                        if(mBluetoothLeService != null) {
                            mBluetoothLeService.getAirohaOtaMgr().setOtaBinFileRaw(raw);
                            mFilePath = null;
                        } else {
                            mFilePath = FileBrowser.getPathFromUri(this, uri);
                        }

                        inputStream.close();
                        buffer.close();
                        mBtnUpdate.setEnabled(true);
                    }
                    catch (Exception e){
                        Log.d(TAG, " inputStream exception:" + e.getMessage());
                    }

                }
                else{
                    mViewSelFileName.setText(path + " is not valid.");
                    mViewSelFileName.setTextColor(Color.RED);
                }
                break;
        }
    }

    private void startOTA(){
        if(mStrSelectedBinFileName == null){
            // toast
            return;
        }
        disableButtons();
        mBluetoothLeService.getAirohaOtaMgr().startFota();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mBluetoothLeService != null) {
        mBluetoothLeService.getAirohaOtaMgr().close();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                updateConnectionState(R.string.connecting);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        return intentFilter;
    }

    private void checkReadPermission(){
        if(ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE")!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                    0);
        }
    }

    private void initFileDialog(){
        DialogProperties properties= new DialogProperties();
        properties.selection_mode= DialogConfigs.SINGLE_MODE;
        properties.selection_type=DialogConfigs.FILE_SELECT;
        properties.root=new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions=new String[]{"bin", "BIN"};
        mFilePickerDialog = new FilePickerDialog(this, properties);
        mFilePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if(files!=null && files.length>0){
                    mViewSelFileName.setText(files[0]);
                    mStrSelectedBinFileName = files[0];

                    mBluetoothLeService.getAirohaOtaMgr().setOtaBinFileName(files[0]);

//                    if(mViewWorkingArea.getText().equals("1")){
//                        mBtnUpdateWa2.setEnabled(true);
//                    }else {
                        mBtnUpdate.setEnabled(true);
//                    }
                }
            }
        });
    }


    private OnAirohaOtaEventListener mOnAirohaOtaEventListener = new OnAirohaOtaEventListener() {

        @Override
        public synchronized void OnRequestMtuChangeStatus(boolean isAccepted) {
            if(isAccepted){
                mStrErrorReadable = "request MTU accepted";
            }else {
                mStrErrorReadable = "request MTU not accepted";
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewErrorMsg.setText(mStrErrorReadable);
                }
            });


        }

        @Override
        public synchronized void OnNewMtu(final int mtu) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewErrorMsg.setText("MTU changed to: " + mtu);
                }
            });

        }

        @Override
        public void OnGattConnected() {
            mConnected = true;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "UI OnGattConnected");
                    updateConnectionState(R.string.connected);
                    invalidateOptionsMenu();

                    if(Build.VERSION.SDK_INT >=21){
                        mBtnRequestMtu.setEnabled(true);
                    }

                    mBtnReadStatus.setEnabled(true);
                }
            });
        }

        @Override
        public void OnGattDisconnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mConnected = false;
                    updateConnectionState(R.string.disconnected);
                    Toast.makeText(DeviceControlActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        }

        @Override
        public void OnWorkingAreaStatus(final String workingArea, final String area1Rev, final boolean isArea1Valid, final String area2Rev, final boolean isArea2Valid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewWorkingArea.setText(workingArea);
                    boolean wa1Stat = isArea1Valid;
                    boolean wa2Stat = isArea2Valid;

                    mBtnChangeWa1.setEnabled(wa1Stat);
                    mBtnChangeWa2.setEnabled(wa2Stat);

                    String strWa1Stat = wa1Stat ? STR_VALID: STR_INVALID;
                    String strWa2Stat = wa2Stat ? STR_VALID : STR_INVALID;

                    mViewArea1Stat.setText(strWa1Stat);
                    mViewArea2Stat.setText(strWa2Stat);
                    mViewArea1Rev.setText(area1Rev);
                    mViewArea2Rev.setText(area2Rev);

                    mBtnFilePicker.setEnabled(true);

                    mSwitchSmall.setEnabled(true);
                }
            });
        }

        @Override
        public void OnUpdateProgrammingProgress(final float progress) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewAction.setText(STR_PROGRAMMING);
                    mViewProgress.setText(String.format("%.1f", progress*100));
                }
            });
        }

        @Override
        public void OnWorkingAreaChanged() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewAction.setText(STR_WACHANGED);

                    // 2018.08.30 Daniel
                    mBtnApplyNewFw.setEnabled(true);
                }
            });
        }

        @Override
        public void OnHandleBootCodeNotMatching() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStrErrorReadable = "Boot Code Not Matching";

                    Toast.makeText(DeviceControlActivity.this, mStrErrorReadable, Toast.LENGTH_SHORT).show();
                    mBtnUpdate.setEnabled(false);
//                    mBtnUpdateWa2.setEnabled(false);

                    mViewErrorMsg.setText(mStrErrorReadable);
                }
            });
        }

        @Override
        public void OnHandleCodeAreaAddrNotMatching() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStrErrorReadable = "Code Area Address Not Matching";

                    Toast.makeText(DeviceControlActivity.this, mStrErrorReadable, Toast.LENGTH_SHORT).show();
                    mBtnUpdate.setEnabled(false);
//                    mBtnUpdateWa2.setEnabled(false);

                    mViewErrorMsg.setText(mStrErrorReadable);
                }
            });
        }

        @Override
        public void OnHandleOtaDisabled(final byte errorCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mStrErrorReadable = String.format("ERROR_CODE 0x%s. %s", Integer.toHexString(errorCode),"OTA function is disabled on the target device");

                    Toast.makeText(DeviceControlActivity.this, mStrErrorReadable, Toast.LENGTH_LONG).show();

                    mViewErrorMsg.setText(mStrErrorReadable);
                }
            });
        }

        @Override
        public void OnBinFileParseException() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStrErrorReadable = "BIN info can't be parsed correctly";

                    Toast.makeText(DeviceControlActivity.this, mStrErrorReadable, Toast.LENGTH_SHORT).show();

                    mViewErrorMsg.setText(mStrErrorReadable);
                }
            });
        }

        @Override
        public void OnReportProgramThroughput(final float throughput) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String thrStr = String.format("%.1f", (throughput));

                    mViewThroughput.setText(String.format("throughput: %s kB/s", thrStr));
                }
            });
        }

        @Override
        public void OnBatteryLevel(final int batteryLevel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewBatteryLevel.setText(String.format("%d %%", batteryLevel));
                }
            });
        }

        @Override
        public void OnRetryFailed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStrErrorReadable = "OnRetryFailed";

                    Toast.makeText(DeviceControlActivity.this, mStrErrorReadable, Toast.LENGTH_SHORT).show();

                    mViewErrorMsg.setText(mStrErrorReadable);
                }
            });
        }

        @Override
        public void OnStatusError(final byte errorCode, final String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStrErrorReadable = String.format("ERROR_CODE 0x%s. %s", Integer.toHexString(errorCode),errorMsg);

                    Toast.makeText(DeviceControlActivity.this, mStrErrorReadable, Toast.LENGTH_LONG).show();

                    mViewErrorMsg.setText(mStrErrorReadable);
                }
            });
        }

        @Override
        public void OnOtaServiceNotFound() {

        }
    };
}

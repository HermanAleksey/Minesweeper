package com.example.sapper.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceListActivity extends Activity implements View.OnClickListener {
    final static int REQUEST_ENABLE_BT = 0;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    BluetoothAdapter bluetooth;
    ArrayAdapter<String> mArrayAdapter;
    ArrayAdapter<String> mNewDevicesArrayAdapter;
    ListView lvPairedDevices, lvNewDevices;
    TextView tvPairedDevices, tvNewDevices;
    Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // Create LinearLayout
        LinearLayout linLayout = new LinearLayout(this);
        // Set vertical orientation
        linLayout.setOrientation(LinearLayout.VERTICAL);
        // Create LayoutParams
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setContentView(linLayout, linLayoutParam);

        // Create LayoutParams for Button
        LinearLayout.LayoutParams buttonLayoutParam = new LinearLayout.LayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        buttonLayoutParam.gravity = Gravity.CENTER_HORIZONTAL;

        tvPairedDevices = new TextView(this);
        tvPairedDevices.setText("R.string.title_paired_devices");
        linLayout.addView(tvPairedDevices);

        lvPairedDevices = new ListView(this);
        linLayout.addView(lvPairedDevices);

        btnScan = new Button(this);
        btnScan.setText("R.string.button_scan");

        linLayout.addView(btnScan, buttonLayoutParam);
        btnScan.setOnClickListener(this);

        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        tvNewDevices = new TextView(this);
        tvNewDevices.setText("R.string.title_other_devices");
        tvNewDevices.setVisibility(View.INVISIBLE);
        linLayout.addView(tvNewDevices);

        lvNewDevices = new ListView(this);
        lvNewDevices.setVisibility(View.INVISIBLE);
        linLayout.addView(lvNewDevices);
        lvNewDevices.setAdapter(mNewDevicesArrayAdapter);
        lvNewDevices.setOnItemClickListener(deviceNamesClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onStart() {
        Log.d("myLogs", "DeviceListActivity onStart ");
        super.onStart();
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) // Device has Bluetooth-module
        {
            if (bluetooth.isEnabled()) {
                // Bluetooth is switched on. Working.
                lvPairedDevices.setAdapter(findPairedDevices());
            } else {
                // Bluetooth is switched off. Предложим пользователю включить его.
                Log.d("myLogs", "Bluetooth is switched off. Предложим пользователю включить его.");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            lvPairedDevices.setOnItemClickListener(deviceNamesClickListener);
        } else {
            // Device has not Bluetooth-module
            Toast.makeText(this, "R.string.bluetoothNotAvailable", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("cntPlayers", 1);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        Log.d("myLogs", "DeviceListActivity onResume ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("myLogs", "DeviceListActivity onPause ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("myLogs", "DeviceListActivity onStop ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("myLogs", "DeviceListActivity onDestroy ");
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        if (bluetooth != null) {
            bluetooth.cancelDiscovery();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Context context = MainActivity.context;

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "R.string.bluetoothOn", Toast.LENGTH_LONG).show();
                lvPairedDevices.setAdapter(findPairedDevices());
            } else {
                /* User refused to turn on BlueTooth*/
                Toast.makeText(context, "R.string.cancelled", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("cntPlayers", 1);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    /*** Create the list of paired devices ***/
    protected ArrayAdapter<String> findPairedDevices() {
        Log.d("myLogs", "Caught on filling Adapter");
        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
        List<String> deviceNames = new ArrayList<String>();
        // If list of paired devices is empty
        if (pairedDevices.size() > 0) {
            Log.d("myLogs", "list of paired devices is not empty");
            // loops through this list
            for (BluetoothDevice device : pairedDevices) {
                // Add names and address to mArrayAdapter, that to show its throw ListView
                deviceNames.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            Log.d("myLogs", "If list of paired devices is empty");
            // If list of paired devices is empty
            tvPairedDevices.setText("R.string.notPaired");
        }
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames);
        return mArrayAdapter;
    }

    private final OnItemClickListener deviceNamesClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            bluetooth.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra("cntPlayers", 2);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public void onClick(View v) {
        v.setVisibility(View.GONE);
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle("R.string.scanning");

        tvNewDevices.setVisibility(View.VISIBLE);
        lvNewDevices.setVisibility(View.VISIBLE);
        // If we're already discovering, stop it
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        bluetooth.startDiscovery();
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    // Create  BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add name and address to array adapter, to show in ListView
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle("R.string.select_device");
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "R.string.none_found"; /*getResources().getText(
                            R.string.none_found).toString();*/
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
}
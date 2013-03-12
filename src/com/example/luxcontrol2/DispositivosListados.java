package com.example.luxcontrol2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.luxcontrol2.bluetooth.BluetoothListService;

public class DispositivosListados extends BluetoothListService {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		regresarAMain();
	}

	@SuppressWarnings("unchecked")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the local Bluetooth adapter
        regresarAMain();

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_dispositivos_listados);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(this);

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.nombre_dispositivo);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.nombre_dispositivo);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(getBtReceiver(), filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(getBtReceiver(), filter);


        // Get a set of currently paired devices
        mPairedDevicesArrayAdapter = (ArrayAdapter<String>) getPairedDevicesAdapter(R.id.title_paired_devices);
    }

	private void regresarAMain() {
		if(!isOn()){
        	Intent mainActivity = new Intent(this, MainActivity.class);
        	startActivity(mainActivity);
        	finish();
        }
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Make sure we're not doing discovery anymore
        stopDiscovering();

        // Unregister broadcast listeners
        this.unregisterReceiver(getBtReceiver());
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        discoverDevices();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
        	stopDiscovering();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent(getApplicationContext(), ListaBombillos.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(intent);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

	@Override
	public void onClick(View v) {
        if (v.getId() == R.id.button_scan) {
			doDiscovery();
			v.setVisibility(View.GONE);
		}
	}

	@Override
	protected void addNewFoundDevice(BluetoothDevice device) {
		mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	}

	@Override
	protected void addPairedDevice(BluetoothDevice device) {
		mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	}

}

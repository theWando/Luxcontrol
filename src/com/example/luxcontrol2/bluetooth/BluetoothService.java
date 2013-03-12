package com.example.luxcontrol2.bluetooth;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.luxcontrol2.R;

public abstract class BluetoothService extends Activity implements OnClickListener {
	//CONSTANTS
	public static final int REQUEST_ENABLE_BT = 3;
	
	//ATRIBUTES
	private BluetoothAdapter btAdapter;
	private Set<BluetoothDevice> pairedDevices; // Get a set of currently paired devices
	private Set<BluetoothDevice> newDevices; 
	
	
	
	public BluetoothService() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public boolean isOn() {
		if(btAdapter != null && btAdapter.isEnabled()){
			return true;
		}
		return false;
	}

	/**
	 * Enciende la antena del Bluetooth
	 * @return
	 */
	public boolean turnOn() {
		if(btAdapter != null) {
	        // If BT is not on, request that it be enabled.
	        // setupChat() will then be called during onActivityResult
	        if (!btAdapter.isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        // Otherwise, setup the chat session
	        } else {
	        	callToast(R.string.BLUETOOTH_ALREADY_ENABLE);
	        }
		} else if (btAdapter == null || !btAdapter.isEnabled()) { // If the adapter is null, then Bluetooth is not supported
        	callToast(R.string.BLUETOOTH_NOT_AVIABLE);
        }
		return false;
	}

	/**
	 * Muestra un mensaje en pantalla a través de un Toast.
	 * @param bluetoothNotEnable Referencia a los string en el strings.xml
	 */
	protected void callToast(int bluetoothNotEnable) {
		Toast.makeText(this, bluetoothNotEnable, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Muestra un mensaje en pantalla a través de un Toast
	 * @param msg
	 */
	protected void callToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	public Set<BluetoothDevice> findPairedDevices() {
		if(pairedDevices == null) {
			pairedDevices = btAdapter.getBondedDevices();
		}
		return pairedDevices;
	}
	
	public Set<BluetoothDevice> findNewDevices() {
		if (newDevices == null) {
			discoverDevices();
		}
		return newDevices;		
	}
	
	public void discoverDevices() {
		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);
		stopDiscovering();
		btAdapter.startDiscovery();
		
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(btReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(btReceiver, filter);
	}
	
	public void stopDiscovering() {
		if (btAdapter != null && btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
	}
	
	/**
	 * Envía un mensaje al dispositivo con el que está conectado
	 * @param msg
	 */
	public void sendMessage(String msg) {
		
	}
	
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (newDevices == null) {
					newDevices = new HashSet<BluetoothDevice>();
				}
                
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					newDevices.add(device);
				}
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {  // When discovery is finished, change the Activity title
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
            }
        }
    };

	public BroadcastReceiver getBtReceiver() {
		return btReceiver;
	}

}

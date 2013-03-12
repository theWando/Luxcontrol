package com.example.luxcontrol2.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.luxcontrol2.R;

public abstract class BluetoothService extends Activity implements OnClickListener {
	//CONSTANTS
	public static final int REQUEST_ENABLE_BT = 3;
	
	//ATRIBUTES
	private BluetoothAdapter btAdapter;
	
	public BluetoothService() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	/**
	 * Returns a boolean value depending on whether it is or isn't on.
	 * @return
	 */
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
	
	/**
	 * Stops looking for new devices
	 */
	protected void stopDiscovering() {
		if (btAdapter != null && btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
	}
	
	/**
	 * Starts to look for new close devices
	 */
	protected void startDescovery() {
		if (btAdapter != null && !btAdapter.isDiscovering()) {
			btAdapter.startDiscovery();
		}
	}
	/**
	 * Envía un mensaje al dispositivo con el que está conectado
	 * @param msg
	 */
	public void sendMessage(String msg) {
		
	}

	
	protected BluetoothAdapter getAdapter() {
		return btAdapter;
	}
}

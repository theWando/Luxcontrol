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
	private BluetoothAdapter mBluetoothAdapter;
	
	
	
	public BluetoothService() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
        	callToast(R.string.BLUETOOTH_NOT_AVIABLE);
        }

	}
	
	public boolean isOn() {
		if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
			return true;
		}
		return false;
	}

	public boolean turnOn() {
		if(mBluetoothAdapter != null) {
	        // If BT is not on, request that it be enabled.
	        // setupChat() will then be called during onActivityResult
	        if (!mBluetoothAdapter.isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        // Otherwise, setup the chat session
	        } else {
	        	callToast(R.string.BLUETOOTH_ALREADY_ENABLE);
	        }

		}
		return false;
	}

	protected void callToast(int bluetoothNotEnable) {
		Toast.makeText(this, bluetoothNotEnable, Toast.LENGTH_LONG).show();
	}
	
}

/**
 * 
 */
package com.example.luxcontrol2.bluetooth;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.example.luxcontrol2.R;


/**
 * Esta clase lista lis dispositivos encontrados cercanos al bluetooth 
 * @author elaguna
 *
 */
public abstract class BluetoothListService extends BluetoothService {

	//ATRIBUTES
	private Set<BluetoothDevice> pairedDevicesSet; // Get a set of currently paired devices
	private Set<BluetoothDevice> newDevices; 

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
					addNewFoundDevice(device);
				}
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {  // When discovery is finished, change the Activity title
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
            }
        }
    };

	/**
	 * This is used to discover close devices.
	 */
    protected void discoverDevices() {
		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);
		stopDiscovering();
		startDescovery();
		
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(btReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(btReceiver, filter);
	}
	
	/**
	 * This method is use to add the new devices to your {@link ListAdapter} or {@link ArrayAdapter}.
	 * @param device
	 */
	protected abstract void addNewFoundDevice(BluetoothDevice device);

	protected abstract void addPairedDevice(BluetoothDevice device);

	protected ListAdapter getPairedDevicesAdapter(int layout) {
		ArrayAdapter<String> pairedDevices = new ArrayAdapter<String>(this, layout);
		pairedDevicesSet = findPairedDevices();
		if (pairedDevicesSet.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevicesSet) {
                addPairedDevice(device);
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevices.add(noDevices);
        }
		return pairedDevices;
	}
	
	/**
	 * Returns your paired devices
	 * @return
	 */
	protected Set<BluetoothDevice> findPairedDevices() {
		if(pairedDevicesSet == null) {
			pairedDevicesSet = getAdapter().getBondedDevices();
		}
		return pairedDevicesSet;
	}
	
	/**
	 * Returns new devices
	 * @return
	 */
	protected Set<BluetoothDevice> findNewDevices() {
		if (newDevices == null) {
			discoverDevices();
		}
		return newDevices;		
	}
	
	
	/**
	 * Returns the {@link BroadcastReceiver}.
	 * @return
	 */
	protected BroadcastReceiver getBtReceiver() {
		return btReceiver;
	}
}

package com.eduardolaguna.luxcontrol.bluetooth;

import java.util.UUID;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.EventLogTags.Description;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.eduardolaguna.luxcontrol.DispositivosListados;
import com.example.luxcontrol2.R;

public abstract class BluetoothService extends Activity implements
		OnClickListener {
	// CONSTANTS
	public static final int REQUEST_ENABLE_BT = 3;
	protected static final UUID uuid = UUID
			.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	public static final String TOAST = "toast";
	public static final String DEVICE_NAME = "device_name";

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_LISTEN = 1; // now listening for incoming
												// connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	// ATRIBUTES
	private BluetoothAdapter btAdapter;
	private BluetoothServerThread serverThread;
	private BluetoothClientThread clientThread;
	private ConnectedThread connected;
	private int state;
	private String deviceAddress;
	private String mConnectedDeviceName = null;

	public BluetoothService() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * Returns a boolean value depending on whether it is or isn't on.
	 * 
	 * @return
	 */
	public boolean isOn() {
		if (btAdapter != null && btAdapter.isEnabled()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			deviceAddress = extras
					.getString(DispositivosListados.EXTRA_DEVICE_ADDRESS);
			if (deviceAddress != null) {
				BluetoothDevice device = getAdapter().getRemoteDevice(
						deviceAddress);
				connect(device);
			}
		}
	}

	/**
	 * Enciende la antena del Bluetooth
	 * 
	 * @return
	 */
	public boolean turnOn() {
		if (btAdapter != null) {
			// If BT is not on, request that it be enabled.
			// setupChat() will then be called during onActivityResult
			if (!btAdapter.isEnabled()) {
				Intent enableIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				// Otherwise, setup the chat session
			} else {
				callToast(R.string.BLUETOOTH_ALREADY_ENABLE);
			}
		} else if (btAdapter == null || !btAdapter.isEnabled()) { // If the
																	// adapter
																	// is null,
																	// then
																	// Bluetooth
																	// is not
																	// supported
			callToast(R.string.BLUETOOTH_NOT_AVIABLE);
		}
		return false;
	}

	/**
	 * Muestra un mensaje en pantalla a trav�s de un Toast.
	 * 
	 * @param bluetoothNotEnable
	 *            Referencia a los string en el strings.xml
	 */
	protected void callToast(int bluetoothNotEnable) {
		Toast.makeText(this, bluetoothNotEnable, Toast.LENGTH_LONG).show();
	}

	/**
	 * Muestra un mensaje en pantalla a trav�s de un Toast
	 * 
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
	 * Env�a un mensaje al dispositivo con el que est� conectado
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		// Check that we're actually connected before trying anything
		if (getState() != STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (msg.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = msg.getBytes();
			write(send);
		}
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * 
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (state != STATE_CONNECTED)
				return;
			r = connected;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		this.state = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	private final void setStatus(int resId) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(subTitle);
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName));
					break;
				case STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case STATE_LISTEN:
				case STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				callToast(msg.getData().getString(TOAST));
				break;
			}
		}
	};

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return this.state;
	}

	protected BluetoothAdapter getAdapter() {
		return btAdapter;
	}

	public synchronized void connect(BluetoothDevice device) {
		// Cancel any thread attempting to make a connection
		if (state == STATE_CONNECTING) {
			if (clientThread != null) {
				clientThread.cancel();
				clientThread = null;
			}
		}

		// Cancel any thread currently running a connection
		// if (mConnectedThread != null) {mConnectedThread.cancel();
		// mConnectedThread = null;}

		if (state != STATE_CONNECTING) {
			// Start the thread to connect with the given device
			clientThread = new BluetoothClientThread(this, device,
					getAdapter(), uuid); // ConnectThread(device, secure);
			clientThread.start();
			setState(STATE_CONNECTING);
		}
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

		// Cancel the thread that completed the connection
		if (clientThread != null) {
			clientThread.cancel();
			clientThread = null;
		}

		// Cancel any thread currently running a connection
		if (connected != null) {
			connected.cancel();
			connected = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (serverThread != null) {
			serverThread.cancel();
			serverThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		connected = new ConnectedThread(socket, this, mHandler);
		connected.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);
		setState(STATE_CONNECTED);
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	public void connectionLost() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		connected.cancel();
		connected = null;

		Intent i = new Intent(getApplicationContext(), DispositivosListados.class);
		startActivity(i);
		finish();
	}
}

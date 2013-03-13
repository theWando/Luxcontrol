package com.example.luxcontrol2.bluetooth;

import java.io.IOException;
import java.util.UUID;

import com.example.luxcontrol2.bluetooth.exception.BluetoothException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothThread extends Thread {
	
	private final BluetoothServerSocket btServerSocket;

	public BluetoothThread(BluetoothAdapter btAdapter) throws BluetoothException {
		super();
		BluetoothServerSocket btServerSocket = null;
		try {
			UUID uuid = UUID.randomUUID();
			btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("blablilu", uuid);
		} catch (IOException e) {
			throw new BluetoothException(e.fillInStackTrace());
		}
		this.btServerSocket = btServerSocket;
	}

	@Override
	public void run() {
		BluetoothSocket socket = null;
		 // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = btServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
//                manageConnectedSocket(socket);
				try {
					btServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            }
        }
	}

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
        	btServerSocket.close();
        } catch (IOException e) { }
    }
 }

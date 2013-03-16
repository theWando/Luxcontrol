package com.example.luxcontrol2.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothServerThread extends Thread {
	
	private final BluetoothServerSocket btServerSocket;

	public BluetoothServerThread(BluetoothAdapter btAdapter, final UUID uuid) {
		super();
		BluetoothServerSocket btServerSocket = null;
		try {
			btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("blablilu", uuid);
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "Error conectando", e);
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
            	Log.e(this.getClass().getSimpleName(), "Error buscando dispositivos en el ServerSocket", e);
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
//                manageConnectedSocket(socket);
				try {
					btServerSocket.close();
				} catch (IOException e) {
					Log.e(this.getClass().getSimpleName(), "Error cerrando la conexión", e);
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

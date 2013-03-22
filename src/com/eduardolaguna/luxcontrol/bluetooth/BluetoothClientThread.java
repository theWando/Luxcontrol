package com.eduardolaguna.luxcontrol.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * This class allows us to engage with a remote device that have an open server socket
 * @author eduardo
 *
 */
public class BluetoothClientThread extends Thread {
	
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private BluetoothAdapter btAdapter;
	private BluetoothService service;
	
	public BluetoothClientThread(BluetoothService service, BluetoothDevice device, BluetoothAdapter btAdapter, final UUID uuid) {
		BluetoothSocket socket = null;
		this.btAdapter = btAdapter;
		this.device = device;
		this.service = service;
		try {
			socket = device.createRfcommSocketToServiceRecord(uuid);
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), "Error creando el socket", e);
		}
		this.socket = socket;
	}

	@Override
    public void run() {
        // Cancel discovery because it will slow down the connection
		btAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            this.socket.connect();
        } catch (IOException connectException) {
        	Log.e(this.getClass().getSimpleName(), "Error conectando con el dispositivo, intentando cerrar el socket", connectException);
            // Unable to connect; close the socket and get out
            try {
            	this.socket.close();
            } catch (IOException closeException) {
            	Log.e(this.getClass().getSimpleName(), "Error cerrando la conexion", closeException);
            }
            return;
        }
 
        // Start the connected thread
        service.connected(this.socket, this.device);
    }
	
	/**
	 * Will cancel an in-progress connection, and close the socket
	 */
	public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }
	
}

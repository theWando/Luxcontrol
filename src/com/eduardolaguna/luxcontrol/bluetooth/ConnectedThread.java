package com.eduardolaguna.luxcontrol.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final Handler mHandler;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private BluetoothService service;
    
    private final static int MESSAGE_READ = 2;
 
    public ConnectedThread(BluetoothSocket socket, BluetoothService service, Handler mHandler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.service = service;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        	Log.e(this.getClass().getSimpleName(), "Error obteniendo los streams", e);
        }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        
        this.mHandler = mHandler;
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI activity
				mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
            	Log.e(this.getClass().getSimpleName(), "desconectado", e);
				service.connectionLost();
            	Log.e(this.getClass().getSimpleName(), "Error leyendo el mensaje", e);
                break;
            }
        }
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        	Log.e(this.getClass().getSimpleName(), "Error escribiendo", e);
        }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        	Log.e(this.getClass().getSimpleName(), "Error Cancelando", e);
        }
    }
}
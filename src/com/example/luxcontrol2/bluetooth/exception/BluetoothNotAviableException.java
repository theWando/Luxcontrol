package com.example.luxcontrol2.bluetooth.exception;

import android.content.res.Resources;

import com.example.luxcontrol2.R;

public class BluetoothNotAviableException extends BluetoothException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4342927764759965267L;
	
	private static String message;
	
	static {
		message = Resources.getSystem().getString(R.string.BLUETOOTH_NOT_AVIABLE).toString();
	}

	public BluetoothNotAviableException() {
		super(message);
	}

	public BluetoothNotAviableException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

	public BluetoothNotAviableException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public BluetoothNotAviableException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}
	
}

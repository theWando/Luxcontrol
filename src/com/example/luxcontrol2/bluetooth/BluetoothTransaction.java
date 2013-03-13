package com.example.luxcontrol2.bluetooth;

public class BluetoothTransaction {
	
	private static BluetoothTransaction instance = null;

	private BluetoothTransaction(){
		super();
	}
	
	public BluetoothTransaction getTransaction(){
		if (instance == null) {
			instance = new BluetoothTransaction();
		}
		return instance;
	}
	
	public void begin() {
		//TODO hacer implementación del inicio de la transaccion
	}
	
	public void execute() {
		//TODO hacer implementacion en la que se realiza la comunicación con el dispositivo 
	}
	
	public void end() {
		//TODO hacer implementación en la que se libere el dispositivo para hacertar nuevas conexcciones
	}
}

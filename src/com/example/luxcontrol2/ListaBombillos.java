package com.example.luxcontrol2;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.example.luxcontrol2.bluetooth.BluetoothService;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListaBombillos extends BluetoothService {
	
	ToggleButton botonBombillo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_bombillos);
//		List<Bombillo> bombillos = new ArrayList<Bombillo>();
//		for (int i = 0; i < 5; i++) {
//			Bombillo b = new Bombillo();
//			b.setNombre("Bombillo "+i+1);
//			b.setEstado(i%2==0);
//			bombillos.add(b);
//		}
		botonBombillo = (ToggleButton) findViewById(R.id.b1);
		botonBombillo.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.b1) {
			if (botonBombillo.isChecked()) { //Si esta encendido
				sendMessage("0");
			} else { //Si esta apagado
				sendMessage("1");
			}
		}
		
	}
}

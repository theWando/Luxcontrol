package com.example.luxcontrol2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.luxcontrol2.bluetooth.BluetoothService;

public class MainActivity extends BluetoothService  {
	
	private Button bluetoothBttn;
	private Intent listaDispositivos;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothBttn = (Button) findViewById(R.id.main_blue);
        bluetoothBttn.setOnClickListener(this);
        
		if(this.isOn()){
			cargarDispositivos();
		}
    }


	private void cargarDispositivos() {
		listaDispositivos = new Intent(this, DispositivosListados.class);
		startActivity(listaDispositivos);
		finish();
	}


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.main_blue) {
			this.turnOn();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			//se pregunta si se desea activar el bluetooth
			if (resultCode == RESULT_OK) {
				while(true){
					if(this.isOn()){
						break;
					}
				}
				cargarDispositivos();
			} else {
				callToast(R.string.BLUETOOTH_NOT_ENABLE);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}

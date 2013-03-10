package com.example.luxcontrol2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener  {
	
	private static final int REQUEST_ENABLE_BT = 3;
	private Button bluetoothBttn;
	private BluetoothAdapter mBluetoothAdapter;
	private Intent listaDispositivos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothBttn = (Button) findViewById(R.id.main_blue);
        bluetoothBttn.setOnClickListener(this);
//        Resources.getSystem().getString(R.string.bluetooth);
	     // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.BLUETOOTH_NOT_AVIABLE, Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if(mBluetoothAdapter.isEnabled()) {
        	cargarDispositivos();
        }

    }


	private void cargarDispositivos() {
		if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
			listaDispositivos = new Intent(this, DispositivosListados.class);
			startActivity(listaDispositivos);
			finish();
		}
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.main_blue) {
			Log.d(this.getClass().getSimpleName(), "Intentando activar Bluetooth");
			activarBluetooth();
		}
		
	}
    
	private void activarBluetooth() {
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
        	Toast.makeText(getApplicationContext(), R.string.BLUETOOTH_ALREADY_ENABLE, Toast.LENGTH_LONG).show();
        }
        while(true){
        	if(mBluetoothAdapter.isEnabled()){
        		break;
        	}
        }
        cargarDispositivos();
	}
}

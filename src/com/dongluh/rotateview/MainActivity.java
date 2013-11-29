package com.dongluh.rotateview;

import com.dongluh.rotateview.view.Rotatable;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Rotatable rotatable1 = (Rotatable) findViewById(R.id.rotatelayout1);
		Rotatable rotatable2 = (Rotatable) findViewById(R.id.rotatelayout2);
		Rotatable rotatable3 = (Rotatable) findViewById(R.id.rotatelayout3);
		Rotatable rotatable4 = (Rotatable) findViewById(R.id.rotatelayout4);

		rotatable1.setOrientation(90, false);
		rotatable2.setOrientation(180, false);
		rotatable3.setOrientation(270, false);
		rotatable4.setOrientation(90,  false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}

package com.example.triageii;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class NewPrescriptionActivity extends Activity {

	private Physician physician;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_prescription);
		File dir = new File(this.getApplicationContext().getFilesDir()
				.getPath());
		try {
			physician = new Physician(dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_prescription, menu);
		return true;
	}

	public void savePrescription(View view) throws FileNotFoundException {
		EditText prescriptiontext = (EditText) findViewById(R.id.editText1);
		String prescription = prescriptiontext.getText().toString();

		// save prescription
		physician.prescribe(prescription);

		Intent intent = new Intent(this, PhysicianActivity.class);
		startActivity(intent);
	}

	public void goBack(View view) {
		Intent intent = new Intent(this, PhysicianActivity.class);
		startActivity(intent);
	}

}
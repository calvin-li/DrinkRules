package com.example.drinkrules;

import android.app.Activity;
import android.os.Bundle;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		MainActivity.customTitle(this, R.layout.activity_info);

	}

}

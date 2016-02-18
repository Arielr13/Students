package com.ArielR.students;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SignUpAsSchoolInfo extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_school_info);
	}
	
	public void proceed(View v){
		startActivity(new Intent(this, SignUpAsSchool.class));
		finish();
	}
	
	//Open Splash
	@Override
	public void onBackPressed() {
		startActivity(new Intent(SignUpAsSchoolInfo.this, Splash.class));
		finish();
	}
}

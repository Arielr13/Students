package com.ArielR.students;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Set content view to a WebView
		WebView web = new WebView(this);
		setContentView(web);
		
		String url = getIntent().getExtras().getString("url");
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.getSettings().setLoadWithOverviewMode(true);
	    web.getSettings().setUseWideViewPort(true);
	    web.loadUrl(url);
	    //URL Handling
	    web.setWebViewClient(new WebViewClient() {
	        public boolean shouldOverrideUrlLoading(WebView view, String url){
	        	if(url.equals("http://payment-success-url/")){
	        		Log.d("PayPal Payment","Successful Payment");
	        		Intent successfulPayment = new Intent(WebViewActivity.this,ViewList.class);
	        		successfulPayment.putExtra("isSchool",false);
	        		startActivity(successfulPayment);
	        	}
	        	else if(url.equals("http://payment-cancel-url/")){
	        		Log.d("PayPal Payment","Failed Payment");
	        		Intent backToDonate = new Intent(WebViewActivity.this,Donate.class);
	        		backToDonate.putExtra("schoolid",getIntent().getExtras().getString("schoolid"));
	        		startActivity(backToDonate);
	        	}
	            return false; 
	       }
	    });
		
	}
}

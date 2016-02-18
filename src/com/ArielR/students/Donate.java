package com.ArielR.students;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Donate extends Activity {

	String schoolEmail;
	TextView error;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate);
		
		//TODO Fix This -- Bad Practice
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		//Get School
		String schoolId = getIntent().getExtras().getString("schoolid");
		ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
	    query.whereEqualTo("objectId",schoolId);
	    query.findInBackground(new FindCallback<ParseUser>() {
	        public void done(List<ParseUser> objects, ParseException e) {
	            if (e == null) {
	            	schoolEmail = objects.get(0).getString("email");
	            	String schoolName = objects.get(0).getString("name");
	            	TextView title = (TextView)findViewById(R.id.donateTitle);
	            	title.setText("Donate to " +schoolName);
	            } 
	            else {
	                //TODO handle errors
	            }
	        }
	    });
	}
	
	public void donate(View v){
		//Get Data
		EditText amountEdit = (EditText)findViewById(R.id.donateAmount);
		EditText memoEdit = (EditText)findViewById(R.id.donateMemo);
		error = (TextView)findViewById(R.id.donateError);
		String amountString = amountEdit.getText().toString().trim();
		Double amount;
		if(amountString.equals("")||Pattern.matches("[a-zA-Z]+", amountString) == true){
			error.setText("Please Enter a Valid Message");
		}
		else{
			amount = Double.parseDouble(amountString);
			String memo = memoEdit.getText().toString().trim();
			if(amount!=null&&!(schoolEmail.equals(""))){
				Log.d("Students Payment","Send to PayPal");
				try {
					processPayment(amount, memo, schoolEmail);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void processPayment(double amount, String memo, String schoolEmail) throws IOException{
		//Set up JSON body
		String payload = "{\n" + 
				"\"actionType\":\"PAY\",    \n" + 
				"\"currencyCode\":\"USD\",  \n" + 
				"\"receiverList\":{\"receiver\":[{\n" + 
				"\"amount\":\""+amount+"\",                    \n" + 
				//"\"email\":\""+schoolEmail+"\"}]  \n" +
				"\"email\":\""+"ariel.rakovitsky-facilitator@gmail.com"+"\"}]  \n" +
				"},\n" + 
				"\n" + 
				"\n" + 
				"\"returnUrl\":\"http://Payment-Success-URL\",\n" + 
				"\"feesPayer\":\"SENDER\",\n" + 
				"\n" + 
				"\"memo\":\""+memo+" -- Donation via Students App"+"\",\n" + 
				"\n" + 
				"\n" + 
				"\"cancelUrl\":\"http://Payment-Cancel-URL\",\n" + 
				"\"requestEnvelope\":{\n" + 
				"\"errorLanguage\":\"en_US\",    \n" + 
				"\"detailLevel\":\"ReturnAll\"   \n" + 
				"}\n" + 
				"}\n";
		
	    //Set up URL
	    URL url = new URL("https://svcs.sandbox.paypal.com/AdaptivePayments/Pay");
	    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	    
	    urlConnection.setUseCaches(false);
	    urlConnection.setDoInput(true);
	    urlConnection.setDoOutput(true);
       
	    //Headers
        urlConnection.setRequestProperty("X-PAYPAL-SECURITY-USERID","ariel.rakovitsky-facilitator_api1.gmail.com");
        urlConnection.setRequestProperty("X-PAYPAL-SECURITY-PASSWORD","4ZFZNQCFJKJUZATX");
        urlConnection.setRequestProperty("X-PAYPAL-SECURITY-SIGNATURE","AFcWxV21C7fd0v3bYYYRCpSSRl31AwkTzYCO-4q8vj0z5cdImXIO89Y4");
        urlConnection.setRequestProperty("X-PAYPAL-APPLICATION-ID","APP-80W284485P519543T");
        urlConnection.setRequestProperty("X-PAYPAL-REQUEST-DATA-FORMAT","JSON");
        urlConnection.setRequestProperty("charset", "utf-8");
        urlConnection.setRequestProperty("Content-Type", "application/json"); 
        urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(payload.getBytes().length));
        
        //Add JSON object to POST object
        urlConnection.setRequestMethod("POST");
        OutputStream out = urlConnection.getOutputStream();
        out.write(payload.getBytes("UTF-8"));

	    //Read call results
	    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
		String inString = s.hasNext() ? s.next() : "";
		if(inString.contains("\"ack\":\"Success\"")){
			Log.d("PayPall Call","Success");
			handlePayment(inString);
		}
		else{
			Log.d("PayPall Call","Error\n"+inString);
			error.setText("Something Went Wrong :( - Please Contact the School");
		}
	}
	
	public void handlePayment(String inString){
		//"payKey": "Your-payKey"
		String payKey1 = inString.substring(inString.indexOf("payKey\":"),inString.indexOf("\",\"paymentExecStatus"));
		String payKey2 = payKey1.substring(payKey1.indexOf(":"),payKey1.length());
		String payKey = payKey2.substring(payKey2.indexOf("\"")+1,payKey2.length());
		Log.d("payKey",payKey);
		//Bring up webview and redirect to PayPal
		
		Intent displayWebPage = new Intent(Donate.this, WebViewActivity.class);
		displayWebPage.putExtra("url","https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-payment&paykey="+payKey+"&expType=mini");
		displayWebPage.putExtra("schoolid",getIntent().getExtras().getString("schoolid"));
		startActivity(displayWebPage);
	}
}

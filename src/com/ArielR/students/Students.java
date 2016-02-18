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
import java.util.Map;

import org.json.JSONObject;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import android.app.Application;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

public class Students extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		
		//Initialize Parse
		Parse.enableLocalDatastore(this);
		Parse.initialize(this);
	}
}

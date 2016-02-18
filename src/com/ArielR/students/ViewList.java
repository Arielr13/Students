package com.ArielR.students;

import java.util.List;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ViewList extends Activity {

	ParseUser school;
	List<ParseObject> masterList;
	ListView listView;
	ListAdapter listAdapter = new ListAdapter();
	String schoolId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_list);
		final TextView title = (TextView)findViewById(R.id.viewListTitle);
		final ImageView schoolImage = (ImageView)findViewById(R.id.viewListImage);
		final ImageButton settingsButton = (ImageButton)findViewById(R.id.viewListSettings);
		Button viewSchoolButton = (Button)findViewById(R.id.viewListViewSchool);
		Button donateButton = (Button)findViewById(R.id.viewListDonate);
		listView = (ListView)findViewById(R.id.viewListListView);
		
		Boolean isSchoolView = getIntent().getExtras().getBoolean("isSchool");
		if(!isSchoolView){
			//Set Up General Page
			viewSchoolButton.setVisibility(View.GONE);
			donateButton.setVisibility(View.GONE);
			schoolImage.setVisibility(View.INVISIBLE);
			title.setText("Recent Stories");
			getStories(null);
		}
		else{
			//Set Up School View Stories
			schoolId = getIntent().getExtras().getString("schoolid");
			//If school is viewing
			if(ParseUser.getCurrentUser().getObjectId().equals(schoolId)){
	        	donateButton.setText("Add New Story");
	        }
			ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
		    query.whereEqualTo("objectId",schoolId);
		    query.findInBackground(new FindCallback<ParseUser>() {
		        public void done(List<ParseUser> objects, ParseException e) {
		            if (e == null) {
		            	//Get All Data
		                school = objects.get(0);
		                //Display Data
		                title.setText(school.getString("name"));
		                //Display Image
		                ParseFile imageFile = (ParseFile)school.get("image");
		                imageFile.getDataInBackground(new GetDataCallback() {
		                  public void done(byte[] data, ParseException e) {
		                    if (e == null) {
		                    	Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		    	        	    schoolImage.setImageBitmap(bmp);
		    	        	    getStories(school);
		                    } else {
		                      //TODO handle errors
		                    }
		                  }
		                }); 
		            } else {
		                //TODO handle errors
		            }
		        }
		    });
		}	
	}
	
	public void viewSchool(View v){
		Intent moveToSchool = new Intent(ViewList.this, SchoolView.class);
		moveToSchool.putExtra("schoolid", school.getObjectId());
		startActivity(moveToSchool);
	}
	
	public void donate(View v){
		if(ParseUser.getCurrentUser().getObjectId().equals(schoolId)){
        	addNew();
        }
		else{
			Intent moveToDonate = new Intent(ViewList.this, Donate.class);
			moveToDonate.putExtra("schoolid", school.getObjectId());
			startActivity(moveToDonate);
		}
	}
	
	public void addNew(){
		Intent moveToSchool = new Intent(ViewList.this, AddNew.class);
		moveToSchool.putExtra("storyid", "");
		startActivity(moveToSchool);
	}
	public void settings(View v){
    	LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
    	View popupView = layoutInflater.inflate(R.layout.settings_popout, null);
    	float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    	
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0); 
       
        TextView popupusername=(TextView)popupView.findViewById(R.id.PopupUsername);
        popupusername.setText(ParseUser.getCurrentUser().getString("firstName")+"");
        
        TextView popupLogout=(TextView)popupView.findViewById(R.id.PopupLogout);
        popupLogout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	ParseUser.getCurrentUser().logOut();
            	popupWindow.dismiss();
            	startActivity(new Intent(ViewList.this, Splash.class));
            }
        });

        TextView popupcancel=(TextView)popupView.findViewById(R.id.PopupCancel);
        popupcancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	popupWindow.dismiss();
            }
        });
	}
	
	public void getStories(ParseUser school){
		ParseQuery<ParseObject> query = new ParseQuery("Story");
		if(school!=null){
			query.whereEqualTo("schoolId", school.getObjectId());
		}
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> list, ParseException e) {
		        if (e == null&&list.size()>0) {
		        	masterList = list;
		        	listView.setAdapter(listAdapter);
		        	listView.setOnItemClickListener(new OnItemClickListener() {
	                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
	                    	Intent moveToStory = new Intent(ViewList.this, ViewStory.class);
			    			moveToStory.putExtra("storySchoolId", masterList.get(masterList.size()-(1+position)).getString("schoolId"));
			    			moveToStory.putExtra("storyId", masterList.get(masterList.size()-(1+position)).getObjectId());
			    			startActivity(moveToStory);
			    			if(ParseUser.getCurrentUser().getObjectId().equals(schoolId)){
			    	        	finish();
			    	        }
	                    }
	                });
		        }
		        else{
		        	//TODO handle no data
		        }
		    }
		});	
	}
	
	//ListView Adapter
	public class ListAdapter extends BaseAdapter{
		TextView description;
		TextView title;
		
		@Override
		public int getCount() {
			return masterList.size();
		}

		@Override
		public ParseObject getItem(int arg0) {
			return masterList.get(masterList.size()-(1+arg0));
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			if(arg1==null){
		        LayoutInflater inflater = (LayoutInflater) ViewList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        arg1 = inflater.inflate(R.layout.list_item, arg2,false);
		        description = (TextView)arg1.findViewById(R.id.itemDescription);
				title = (TextView)arg1.findViewById(R.id.itemTitle);
				//Get Image
				ParseObject story = getItem(arg0);
				if(story==null||(ParseFile)story.get("storyImage")==null){	
					Log.e("Picture not displayed at: ", story.getObjectId());
				}
				else{
					//Fill Image Via Alt. Function -- Eliminates issues of stacking images
					fillImage(arg1,story);	
				}
				description.setText(getItem(arg0).getString("description"));
				title.setText(getItem(arg0).getString("title"));
		    }
			return arg1;
		}
		
		public void fillImage(View arg1, ParseObject story){
			final ImageView image = (ImageView)arg1.findViewById(R.id.itemImage);
			story.getParseFile("storyImage").getDataInBackground(new GetDataCallback() {
	            public void done(byte[] data, ParseException e) {
		        	Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		    	    image.setImageBitmap(bmp);
	            }
	        });
		}
	}
}

package com.example.drinkrules;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class SubmitRuleActivity extends Activity {

	final ImageButton[] submitButtons = new ImageButton[3];
	final String[] iconNames = new String[]{
			"drawable/iconemail",
			"drawable/icontweet",
			"drawable/iconcancel",
		};//iconNames

	@SuppressLint("UseValueOf")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MainActivity.customTitle(this, R.layout.activity_submit_rule);
		
        //Submit buttons
        submitButtons[0] = (ImageButton) findViewById(R.id.submitEmail);
        submitButtons[1] = (ImageButton) findViewById(R.id.submitTweet);
        submitButtons[2] = (ImageButton) findViewById(R.id.submitCancel);
        //id are formatted main<row><column>

        
        //set buttons to change upon being pressed
        for(int i=0; i < iconNames.length; i++){
	        StateListDrawable states = new StateListDrawable();
	        states.addState(new int[] {android.R.attr.state_pressed},
	        	    getResources().getDrawable(getResources().getIdentifier(
	        	    		iconNames[i] + "pressed", null, getPackageName())));
	        states.addState(new int[] {},
	        	    getResources().getDrawable(getResources().getIdentifier(
	        	    		iconNames[i], null, getPackageName())));
	        submitButtons[i].setImageDrawable(states);
        }//for
        
        for (int i=0; i < submitButtons.length; i++) {
        	final int j = new Integer(i);
        	//Copy i to a final variable so the
        	//OnClickListener can use it
    		submitButtons[i].setOnClickListener(
    				new View.OnClickListener() {
    					@Override
    					public void onClick(View view) {
    						submitRule(j);
    					}
    		});
        }//for

	}//onCreate

	protected void submitRule(int i) {
		
		if(i == 0){
			
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
					new String[]{"drinkrules@thecreativeappco.com"});
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Rule Suggestion");
			
			this.startActivity(emailIntent);		
			
		}//if Email
		
		if(i == 1){

			Intent twitterIntent = findTwitterClient();
			
			if(twitterIntent == null){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder
					.setTitle("Error:")
					.setMessage("It looks like you don't have a Twitter app installed.")
					.setCancelable(true);
				
				alertDialogBuilder.setPositiveButton
						("Download Twitter",new DialogInterface.OnClickListener() {		
						public void onClick(DialogInterface dialog,int id) {						
							String APP_PNAME = "com.twitter.android";
							Intent SMIntent = new Intent(Intent.ACTION_VIEW, 
									Uri.parse("market://details?id=" + APP_PNAME));
							SubmitRuleActivity.this.startActivity(SMIntent);	
						}
					});//setPositiveButton
				alertDialogBuilder.setNeutralButton
						("Close",new DialogInterface.OnClickListener() {		
						public void onClick(DialogInterface dialog,int id) {						
							dialog.cancel();
						}
					});//setNegativeButton
				;//alertDialogBuilder				
					
				//create and show dialog
				alertDialogBuilder.create().show();
			}//if no Twitter client found
			
			else{
				twitterIntent.putExtra(android.content.Intent.EXTRA_TEXT, "@Rulebook_App");
				this.startActivity(twitterIntent);
			}//else
			
		}//if Tweet
		
		if(i == 2){
			finish();
		}//if Cancel
	}//submitRule

	//Checks, based on package name, for installed Twitter apps
	//Returns an Intent for the first match
	//Source: http://regis.decamps.info/blog/2011/06/
	//	intent-to-open-twitter-client-on-android/
	public Intent findTwitterClient() {
		final String[] twitterApps = {
			// package // name - nb installs (thousands)
			"com.twitter.android", // official - 10 000
			"com.twidroid", // twidroyd - 5 000
			"com.handmark.tweetcaster", // Tweecaster - 5 000
			"com.thedeck.android" // TweetDeck - 5 000 
		};
		
		Intent tweetIntent = new Intent();
		tweetIntent.setType("text/plain");
		
		final PackageManager packageManager = getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(
				tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
		 
		for (int j = 0; j < twitterApps.length; j++) {
			for (ResolveInfo resolveInfo : list) {
				String p = resolveInfo.activityInfo.packageName;
				if (p != null && p.startsWith(twitterApps[j])) {
					tweetIntent.setPackage(p);
					return tweetIntent;
				}//if
			}//for
		}//for
		return null;
	}//findTwitterClient

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.submit_rule, menu);
		return true;
	}

}

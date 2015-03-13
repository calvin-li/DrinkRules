package com.example.drinkrules;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity implements TextWatcher{

	boolean expanded = false, searching = false;
    
	final ImageButton[] menuGrid = new ImageButton[9];
	final ImageButton[] footerMenu = new ImageButton[6];
	final String[] iconNames = new String[]{
			"drawable/icon_dropdime",
			"drawable/icon_flipcup",
			"drawable/icon_movies",
			"drawable/icon_kings",
			"drawable/icon_cards",
			"drawable/icon_beerbomb",
			"drawable/icon_quarters",
			"drawable/icon_powerhour",
			"drawable/icon_beerpong"			
		};//iconNames
	final String[] footerNames = new String[]{
			"drawable/myrules",
			"drawable/search",
			"drawable/submitrule",
			"drawable/rateapp",
		};//iconNames
    //name of footer button icons, matches names @drawable/
	
	String[] gameNames = new String[]{
			"Drop the Dime",
			"Flip Cup",
			"TV/Movies",
			"Kings",
			"Cards",
			"Beer Bomb",
			"Quarters",
			"Power Hour",
			"Beer Pong"
	};//gameNames

	@SuppressLint("UseValueOf")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		customTitle(this, R.layout.main_activity);
		
		findViewById(R.id.menu_wrapper).requestFocus();
		//The searchbox was getting focus, so this fixes it.

        //Menu buttons
        menuGrid[0] = (ImageButton) findViewById(R.id.main00);
        menuGrid[1] = (ImageButton) findViewById(R.id.main01);
        menuGrid[2] = (ImageButton) findViewById(R.id.main02);
        menuGrid[3] = (ImageButton) findViewById(R.id.main10);
        menuGrid[4] = (ImageButton) findViewById(R.id.main11);
        menuGrid[5] = (ImageButton) findViewById(R.id.main12);
        menuGrid[6] = (ImageButton) findViewById(R.id.main20);
        menuGrid[7] = (ImageButton) findViewById(R.id.main21);
        menuGrid[8] = (ImageButton) findViewById(R.id.main22);
        //id are formatted main<row><column>
                
        //Footer buttons
        footerMenu[0] = (ImageButton) findViewById(R.id.myrules);
        footerMenu[1] = (ImageButton) findViewById(R.id.submitrule);
        footerMenu[2] = (ImageButton) findViewById(R.id.search);
        footerMenu[3] = (ImageButton) findViewById(R.id.rateapp);
        footerMenu[4] = (ImageButton) findViewById(R.id.shareButton);
        footerMenu[5] = (ImageButton) findViewById(R.id.legalInfo);
        //Buttons go from top to bottom, left to right
        
        //set buttons to change upon being pressed
        for(int i=0; i < iconNames.length; i++){
	        StateListDrawable states = new StateListDrawable();
	        states.addState(new int[] {android.R.attr.state_pressed},
	        	    getResources().getDrawable(getResources().getIdentifier(
	        	    		iconNames[i] + "_pressed", null, getPackageName())));
	        states.addState(new int[] {},
	        	    getResources().getDrawable(getResources().getIdentifier(
	        	    		iconNames[i], null, getPackageName())));
	        menuGrid[i].setImageDrawable(states);
        }//for
        
        //same thing, for footer buttons
        for(int i=0; i < footerNames.length; i++){
	        StateListDrawable states = new StateListDrawable();
	        states.addState(new int[] {android.R.attr.state_pressed},
	        	    getResources().getDrawable(getResources().getIdentifier(
	        	    		footerNames[i] + "_pressed", null, getPackageName())));
	        states.addState(new int[] {},
	        	    getResources().getDrawable(getResources().getIdentifier(
	        	    		footerNames[i], null, getPackageName())));
	        footerMenu[i].setImageDrawable(states);
        }//for
        
        for (int i=0; i < menuGrid.length; i++) {
        	final int j = new Integer(i);
        	//Copy i to a final variable so the
        	//OnClickListener can use it
    		menuGrid[i].setOnClickListener(
    				new View.OnClickListener() {
    					@Override
    					public void onClick(View view) {
    						openSubMenu(j);
    		 			}
    		});
        }//for
        
        //similar to the preceding loop, but for the footer buttons
        for (int i=0; i < footerMenu.length; i++) {
        	final int j = new Integer(i);
    		footerMenu[i].setOnClickListener(
    				new View.OnClickListener() {
    					@Override
    					public void onClick(View view) {
    						openfooter(j);
    					}
    		});
        }//for
            
        final EditText searchbox = (EditText) findViewById(R.id.searchbox);
        final ListView sR = (ListView) findViewById(R.id.searchResults);
        final ScrollView sv1 = (ScrollView) findViewById(R.id.scrollView1);

        searchbox.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    search();
                }//if
            }//onFocusChange
        });//setOnFocusChangeListener

        sv1.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchbox.getWindowToken(), 0);
                }//if
            }//onFocusChange
        });//setOnFocusChangeListener

        //set search cancel button
        ImageButton searchCancel = (ImageButton)findViewById(R.id.searchCancel);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed},
                getResources().getDrawable(R.drawable.searchcancel_pressed));
        states.addState(new int[] {},
                getResources().getDrawable(R.drawable.searchcancel));
        searchCancel.setImageDrawable(states);
        searchCancel.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						cancel();
		 			}
		});//On cancel button click

	}//OnCreate

	protected void search() {
        final EditText searchbox = (EditText) findViewById(R.id.searchbox);
        final ListView sR = (ListView) findViewById(R.id.searchResults);
        final LinearLayout subfooter = (LinearLayout) findViewById(R.id.subfooter);
        final RelativeLayout footer = (RelativeLayout) findViewById(R.id.footer);

        searching = true;

        //hide footer and subfooter
        subfooter.setVisibility(View.GONE);
        footer.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager)
        		getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchbox, InputMethodManager.SHOW_IMPLICIT);
        //show keyboard
        
        sR.setVisibility(View.VISIBLE);	//show list of results (starts empty)
        searchbox.addTextChangedListener(this);	//listens for text change

        //Locks the parent scrollView so only the ListView can scroll
        sR.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchbox.getWindowToken(), 0);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                }//switch

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }//onTouch
        });//setOnTouchListener
	}//search

    private void find(String query){
        final ListView sR = (ListView) findViewById(R.id.searchResults);
        final EditText searchbox = (EditText) findViewById(R.id.searchbox);
        final ArrayList<GameTitle> gameTitles = new ArrayList<GameTitle>();
        final ArrayList<String> fileUrl = new ArrayList<String>();

        try{
	    	JSONParser jParser = new JSONParser (this, R.raw.data);
	        JSONObject jObject = jParser.getJSONObject();

			for(int i = 0; i < gameNames.length; i++){
				JSONArray gameArray = jObject.getJSONArray(gameNames[i]);
				
				for(int j = 0; j < gameArray.length(); j++){
					JSONObject temp = gameArray.getJSONObject(j);
					String value = temp.getString("Title");
					if(check(value, query)){	//see if title matches search string
						String name = temp.getString("File").toLowerCase();
						String icon = temp.getString("Icon").toLowerCase()+"_small";
						
						int resID = getResources().getIdentifier(
								icon, "drawable", this.getPackageName());
						fileUrl.add(name + ".html");
						gameTitles.add(new GameTitle(resID, value));
					}//if
				}//for
			}//for
			
			GameAdapter adapter = new GameAdapter (this, R.layout.listview_item_row, gameTitles);
			
		    sR.setAdapter(adapter);
			
			sR.setTextFilterEnabled(true);
			sR.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
                    Intent newActivity = new Intent (MainActivity.this, RulesPage.class);
                    newActivity.putExtra("urlFile", fileUrl.get(position));
                    newActivity.putExtra("gameSelected", gameTitles.get(position).title);
                    newActivity.putExtra("icon",
                            Integer.toString(gameTitles.get(position).icon));
                    startActivity(newActivity);
				}
			});//OnClick
						
		}//try
	    
	    catch (JSONException e) {
			Log.d("SubMenuActivity", "Error in putting data into Map!");
			e.printStackTrace();
		}//catch

        searchbox.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        Intent newActivity = new Intent (MainActivity.this, RulesPage.class);
                        newActivity.putExtra("urlFile", fileUrl.get(0));
                        newActivity.putExtra("gameSelected", gameTitles.get(0).title);
                        startActivity(newActivity);
                        return true;
                    }//onEditorAction
                });//onEditorAction
        //set behavior on "enter" key

	}//find

    protected void cancel() {
        //show footer and subfooter
        final LinearLayout subfooter = (LinearLayout) findViewById(R.id.subfooter);
        final RelativeLayout footer = (RelativeLayout) findViewById(R.id.footer);
        final EditText searchbox = (EditText) findViewById(R.id.searchbox);
        final ListView sR = (ListView) findViewById(R.id.searchResults);
        final ScrollView sv1 = (ScrollView) findViewById(R.id.scrollView1);

        searching = false;

        subfooter.setVisibility(View.VISIBLE);
        footer.setVisibility(View.VISIBLE);

        sR.setVisibility(View.INVISIBLE);
        InputMethodManager imm = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchbox.getWindowToken(), 0);
        sv1.post(new Runnable() {
            @Override
            public void run(){
                sv1.smoothScrollTo(0, View.FOCUS_DOWN);
            }
        });//post
        //hide search results, hide keyboard, scroll down

        findViewById(R.id.menu_wrapper).requestFocus();
    }//cancel

    //Override the onKeyDown method
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
            if(searching == true)
                cancel();
            else
                finish();
        return true;
    }//Change Back button behavior

    private boolean check(String value, String query) {
		value = " " + value;
		value = value.toLowerCase();
		query = query.toLowerCase();
		return value.matches(".* " + query + ".*");
	}

	//onWindowFocusChanged is called after the layuot is drawn. Then, the menu grid
	//is too short, so we change its height. Afterwards, sv1.post() scrolls the view
	//up to hide the search bar. 
	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		
        final LinearLayout menu_grid = (LinearLayout) findViewById(R.id.menu_grid);
        EditText searchbox = (EditText) findViewById(R.id.searchbox);
        final ScrollView sv1 = (ScrollView) findViewById(R.id.scrollView1);
        final ListView sR = (ListView) findViewById(R.id.searchResults);
        
        //vars are final so they can be used in post
		if(expanded == false){
	        // the height will be set at this point
	        
			LayoutParams menuParams = menu_grid.getLayoutParams();
			LayoutParams sRParams = sR.getLayoutParams();
	        
	        int mgHeight = menu_grid.getMeasuredHeight();
	        int sbHeight = searchbox.getMeasuredHeight();
	        
	        //sRParams.height = mgHeight;
	        mgHeight += sbHeight + 8;
	        menuParams.height = mgHeight;
	        
	        expanded = true;	        
		}//if
				
		sv1.post(new Runnable() {
			@Override
			public void run(){
				sv1.scrollTo(0, View.FOCUS_DOWN);
			}
		});//post
		
	}//OnWindowFocusChanged
	
	@Override
	protected void onResume() {
		super.onResume();		
	}//onResume
	
	//Creates the title bar. Static method can be used elsewhere.
	public static void customTitle(Activity a, int ResID){
	    a.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		a.setContentView(ResID);
	    a.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
	}//customTitle
	
	private void openSubMenu(int i) {
		
		Intent SMIntent = new Intent(MainActivity.this, SubMenuActivity.class);
		SMIntent.putExtra("gametype", gameNames[i]);
		MainActivity.this.startActivity(SMIntent);				
		
	}
	
	protected void openfooter(int i) {
		if(i == 0){
			Intent SMIntent = new Intent(MainActivity.this, MyRulesActivity.class);
			MainActivity.this.startActivity(SMIntent);				
		}//if (My rules)
		
		if(i == 1){
			//scroll up tp hise search bar
	        ((ScrollView)findViewById(R.id.scrollView1)).smoothScrollTo(0, 0);
	        //focus on search box
	        findViewById(R.id.searchbox).requestFocus();
		}//if (Search)
		
		if(i == 2){
			Intent SMIntent = new Intent(MainActivity.this, SubmitRuleActivity.class);
			MainActivity.this.startActivity(SMIntent);				
		}//if (Submit Rule)
		
		//takes user to webpage to rate app
		if(i == 3){
			String APP_PNAME = "com.theCreativeAppCo.Eminem";//package name. Using a filler until app is published
			Intent SMIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME));
			MainActivity.this.startActivity(SMIntent);	
		}//if (Rate)
		
		if(i == 4){
			Intent shareIntent = new Intent (android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
			String shareMessage = "@Rulebook_App";
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
		    startActivity(Intent.createChooser(shareIntent, "Share Options"));				
		}//if (Share)
		
		if(i == 5){
			Intent SMIntent = new Intent(MainActivity.this, InfoActivity.class);
			MainActivity.this.startActivity(SMIntent);				
		}//if (Info)
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}//onCreateOptionsMenu

	@Override
	public void afterTextChanged(Editable arg0) {
        final EditText searchbox = (EditText) findViewById(R.id.searchbox);
        find(searchbox.getText().toString());
	}

	//These methods aren't used, but are implemented as part of the interface
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
}

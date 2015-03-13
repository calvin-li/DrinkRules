package com.example.drinkrules;

import java.io.Console;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.*;

@SuppressLint("DefaultLocale")
public class SubMenuActivity extends Activity {
    static boolean paid = false;

    final ImageButton[] menuGrid = new ImageButton[10];
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

    AdView banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*menuGrid[0] = (ImageButton) findViewById(R.id.homeButton);
        menuGrid[1] = (ImageButton) findViewById(R.id.ImageButton1);
        menuGrid[2] = (ImageButton) findViewById(R.id.ImageButton2);
        menuGrid[3] = (ImageButton) findViewById(R.id.ImageButton3);
        menuGrid[4] = (ImageButton) findViewById(R.id.ImageButton4);
        menuGrid[5] = (ImageButton) findViewById(R.id.ImageButton5);
        menuGrid[6] = (ImageButton) findViewById(R.id.ImageButton6);
        menuGrid[7] = (ImageButton) findViewById(R.id.ImageButton7);
        menuGrid[8] = (ImageButton) findViewById(R.id.ImageButton8);
        menuGrid[9] = (ImageButton) findViewById(R.id.ImageButton9);*/


        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_sub_menu);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.listview_header_row);

        //remove ads if paid
        if(paid == true){
            banner = (AdView) findViewById(R.id.ad);
            ((LinearLayout)findViewById(R.id.outerView)).removeView(banner);
        }//if

        String currentGame = this.getIntent().getExtras().getString("gametype");
               
        TextView tV = (TextView) this.findViewById(R.id.txtHeader);
		tV.setText(currentGame);
        findViewById(R.id.faveButton).setVisibility(View.INVISIBLE);

        final ListView listview = (ListView) findViewById(R.id.listview);
        
        try 
        {
        	JSONParser jParser = new JSONParser (this, R.raw.data);
            JSONObject jObject = jParser.getJSONObject();
			
			
			JSONArray gameArray = jObject.getJSONArray(currentGame);
			
			final ArrayList<GameTitle> gameTitles = new ArrayList<GameTitle>(gameArray.length());
			final ArrayList<String> fileUrl = new ArrayList<String>(gameArray.length());

			for(int i = 0; i < gameArray.length(); i++)
			{
				JSONObject temp = gameArray.getJSONObject(i);
				String value = temp.getString("Title");
				String name = temp.getString("File").toLowerCase();
				String icon = temp.getString("Icon").toLowerCase()+"_small";
				
				int resID = getResources().getIdentifier(icon, "drawable", this.getPackageName());
				fileUrl.add(name + ".html");
				gameTitles.add(new GameTitle(resID, value));
			}
			
			gameTitles.add(new GameTitle(R.drawable.icondontseeit, "Have a suggestion?"));
			GameAdapter adapter = new GameAdapter (this, R.layout.listview_item_row, gameTitles);
			
		    listview.setAdapter(adapter);
			
			listview.setTextFilterEnabled(true);
			listview.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
                    Intent newActivity;
                    if(position == listview.getCount() - 1){
                        newActivity = new Intent (SubMenuActivity.this, SubmitRuleActivity.class);
                    }//if
                    else{
                        newActivity = new Intent (SubMenuActivity.this, RulesPage.class);
                        newActivity.putExtra("urlFile", fileUrl.get(position));
                        newActivity.putExtra("gameSelected", gameTitles.get(position).title);
                        newActivity.putExtra("icon",
                                Integer.toString(gameTitles.get(position).icon));
                    }//else
                        startActivity(newActivity);
				}//onClick
			});
						
		}  catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("SubMenuActivity", "Error in putting data into Map!");
			e.printStackTrace();
		}


        ImageButton shButton = (ImageButton) this.findViewById(R.id.shareButtonSubMenu);
        shButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent shareIntent = new Intent (android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
				String shareMessage = "@Rulebook_App";
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
			    startActivity(Intent.createChooser(shareIntent, "Share Options"));
			}
		} );
        
        final ImageButton popButton = (ImageButton) this.findViewById(R.id.popUpButton);
        final HorizontalScrollView sV = (HorizontalScrollView) findViewById(R.id.menuscrollview);

       // this.addContentView(sV, null);
        popButton.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if(sV.getVisibility() == View.GONE){
                    popButton.setImageResource(R.drawable.footerpopdown);
                    sV.setVisibility(View.VISIBLE);
                }
                else{
                    popButton.setImageResource(R.drawable.footerpopup);
                    sV.setVisibility(View.GONE);
                }
			}
		} );

        ((Button)findViewById(R.id.NoAds)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        paid=true;
        }});//onClick

    }//onCreate

    private void openSubMenu(int i) {

        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", gameNames[i]);
        this.startActivity(SMIntent);

    }

    //On click event for button1
    public void button1Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, MainActivity.class);
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button2Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Drop the Dime");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button3Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Flip Cup");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button4Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "TV/Movies");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button5Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Kings");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button6Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Cards");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button7Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Beer Bomb");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button8Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Quarters");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button9Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Power Hour");
        startActivity(SMIntent);
    }

    //On click event for button1
    public void button10Response(View v) {
        //Inform the user the button has been clicked
        Intent SMIntent = new Intent(this, SubMenuActivity.class);
        SMIntent.putExtra("gametype", "Beer Pong");
        startActivity(SMIntent);
    }

    @SuppressLint("NewApi")
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);

        return true;
    }
    
}

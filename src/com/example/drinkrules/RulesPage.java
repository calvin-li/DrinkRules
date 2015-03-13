package com.example.drinkrules;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class RulesPage extends Activity{
    AdView banner;

    ArrayList<String> faveList = new ArrayList<String>();
    HashMap<String, String[]> favorites = new HashMap<String, String[]>(faveList.size());
    final String dataFile = "favorites";
    final String listFile = "faves list";
    boolean isFave = true;
    BackupManager bm = new BackupManager(this);
    static final Object lock = new Object();
    // Object for intrinsic lock because backup is not threadsafe

    String url;
    String currentGame;
    String icon;

    ImageButton faveButton = null;
    TextView tV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.rules_page);

        //remove ads if paid
        if(SubMenuActivity.paid == true){
            banner = (AdView) findViewById(R.id.ad);
            ((LinearLayout)findViewById(R.id.outerView)).removeView(banner);
        }//if

        url = this.getIntent().getExtras().getString("urlFile");
        currentGame = this.getIntent().getExtras().getString("gameSelected");
        icon = this.getIntent().getExtras().getString("icon");
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.listview_header_row);
        tV = (TextView) this.findViewById(R.id.txtHeader);
        tV.setText(currentGame);
        faveButton = (ImageButton)findViewById(R.id.faveButton);

		/*
         * The following block of code creates a WebView,
         * appends the name of the corresponding .HTML page,
         * and loads it onto the WebView.
         */
        WebView wV = (WebView) this.findViewById(R.id.webview);

        // All the .HTML documents are located in the assets folder
        wV.loadUrl("file:///android_asset/" + url);

        ImageButton shButton = (ImageButton) this.findViewById(R.id.shareButtonRulesPage);
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

        try{
            synchronized(lock){
                ObjectInputStream in;
                in = new ObjectInputStream(openFileInput(listFile));
                faveList = (ArrayList<String>)in.readObject();
            }//lock
        } catch (IOException i){
            Log.d("RulesPage", "IOException while getting faveList");
        } catch(ClassNotFoundException c){
            Log.d("RulesPage", "ClassNotFoundException while getting faveList");
        }//catch

        if(faveList.contains(currentGame))
            fadeFaveButton();
        else
            resetFaveButton();

        faveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFaves();
        }});//onClick

        ((Button)findViewById(R.id.NoAds)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SubMenuActivity.paid=true;
        }});//onClick

    }//onCreate

    private void changeFaves() {

        //get hash map
        try{
            synchronized(lock){
                FileInputStream fis = openFileInput(dataFile);
                ObjectInputStream in;
                in = new ObjectInputStream( new BufferedInputStream(fis));
                favorites = (HashMap<String, String[]>)in.readObject();
            }//lock
        } catch (IOException i){
            Log.d("RulesPage", "IOException while getting favorites");
        } catch(ClassNotFoundException c){
            Log.d("RulesPage", "ClassNotFoundException while getting favorites");
        }//catch

        if(isFave == false){
            favorites.put(currentGame, new String[]{url, icon});
            //add currentGame and sort faveList
            faveList.add(currentGame);
            Collections.sort(faveList);
            fadeFaveButton();
        }//if
        else if (isFave == true){
            favorites.remove(currentGame);
            faveList.remove(currentGame);
            resetFaveButton();
        }//else if

        try{
            synchronized(lock){
                ObjectOutputStream out;
                out = new ObjectOutputStream(openFileOutput(dataFile, Context.MODE_PRIVATE));
                out.writeObject(favorites);
            }//lock
        } catch (IOException i){
            Log.d("MyRulesActivity", "IOException while writing favorites");
        }//catch
        try{
            synchronized(lock){
                ObjectOutputStream out;
                out = new ObjectOutputStream(openFileOutput(listFile, Context.MODE_PRIVATE));
                out.writeObject(faveList);
            }//lock
        } catch (IOException i){
            Log.d("MyRulesActivity", "IOException while writing faveList");
        }//catch

        bm.dataChanged();
    }//changeFaves

    private void fadeFaveButton() {
        isFave = true;
        faveButton.setImageResource(R.drawable.icon_subtractfavorite);
    }//fadeFaveButton

    private void resetFaveButton() {
        isFave = false;
        faveButton.setImageResource(R.drawable.icon_addfavorite);
    }//resetFaveButton

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }//onCreateOptionsMenu

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






}//RulesPage

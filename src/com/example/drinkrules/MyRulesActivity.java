package com.example.drinkrules;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MyRulesActivity extends Activity {
    ArrayList<String> faveList = new ArrayList<String>();
    HashMap<String, String[]> favorites = new HashMap<String, String[]>(faveList.size());
    BackupManager bm = new BackupManager(this);
    static final Object lock = new Object();
    // Object for intrinsic lock because backup is not threadsafe

    static final String dataFile = "favorites";
    static final String listFile = "faves list";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_sub_menu);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.listview_header_row);

        TextView tV = (TextView) findViewById(R.id.txtHeader);
        tV.setText("My Rules");
        findViewById(R.id.faveButton).setVisibility(View.INVISIBLE);
    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();

        try{
            synchronized (lock){
                ObjectInputStream in;
                in = new ObjectInputStream(openFileInput(listFile));
                faveList = (ArrayList<String>)in.readObject();
            }//lock
        } catch (IOException i){
            Log.d("MyRulesActivity", "IOException while getting faveList");
        } catch(ClassNotFoundException c){
            Log.d("MyRulesActivity", "ClassNotFoundException while getting faveList");
        }//catch

        try{
            synchronized (lock){
                ObjectInputStream in;
                in = new ObjectInputStream(openFileInput(dataFile));
                favorites = (HashMap<String, String[]>)in.readObject();
            }//lock
        } catch (IOException i){
            Log.d("MyRulesActivity", "IOException while getting favorites");
        } catch(ClassNotFoundException c){
            Log.d("MyRulesActivity", "ClassNotFoundException while getting favorites");
        }//catch

        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayList<GameTitle> gameTitles = new ArrayList<GameTitle>(faveList.size());
        final ArrayList<String> fileUrl = new ArrayList<String>(faveList.size());

        for(int i=0; i < faveList.size(); i++){
            String gameName = faveList.get(i);
            String[] gameInfo = favorites.get(gameName);
            fileUrl.add(gameInfo[0]);
            gameTitles.add(new GameTitle(Integer.parseInt(gameInfo[1]), gameName));
        }//for

        final GameAdapter adapter = new GameAdapter (this, R.layout.listview_item_row, gameTitles);
        listview.setAdapter(adapter);
        listview.setTextFilterEnabled(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent newActivity;

                newActivity = new Intent (MyRulesActivity.this, RulesPage.class);
                newActivity.putExtra("urlFile", fileUrl.get(position));
                newActivity.putExtra("gameSelected", gameTitles.get(position).title);
                newActivity.putExtra("icon",
                        Integer.toString(gameTitles.get(position).icon));

                startActivity(newActivity);
        }});//setOnClickListener
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        ListviewSwipeListener touchListener =
                new ListviewSwipeListener(
                        listview,
                        new ListviewSwipeListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    GameTitle GT = adapter.getItem(position);
                                    String title = GT.title;
                                    adapter.remove(GT);
                                    faveList.remove(title);
                                    favorites.remove(title);
                                    pushFave();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        listview.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listview.setOnScrollListener(touchListener.makeScrollListener());

    }//onResume

    protected void pushFave(){

        try{
            synchronized (lock){
                ObjectOutputStream out;
                out = new ObjectOutputStream(openFileOutput(dataFile, Context.MODE_PRIVATE));
                out.writeObject(favorites);
            }//lock
        } catch (IOException i){
            Log.d("MyRulesActivity", "IOException while writing favorites");
        }//catch
        try{
            synchronized (lock){
                ObjectOutputStream out;
                out = new ObjectOutputStream(openFileOutput(listFile, Context.MODE_PRIVATE));
                out.writeObject(faveList);
            }//lock
        } catch (IOException i){
            Log.d("MyRulesActivity", "IOException while writing faveList");
        }//catch

        bm.dataChanged();
    }//pushFave

}//MyRules

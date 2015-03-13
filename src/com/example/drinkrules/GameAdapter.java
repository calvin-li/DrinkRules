package com.example.drinkrules;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GameAdapter extends ArrayAdapter<GameTitle>
{
    Context context;
    int layoutResourceId;    
    ArrayList<GameTitle> data = null;

    boolean unFaveButton = false;
    MyRulesActivity myRules = null;

    public GameAdapter(Context context, int layoutResourceId,  ArrayList<GameTitle> data)
    {
    	super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }//normal constructor

    public GameAdapter(MyRulesActivity context, int layoutResourceId,  ArrayList<GameTitle> data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        unFaveButton = true;
        myRules = context;
    }//constructor for MyRulesActivity

    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
    	View row = convertView;
        GameHolder holder;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new GameHolder();
            holder.faveIcon = (ImageButton)row.findViewById(R.id.unFaveButton);
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        }
        else
        {
            holder = (GameHolder)row.getTag();
        }
        
        final GameTitle game = data.get(position);
        if(unFaveButton){
            holder.faveIcon.setVisibility(View.VISIBLE);
            holder.faveIcon.setFocusable(false);
            holder.faveIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(game);
                    myRules.faveList.remove(game.title);
                    myRules.favorites.remove(game.title);
                    myRules.pushFave();
            }});//onClick
        }//if
        holder.txtTitle.setText(game.title);
        holder.imgIcon.setImageResource(game.icon);
        
        return row;
    }
    
    static class GameHolder
    {
        ImageButton faveIcon;
        ImageView imgIcon;
        TextView txtTitle;
    }
}

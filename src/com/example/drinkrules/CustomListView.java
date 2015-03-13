package com.example.drinkrules;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomListView extends ListView {
	public CustomListView (Context context) {
	    super(context);
	}//constructor

	public CustomListView (Context context, AttributeSet attrs) {
	    super(context, attrs);
	}//constructor

	public CustomListView (Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}//constructor

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
	    super.onSizeChanged(xNew, yNew, xOld, yOld);

	    post(new Runnable() {
	        public void run() {
	            setSelection(getCount()-1);
	        }
	    });
	}//onSizeChanged
}

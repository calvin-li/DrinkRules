package com.example.drinkrules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;
import android.app.Activity;

public class JSONParser {

	private StringBuilder stringData;
	private BufferedReader streamReader;
	
	public JSONParser(Activity activityObject, int resourceID)
	{
		InputStream iS = activityObject.getResources().openRawResource(resourceID);
	    streamReader = new BufferedReader (new InputStreamReader(iS));
	    stringData = new StringBuilder();
	    initializeJSONFileData();
	}
	
	private void initializeJSONFileData()
	{
		String inputStr = null;
		
		try {
			while((inputStr = streamReader.readLine()) != null)
				stringData.append(inputStr);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getJSONFileData()
	{
		return stringData.toString();
	}
	
	public JSONObject getJSONObject() 
	{
		try {
			return new JSONObject(getJSONFileData());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

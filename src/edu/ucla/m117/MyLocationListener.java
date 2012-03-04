package edu.ucla.m117;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class MyLocationListener implements LocationListener
{
	Context appContext;
	EventDataSQLHelper eventsData;
	String imei;
	MyLocationListener(Context context,String imeiM)
	{
		super();
		appContext=context;
		imei = imeiM; 
		eventsData = new EventDataSQLHelper(context);
	}
  @Override
  public void onLocationChanged(Location loc)
  {

    loc.getLatitude();
    loc.getLongitude();
    
    String Text = "My current location is: " +
    "Latitud = " + loc.getLatitude() +
    "Longitud = " + loc.getLongitude() + "accuracy is "+loc.getAccuracy();
    if(Text!=null)
    	addGPSData(Text);
//    //Log.v("aditya", Text);
  }

  @Override
  public void onProviderDisabled(String provider)
  {
    Toast.makeText( appContext, "Gps Disabled", Toast.LENGTH_SHORT ).show();
  }

  @Override
  public void onProviderEnabled(String provider)
  {
    Toast.makeText( appContext, "Gps Enabled", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras)
  {

  }
  private  void addGPSData(String location)
  {
	  	Log.v("aditya", "inside addGSPDATA "+location);
	  	SQLiteDatabase db = eventsData.getWritableDatabase();
	  	
	    ContentValues values = new ContentValues();
	    Calendar calendar = Calendar.getInstance();

	    values.put(EventDataSQLHelper.USERID, imei);
	    values.put(EventDataSQLHelper.TIME, DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar.getTime()) + "");
	    values.put(EventDataSQLHelper.GPS,location);
	    db.insert(EventDataSQLHelper.TABLE2, null, values);

	  
  }
 
}
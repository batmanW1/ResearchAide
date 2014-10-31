package edu.upenn.med.researchaide;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DirectionActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direction);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.direction, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * start google map navigation mode to find the research location
	 */
	public void mapNavigation(String address) {
		String uri =String.format("google.navigation:q=" + address);
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(i);
	}

	public void onLocation1ButtonClick(View view) {
		mapNavigation("3400+Spruce+St+Philadelphia%2C+PA");
	}
}

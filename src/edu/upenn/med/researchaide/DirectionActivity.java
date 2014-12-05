package edu.upenn.med.researchaide;

import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Provides three locations for study visits and instantiates Google Maps
 * so user can get directions to the selected location.
 */
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

	/**
	 * Starts the Google Map navigation mode to find the selected research location.
	 * @param address The location to display in Google Maps.
	 */
	public void mapNavigation(String address) {
		String uri = String.format(Locale.US,
				"http://maps.google.com/maps?daddr=" + address);
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		i.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		startActivity(i);
	}

	public void onLocation1ButtonClick(View view) {
		mapNavigation("39.955481, -75.195363"); // "3615+Chestnut+St+Philadelphia%2C+PA"
	}

	public void onLocation2ButtonClick(View view) {
		mapNavigation("39.956093, -75.195561"); // 3624+Market+St+Philadelphia%2C+PA
	}

	public void onLocation3ButtonClick(View view) {
		mapNavigation("39.958468, -75.199437"); // 51+North+39th+St+Philadelphia%2C+PA
	}
}

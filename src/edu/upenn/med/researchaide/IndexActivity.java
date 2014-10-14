package edu.upenn.med.researchaide;

import android.app.Activity;
import android.os.Bundle;

public class IndexActivity extends Activity {
	
	private String username;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_index);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

}

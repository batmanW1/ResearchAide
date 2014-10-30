package edu.upenn.med.researchaide;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserInfoActivity extends Activity {

	private String username;
	private TextView textView;
	private String userInfo;
	private boolean gotInfo = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_userinfo);
		textView = (TextView)findViewById(R.id.userInfo);
		userInfo = "";

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("edu.upenn.med.reseachaide.username");
		}
		System.out.println("Username is " + username);

		
		if (username == null) {
			textView.setText("An error has occurred. Please try again.");
		} else {
			new GetInfoTask().execute();
		}
		
		textView.setText(userInfo);
	}
	
	public void onBackButtonClick(View view) {
		Intent i = new Intent(this, IndexActivity.class);
		i.putExtra("edu.upenn.med.reseachaide.username", username);
		startActivity(i);
	}


		private class GetInfoTask extends AsyncTask<Void, Void, String> {

			@Override
			protected String doInBackground(Void... params) {
				System.out.println("WompWomp");
				RedCapRecord user = RedCap.exportUser(username);
				System.out.println("Woohoo!!");
				if (user != null) {
					userInfo = user.toString();
//					Iterator it = user.recordAttributes.entrySet().iterator();
//					while (it.hasNext()) {
//						Map.Entry pair = (Map.Entry)it.next();
//						if (!pair.getKey().equals("record_id") || !pair.getKey().equals("username") ||
//								!pair.getKey().equals("password")) {
//							userInfo += pair.getKey() + ": " + pair.getValue() + "/n";
//							it.remove();
//						}
//					}
				} else {
					userInfo = "Failure!";
				}
			return userInfo;
			}
			
			protected void onPostExecute(String userInfo) {
				textView.setText(userInfo);
			}
			
		}
	}

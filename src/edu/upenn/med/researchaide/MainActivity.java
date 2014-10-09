package edu.upenn.med.researchaide;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;



public class MainActivity extends ActionBarActivity {
	
	public static final int SignUpActivity_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
   
    public void onLogInButtonClick(View view) {
    	new Thread(new Runnable() {
    		public void run() {
    			EditText mUsernameEditText = (EditText)findViewById(R.id.userId);
    	    	String username = mUsernameEditText.getText().toString();
    	    	
    	    	EditText mPasswordEditText = (EditText)findViewById(R.id.password);
    	    	String password = mPasswordEditText.getText().toString();
    	    	
    	    	RedCapRecord user = RedCap.exportUser(username);
    	    	if (user == null) {
    	    		System.out.println("GOOD!");
    	    	}
    		}
    		
    	}).start();
    }
}

package edu.upenn.med.researchaide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class ScheduleActivity extends ActionBarActivity {
	
	private final long MILLISECONDS_PER_DAY = 86400000;
	static AmazonDynamoDBClient db = new AmazonDynamoDBClient(
			new BasicAWSCredentials("AKIAIZLADNMN2PLDBZNA",
					"fCjuyDhYVK28gIKtoVSCcwRI5suNatC3oJgy4vi"));
//	static AmazonDynamoDBClient db = new AmazonDynamoDBClient(
//			new BasicAWSCredentials("AKIAI5XSEH47DJ63FG2Q",
//					"ky7OiJmjNOMdnMaacXT3IFl8PdI4pl2duPdIQsIr"));
	private String userSchedule;
	private TextView[] tvs;
	private String record_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		
		// get the extra values passed from last activity (record_id)
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			record_id = extras.getString("record_id");
		}
		
		tvs = new TextView[6];
		tvs[0] = (TextView) findViewById(R.id.userSchedule1);
		tvs[1] = (TextView) findViewById(R.id.userSchedule2);
		tvs[2] = (TextView) findViewById(R.id.userSchedule3);
		tvs[3] = (TextView) findViewById(R.id.userSchedule4);
		tvs[4] = (TextView) findViewById(R.id.userSchedule5);
		tvs[5] = (TextView) findViewById(R.id.userSchedule6);
		new GetScheduleTask().execute();
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
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
	 * This class runs on a separate thread as needed in order to make requests
	 * to the AWS DynamoDB database.
	 */
	public class GetScheduleTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
//			// Find all record_id in DynamoDB that match record_id. For now,
//			// I've hardcoded 1111 as the record ID.
//			// This will need to be updated once
//			// we can pass the record_id from MainActivity to IndexActivity to
//			// this activity.
//			Condition hashKeyCondition = new Condition()
//					.withComparisonOperator(ComparisonOperator.EQ.toString())
//					.withAttributeValueList(new AttributeValue().withS(record_id));
//
//			// Since we cannot search DynamoDB using record_id alone, I've
//			// created this condition which will get
//			// all matches with log-in record_id as hash key and anything where visit_date
//			// is greater than current time
//		    SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
//			Condition rangeKeyCondition = new Condition()
//					.withComparisonOperator(ComparisonOperator.GT.toString())
//					.withAttributeValueList(
//							new AttributeValue().withS(sdf.format(new Date())));
//
//			Map<String, Condition> keyConditions = new HashMap<String, Condition>();
//			keyConditions.put("record_id", hashKeyCondition);
//			keyConditions.put("visit_date", rangeKeyCondition);
//
//			// Sets up the DynamoDB request to only retrieve visit_date and
//			// compensation values.
//			QueryRequest queryRequest = new QueryRequest()
//					.withTableName("SCHEDULE").withKeyConditions(keyConditions)
//					.withAttributesToGet("visit_date", "compensation");
//
//			// Adds each result to the scheduleStatus string to be put into
//			// the TextView.
//			QueryResult result = db.query(queryRequest);
//			userSchedule = "";
//			for (Map<String, AttributeValue> item : result.getItems()) {
//				userSchedule += item.get("visit_date").getS() + "\n";
//			}
			
			// hard code for testing only
			userSchedule = "12012014\n12122014\n12242014\n01012015" +
					"\n02042015\n03032015\n08272015\n";
			return userSchedule;
		}

		// Now we're back to the UI thread. compensationStatus is now added to
		// the textView.
		protected void onPostExecute(String userSchedule) {
			BufferedReader br = new BufferedReader(new StringReader(userSchedule));
			String line;
			int i = 0;
			try {
				while ((line = br.readLine()) != null) {
					if (i < 6) {
						tvs[i].setText(DateFormat(line));
						i++;
					} else break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private String DateFormat(String str_date) throws ParseException {
			
			String result;
			DateFormat formatter = new SimpleDateFormat("MMddyyyy");
			Date date = (Date) formatter.parse(str_date);
			long elapsedTime = date.getTime() - System.currentTimeMillis();
			
			result = "Day " + elapsedTime / MILLISECONDS_PER_DAY + ": "
					+ str_date.charAt(0) + str_date.charAt(1) + "/"
					+ str_date.charAt(2) + str_date.charAt(3) + "/"
					+ str_date.charAt(4) + str_date.charAt(5)
					+ str_date.charAt(6) + str_date.charAt(7);
			
			return result;
		}
	}
	
}

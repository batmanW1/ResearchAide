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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Obtains and displays the compensation status of a user's research study visits.
 *
 */
public class CompensationActivity extends ActionBarActivity {
	
	private final long MILLISECONDS_PER_DAY = 86400000;
	// This AWSAccessKeyID / AWSSecret key will be invalidated after the demo on 12/8/2014.
	static AmazonDynamoDBClient db = new AmazonDynamoDBClient(
			new BasicAWSCredentials("AKIAI5XSEH47DJ63FG2Q",
					"ky7OiJmjNOMdnMaacXT3IFl8PdI4pl2duPdIQsIr"));
	private String compensationStatus;
	private TextView[] tvs;
	private String record_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compensation);
		
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
		new GetCompensationTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compensation, menu);
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
	public class GetCompensationTask extends AsyncTask<Void, Void, String> {

		/**
		 * Makes the call to AWS DynamoDB to retrieve the date of research study visit and
		 * the compensation status for that visit.
		 */
		@Override
		protected String doInBackground(Void... params) {
			// Creates the condition to find all entries in DynamoDB that match user's record_id. 
			Condition hashKeyCondition = new Condition()
					.withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withS(record_id));

			// Since we cannot search DynamoDB using record_id alone, this condition will get
			// visit dates with today's date and in the future.
			SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
			Condition rangeKeyCondition = new Condition()
					.withComparisonOperator(ComparisonOperator.GT.toString())
					.withAttributeValueList(
							new AttributeValue().withS(sdf.format(new Date())));

			// Puts our record_id and visit_date conditions together to send to DynamoDB.
			Map<String, Condition> keyConditions = new HashMap<String, Condition>();
			keyConditions.put("record_id", hashKeyCondition);
			keyConditions.put("visit_date", rangeKeyCondition);

			// Sets up the DynamoDB request to only retrieve visit_date and
			// compensation values.
			QueryRequest queryRequest = new QueryRequest()
					.withTableName("SCHEDULE").withKeyConditions(keyConditions)
					.withAttributesToGet("visit_date", "compensation");

			// Adds each result to the compensationStatus string to be put into
			// the TextView.
			QueryResult result = db.query(queryRequest);
			compensationStatus = "";
			for (Map<String, AttributeValue> item : result.getItems()) {
				compensationStatus += item.get("visit_date").getS() + "\n"
						+ item.get("compensation").getS() + "\n";
			}
			return compensationStatus;
		}

		/**
		 * This method, handled by the original UI thread, now puts the return value from DynamoDB into
		 * the appropriate text field.
		 */
		protected void onPostExecute(String compensationStatus) {
			BufferedReader br = new BufferedReader(new StringReader(
					compensationStatus));
			String line;
			int i = 0;
			String temp = "";
			try {
				while ((line = br.readLine()) != null) {
					if (i < 12) {
						if (i % 2 == 0) temp = DateFormat(line);
						else {
							temp += "\t" + line;
							tvs[i/2].setText(temp);
						}
						i++;
					} else
						break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Formats the returned date string from DynamoDB. 
		 * @param str_date The visit_date string from DynamoDB
		 * @return The formatted date string
		 * @throws ParseException
		 */
		private String DateFormat(String str_date) throws ParseException {

			String result;
			DateFormat formatter = new SimpleDateFormat("MMddyyyy");
			Date date = (Date) formatter.parse(str_date);
			long elapsedTime = date.getTime() - System.currentTimeMillis();

			result = "Day " + ((elapsedTime / MILLISECONDS_PER_DAY) + 1);

			return result;
		}
	}
}

/**
 * 
 */
package com.sleepDiary.backend.aws;

// Logger class
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.sleepDiary.backend.data.UnitData;
import com.sleepDiary.backend.redcap.RedCap;
import com.sleepDiary.backend.redcap.RedCapRecord;
import com.sleepDiary.backend.statusCodes.DBCodes;


/**
 * Support API to write data into AWS
 * 	- Verifies if the userName exists in AWS
 * 	- Create a new user and pushes the details into both AWS and RedCap
 * 	- Write user data into AWS
 * 	- Aggregate data of a single user
 *  - Get a list of all users existing in the data base
 * 
 * @author sunil
 * 
 */
public class SimpleDB {
	
	static AmazonSimpleDB sdb;
	static BasicAWSCredentials awsCred;
	static Logger logger = Logger.getLogger(SimpleDB.class);
	
	// Rename the domains to <ProjectName>Users and <ProjectName>Data
	static String userDomain = "TestUsers";
	static String dataDomain = "TestData";
	static String researchDomain = "TestResearch";
	static String tapDomain = "TestTapData";
	
	// AWS instances are being deployed only on the east coast
	static String tzid = "EST";
	public SimpleDB() {
		
	}

	/**
	 * Initializes the connector to AWS domain defined in domain and stores that 
	 * connector in a global variable. Connector can be reused for multiple operations
	 * The access key and the secret key should be provided in the file 
	 * "AwsCredentials.properties"
	 *
	 * @param  domain  an absolute URL giving the base location of the image
	 * @return void
	 * @see    AwsCredentials.properties
	 */
	private static void init(String myDomain) throws Exception {
		// Init connector to AWS
		if(sdb == null) {
			sdb = new AmazonSimpleDBClient(new ClasspathPropertiesFileCredentialsProvider());
			Region usEast1 = Region.getRegion(Regions.US_EAST_1);
			sdb.setRegion(usEast1);
		}
		
		List<String> domainNames = sdb.listDomains().getDomainNames(); 
		if(! domainNames.contains(myDomain)) {
			try {
				sdb.createDomain(new CreateDomainRequest(myDomain));
			} catch (Exception e) {
				throw e;
			}
		}
	}	
	
	/**
	 * Close the connector
	 */
	private static void uninit() {
		sdb =  null;
	}
		
	/**
	 * Verifies if the userName exists in AWS
	 * Must be noted that this should be used only for the uniqueID which is 
	 * de-identified. No personal information should be reflected in any part
	 * of the userName. 
	 * This will act as a key to data in RedCap
	 *
	 * @param  userName  user name / unique id of the subject
	 * @return DBCodes	 USER_EXISTS			 - ID is present
	 * 					 USER_NOT_EXISTS         - ID doesnt exist in AWS
	 * 					 DOMAIN_CREATION_FAILED  - If the connector could not be initialized
	 * @see    DBCodes   Contains definitions of possible status codes
	 */
	public static DBCodes userExists(String userName) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		logger.debug("Checking if user "+userName+" exists.");
		String selectExpression = "select * from `" + userDomain + "` where userName = '"+userName+"'";
        logger.debug("Selecting: " + selectExpression + "\n");
        
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        
        if(sdb.select(selectRequest).getItems().isEmpty()) {
            return DBCodes.USER_NOT_EXISTS;
        } else  {
            return DBCodes.USER_EXISTS;
        } 
	}
	
	/**
	 * Create a new user and pushes the details into both AWS and RedCap
	 *
	 * @param  userDetails  user information marshalled into RedCapRecord Object
	 * @return DBCodes	 DATA_CORRUPT			 - RedCapRecord is not complete
	 * 					 USER_EXISTS             - If the user is already present.
	 * 					 DOMAIN_CREATION_FAILED  - If the connector could not be initialized
	 * 					 USER_ADDED				 - User has been added to both
	 * 					 USER_NOT_ADDED			 - If addition fails to AWS
	 * 					 USER_RED_CAP_ERROR		 - If addition fails to RedCap
	 * @see    DBCodes   Contains definitions of possible status codes
	 */
	public static DBCodes createUser(RedCapRecord userDetails) {
		
		if(userDetails == null || userDetails.recordAttributes == null || !userDetails.recordAttributes.containsKey("username"))
			return DBCodes.DATA_CORRUPT;
		
		String userName = userDetails.recordAttributes.get("username");
		logger.debug("Creating user :" + userName);

		// Create AWS domain if not exist
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		// If user already exists return error code
		if(userExists(userName) ==  DBCodes.USER_EXISTS) {
			logger.error("User Already exists");
			return DBCodes.USER_EXISTS;
		}
		
		// Push the data
		
		List<ReplaceableItem> entity = new ArrayList<ReplaceableItem>();
		entity.add(new ReplaceableItem(userName)
				.withAttributes(new ReplaceableAttribute("userName", userName,true)));

		try {
			// Put data into AWS domain
            logger.debug("Pushing user name : " +userName+" into  " + userDomain + " domain.");
            sdb.batchPutAttributes(new BatchPutAttributesRequest(userDomain, entity));
            
            // Put data into RedCap domain
            if(!RedCap.commitToRedCap(userDetails)) {
            	return DBCodes.USER_RED_CAP_ERROR;
            }

		} catch (AmazonServiceException ase ) {
			printException(ase);
            return DBCodes.USER_NOT_ADDED;
        } catch (AmazonClientException ase ) {
            printException(ase);
            return DBCodes.USER_NOT_ADDED;
        } 

		
		return DBCodes.USER_ADDED;
	}
		
	
	/**
	 * Write user data into AWS. 
	 *
	 * @param  userDetails  username for the subject
	 * @param  tupleData  Single unit of data captured to be written into DB
	 * @return DBCodes	 DATA_CORRUPT			 - UnitData is not complete
	 * 					 USER_EXISTS             - If the user is already present.
	 * 					 DOMAIN_CREATION_FAILED  - If the connector could not be initialized
	 * 					 DATA_ADDED				 - Data has been added to AWS
	 * 					 DATA_NOT_ADDED			 - Data addition fails to AWS
	 * @see    DBCodes   Contains definitions of possible status codes
	 */
	public static DBCodes commitData(String userName, UnitData tupleData) {
		try {
			init(dataDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}

		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		if(tupleData == null ) {
			logger.debug("Questionnaire is empty");
			return DBCodes.DATA_CORRUPT;
		}
		
	    TimeZone tz = TimeZone.getTimeZone(tzid);
	    long utc = System.currentTimeMillis();
	    
	    Date d = new Date(utc);
	    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
	    //format.setTimeZone(tz);
	    
	    Date currentDate = new Date();
		
	    // Key for DB : UserName+time
		List<ReplaceableItem> entity = new ArrayList<ReplaceableItem>();
		ReplaceableItem replaceableItem = new ReplaceableItem(userName + ":" + currentDate.getTime() );
		
		// Create Headers
		ArrayList<ReplaceableAttribute> rAttributes = new ArrayList<ReplaceableAttribute>();
		rAttributes.add(new ReplaceableAttribute("userName", userName, true));
		rAttributes.add(new ReplaceableAttribute("DateTime", (new Long(currentDate.getTime())).toString(), true));
		// TODO: Redundant data : Can be removed
		rAttributes.add(new ReplaceableAttribute("Date", format.format(d), true));
		
		
		ArrayList<String> answers = tupleData.getAnswers() ;
		ArrayList<String> questions  = tupleData.getQuestions();
		
		// TODO: Modify this as per App requirements
		
		for(int answerIndex = 0 ; answerIndex < answers.size(); answerIndex++) {
			String ans = (new Integer(answerIndex)).toString();
			rAttributes.add(new ReplaceableAttribute(questions.get(answerIndex), answers.get(answerIndex), true));
			
			//   ( or )
			// You might not want to save the questions repeatedly
			// rAttributes.add(new ReplaceableAttribute(ans, answers.get(answerIndex), true));
		}
		replaceableItem.setAttributes(rAttributes);
		entity.add(replaceableItem);
		
		try {
			// Put data into AWS domain
            logger.debug("Commiting data into " + dataDomain + " domain for "+userName+" at time "+ format.format(d)+"\n");
            sdb.batchPutAttributes(new BatchPutAttributesRequest(dataDomain, entity));

		} catch (AmazonServiceException ase ) {
			printException(ase);
            return DBCodes.DATA_NOT_ADDED;
        } catch (AmazonClientException ase ) {
            printException(ase);
            return DBCodes.DATA_NOT_ADDED;
        } 
		return DBCodes.DATA_ADDED;
	}
	
	/**
	 * Get a list of all users existing in the data base 
	 *
	 * @param	void  
	 * @return	null	 if the domain doesnt exist then there are no users that are present
	 * @return	userName	ArrayList of all the users that are present in the system.
	 * 					
	 */
	public static ArrayList<String> getUserNames() {

		ArrayList<String> userNames = new ArrayList<String>();
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}

		String selectExpression = "select * from `" + userDomain + "`";
		logger.debug("Users :\n");
		SelectRequest selectRequest = new SelectRequest(selectExpression);
		for (Item item : sdb.select(selectRequest).getItems()) {
			for (Attribute attribute : item.getAttributes()) {
				if (attribute.getName() != null
						&& attribute.getName().equalsIgnoreCase("userName")) {
					logger.debug(attribute.getValue().toString());
					userNames.add(attribute.getValue().toString());
				}
			}
		}
		logger.debug("\n");
		return userNames;
	}
	
	
	/**
	 * Get all entries of a particular user 
	 *
	 * @param	userName	user name of the subject  
	 * @return	userdetails	First row are the headers, the remaining rows contain tuple Data
	 */
	public static ArrayList<ArrayList<String>> getUserDetails(String userName) {

		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		String myDomain = dataDomain;
		  String selectExpression = "select * from `" + myDomain + "` where userName = \'" + userName + "\' intersection DateTime is not null order by DateTime desc limit 100";
          logger.debug("Selecting: " + selectExpression + "\n");
          SelectRequest selectRequest = new SelectRequest(selectExpression);
          
          boolean first = true;
          
          for (Item item : sdb.select(selectRequest).getItems()) {
        	  if(first) {
        		  ArrayList<String> cols = new ArrayList<String>();
                  for (Attribute attribute : item.getAttributes()) {
                	  cols.add(attribute.getName());
                  }
                  data.add(cols);
                  first = false;
        	  }
        	  
        	  ArrayList<String> tuple = new ArrayList<String>();
              for (Attribute attribute : item.getAttributes()) {
            	  tuple.add(attribute.getValue());
              }
              data.add(tuple);
          }
          logger.info("\n");
          return data;
	}

	/**
	 * Exception Handling - Prints out details
	 * @param ase	Exception of type AmazonServiceException
	 */
	public static void printException(AmazonServiceException ase) {
		logger.error("Caught an AmazonServiceException, which means your request made it "
                + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
        logger.error("Error Message:    " + ase.getMessage());
        logger.debug("HTTP Status Code: " + ase.getStatusCode());
        logger.debug("AWS Error Code:   " + ase.getErrorCode());
        logger.debug("Error Type:       " + ase.getErrorType());
        logger.debug("Request ID:       " + ase.getRequestId());
	}
	
	/**
	 * Exception Handling - Prints out details
	 * @param ase	Exception of type AmazonClientException
	 */
	public static void printException(AmazonClientException ace) {
		 logger.error("Caught an AmazonClientException, which means the client encountered "
                 + "a serious internal problem while trying to communicate with SimpleDB, "
                 + "such as not being able to access the network.");
         logger.error("Error Message: " + ace.getMessage());
	}
	
	/**
	 * For testing alone. Not to be used in production.
	 * @param null 
	 */
	public static void testPrintData() {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		String myDomain = userDomain;
		  String selectExpression = "select * from `" + myDomain + "`";
          logger.debug("Selecting: " + selectExpression + "\n");
          SelectRequest selectRequest = new SelectRequest(selectExpression);
          for (Item item : sdb.select(selectRequest).getItems()) {
              logger.info("  Item");
              logger.info("    Name: " + item.getName());
              for (Attribute attribute : item.getAttributes()) {
                  logger.info("      Attribute");
                  logger.info("        Name:  " + attribute.getName());
                  logger.info("        Value: " + attribute.getValue());
              }
          }
          logger.info("\n");
	}
}

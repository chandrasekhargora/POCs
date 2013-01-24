package com.apigee.cs.poc;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;
import org.junit.runner.Parameterized;
import org.junit.runner.RunWith;
import org.junit.runner.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apigee.cs.testframework.generic.BasicIntegrationTest;
import com.apigee.cs.testframework.generic.ParameterizedTestdataBase;
import com.apigee.cs.testframework.util.FileUtils;
import com.apigee.cs.testframework.util.GenericUtils;
import com.att.blackflag.common.testbase.messaging.AbstractSMSTest;
import com.att.blackflag.common.xmlObjects.SMSRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;


/**
 * JUnit Parameterized Test
 * @author: ajose 
 *
 */

@RunWith(value=Parameterized.class)
public class ParameterizedCsvPOCTest extends AbstractSMSTest {

	Logger log = LoggerFactory.getLogger(this.getClass());
	 ParamsVO params;
	
	 public ParameterizedCsvPOCTest(String a1,String a2,String a3,String a4,String a5,String a6,String a7) throws Exception{
		 params = new ParamsVO(new String[]{a1,a2,a3,a4,a5,a6,a7}, this);
	 }
	 
	//The first parameter will be used in the report as testcase name
	@Parameters(name= "{0}") 
	public static Collection<String[]> data() throws Exception{
		ArrayList<String[]> paramsFromFile =  new ArrayList<String[]>(
					FileUtils.readFromCSV(URLDecoder.decode(ParamsVO.class
							.getClassLoader().getResource("ParameterizedCsvPOC.csv")
							.getFile(), "UTF-8")));

		//Validating if the data is in the right format
		try {
			for (String[] strings : paramsFromFile) {
				ParamsVO.ValidateDatasetFormat(strings);
			}

		} catch (Exception e) {
			throw new Exception("Test parameters are not formatted properly - "
					+ e.getMessage());
		}
	
		return paramsFromFile;
	}

	
	@Test
	public void testSendSMS() throws Exception {
		System.out.println("Testcase name: " + params.getTestcaseName());
		WebResource wr = getGatewayWebResource().path(p("sendsms.url"));
		
        ClientResponse cr = wr
       		 	 .header("Authorization","Bearer " + getAuth().getAccessToken())
				 .header("Accept", params.getAcceptHeader())
				 .header("Content-Type", params.getContentTypeHeader())
				 .header("Host", p("g_host"))
				 .post(ClientResponse.class, buildSendSMSRequest(params.getContentTypeHeader(), params.getDestinationAddress(), params.getMessageToSend()));  
		int responseCode = cr.getStatus();
		String responsePayload = cr.getEntity(String.class);
		
		
		verifyResponseCode(Integer.parseInt(params.getResponseCodeToAssert()), responseCode);
		verifyResponsePayload(params.getAcceptHeader(), responsePayload, params.getResponseSubStringToAssert());
		
	}

	
	
	private Object buildSendSMSRequest (String ContentTypeHeader, String Address, String Message) throws Exception
	{ 
		Object retVal = null;
		int switchVal = -1;
		if (ContentTypeHeader.equalsIgnoreCase("application/json")) switchVal = 1;
		else if (ContentTypeHeader.equalsIgnoreCase("application/xml")) switchVal = 2;
		else if (ContentTypeHeader.equalsIgnoreCase("application/x-www-form-urlencoded")) switchVal = 3;
		
		
		switch (switchVal) {
		case 1:
			retVal = CreateNewSMSRequest();
			break;
		case 2:
			retVal = SMSRequest.getSMSXMLTwelveDigit(Address);
			break;
		case 3:
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.putSingle("Address", Address);
			formData.putSingle("Message", Message);	
			retVal = formData;
			break;
		default:
			throw new Exception("Test code error: Incorrect Value for content-type header");
		}
	return retVal;
		
	}
	
	
	//This defines the value-object which holds a tuple from the test-data (may be from CSV file)
	public static class ParamsVO  extends ParameterizedTestdataBase{
		
		private String TestcaseName;
		private String AcceptHeader;
		private String ContentTypeHeader;
		private String DestinationAddress;
		private String MessageToSend;
		private String ResponseCodeToAssert;
		private String ResponseSubStringToAssert;
		
		private BasicIntegrationTest testObj = null;
		
		public ParamsVO(String[] params, BasicIntegrationTest testObject) throws Exception{
			//Remember to call the parent's constructor. It replaces the ${variable}s from the properties file
			super(params,testObject);
			this.testObj = testObject;
			AssignVariables(params);

		}
		
		public ParamsVO() {
			super();
		}
		
		private void AssignVariables(String[] params) throws Exception{
			//Remember that the order of assignment is important
			TestcaseName = params[0];
			AcceptHeader = params[1];
			ContentTypeHeader = params[2];
			DestinationAddress = params[3];
			MessageToSend = params[4];
			ResponseCodeToAssert = params[5];
			ResponseSubStringToAssert = params[6];
		
		}
		
		/*
		 * Throws Exception if validation fails
		 */
		public static void ValidateDatasetFormat(String[] params) throws Exception{
			new ParamsVO().AssignVariables(params);
		}
		
		public String getTestcaseName() {
			return TestcaseName;
		}
		public String getAcceptHeader() {
			return AcceptHeader;
		}
		public String getContentTypeHeader() {
			return ContentTypeHeader;
		}
		public String getDestinationAddress() {
			return DestinationAddress;
		}
		public String getMessageToSend() {
			return MessageToSend;
		}
		public String getResponseCodeToAssert() {
			return ResponseCodeToAssert;
		}
		public String getResponseSubStringToAssert() {
			return ResponseSubStringToAssert;
		}
		
	}

	
}

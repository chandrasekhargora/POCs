package com.apigee.cs.poc;

import java.io.File;
import java.net.URLDecoder;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.apigee.cs.extensions.junit.DatasourceRunner;
import com.apigee.cs.extensions.junit.DatasourceRunner.AbstractTestdataBase;
import com.apigee.cs.testframework.generic.AbstractParameterizedWithVariableTestdataBase;
import com.apigee.cs.testframework.generic.BasicIntegrationTest;
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

@RunWith(value=DatasourceRunner.class)
public class DatasourceRunnerCsvPOCTest extends AbstractSMSTest {

	Logger log = LoggerFactory.getLogger(this.getClass());
	 ParamsVO params;
	
		public void getAccessToken() throws Exception {
		}
	 
	 /**
	  * Note: Any changes made to the "param" within this constructor will be overwritten by the framework.	
	  * 
	  * @param param
	  * @throws Exception
	  */
	 public DatasourceRunnerCsvPOCTest(AbstractTestdataBase param) throws Exception{
		 params = (ParamsVO)param;
	 }

	@DatasourceRunner.DataSource (returnBeanType=ParamsVO.class)
	public static Object DataSource() throws Exception{
		return new File(URLDecoder.decode(ParamsVO.class
							.getClassLoader().getResource("ParameterizedCsvPOC.csv")
							.getFile(), "UTF-8"));	
		}
	
	@Test
	public void testSendSMS() throws Exception {
		System.out.println("Testcase name: " + params.getTestcaseName());
		WebResource wr = getGatewayWebResource().path(p("sendsms.url"));
		
        ClientResponse cr = wr
       		 	 .header("Authorization","Bearer c90505cda639053ae4fdf1577e13eb1d")// + getAuth().getAccessToken())
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
	
	
	public static class ParamsVO  extends AbstractParameterizedWithVariableTestdataBase{
		
		private String AcceptHeader;
		private String ContentTypeHeader;
		private String DestinationAddress;
		private String MessageToSend;
		private String ResponseCodeToAssert;
		private String ResponseSubStringToAssert;
		
		public ParamsVO() {
			super();
		}
		
		public void AssignParameters(String[] params) throws Exception {
			
			super.AssignParameters(params); //Don't ever miss this statement if you want ${variable}s to be replaced
			
			setTestcaseName(params[0]);
			AcceptHeader = params[1];
			ContentTypeHeader = params[2];
			DestinationAddress = params[3];
			MessageToSend = params[4];
			ResponseCodeToAssert = params[5];
			ResponseSubStringToAssert = params[6];

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

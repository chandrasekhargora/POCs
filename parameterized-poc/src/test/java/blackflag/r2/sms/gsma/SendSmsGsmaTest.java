package blackflag.r2.sms.gsma;

import java.io.IOException;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.apigee.cs.testframework.util.XmlObjectUtil;
import com.att.blackflag.common.testbase.messaging.AbstractSMSTestGSMA;
import com.att.blackflag.common.xmlObjects.GSMASMSRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class SendSmsGsmaTest extends AbstractSMSTestGSMA {

	Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void testAcceptJsonContentTypeXMLOAuthV2d10() throws Exception {
		
		setDelay();
		//setting the Path
		WebResource wr = getGatewayWebResource().path(p("gsmasendsms.url")).path(p("gsma_senderaddress"))
				.path("requests");

		ClientResponse cr = wr
  		 	 .queryParam("access_token", getAuth().getAccessToken())
//				  .header("Authorization","Bearer " + getAuth().getAccessToken())
				 .header("Accept", "application/json")
				 .header("Content-Type", "application/xml")
				 .header("Host", p("g_host"))
				  .post(ClientResponse.class, GSMASMSRequest.getSMSXML(p("g_Address")));

		JSONObject responseObject = cr.getEntity(JSONObject.class);
		
		int statusCode = cr.getStatus();
		assertStatusCode(statusCode);
		verifyJsonResponseSendGSMASMS(responseObject);
	}

	@Test
	public void testAcceptJsonContentTypeXML() throws Exception {
		
		setDelay();
		//setting the Path
		WebResource wr = getGatewayWebResource().path(p("gsmasendsms.url")).path(p("gsma_senderaddress"))
				.path("requests");

		ClientResponse cr = wr
//  		 	 .queryParam("access_token", getAuth().getAccessToken())
				  .header("Authorization","Bearer " + getAuth().getAccessToken())
				 .header("Accept", "application/json")
				 .header("Content-Type", "application/xml")
				 .header("Host", p("g_host"))
				  .post(ClientResponse.class, GSMASMSRequest.getSMSXML(p("g_Address")));

		JSONObject responseObject = cr.getEntity(JSONObject.class);
		
		int statusCode = cr.getStatus();
		assertStatusCode(statusCode);
		verifyJsonResponseSendGSMASMS(responseObject);
	}
	
	
	@Test
	public void testAcceptXMLContentTypeJson() throws Exception {
		
		setDelay();
		//setting the Path
		WebResource wr = getGatewayWebResource().path(p("gsmasendsms.url")).path(p("gsma_senderaddress"))
				.path("requests");

		ClientResponse cr = wr
//  		 	 .queryParam("access_token", getAuth().getAccessToken())
				  .header("Authorization","Bearer " + getAuth().getAccessToken())
				 .header("Accept", "application/xml")
				 .header("Content-Type", "application/json")
				 .header("Host", p("g_host"))
				  .post(ClientResponse.class, createGSMANewSMSRequest());

		String responseXML = cr.getEntity(String.class);
		
		int statusCode = cr.getStatus();
		assertStatusCode(statusCode);
		verifyXmlResponseSendGSMASMS(responseXML);
	}
	
	
	@Test
	public void testAcceptJsonContentTypeFormEncoded() throws Exception {
		
		setDelay();
		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.putSingle("address", p("g_Address"));
		formData.putSingle("message", "Hello World");

		//setting the Path
		WebResource wr = getGatewayWebResource().path(p("gsmasendsms.url")).path(p("gsma_senderaddress"))
				.path("requests");

		ClientResponse cr = wr
//  		 	 .queryParam("access_token", getAuth().getAccessToken())
				  .header("Authorization","Bearer " + getAuth().getAccessToken())
				 .header("Accept", "application/json")
				 .header("Content-Type", "application/x-www-form-urlencoded")
				 .header("Host", p("g_host"))
				  .post(ClientResponse.class, formData);

		JSONObject responseObject = cr.getEntity(JSONObject.class);		
		int statusCode = cr.getStatus();
		assertStatusCode(statusCode);
		verifyJsonResponseSendGSMASMS(responseObject);
	}

	

	
	@Test
	public void testSendSmsTwelveDigit() throws Exception {
		
		setDelay();
		//setting the Path
		//setting the Path
		WebResource wr = getGatewayWebResource().path(p("gsmasendsms.url")).path(p("gsma_senderaddress"))
				.path("requests");

		ClientResponse cr = wr
//  		 	 .queryParam("access_token", getAuth().getAccessToken())
				  .header("Authorization","Bearer " + getAuth().getAccessToken())
				 .header("Accept", "application/json")
				 .header("Content-Type", "application/xml")
				 .header("Host", p("g_host"))
				  .post(ClientResponse.class, GSMASMSRequest.getSMSXMLTwelveDigit(p("g_Address")));

		JSONObject responseObject = cr.getEntity(JSONObject.class);
		
		int statusCode = cr.getStatus();
		assertStatusCode(statusCode);
		verifyJsonResponseSendGSMASMS(responseObject);		
	}

		
	private void verifyJsonResponseSendGSMASMS(JSONObject resjsonobj) throws JSONException
	{
		
		JSONObject resourceReference = resjsonobj.getJSONObject("resourceReference");
		String strResourceUrl = resourceReference.getString("resourceURL");
		Assert.assertTrue("resourceURL is null", strResourceUrl.length()>0);
		
		String arrResourceUrl[] = strResourceUrl.split("/");
		String id = arrResourceUrl[arrResourceUrl.length-2];
		log.info("id is " + id);
		Assert.assertTrue("id is null", id.length() > 0);
	}
	
	private void verifyXmlResponseSendGSMASMS(String responseXML) throws ParserConfigurationException, SAXException, IOException
	{
		Document doc = XmlObjectUtil.getDoc(responseXML);
		Node SmsResponse  = XmlObjectUtil.getNode(doc, "smsResponse");
		String id = XmlObjectUtil.getAttributeValue(SmsResponse , "id");
		log.info("MMS id is " + id);
		Assert.assertTrue("id is null", id.length()>0);
		Assert.assertTrue("resourceURL is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "resourceURL")).length()>0);		
	}	
}

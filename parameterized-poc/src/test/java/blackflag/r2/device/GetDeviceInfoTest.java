package blackflag.r2.device;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import com.apigee.cs.testframework.generic.Authorization;
import com.apigee.cs.testframework.util.XmlObjectUtil;
import com.att.blackflag.common.framework.APIOperationsFactory;
import com.att.blackflag.common.framework.ApiOperationEnum;
import com.att.blackflag.common.framework.IBaseOperation;
import com.att.blackflag.common.framework.device.DeviceInfoProcessorOld;
import com.att.blackflag.common.testbase.oauth.AbstractAuthenticationTestbase;
import com.sun.jersey.api.client.ClientResponse;

public class GetDeviceInfoTest extends AbstractAuthenticationTestbase {	
	static Authorization auth = null;
	private IBaseOperation deviceInfoOperation;
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void getAccessToken() throws Exception{
		if (auth == null) {
			auth = performLoginConsent("DC", p("client_id"),
					p("client_secret"), p("StoredRedirectUrl"), false, false);
			
		}
	}

	static Authorization getAuth() {
		return auth;
	}
	
	@Before
	public void initOperation() throws Exception{
		getAccessToken();
		APIOperationsFactory.getInstance();
		deviceInfoOperation = 
				APIOperationsFactory.initialize(getProperties(), auth, getGatewayWebResource())
				.getFactory(ApiOperationEnum.DEVICE_INFO_OLD);
	}
	
	@Test	
	public void testDeviceInfo_acceptNothingOAuthV2d10() throws Exception {
		
		ClientResponse cr = deviceInfoOperation.callOperation(new DeviceInfoProcessorOld.DeviceInfoProcessAcceptNothing(p("g_Address"))).getOperationResp();
		JSONObject responseObject = cr.getEntity(JSONObject.class);
		int statusCode = cr.getStatus();
		verifyStatusJSON(statusCode, responseObject);
	}
	
	@Test	
	public void testDeviceInfo_acceptJsonOAuthV2d10() throws Exception {
				
		ClientResponse cr = deviceInfoOperation.callOperation(new DeviceInfoProcessorOld.DeviceInfoProcesAcceptJson(p("g_Address"))).getOperationResp();
		JSONObject responseObject = cr.getEntity(JSONObject.class);
		int statusCode = cr.getStatus();
		verifyStatusJSON(statusCode, responseObject);
	}
	
	@Test	
	public void testDeviceInfo_twelveDigitNumberOAuthV2d10() throws Exception {
				
		ClientResponse cr = deviceInfoOperation.callOperation(new DeviceInfoProcessorOld.DeviceInfoProcesAcceptJson(appendPlusOne(p("g_Address")))).getOperationResp();

		JSONObject responseObject = cr.getEntity(JSONObject.class);
		int statusCode = cr.getStatus();
		verifyStatusJSON(statusCode, responseObject);
	}
	
	@Test	
	public void testDeviceInfo_acceptXmlOAuthV2d10() throws Exception {
				
		ClientResponse cr = deviceInfoOperation.callOperation(new DeviceInfoProcessorOld.DeviceInfoProcesAcceptXml(p("g_Address"))).getOperationResp();
		String responseXML = cr.getEntity(String.class);
		int statusCode = cr.getStatus();
		if (statusCode == 200){
			Assert.assertEquals("200 HTTP response code", 200, cr.getStatus());
			log.info("XML request"+ responseXML);
			
			Document doc = XmlObjectUtil.getDoc(responseXML);
			
			Assert.assertTrue("acwdevcert is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwdevcert")).length()>0);
			Assert.assertTrue("acwrel is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwrel")).length()>0);
			Assert.assertTrue("acwmodel is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwmodel")).length()>0);
			Assert.assertTrue("acwvendor is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwvendor")).length()>0);

			Assert.assertTrue("acwav is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwav")).length()>0);
			Assert.assertTrue("acwaocr is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwaocr")).length()>0);
			Assert.assertTrue("acwcf is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwcf")).length()>0);
			Assert.assertTrue("acwtermtype is null", XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "acwtermtype")).length()>0);
		}
		else if (statusCode == 500){
			Assert.assertEquals("500 HTTP response code", 500, cr.getStatus());
			Document doc = XmlObjectUtil.getDoc(responseXML.toString());
			String resVariable = XmlObjectUtil.getNodeValue( XmlObjectUtil.getNode(doc, "Variables"));
			Assert.assertEquals("MLP-005", " MLP-005", resVariable);
		}
		else {
			log.info("Other Status Code "+ statusCode);
			Assert.assertTrue("Unintended Request" + statusCode, false);
		}
	}
	
	public String appendPlusOne(String telNumber) {
		if (null != telNumber) {
			return telNumber.replaceFirst("tel:", "tel:+1");
		}
		return null;
	}
	
	/*
	 * Method that checks the Status Code
	 */
	public void verifyStatusJSON(int statusCode, JSONObject responseObject) throws Exception{
		if(statusCode == 200){
			Assert.assertEquals("200 HTTP response code", 200, statusCode);
			
			JSONObject resObjdeviceid = responseObject.getJSONObject("deviceId");

			Assert.assertTrue("acwdevcert is null", resObjdeviceid.getString("acwdevcert").length()>0);
			Assert.assertTrue("acwrel is null", resObjdeviceid.getString("acwrel").length()>0);
			Assert.assertTrue("acwmodel is null", resObjdeviceid.getString("acwmodel").length()>0);
			Assert.assertTrue("acwvendor is null", resObjdeviceid.getString("acwvendor").length()>0);
			
			JSONObject resObjcapabilities = responseObject.getJSONObject("capabilities");
			
			Assert.assertTrue("acwav is null", resObjcapabilities.getString("acwav").length()>0);
			Assert.assertTrue("acwaocr is null", resObjcapabilities.getString("acwaocr").length()>0);
			Assert.assertTrue("acwcf is null", resObjcapabilities.getString("acwcf").length()>0);
			Assert.assertTrue("acwtermtype is null", resObjcapabilities.getString("acwtermtype").length()>0);			
		}
		else if(statusCode == 500){
			Assert.assertEquals("500 Http response code", 500, statusCode);
			Assert.assertEquals("MLP-005", "MLP-005", responseObject.getJSONObject("requestError").getJSONObject("serviceException").getString("variables"));
		}
		else {
			log.info("Other Status Code "+ statusCode);
			Assert.assertTrue("Unintended Request" + statusCode, false);
		}		
	}
}

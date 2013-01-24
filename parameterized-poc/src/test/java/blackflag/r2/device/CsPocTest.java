package blackflag.r2.device;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Parameterized;
import org.junit.runner.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apigee.cs.extensions.junit.DatasourceRunner;
import com.apigee.cs.extensions.junit.DatasourceRunner.AbstractTestdataBase;
import com.apigee.cs.testframework.generic.AbstractParameterizedWithVariableTestdataBase;
import com.apigee.cs.testframework.generic.Authorization;
import com.apigee.cs.testframework.generic.BasicIntegrationTest;
import com.apigee.cs.testframework.generic.ParameterizedTestdataBase;
import com.apigee.cs.testframework.util.FileUtils;
import com.apigee.cs.testframework.util.GenericUtils;
import com.apigee.cs.testframework.util.HttpUtils;
import com.apigee.cs.testframework.util.XmlObjectUtil;
import com.att.blackflag.common.testbase.oauth.AbstractAuthenticationTestbase;
import com.att.blackflag.common.testbase.utils.KmsUtils;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author: Chandra Gora
 * 
 */

@RunWith(value = DatasourceRunner.class)
public class CsPocTest extends AbstractAuthenticationTestbase {

	Logger log = LoggerFactory.getLogger(this.getClass());
	ParamsVO params;
	public static Properties prop = new Properties();

	public CsPocTest(AbstractTestdataBase param) throws Exception {
		params = (ParamsVO) param;
	}

	@DatasourceRunner.DataSource(returnBeanType = ParamsVO.class)
	public static Object DataSource() throws Exception {
		return new File(URLDecoder.decode(ParamsVO.class.getClassLoader()
				.getResource("poc.csv").getFile(), "UTF-8"));
	}

	@BeforeClass
	public static void executeBeforeEachTestClass() throws Exception {
		try {
			prop.load(new FileInputStream(
					"./src/test/resources/Env-propeties.properties"));

		} catch (Exception e) {
		}
	}

	@Test
	public void InfoGroupTestCase() throws Exception {

		ApacheHttpClient4 client = HttpUtils.getClient();
		System.out.println(prop.getProperty("api-url"));
		WebResource GETCompanies = client.resource(prop.getProperty("api-url"))
				.path(params.getURI()).queryParam("apikey", params.QueryParam1);
		WebResource.Builder builder = null;
		builder = GETCompanies.header("Content-Type", params.getContentType())
				.header("Accept", params.getAccept());

		ClientResponse crApi = null;

		if (params.getVerb().equals("GET")) {
			crApi = builder.get(ClientResponse.class);
		} else if (params.getVerb().equals("POST")) {
			crApi = builder.post(ClientResponse.class);
		} else if (params.getVerb().equals("PUT")) {
			crApi = builder.put(ClientResponse.class);
		} else if (params.getVerb().equals("DELETE")) {
			crApi = builder.delete(ClientResponse.class);
		}

		String response = crApi.getEntity(String.class);
		int statusCode = crApi.getStatus();

		Assert.assertEquals("Test failed due to mismatch in Response Code",
				Integer.parseInt(params.getResponseCode()), statusCode);
	}

	/**
	 * This defines the value-object which holds a tuple from the test-data (may
	 * be from CSV file)
	 * 
	 * @param params
	 * @throws Exception
	 */
	public static class ParamsVO extends
			AbstractParameterizedWithVariableTestdataBase {

		private String URI;
		private String QueryParam1;
		private String Verb;
		private String Accept;
		private String ContentType;
		private String ResponseCode;
		private String ResponseText;

		public ParamsVO() {
			super();
		}

		public void AssignParameters(String[] params) throws Exception {

			super.AssignParameters(params); // Don't ever miss this statement if
			// you want ${variable}s to be
			// replaced

			setTestcaseName(params[0]);
			URI = params[1];
			QueryParam1 = params[2];
			Verb = params[3];
			Accept = params[4];
			ContentType = params[5];
			ResponseCode = params[6];
			ResponseText = params[7];
		}

		public String getURI() {
			return URI;
		}

		public String getQueryParam1() {
			return QueryParam1;
		}

		public String getVerb() {
			return Verb;
		}

		public String getAccept() {
			return Accept;
		}

		public String getContentType() {
			return ContentType;
		}

		public String getResponseCode() {
			return ResponseCode;
		}

		public String getResponseText() {
			return ResponseText;
		}

	}
}
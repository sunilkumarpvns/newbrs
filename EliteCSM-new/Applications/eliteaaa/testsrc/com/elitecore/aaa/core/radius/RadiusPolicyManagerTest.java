package com.elitecore.aaa.core.radius;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.configuration.LoadCacheFailedException;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.policies.data.IPolicyData;
import com.elitecore.core.serverx.policies.data.PolicyData;
import com.elitecore.core.serverx.policies.data.PolicyGroupData;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadiusPolicyManagerTest {
	private RadiusPolicyManager rpm;
	private static final String SYSTEM_FOLDER = "system";
	@Mock private AAAServerContext serverContext;

	@BeforeClass
	public static void setUpBeforeClass() {
		loadDictionary();
	}

	private static void loadDictionary() {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		rpm = new RadiusPolicyManager(RadiusConstants.RADIUS_AUTHORIZATION_POLICY) {

			@Override
			protected void serializePolicies() {
				// No op as we do not want to test serialization
			}

			@Override
			protected void serializePolicyGroup() {
				// No op as we do not want to test serialization
			}

			@Override
			protected List<IPolicyData> readFromDB() throws LoadCacheFailedException{

				List<IPolicyData> policyDataList = new ArrayList<IPolicyData> ();

				IPolicyData policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_1");
				policyData.setDescription("Default Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_2");
				policyData.setDescription("Test_Policy_2");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_3");
				policyData.setDescription("Test_Policy_3");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_4");
				policyData.setDescription("Test_Policy_4");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_5");
				policyData.setDescription("Test_Policy_5");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// conditional filter item for string type attribute 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_6");
				policyData.setDescription("Test_Policy_6");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_7");
				policyData.setDescription("Test_Policy_7");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_8");
				policyData.setDescription("Test_Policy_8");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_9");
				policyData.setDescription("Test_Policy_9");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for string type attribute 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_10");
				policyData.setDescription("Test_Policy_10");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_11");
				policyData.setDescription("Test_Policy_11");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//negative conditional filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_12");
				policyData.setDescription("Test_Policy_12");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_13");
				policyData.setDescription("Test_Policy_13");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);



				// conditional filter item for string type attribute with two attribute to filter. 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_14");
				policyData.setDescription("Test_Policy_14");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_15");
				policyData.setDescription("Test_Policy_15");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_16");
				policyData.setDescription("Test_Policy_16");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_17");
				policyData.setDescription("Test_Policy_17");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// negative conditional filter item for string type attribute with two attribute to filter. 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_18");
				policyData.setDescription("Test_Policy_18");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for int type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_19");
				policyData.setDescription("Test_Policy_19");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// negative conditional filter item for octets type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_20");
				policyData.setDescription("Test_Policy_20");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for VSA type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_21");
				policyData.setDescription("Test_Policy_21");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// negative conditional filter item for string type attribute with two attribute to filter. 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_22");
				policyData.setDescription("Test_Policy_22");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for int type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_23");
				policyData.setDescription("Test_Policy_23");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// negative conditional filter item for octets type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_24");
				policyData.setDescription("Test_Policy_24");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for VSA type attribute with two attribute to filter.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_25");
				policyData.setDescription("Test_Policy_25");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// conditional filter item for string type attribute with * expression 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_26");
				policyData.setDescription("Test_Policy_26");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_27");
				policyData.setDescription("Test_Policy_27");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_28");
				policyData.setDescription("Test_Policy_28");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_29");
				policyData.setDescription("Test_Policy_29");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// conditional filter item for string type attribute with * expression 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_30");
				policyData.setDescription("Test_Policy_30");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_31");
				policyData.setDescription("Test_Policy_31");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_32");
				policyData.setDescription("Test_Policy_32");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_33");
				policyData.setDescription("Test_Policy_33");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);



				// negative conditional filter item for string type attribute with * expression 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_34");
				policyData.setDescription("Test_Policy_34");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for int type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_35");
				policyData.setDescription("Test_Policy_35");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//negative conditional filter item for octets type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_36");
				policyData.setDescription("Test_Policy_36");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional filter item for VSA type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_37");
				policyData.setDescription("Test_Policy_37");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// conditional filter item for string type attribute with * expression 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_38");
				policyData.setDescription("Test_Policy_38");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_39");
				policyData.setDescription("Test_Policy_39");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_40");
				policyData.setDescription("Test_Policy_40");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_41");
				policyData.setDescription("Test_Policy_41");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);



				// conditional filter item for string type attribute with * expression 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_42");
				policyData.setDescription("Test_Policy_42");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_43");
				policyData.setDescription("Test_Policy_43");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_44");
				policyData.setDescription("Test_Policy_44");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_45");
				policyData.setDescription("Test_Policy_45");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);




				// conditional filter item for string type attribute with * expression 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_46");
				policyData.setDescription("Test_Policy_46");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_47");
				policyData.setDescription("Test_Policy_47");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_48");
				policyData.setDescription("Test_Policy_48");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute with * expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_49");
				policyData.setDescription("Test_Policy_49");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);




				// conditional filter item for string type attribute with ? expression 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_50");
				policyData.setDescription("Test_Policy_50");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for int type attribute with ? expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_51");
				policyData.setDescription("Test_Policy_51");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				//conditional filter item for octets type attribute with ? expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_52");
				policyData.setDescription("Test_Policy_52");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// conditional filter item for VSA type attribute with ? expression
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_53");
				policyData.setDescription("Test_Policy_53");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// filter item for Group type attribute 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_54");
				policyData.setDescription("Test_Policy_54");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// filter item for Group type attribute RejectOnCheckItemNotFound true and check attribute present
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_55");
				policyData.setDescription("Test_Policy_55");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// filter item for Group type attribute RejectOnCheckItemNotFound true and check attribute not present
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_56");
				policyData.setDescription("Test_Policy_56");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// filter item for Group type attribute with negative condition 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_57");
				policyData.setDescription("Test_Policy_57");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				//group attribute in check item			 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_58");
				policyData.setDescription("Test_Policy_58");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				//group attribute in check item with RejectOnCheckItemNotFound true and attribute present		 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_59");
				policyData.setDescription("Test_Policy_59");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				//group attribute in check item with RejectOnCheckItemNotFound true and attribute not present		 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_60");
				policyData.setDescription("Test_Policy_60");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				//group attribute in check item with RejectOnCheckItemNotFound false and nested attribute not present		 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_61");
				policyData.setDescription("Test_Policy_61");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				//group attribute in check item with RejectOnCheckItemNotFound true and nested attribute not present		 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_62");
				policyData.setDescription("Test_Policy_62");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// customer level filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_63");
				policyData.setDescription("Test_Policy_63");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  customer level filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_64");
				policyData.setDescription("Test_Policy_64");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// customer level filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_65");
				policyData.setDescription("Test_Policy_65");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// customer level filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_66");
				policyData.setDescription("Test_Policy_66");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);



				// conditional customer level filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_67");
				policyData.setDescription("Test_Policy_67");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  conditional customer level filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_68");
				policyData.setDescription("Test_Policy_68");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// conditional customer level filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_69");
				policyData.setDescription("Test_Policy_69");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// conditional customer level filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_70");
				policyData.setDescription("Test_Policy_70");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);


				// negative condition customer level filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_71");
				policyData.setDescription("Test_Policy_71");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  negative condition conditional customer level filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_72");
				policyData.setDescription("Test_Policy_72");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// negative condition conditional customer level filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_73");
				policyData.setDescription("Test_Policy_73");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// negative condition conditional customer level filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_74");
				policyData.setDescription("Test_Policy_74");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);



				// negative condition customer level filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_75");
				policyData.setDescription("Test_Policy_75");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  negative condition conditional customer level filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_76");
				policyData.setDescription("Test_Policy_76");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// negative condition conditional customer level filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_77");
				policyData.setDescription("Test_Policy_77");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// negative condition conditional customer level filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_78");
				policyData.setDescription("Test_Policy_78");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);



				// conditional and unconditional customer level filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_79");
				policyData.setDescription("Test_Policy_79");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  conditional and unconditional customer level filter item for int type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_80");
				policyData.setDescription("Test_Policy_80");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// conditional and unconditional customer level filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_81");
				policyData.setDescription("Test_Policy_81");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// conditional and unconditional customer level filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_82");
				policyData.setDescription("Test_Policy_82");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);


				// multiple conditional customer level filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_83");
				policyData.setDescription("Test_Policy_83");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  conditional customer level filter item for int type attribute 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_84");
				policyData.setDescription("Test_Policy_84");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// conditional customer level filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_85");
				policyData.setDescription("Test_Policy_85");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// conditional customer level filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_86");
				policyData.setDescription("Test_Policy_86");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);



				// multiple conditional customer level filter item for string type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_87");
				policyData.setDescription("Test_Policy_87");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  conditional customer level filter item for int type attribute 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_88");
				policyData.setDescription("Test_Policy_88");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// conditional customer level filter item for octets type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_89");
				policyData.setDescription("Test_Policy_89");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// conditional customer level filter item for VSA type attribute
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_90");
				policyData.setDescription("Test_Policy_90");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);


				// overriding radius policy filter item with customer level filter item for string type attribute with single policy.
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_91");
				policyData.setDescription("Test_Policy_91");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				//  overriding radius policy filter item with customer level filter item for int type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_92");
				policyData.setDescription("Test_Policy_92");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// overriding radius policy filter item with customer level filter item for octets type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_93");
				policyData.setDescription("Test_Policy_93");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// overriding radius policy filter item with customer level filter item for VSA type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_94");
				policyData.setDescription("Test_Policy_94");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);


				// positive conditional overriding radius policy filter item with customer level filter item for string type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_95");
				policyData.setDescription("Test_Policy_95");
				policyData.setCheckItem("0:4=1.1.1.1");
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=filter-id,0:19=call-back-num,0:20=call-back-id,0:22=frame-route");
				policyDataList.add(policyData);

				// positive conditional overriding radius policy filter item with customer level filter item for int type attribute single policy 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_96");
				policyData.setDescription("Test_Policy_96");
				policyData.setCheckItem("0:7=2");
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:6=1,0:7=2,0:13=3,0:15=4");
				policyDataList.add(policyData);


				// positive conditional overriding radius policy filter item with customer level filter item for octect type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_97");
				policyData.setDescription("Test_Policy_97");
				policyData.setCheckItem("0:3=0x123");
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:3=0x12,0:33=0x23,0:36=0x46");
				policyDataList.add(policyData);


				// positive conditional overriding radius policy filter item with customer level filter item for VSA type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_98");
				policyData.setDescription("Test_Policy_98");
				policyData.setCheckItem("9:9=test");
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:3=Cisco-Fax-Account-Id-Origin,9:4=isco-Fax-Msg-Id,9:5=Cisco-Fax-Pages");
				policyDataList.add(policyData);



				// negative conditional overriding radius policy filter item with customer level filter item for string type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_99");
				policyData.setDescription("Test_Policy_99");
				policyData.setCheckItem("0:4=1.1.1");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// negative conditional overriding radius policy filter item with customer level filter item for int type attribute single policy 
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_100");
				policyData.setDescription("Test_Policy_100");
				policyData.setCheckItem("0:7=22");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// negative conditional overriding radius policy filter item with customer level filter item for octect type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_101");
				policyData.setDescription("Test_Policy_101");
				policyData.setCheckItem("0:3=0x1233");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// negative conditional overriding radius policy filter item with customer level filter item for VSA type attribute single policy
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_102");
				policyData.setDescription("Test_Policy_102");
				policyData.setCheckItem("9:9=test2");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);



				// check item policy for string type attribute
				// overriding with multiple policy and || operator


				policyData = new PolicyData();
				policyData.setPolicyName("StringCheckItemPolicy1");
				policyData.setDescription("StringCheckItemPolicy1");
				policyData.setCheckItem("0:31=calling-station-id");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("StringCheckItemPolicy2");
				policyData.setDescription("StringCheckItemPolicy2");
				policyData.setCheckItem("0:19=callback-num");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);



				// check item policy for int type attribute
				// overriding with multiple policy and || operator


				policyData = new PolicyData();
				policyData.setPolicyName("IntCheckItemPolicy1");
				policyData.setDescription("IntCheckItemPolicy1");
				policyData.setCheckItem("0:5=1");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("IntCheckItemPolicy2");
				policyData.setDescription("IntCheckItemPolicy2");
				policyData.setCheckItem("0:10=1");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// check item policy for Octets type attribute
				// overriding with multiple policy and || operator

				policyData = new PolicyData();
				policyData.setPolicyName("OctetsCheckItemPolicy1");
				policyData.setDescription("OctetsCheckItemPolicy1");
				policyData.setCheckItem("0:3=0x12");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("OctetsCheckItemPolicy2");
				policyData.setDescription("OctetsCheckItemPolicy2");
				policyData.setCheckItem("0:33=0x34");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);


				// check item policy for VSA type attribute
				// overriding with multiple policy and || operator

				policyData = new PolicyData();
				policyData.setPolicyName("VSACheckItemPolicy1");
				policyData.setDescription("VSACheckItemPolicy1");
				policyData.setCheckItem("9:1=Cisco-AVPair");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("VSACheckItemPolicy2");
				policyData.setDescription("VSACheckItemPolicy2");
				policyData.setCheckItem("9:2=Cisco-NAS-Port");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);



				////////////////////////////



				// Add check item policy for String Attributes only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_127");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:1=*@elitecore.com");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Add check item policy for Integer Attributes only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_128");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:5=1402");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Add check item policy for Octet Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_129");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:3=0x1111");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				/*
				 * //Add check item policy for IPv6 Attribute only policyData =
				 * new PolicyData(); policyData.setPolicyName("Test_Policy_5");
				 * policyData.setDescription("Test Policy");
				 * policyData.setCheckItem
				 * ("0:97=\"0:0:0:0:0:ffff:192.1.56.10\"");
				 * policyData.setRejectItem(null);
				 * policyData.setReplyItem(null);
				 * policyDataList.add(policyData);
				 */

				// Add check item policy for VSA Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_130");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("9:2=1009");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Add reject item policy for String Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_131");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:1=*@elitecore.com");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Add reject item policy for Integer Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_132");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:5=1402");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Add reject item policy for Octet Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_133");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:3=0x1111");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Add reject item policy for VSA only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_134");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("9:2=1009");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Add reply item policy for String Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_135");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:11=test");
				policyDataList.add(policyData);
				// Add reply item policy for Integer Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_136");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:12=420");
				policyDataList.add(policyData);

				// Add reply item policy for Octet Attribute only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_137");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:36=0x420");
				policyDataList.add(policyData);

				// Add reply item policy for VSA only
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_138");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:2=420");
				policyDataList.add(policyData);

				// Check Item for String Attribute with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_139");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:1=*@elitecore.com || 0:2=rahul");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Integer Attribute with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_140");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:5= 1402 || 0:12= 1403");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Octet Attribute with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_141");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:3= 0x1111 || 0:36= 0x0016");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for VSA with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_142");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("9:2= 1009 || 9:1= trunc");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for String Attribute with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_143");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:1=*@elitecore.com && 0:2=rahul");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Integer Attribute with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_144");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:5= 1402 && 0:12= 1403");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Octet Attribute with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_145");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:3= 0x1111 && 0:36= 0x0016");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for VSA with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_146");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("9:2= 1009 && 9:1= trunc");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Standard Attribute with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_147");
				policyData.setDescription("Test Policy");
				policyData
				.setCheckItem("0:1= *@elitecore.com || 0:5 = 1402 || 0:3 = 0x1111");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for all types of VSA with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_148");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("9:1= trunc || 9:194= 200");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Standard Attribute with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_149");
				policyData.setDescription("Test Policy");
				policyData
				.setCheckItem("0:1= *@elitecore.com && 0:5 = 1402 && 0:3=0x0111");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for all types of VSA with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_150");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("9:2= 1009 && 9:195= 2");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Standard Attribute with AND and OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_151");
				policyData.setDescription("Test Policy");
				policyData
				.setCheckItem("0:1= *@elitecore.com || 0:5 = 1402 && 0:3=0x0111");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for comb. of Standard Attribute(OR) and VSA(AND)
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_152");
				policyData.setDescription("Test Policy");
				policyData
				.setCheckItem("(0:1= *@elitecore.com || 0:5 = 1402 || 0:3=0x0111)"
						+ "&& (9:2= 1009 && 9:1= trunc)");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for comb. of Standard Attribute(AND) and VSA(OR)
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_153");
				policyData.setDescription("Test Policy");
				policyData
				.setCheckItem("(0:1= *@elitecore.com && 0:5 = 1402 && 0:3=0x0111)"
						+ "&& (9:2= 1009 || 9:1= trunc)");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_154");
				policyData.setDescription("Test Policy");
				policyData
				.setCheckItem("(9:1= trunc || 9:194= 200) && (9:2= 1009 && 9:195= 2)");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for String Attribute with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_155");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:1=*@elitecore.com || 0:2=rahul");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Integer Attribute with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_156");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:5= 1402 || 0:12= 1403");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Octet Attribute with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_157");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:3= 0x1111 || 0:36= 0x0016");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for VSA with OR Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_158");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("9:2= 1009 || 9:1= trunc");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for String Attribute with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_159");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:1=*@elitecore.com && 0:2=rahul");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Integer Attribute with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_160");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:5= 1402 && 0:12= 1403");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for Octet Attribute with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_161");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("0:3= 0x1111 && 0:36= 0x0016");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				// Check Item for VSA with AND Operator
				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_162");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem("9:2= 1009 && 9:1= trunc");
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_163");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:1=*@elitecore.com(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_164");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:5=1402(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_165");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:3=0x1111(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_166");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:68=tr*(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_167");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:9:2=1009(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_168");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:1!=*@elitecore.com(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_169");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:5!=1402(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_170");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:3!=0x1111(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_171");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:0:68!=tr*(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_172");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:9:2!=1009(0:11=test)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_173");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("9:1.key=value");
				policyData.setRejectItem(null);
				policyData.setReplyItem(null);
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_174");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:1.key=value");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_175");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("9:1.k1=$REQ:9:1.key");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_176");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:9:1.k1=\"v1\"(0:18=success)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_177");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:1=elitecore");
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:18=test_policy_177");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_178");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem("0:1=eliteaaa");
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:18=test_policy_178");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_179");
				policyData.setDescription("Test Policy");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("$REQ:9:1.k1(0:18=success)");
				policyDataList.add(policyData);

				policyData = new PolicyData();
				policyData.setPolicyName("Test_Policy_180");
				policyData.setDescription("Test Concate with same attribute");
				policyData.setCheckItem(null);
				policyData.setRejectItem(null);
				policyData.setReplyItem("0:1 = \"someValue\", 0:1 = $REQ:0:1 + \"static\"");
				policyDataList.add(policyData);



				return policyDataList;

			}

			protected List<PolicyGroupData> readPolicyGroupFromDB() throws LoadCacheFailedException {
				return Collectionz.newArrayList();
			}

			@Override
			protected String getSerializeFileNameForPolicy() {
				//return "";
				return "D:"+File.separator+"trunk_1_7_11"+File.separator+"trunk"+File.separator+"Applications"+File.separator+"eliteaaa"+File.separator+ SYSTEM_FOLDER + File.separator + "_system.radiuspolicy";
			}


		};

	}

	// It should return Allow user at any time.
	@Test public void testAccessPolicy1() throws Exception {


		rpm.initCache(serverContext, false);

		RadiusPacket requestPacket = new RadiusPacket();
		requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
		IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
				RadiusAttributeConstants.USER_NAME);
		radAttr.setStringValue("elitecore");
		requestPacket.addAttribute(radAttr);
		requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
				(RadiusAttributeConstants.NAS_IP_ADDRESS)));
		requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
				(RadiusAttributeConstants.NAS_PORT)));
		requestPacket.refreshPacketHeader();

		RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

		RadiusPacket responsePacket = new RadiusPacket();
		responsePacket.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);

		responsePacket.refreshPacketHeader();


		RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(responsePacket.getBytes(), 0, serverContext);


		rpm.applyPolicies(radAuthRequest,radAuthResponse,
				"Test_Policy_1",0,null,
				null,null,false,
				true,true);

	}


	private RadAuthRequest formServiceSpecificRequest(RadiusPacket requestPacket)
			throws UnknownHostException {
		return new BaseRadiusAuthService.RadiusAuthRequestImpl(requestPacket.getBytes(), InetAddress.getLocalHost(), 0, serverContext, new SocketDetail(InetAddress.getLocalHost().getHostAddress(), 0));
	}

	@Test public void testAccessPolicy2() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_2",0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_2. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy3() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_3",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_3. Reason : " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy4() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_4",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_4. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy5() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_5",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_5. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy6() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);


			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");

			requestPacket.addAttribute(radAttr);


			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_6",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_6. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy7() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);


			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);


			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_7",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_7. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy8() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);


			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);


			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_8",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_8. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy9() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_9",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_9. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy10() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);



			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);




			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_10",0,null,
					null,null,false,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_10. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy11() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_11",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_11. Reason: " + e.getMessage() );
		}
	}


	@Test public void testAccessPolicy12() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_12",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_12. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy13() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_13",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_13. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy14() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			addAttribute.setStringValue("second attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_14",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_14. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy15() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			addAttribute.setIntValue(2);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_15",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_15. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy16() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			addAttribute.setStringValue("0x2222");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_16",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_16. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy17() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute("9:3");
			addAttribute.setStringValue("cisco Cisco-Fax-Account-Id-Origin attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_17",0,null,
					null,null,false,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_17. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy18() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			addAttribute.setStringValue("second attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_18",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_18. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy19() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			addAttribute.setIntValue(2);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_19",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy: Test_Policy_19. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy20() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			addAttribute.setStringValue("0x2222");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_20",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_20. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy21() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute("9:3");
			addAttribute.setStringValue("cisco Cisco-Fax-Account-Id-Origin attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_21",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_21. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy22() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			addAttribute.setStringValue("second attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_22",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_22. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy23() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			addAttribute.setIntValue(2);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_23",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_23. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy24() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			addAttribute.setStringValue("0x2222");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_24",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_24. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy25() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);

			addAttribute = Dictionary.getInstance().getAttribute("9:3");
			addAttribute.setStringValue("cisco Cisco-Fax-Account-Id-Origin attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_25",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_25. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy26() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_26",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_26. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy27() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_27",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_27. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy28() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_28",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_28. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy29() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_29",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_29. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy30() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_30",0,null,
					null,null,true,
					true,true);





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_30. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy31() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_31",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_31. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy32() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_32",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_32. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy33() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_33",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_33. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy34() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_34",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_34. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy35() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_35",0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_35. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy36() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_36",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_36. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy37() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_37",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_37. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy38() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_38",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_38. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy39() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_39",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_39. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy40() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_40",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_40. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy41() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("testelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_41",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_41. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy42() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_42",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_42. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy43() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_43",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_43. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy44() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_44",0,null,
					null,null,true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_44. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy45() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_45",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_45. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy46() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_46",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_46. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy47() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_47",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_47. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy48() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_48",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_48. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy49() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitetestelitetestelite");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_49",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_49. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy50() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elita");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			addAttribute.setStringValue("add attribute");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_50",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_50. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy51() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elita");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			addAttribute.setIntValue(10);
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_51",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_51. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy52() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elita");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			addAttribute.setStringValue("0x1111");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_52",0,null,
					null,null,true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_52. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy53() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elita");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_53",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_53. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy54() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param2Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM2);
			if (param2Attr != null) {
				param2Attr.setStringValue("param2");
				profileAVPairGroupedAttr.addTLVAttribute(param2Attr);
			}


			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			radAuthResponse.addAttribute(profileAVPairGroupedAttr);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_54",0,null,
					null,null,false,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_54. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy55() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param2Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM2);
			if (param2Attr != null) {
				param2Attr.setStringValue("param2");
				profileAVPairGroupedAttr.addTLVAttribute(param2Attr);
			}


			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			radAuthResponse.addAttribute(profileAVPairGroupedAttr);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_55",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_55. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy56() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param2Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM2);
			if (param2Attr != null) {
				param2Attr.setStringValue("param2");
				profileAVPairGroupedAttr.addTLVAttribute(param2Attr);
			}


			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			radAuthResponse.addAttribute(profileAVPairGroupedAttr);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_56",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_56. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy57() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("9:2");
			addAttribute.setStringValue("cisco nas port attr");
			radAuthResponse.addAttribute(addAttribute);

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param2Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM2);
			if (param2Attr != null) {
				param2Attr.setStringValue("param2");
				profileAVPairGroupedAttr.addTLVAttribute(param2Attr);
			}


			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			radAuthResponse.addAttribute(profileAVPairGroupedAttr);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_57",0,null,
					null,null,true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_57. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy58() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param2Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM2);
			if (param2Attr != null) {
				param2Attr.setStringValue("param2");
				profileAVPairGroupedAttr.addTLVAttribute(param2Attr);
			}


			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			requestPacket.addAttribute(profileAVPairGroupedAttr);


			requestPacket.refreshPacketHeader();

			//	System.out.println("requst packet for test 58 :"+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("0:6");
			addAttribute.setIntValue(1);
			radAuthResponse.addAttribute(addAttribute);

			System.out.println("before filter in group type request : "+radAuthResponse);

			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_58",0,null,
					null,null,true,
					true,true);

			System.out.println("after filter in group type request : "+radAuthResponse);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_58. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy59() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param2Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM2);
			if (param2Attr != null) {
				param2Attr.setStringValue("param2");
				profileAVPairGroupedAttr.addTLVAttribute(param2Attr);
			}


			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			requestPacket.addAttribute(profileAVPairGroupedAttr);


			requestPacket.refreshPacketHeader();

			//	System.out.println("requst packet for test 58 :"+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("0:6");
			addAttribute.setIntValue(1);
			radAuthResponse.addAttribute(addAttribute);

			System.out.println("before filter in group type request : "+radAuthResponse);

			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_59",0,null,
					null,null,true,
					true,true);

			System.out.println("after filter in group type request : "+radAuthResponse);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_59. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy60() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));




			requestPacket.refreshPacketHeader();

			//	System.out.println("requst packet for test 58 :"+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("0:6");
			addAttribute.setIntValue(1);
			radAuthResponse.addAttribute(addAttribute);

			System.out.println("before filter in group type request : "+radAuthResponse);

			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_60",0,null,
					null,null,true,
					true,true);

			System.out.println("after filter in group type request : "+radAuthResponse);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_60. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy61() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			requestPacket.addAttribute(profileAVPairGroupedAttr);


			requestPacket.refreshPacketHeader();

			//	System.out.println("requst packet for test 58 :"+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("0:6");
			addAttribute.setIntValue(1);
			radAuthResponse.addAttribute(addAttribute);

			System.out.println("before filter in group type request : "+radAuthResponse);

			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_61",0,null,
					null,null,false,
					true,true);

			System.out.println("after filter in group type request : "+radAuthResponse);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_61. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy62() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);


			IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM1);
			if (param1Attr != null) {
				param1Attr.setStringValue("param1");
				profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
			}		



			IRadiusAttribute param3Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_PROFILE_AVPAIR,RadiusAttributeConstants.ELITE_PARAM3);
			if (param3Attr != null) {
				param3Attr.setStringValue("param3");
				profileAVPairGroupedAttr.addTLVAttribute(param3Attr);
			}

			requestPacket.addAttribute(profileAVPairGroupedAttr);


			requestPacket.refreshPacketHeader();

			//	System.out.println("requst packet for test 58 :"+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			IRadiusAttribute addAttribute = Dictionary.getInstance().getAttribute("0:6");
			addAttribute.setIntValue(1);
			radAuthResponse.addAttribute(addAttribute);

			System.out.println("before filter in group type request : "+radAuthResponse);

			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_62",0,null,
					null,null,true,
					true,true);

			System.out.println("after filter in group type request : "+radAuthResponse);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_62. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy63() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_63",0,null,
					null,"$RES:0:19",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_63. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy64() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_64",0,null,
					null,"$RES:0:7",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_64. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy65() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_65",0,null,
					null,"$RES:0:33",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_65. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy66() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_66",0,null,
					null,"$RES:9:4",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_66. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy67() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			System.out.println("before filter at customer level "+radAuthResponse);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_67",0,null,
					null,"$REQ:0:1=elitecore($RES:0:19)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_67. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy68() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_68",0,null,
					null,"$REQ:0:1=elitecore($RES:0:7)",true,
					true,true);





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_68. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy69() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_69",0,null,
					null,"$REQ:0:1=elitecore($RES:0:33)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_69. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy70() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_70",0,null,
					null,"$REQ:0:1=elitecore($RES:9:4)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_70. Reason: " + e.getMessage());
		}
	}



	@Test public void testAccessPolicy71() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_71",0,null,
					null,"$REQ:0:1!=elite($RES:0:19)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_71. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy72() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_72",0,null,
					null,"$REQ:0:1!=elite($RES:0:7)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_72. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy73() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_73",0,null,
					null,"$REQ:0:1!=elite($RES:0:33)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_73. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy74() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_74",0,null,
					null,"$REQ:0:1!=elite($RES:9:4)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_74. Reason: " + e.getMessage());
		}
	}



	@Test public void testAccessPolicy75() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_75",0,null,
					null,"$REQ:0:1=elite($RES:0:19)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_75. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy76() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_76",0,null,
					null,"$REQ:0:1=elite($RES:0:7)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_76. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy77() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_77",0,null,
					null,"$REQ:0:1=elite($RES:0:33)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_77. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy78() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_78",0,null,
					null,"$REQ:0:1=elite($RES:9:4)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_78. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy79() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			System.out.println("before filter at customer level "+radAuthResponse);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_79",0,null,
					null,"$REQ:0:1=elitecore($RES:0:19),$RES:0:20",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_79. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy80() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_80",0,null,
					null,"$REQ:0:1=elitecore($RES:0:7),$RES:0:13",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_80. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy81() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_81",0,null,
					null,"$REQ:0:1=elitecore($RES:0:33),$RES:0:36",true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_81. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy82() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_82",0,null,
					null,"$REQ:0:1=elitecore($RES:9:4),$RES:9:5",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_82. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy83() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			System.out.println("before filter at customer level "+radAuthResponse);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_83",0,null,
					null,"$REQ:0:1=elitecore($RES:0:19),$REQ:0:4=1.1.1.1($RES:0:20)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_83. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy84() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute framedProtocolAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttr.setIntValue(2);
			requestPacket.addAttribute(framedProtocolAttr);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_84",0,null,
					null,"$REQ:0:1=elitecore($RES:0:7),$REQ:0:7=2($RES:0:13)",true,
					true,true);





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_84. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy85() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute chapPassAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPassAttr.setStringValue("0x123");
			requestPacket.addAttribute(chapPassAttr);

			requestPacket.refreshPacketHeader();




			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_85",0,null,
					null,"$REQ:0:1=elitecore($RES:0:33),$REQ:0:3=0x123($RES:0:36)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_85. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy86() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute FaxRecipientCount = Dictionary.getInstance().getAttribute("9:9");
			FaxRecipientCount.setStringValue("test");
			requestPacket.addAttribute(FaxRecipientCount);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_86",0,null,
					null,"$REQ:0:1=elitecore($RES:9:4),$REQ:9:9=test($RES:9:5)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_86. Reason: " + e.getMessage());
		}
	}



	@Test public void testAccessPolicy87() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			System.out.println("before filter at customer level "+radAuthResponse);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_87",0,null,
					null,"$REQ:0:1=elitecore($RES:0:19),$REQ:0:4=1.1.1.1($RES:0:20)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_87. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy88() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute framedProtocolAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttr.setIntValue(3);
			requestPacket.addAttribute(framedProtocolAttr);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_88",0,null,
					null,"$REQ:0:1=elitecore($RES:0:7),$REQ:0:7=2($RES:0:13)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_88. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy89() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute chapPassAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPassAttr.setStringValue("0x12");
			requestPacket.addAttribute(chapPassAttr);

			requestPacket.refreshPacketHeader();




			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_89",0,null,
					null,"$REQ:0:1=elitecore($RES:0:33),$REQ:0:3=0x123($RES:0:36)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_89. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy90() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute FaxRecipientCount = Dictionary.getInstance().getAttribute("9:9");
			FaxRecipientCount.setStringValue("test");
			requestPacket.addAttribute(FaxRecipientCount);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_90",0,null,
					null,"$REQ:0:1=elitecore($RES:9:4),$REQ:9:9=0x123($RES:9:5)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_90. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy91() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_91",0,null,
					null,"$Test_Policy_91($RES:0:19)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_91. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy92() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_92",0,null,
					null,"$Test_Policy_92($RES:0:7)",true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_92. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy93() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_93",0,null,
					null,"$Test_Policy_93($RES:0:33)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_93. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy94() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_94",0,null,
					null,"$Test_Policy_94($RES:9:4)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_94. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy95() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			System.out.println("before filter at customer level "+radAuthResponse);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_95",0,null,
					null,"$Test_Policy_95($RES:0:20)",true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_95. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy96() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute framedProtocolAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttr.setIntValue(2);
			requestPacket.addAttribute(framedProtocolAttr);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_96",0,null,
					null,"$Test_Policy_96($RES:0:13)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_96. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy97() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute chapPassAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPassAttr.setStringValue("0x123");
			requestPacket.addAttribute(chapPassAttr);

			requestPacket.refreshPacketHeader();




			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_97",0,null,
					null,"$Test_Policy_97($RES:0:36)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_97. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy98() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute FaxRecipientCount = Dictionary.getInstance().getAttribute("9:9");
			FaxRecipientCount.setStringValue("test");
			requestPacket.addAttribute(FaxRecipientCount);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"Test_Policy_98",0,null,
					null,"$Test_Policy_98($RES:9:5)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_98. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy99() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute filterIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			filterIdAttribute.setStringValue("filter-id");
			radAuthResponse.addAttribute(filterIdAttribute);

			IRadiusAttribute callBackNumAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callBackNumAttribute.setStringValue("call-back-num");
			radAuthResponse.addAttribute(callBackNumAttribute);

			IRadiusAttribute callBackIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			callBackIdAttribute.setStringValue("call-back-id");
			radAuthResponse.addAttribute(callBackIdAttribute);

			IRadiusAttribute frameRouteAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTE);
			frameRouteAttribute.setStringValue("frame-route");
			radAuthResponse.addAttribute(frameRouteAttribute);

			try{

				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"Test_Policy_99",0,null,
						null,"$Test_Policy_99($RES:0:20)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}	




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_99. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy100() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute framedProtocolAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttr.setIntValue(2);
			requestPacket.addAttribute(framedProtocolAttr);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute serviceTypeAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			serviceTypeAttribute.setStringValue("1");
			radAuthResponse.addAttribute(serviceTypeAttribute);

			IRadiusAttribute framedProtocolAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttribute.setStringValue("2");
			radAuthResponse.addAttribute(framedProtocolAttribute);

			IRadiusAttribute framedCompressionAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_COMPRESSION);
			framedCompressionAttribute.setStringValue("3");
			radAuthResponse.addAttribute(framedCompressionAttribute);

			IRadiusAttribute loginServiceAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_SERVICE);
			loginServiceAttribute.setStringValue("4");
			radAuthResponse.addAttribute(loginServiceAttribute);



			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"Test_Policy_100",0,null,
						null,"$Test_Policy_100($RES:0:13)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}	




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_100. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy101() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute chapPassAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPassAttr.setStringValue("0x123");
			requestPacket.addAttribute(chapPassAttr);

			requestPacket.refreshPacketHeader();




			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute chapPassAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPassAttribute.setStringValue("0x12");
			radAuthResponse.addAttribute(chapPassAttribute);

			IRadiusAttribute proxyStateAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttribute.setStringValue("0x23");
			radAuthResponse.addAttribute(proxyStateAttribute);

			IRadiusAttribute loginLateGroup = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			loginLateGroup.setStringValue("0x46");
			radAuthResponse.addAttribute(loginLateGroup);



			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"Test_Policy_101",0,null,
						null,"$Test_Policy_101($RES:0:36)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}	


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_101. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy102() throws ManagerInitialzationException {


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute FaxRecipientCount = Dictionary.getInstance().getAttribute("9:9");
			FaxRecipientCount.setStringValue("test");
			requestPacket.addAttribute(FaxRecipientCount);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute ciscoFaxAccountIdOrigin = Dictionary.getInstance().getAttribute("9:3");
			ciscoFaxAccountIdOrigin.setStringValue("Cisco-Fax-Account-Id-Origin");
			radAuthResponse.addAttribute(ciscoFaxAccountIdOrigin);

			IRadiusAttribute ciscoFaxMsgId = Dictionary.getInstance().getAttribute("9:4");
			ciscoFaxMsgId.setStringValue("cisco-Fax-Msg-Id");
			radAuthResponse.addAttribute(ciscoFaxMsgId);

			IRadiusAttribute ciscoFaxPages = Dictionary.getInstance().getAttribute("9:5");
			ciscoFaxPages.setStringValue("Cisco-Fax-Pages");
			radAuthResponse.addAttribute(ciscoFaxPages);

			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"Test_Policy_102",0,null,
						null,"$Test_Policy_102($RES:9:5)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}	



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_102. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy103() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			IRadiusAttribute callingStationAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
			callingStationAttribute.setStringValue("calling-station-id");
			requestPacket.addAttribute(callingStationAttribute);

			IRadiusAttribute callingBackAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callingBackAttribute.setStringValue("call");
			requestPacket.addAttribute(callingBackAttribute);


			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute filterIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			filterIdAttribute.setStringValue("filter-id");
			radAuthResponse.addAttribute(filterIdAttribute);

			IRadiusAttribute callBackNumAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callBackNumAttribute.setStringValue("call-back-num");
			radAuthResponse.addAttribute(callBackNumAttribute);

			IRadiusAttribute callBackIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			callBackIdAttribute.setStringValue("call-back-id");
			radAuthResponse.addAttribute(callBackIdAttribute);

			IRadiusAttribute frameRouteAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTE);
			frameRouteAttribute.setStringValue("frame-route");
			radAuthResponse.addAttribute(frameRouteAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"StringCheckItemPolicy1 || StringCheckItemPolicy2",0,null,
					null,"$StringCheckItemPolicy1($RES:0:19), $StringCheckItemPolicy2($RES:0:20)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_103. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy104() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			IRadiusAttribute callingStationAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
			callingStationAttribute.setStringValue("calling-station");
			requestPacket.addAttribute(callingStationAttribute);

			IRadiusAttribute callingBackAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callingBackAttribute.setStringValue("callback-num");
			requestPacket.addAttribute(callingBackAttribute);


			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute filterIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			filterIdAttribute.setStringValue("filter-id");
			radAuthResponse.addAttribute(filterIdAttribute);

			IRadiusAttribute callBackNumAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callBackNumAttribute.setStringValue("call-back-num");
			radAuthResponse.addAttribute(callBackNumAttribute);

			IRadiusAttribute callBackIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			callBackIdAttribute.setStringValue("call-back-id");
			radAuthResponse.addAttribute(callBackIdAttribute);

			IRadiusAttribute frameRouteAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTE);
			frameRouteAttribute.setStringValue("frame-route");
			radAuthResponse.addAttribute(frameRouteAttribute);

			try{

				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"StringCheckItemPolicy1 || StringCheckItemPolicy2",0,null,
						null,"$StringCheckItemPolicy1($RES:0:19), $StringCheckItemPolicy2($RES:0:20)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}	


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_104. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy105() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			IRadiusAttribute callingStationAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
			callingStationAttribute.setStringValue("calling-station-id");
			requestPacket.addAttribute(callingStationAttribute);

			IRadiusAttribute callingBackAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callingBackAttribute.setStringValue("callback-num");
			requestPacket.addAttribute(callingBackAttribute);


			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute filterIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			filterIdAttribute.setStringValue("filter-id");
			radAuthResponse.addAttribute(filterIdAttribute);

			IRadiusAttribute callBackNumAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callBackNumAttribute.setStringValue("call-back-num");
			radAuthResponse.addAttribute(callBackNumAttribute);

			IRadiusAttribute callBackIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			callBackIdAttribute.setStringValue("call-back-id");
			radAuthResponse.addAttribute(callBackIdAttribute);

			IRadiusAttribute frameRouteAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTE);
			frameRouteAttribute.setStringValue("frame-route");
			radAuthResponse.addAttribute(frameRouteAttribute);

			try{

				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"StringCheckItemPolicy1 || StringCheckItemPolicy2",0,null,
						null,"$StringCheckItemPolicy1($RES:0:19), $StringCheckItemPolicy2($RES:0:20)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}	




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_105. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy106() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			IRadiusAttribute callingStationAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
			callingStationAttribute.setStringValue("calling-station");
			requestPacket.addAttribute(callingStationAttribute);

			IRadiusAttribute callingBackAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callingBackAttribute.setStringValue("callback");
			requestPacket.addAttribute(callingBackAttribute);


			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute filterIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			filterIdAttribute.setStringValue("filter-id");
			radAuthResponse.addAttribute(filterIdAttribute);

			IRadiusAttribute callBackNumAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callBackNumAttribute.setStringValue("call-back-num");
			radAuthResponse.addAttribute(callBackNumAttribute);

			IRadiusAttribute callBackIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			callBackIdAttribute.setStringValue("call-back-id");
			radAuthResponse.addAttribute(callBackIdAttribute);

			IRadiusAttribute frameRouteAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTE);
			frameRouteAttribute.setStringValue("frame-route");
			radAuthResponse.addAttribute(frameRouteAttribute);

			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"StringCheckItemPolicy1 || StringCheckItemPolicy2",0,null,
						null,"$StringCheckItemPolicy1($RES:0:19), $StringCheckItemPolicy2($RES:0:20)",true,
						true,true);


			}catch (Exception e) {
				// TODO: handle exception
			}





		} catch (Exception e) {

			fail("failed to apply policy : Test_Policy_106. Reason: " + e.getMessage());
		}
	}




	@Test public void testAccessPolicy107() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute framedRoutingAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTING);
			framedRoutingAttribute.setIntValue(2);
			requestPacket.addAttribute(framedRoutingAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			serviceType.setIntValue(1);
			radAuthResponse.addAttribute(serviceType);

			IRadiusAttribute framedProtocolAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttribute.setIntValue(2);
			radAuthResponse.addAttribute(framedProtocolAttribute);

			IRadiusAttribute framedCompressionAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_COMPRESSION);
			framedCompressionAttribute.setIntValue(3);
			radAuthResponse.addAttribute(framedCompressionAttribute);

			IRadiusAttribute loginServiceAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_SERVICE);
			loginServiceAttribute.setIntValue(4);
			radAuthResponse.addAttribute(loginServiceAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"IntCheckItemPolicy1 || IntCheckItemPolicy2",0,null,
					null,"$IntCheckItemPolicy1($RES:0:7), $IntCheckItemPolicy2($RES:0:13)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_107. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy108() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(2);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute framedRoutingAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTING);
			framedRoutingAttribute.setIntValue(1);
			requestPacket.addAttribute(framedRoutingAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			serviceType.setIntValue(1);
			radAuthResponse.addAttribute(serviceType);

			IRadiusAttribute framedProtocolAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttribute.setIntValue(2);
			radAuthResponse.addAttribute(framedProtocolAttribute);

			IRadiusAttribute framedCompressionAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_COMPRESSION);
			framedCompressionAttribute.setIntValue(3);
			radAuthResponse.addAttribute(framedCompressionAttribute);

			IRadiusAttribute loginServiceAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_SERVICE);
			loginServiceAttribute.setIntValue(4);
			radAuthResponse.addAttribute(loginServiceAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"IntCheckItemPolicy1 || IntCheckItemPolicy2",0,null,
					null,"$IntCheckItemPolicy1($RES:0:7), $IntCheckItemPolicy2($RES:0:13)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_108. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy109() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute framedRoutingAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTING);
			framedRoutingAttribute.setIntValue(1);
			requestPacket.addAttribute(framedRoutingAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			serviceType.setIntValue(1);
			radAuthResponse.addAttribute(serviceType);

			IRadiusAttribute framedProtocolAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttribute.setIntValue(2);
			radAuthResponse.addAttribute(framedProtocolAttribute);

			IRadiusAttribute framedCompressionAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_COMPRESSION);
			framedCompressionAttribute.setIntValue(3);
			radAuthResponse.addAttribute(framedCompressionAttribute);

			IRadiusAttribute loginServiceAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_SERVICE);
			loginServiceAttribute.setIntValue(4);
			radAuthResponse.addAttribute(loginServiceAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"IntCheckItemPolicy1 || IntCheckItemPolicy2",0,null,
					null,"$IntCheckItemPolicy1($RES:0:7), $IntCheckItemPolicy2($RES:0:13)",true,
					true,true);


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_109. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy110() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(2);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute framedRoutingAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTING);
			framedRoutingAttribute.setIntValue(2);
			requestPacket.addAttribute(framedRoutingAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			serviceType.setIntValue(1);
			radAuthResponse.addAttribute(serviceType);

			IRadiusAttribute framedProtocolAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttribute.setIntValue(2);
			radAuthResponse.addAttribute(framedProtocolAttribute);

			IRadiusAttribute framedCompressionAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_COMPRESSION);
			framedCompressionAttribute.setIntValue(3);
			radAuthResponse.addAttribute(framedCompressionAttribute);

			IRadiusAttribute loginServiceAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_SERVICE);
			loginServiceAttribute.setIntValue(4);
			radAuthResponse.addAttribute(loginServiceAttribute);


			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"IntCheckItemPolicy1 || IntCheckItemPolicy2",0,null,
						null,"$IntCheckItemPolicy1($RES:0:7), $IntCheckItemPolicy2($RES:0:13)",true,
						true,true);

			}catch (Exception e) {
				// TODO: handle exception
			}





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_110. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy111() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute chapPasswordAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttribute.setStringValue("0x12");
			requestPacket.addAttribute(chapPasswordAttribute);


			IRadiusAttribute proxyStateAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttribute.setStringValue("0x21");
			requestPacket.addAttribute(proxyStateAttribute);


			requestPacket.refreshPacketHeader();
			System.out.println("request packet : "+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute chapPasswordAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttr.setStringValue("0x12");
			radAuthResponse.addAttribute(chapPasswordAttr);

			IRadiusAttribute proxyStateAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttr.setStringValue("0x56");
			radAuthResponse.addAttribute(proxyStateAttr);

			IRadiusAttribute loginLatAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			loginLatAttribute.setStringValue("0x57");
			radAuthResponse.addAttribute(loginLatAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"OctetsCheckItemPolicy1 || OctetsCheckItemPolicy2",0,null,
					null,"$OctetsCheckItemPolicy1($RES:0:33), $OctetsCheckItemPolicy2($RES:0:36)",true,
					true,true);





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_111. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy112() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute chapPasswordAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttribute.setStringValue("0x23");
			requestPacket.addAttribute(chapPasswordAttribute);


			IRadiusAttribute proxyStateAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttribute.setStringValue("0x34");
			requestPacket.addAttribute(proxyStateAttribute);


			requestPacket.refreshPacketHeader();
			System.out.println("request packet : "+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute chapPasswordAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttr.setStringValue("0x12");
			radAuthResponse.addAttribute(chapPasswordAttr);

			IRadiusAttribute proxyStateAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttr.setStringValue("0x56");
			radAuthResponse.addAttribute(proxyStateAttr);

			IRadiusAttribute loginLatAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			loginLatAttribute.setStringValue("0x57");
			radAuthResponse.addAttribute(loginLatAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"OctetsCheckItemPolicy1 || OctetsCheckItemPolicy2",0,null,
					null,"$OctetsCheckItemPolicy1($RES:0:33), $OctetsCheckItemPolicy2($RES:0:36)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_112. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy113() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute chapPasswordAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttribute.setStringValue("0x12");
			requestPacket.addAttribute(chapPasswordAttribute);


			IRadiusAttribute proxyStateAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttribute.setStringValue("0x34");
			requestPacket.addAttribute(proxyStateAttribute);


			requestPacket.refreshPacketHeader();
			System.out.println("request packet : "+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute chapPasswordAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttr.setStringValue("0x12");
			radAuthResponse.addAttribute(chapPasswordAttr);

			IRadiusAttribute proxyStateAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttr.setStringValue("0x56");
			radAuthResponse.addAttribute(proxyStateAttr);

			IRadiusAttribute loginLatAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			loginLatAttribute.setStringValue("0x57");
			radAuthResponse.addAttribute(loginLatAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"OctetsCheckItemPolicy1 || OctetsCheckItemPolicy2",0,null,
					null,"$OctetsCheckItemPolicy1($RES:0:33), $OctetsCheckItemPolicy2($RES:0:36)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_113. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy114() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute chapPasswordAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttribute.setStringValue("0x23");
			requestPacket.addAttribute(chapPasswordAttribute);


			IRadiusAttribute proxyStateAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttribute.setStringValue("0x56");
			requestPacket.addAttribute(proxyStateAttribute);


			requestPacket.refreshPacketHeader();
			System.out.println("request packet : "+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute chapPasswordAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttr.setStringValue("0x12");
			radAuthResponse.addAttribute(chapPasswordAttr);

			IRadiusAttribute proxyStateAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttr.setStringValue("0x56");
			radAuthResponse.addAttribute(proxyStateAttr);

			IRadiusAttribute loginLatAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			loginLatAttribute.setStringValue("0x57");
			radAuthResponse.addAttribute(loginLatAttribute);


			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"OctetsCheckItemPolicy1 || OctetsCheckItemPolicy2",0,null,
						null,"$OctetsCheckItemPolicy1($RES:0:33), $OctetsCheckItemPolicy2($RES:0:36)",true,
						true,true);

			}catch (Exception e) {
				// TODO: handle exception
			}



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_114. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy115() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute vsa1 = Dictionary.getInstance().getAttribute("9:1");
			vsa1.setStringValue("Cisco-AVPair");
			requestPacket.addAttribute(vsa1);


			IRadiusAttribute vsa2 = Dictionary.getInstance().getAttribute("9:2");
			vsa2.setStringValue("Cisco-NAS");
			requestPacket.addAttribute(vsa2);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute vsa3 = Dictionary.getInstance().getAttribute("9:3");
			vsa3.setStringValue("Cisco-Fax-Account-Id-Origin");
			radAuthResponse.addAttribute(vsa3);

			IRadiusAttribute vsa4 = Dictionary.getInstance().getAttribute("9:4");
			vsa4.setStringValue("Cisco-Fax-Msg-Id");
			radAuthResponse.addAttribute(vsa4);

			IRadiusAttribute vsa5 = Dictionary.getInstance().getAttribute("9:5");
			vsa5.setStringValue("Cisco-Fax-Pages");
			radAuthResponse.addAttribute(vsa5);




			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"VSACheckItemPolicy1 || VSACheckItemPolicy2",0,null,
					null,"$VSACheckItemPolicy1($RES:9:4), $VSACheckItemPolicy2($RES:9:5)",true,
					true,true);





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_115. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy116() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute vsa1 = Dictionary.getInstance().getAttribute("9:1");
			vsa1.setStringValue("Cisco");
			requestPacket.addAttribute(vsa1);


			IRadiusAttribute vsa2 = Dictionary.getInstance().getAttribute("9:2");
			vsa2.setStringValue("Cisco-NAS-Port");
			requestPacket.addAttribute(vsa2);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute vsa3 = Dictionary.getInstance().getAttribute("9:3");
			vsa3.setStringValue("Cisco-Fax-Account-Id-Origin");
			radAuthResponse.addAttribute(vsa3);

			IRadiusAttribute vsa4 = Dictionary.getInstance().getAttribute("9:4");
			vsa4.setStringValue("Cisco-Fax-Msg-Id");
			radAuthResponse.addAttribute(vsa4);

			IRadiusAttribute vsa5 = Dictionary.getInstance().getAttribute("9:5");
			vsa5.setStringValue("Cisco-Fax-Pages");
			radAuthResponse.addAttribute(vsa5);




			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"VSACheckItemPolicy1 || VSACheckItemPolicy2",0,null,
					null,"$VSACheckItemPolicy1($RES:9:4), $VSACheckItemPolicy2($RES:9:5)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_116. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy117() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute vsa1 = Dictionary.getInstance().getAttribute("9:1");
			vsa1.setStringValue("Cisco-AVPair");
			requestPacket.addAttribute(vsa1);


			IRadiusAttribute vsa2 = Dictionary.getInstance().getAttribute("9:2");
			vsa2.setStringValue("Cisco-NAS-Port");
			requestPacket.addAttribute(vsa2);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute vsa3 = Dictionary.getInstance().getAttribute("9:3");
			vsa3.setStringValue("Cisco-Fax-Account-Id-Origin");
			radAuthResponse.addAttribute(vsa3);

			IRadiusAttribute vsa4 = Dictionary.getInstance().getAttribute("9:4");
			vsa4.setStringValue("Cisco-Fax-Msg-Id");
			radAuthResponse.addAttribute(vsa4);

			IRadiusAttribute vsa5 = Dictionary.getInstance().getAttribute("9:5");
			vsa5.setStringValue("Cisco-Fax-Pages");
			radAuthResponse.addAttribute(vsa5);




			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"VSACheckItemPolicy1 || VSACheckItemPolicy2",0,null,
					null,"$VSACheckItemPolicy1($RES:9:4), $VSACheckItemPolicy2($RES:9:5)",true,
					true,true);



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_117. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy118() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute vsa1 = Dictionary.getInstance().getAttribute("9:1");
			vsa1.setStringValue("Cisco-AVP");
			requestPacket.addAttribute(vsa1);


			IRadiusAttribute vsa2 = Dictionary.getInstance().getAttribute("9:2");
			vsa2.setStringValue("Cisco");
			requestPacket.addAttribute(vsa2);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute vsa3 = Dictionary.getInstance().getAttribute("9:3");
			vsa3.setStringValue("Cisco-Fax-Account-Id-Origin");
			radAuthResponse.addAttribute(vsa3);

			IRadiusAttribute vsa4 = Dictionary.getInstance().getAttribute("9:4");
			vsa4.setStringValue("Cisco-Fax-Msg-Id");
			radAuthResponse.addAttribute(vsa4);

			IRadiusAttribute vsa5 = Dictionary.getInstance().getAttribute("9:5");
			vsa5.setStringValue("Cisco-Fax-Pages");
			radAuthResponse.addAttribute(vsa5);



			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"VSACheckItemPolicy1 || VSACheckItemPolicy2",0,null,
						null,"$VSACheckItemPolicy1($RES:9:4), $VSACheckItemPolicy2($RES:9:5)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_118. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy119() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			IRadiusAttribute callingStationAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
			callingStationAttribute.setStringValue("calling-station-id");
			requestPacket.addAttribute(callingStationAttribute);

			IRadiusAttribute callingBackAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callingBackAttribute.setStringValue("callback-num");
			requestPacket.addAttribute(callingBackAttribute);


			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute filterIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			filterIdAttribute.setStringValue("filter-id");
			radAuthResponse.addAttribute(filterIdAttribute);

			IRadiusAttribute callBackNumAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callBackNumAttribute.setStringValue("call-back-num");
			radAuthResponse.addAttribute(callBackNumAttribute);

			IRadiusAttribute callBackIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			callBackIdAttribute.setStringValue("call-back-id");
			radAuthResponse.addAttribute(callBackIdAttribute);

			IRadiusAttribute frameRouteAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTE);
			frameRouteAttribute.setStringValue("frame-route");
			radAuthResponse.addAttribute(frameRouteAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"StringCheckItemPolicy1 && StringCheckItemPolicy2",0,null,
					null,"$StringCheckItemPolicy1($RES:0:19), $StringCheckItemPolicy2($RES:0:20)",true,
					true,true);

		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_119. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy120() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));

			IRadiusAttribute nasIpAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
			nasIpAttribute.setStringValue("1.1.1.1");
			requestPacket.addAttribute(nasIpAttribute);

			IRadiusAttribute callingStationAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
			callingStationAttribute.setStringValue("calling-station");
			requestPacket.addAttribute(callingStationAttribute);

			IRadiusAttribute callingBackAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callingBackAttribute.setStringValue("callback");
			requestPacket.addAttribute(callingBackAttribute);


			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute filterIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FILTER_ID);
			filterIdAttribute.setStringValue("filter-id");
			radAuthResponse.addAttribute(filterIdAttribute);

			IRadiusAttribute callBackNumAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_NUMBER);
			callBackNumAttribute.setStringValue("call-back-num");
			radAuthResponse.addAttribute(callBackNumAttribute);

			IRadiusAttribute callBackIdAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLBACK_ID);
			callBackIdAttribute.setStringValue("call-back-id");
			radAuthResponse.addAttribute(callBackIdAttribute);

			IRadiusAttribute frameRouteAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTE);
			frameRouteAttribute.setStringValue("frame-route");
			radAuthResponse.addAttribute(frameRouteAttribute);

			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"StringCheckItemPolicy1 && StringCheckItemPolicy2",0,null,
						null,"$StringCheckItemPolicy1($RES:0:19), $StringCheckItemPolicy2($RES:0:20)",true,
						true,true);

			}catch (Exception e) {
				// TODO: handle exception
			}



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_120. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy121() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute framedRoutingAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTING);
			framedRoutingAttribute.setIntValue(1);
			requestPacket.addAttribute(framedRoutingAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			serviceType.setIntValue(1);
			radAuthResponse.addAttribute(serviceType);

			IRadiusAttribute framedProtocolAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttribute.setIntValue(2);
			radAuthResponse.addAttribute(framedProtocolAttribute);

			IRadiusAttribute framedCompressionAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_COMPRESSION);
			framedCompressionAttribute.setIntValue(3);
			radAuthResponse.addAttribute(framedCompressionAttribute);

			IRadiusAttribute loginServiceAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_SERVICE);
			loginServiceAttribute.setIntValue(4);
			radAuthResponse.addAttribute(loginServiceAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"IntCheckItemPolicy1 && IntCheckItemPolicy2",0,null,
					null,"$IntCheckItemPolicy1($RES:0:7), $IntCheckItemPolicy2($RES:0:13)",true,
					true,true);

		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_121. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy122() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(2);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute framedRoutingAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_ROUTING);
			framedRoutingAttribute.setIntValue(2);
			requestPacket.addAttribute(framedRoutingAttribute);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
			serviceType.setIntValue(1);
			radAuthResponse.addAttribute(serviceType);

			IRadiusAttribute framedProtocolAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_PROTOCOL);
			framedProtocolAttribute.setIntValue(2);
			radAuthResponse.addAttribute(framedProtocolAttribute);

			IRadiusAttribute framedCompressionAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_COMPRESSION);
			framedCompressionAttribute.setIntValue(3);
			radAuthResponse.addAttribute(framedCompressionAttribute);

			IRadiusAttribute loginServiceAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_SERVICE);
			loginServiceAttribute.setIntValue(4);
			radAuthResponse.addAttribute(loginServiceAttribute);


			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"IntCheckItemPolicy1 && IntCheckItemPolicy2",0,null,
						null,"$IntCheckItemPolicy1($RES:0:7), $IntCheckItemPolicy2($RES:0:13)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}



		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_122. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy123() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute chapPasswordAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttribute.setStringValue("0x12");
			requestPacket.addAttribute(chapPasswordAttribute);


			IRadiusAttribute proxyStateAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttribute.setStringValue("0x34");
			requestPacket.addAttribute(proxyStateAttribute);


			requestPacket.refreshPacketHeader();
			System.out.println("request packet : "+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute chapPasswordAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttr.setStringValue("0x12");
			radAuthResponse.addAttribute(chapPasswordAttr);

			IRadiusAttribute proxyStateAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttr.setStringValue("0x56");
			radAuthResponse.addAttribute(proxyStateAttr);

			IRadiusAttribute loginLatAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			loginLatAttribute.setStringValue("0x57");
			radAuthResponse.addAttribute(loginLatAttribute);



			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"OctetsCheckItemPolicy1 && OctetsCheckItemPolicy2",0,null,
					null,"$OctetsCheckItemPolicy1($RES:0:33), $OctetsCheckItemPolicy2($RES:0:36)",true,
					true,true);





		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_123. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy124() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute chapPasswordAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttribute.setStringValue("0x1");
			requestPacket.addAttribute(chapPasswordAttribute);


			IRadiusAttribute proxyStateAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttribute.setStringValue("0x3");
			requestPacket.addAttribute(proxyStateAttribute);


			requestPacket.refreshPacketHeader();
			System.out.println("request packet : "+requestPacket);

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute chapPasswordAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CHAP_PASSWORD);
			chapPasswordAttr.setStringValue("0x12");
			radAuthResponse.addAttribute(chapPasswordAttr);

			IRadiusAttribute proxyStateAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.PROXY_STATE);
			proxyStateAttr.setStringValue("0x56");
			radAuthResponse.addAttribute(proxyStateAttr);

			IRadiusAttribute loginLatAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			loginLatAttribute.setStringValue("0x57");
			radAuthResponse.addAttribute(loginLatAttribute);

			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"OctetsCheckItemPolicy1 && OctetsCheckItemPolicy2",0,null,
						null,"$OctetsCheckItemPolicy1($RES:0:33), $OctetsCheckItemPolicy2($RES:0:36)",true,
						true,true);
			}catch (Exception e) {
				// TODO: handle exception
			}


		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_124. Reason: " + e.getMessage());
		}
	}

	@Test public void testAccessPolicy125() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute vsa1 = Dictionary.getInstance().getAttribute("9:1");
			vsa1.setStringValue("Cisco-AVPair");
			requestPacket.addAttribute(vsa1);


			IRadiusAttribute vsa2 = Dictionary.getInstance().getAttribute("9:2");
			vsa2.setStringValue("Cisco-NAS-Port");
			requestPacket.addAttribute(vsa2);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute vsa3 = Dictionary.getInstance().getAttribute("9:3");
			vsa3.setStringValue("Cisco-Fax-Account-Id-Origin");
			radAuthResponse.addAttribute(vsa3);

			IRadiusAttribute vsa4 = Dictionary.getInstance().getAttribute("9:4");
			vsa4.setStringValue("Cisco-Fax-Msg-Id");
			radAuthResponse.addAttribute(vsa4);

			IRadiusAttribute vsa5 = Dictionary.getInstance().getAttribute("9:5");
			vsa5.setStringValue("Cisco-Fax-Pages");
			radAuthResponse.addAttribute(vsa5);




			rpm.applyPolicies(radAuthRequest,radAuthResponse,
					"VSACheckItemPolicy1 && VSACheckItemPolicy2",0,null,
					null,"$VSACheckItemPolicy1($RES:9:4), $VSACheckItemPolicy2($RES:9:5)",true,
					true,true);




		} catch (Exception e) {
			fail("failed to apply policy : Test_Policy_125. Reason: " + e.getMessage());
		}
	}


	@Test public void testAccessPolicy126() throws ManagerInitialzationException{


		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr);

			IRadiusAttribute nasPortAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
			nasPortAttribute.setIntValue(1);
			requestPacket.addAttribute(nasPortAttribute);

			IRadiusAttribute vsa1 = Dictionary.getInstance().getAttribute("9:1");
			vsa1.setStringValue("Cisco-AV");
			requestPacket.addAttribute(vsa1);


			IRadiusAttribute vsa2 = Dictionary.getInstance().getAttribute("9:2");
			vsa2.setStringValue("Cisco-NA");
			requestPacket.addAttribute(vsa2);


			requestPacket.refreshPacketHeader();


			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			IRadiusAttribute vsa3 = Dictionary.getInstance().getAttribute("9:3");
			vsa3.setStringValue("Cisco-Fax-Account-Id-Origin");
			radAuthResponse.addAttribute(vsa3);

			IRadiusAttribute vsa4 = Dictionary.getInstance().getAttribute("9:4");
			vsa4.setStringValue("Cisco-Fax-Msg-Id");
			radAuthResponse.addAttribute(vsa4);

			IRadiusAttribute vsa5 = Dictionary.getInstance().getAttribute("9:5");
			vsa5.setStringValue("Cisco-Fax-Pages");
			radAuthResponse.addAttribute(vsa5);

			try{
				rpm.applyPolicies(radAuthRequest,radAuthResponse,
						"VSACheckItemPolicy1 && VSACheckItemPolicy2",0,null,
						null,"$VSACheckItemPolicy1($RES:9:4), $VSACheckItemPolicy2($RES:9:5)",true,
						true,true);

			}catch (Exception e) {
				// TODO: handle exception
			}


		} catch (Exception e) {
			fail("failed to apply policy: ");
		}
	}


	// It should return Allow user at any time.
	@Test public void testAccessPolicy127() throws ManagerInitialzationException{

		rpm.initCache(serverContext, false);

		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr);
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_IP_ADDRESS)));
			requestPacket.addAttribute(Dictionary.getInstance().getAttribute(
					(RadiusAttributeConstants.NAS_PORT)));
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_1"
					,0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("Test Case: Check Item with only String Atributes Failed!");
		}
	}

	// Test Case: Valid String Attribute(username)
	@Test public void testAccessPolicy128() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);


			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_127"
					,0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("Test Case: Check Item with only String Atributes Failed!");
		}
	}

	// Test Case: Invalid String Attribute(username)
	@Test public void testAccessPolicy129() {
		// It should return Allow user at any time.
		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_127"
					,0,null,
					null,null,true,
					true,true);

			fail("Test Case: Check Item with only String Atributes Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	/*
	 * // method for creating a clone of an attribute. private IRadiusAttribute
	 * getAttribute(int iAttrId){ IRadiusAttribute radAttr =
	 * standardAttributeMap.get(String.valueOf(iAttrId)); if(radAttr != null)
	 * try { return (IRadiusAttribute) radAttr.clone(); } catch
	 * (CloneNotSupportedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return null; }
	 */

	// Test Case: Valid Integer Attribute(NAS Port no.)
	@Test public void testAccessPolicy130() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);


			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_128",
					0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("Test Case: Check Item with only Integer Atribute(NAS port no.) Failed!");
		}
	}

	// Test Case: Invalid Integer Attribute(NAS Port no.)
	@Test public void testAccessPolicy131() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_128",0,null,
					null,null,true,
					true,true);

			fail("Test Case: Check Item with only Integer Atribute(NAS port no.) Failed!");

		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Valid Octet Attribute(CHAP Password)
	@Test public void testAccessPolicy132() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_129"
					,0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("Test Case: Check Item with only Octet Atribute(CHAP Password) Failed!");
		}
	}

	// Test Case: Invalid Octet Attribute(CHAP Password)
	@Test public void testAccessPolicy133() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_129",0,null,
					null,null,true,
					true,true);

			fail("Test Case: Check Item with only Octet Atribute(CHAP Password) Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	/*
	 * //Test Case: Valid IPv6 Attribute(CHAP Password)Not Working public void
	 * testAccessPolicy8(){
	 * 
	 * try { rpm.initCache("", "", true, true); } catch (ParserException e) { //
	 * TODO Auto-generated catch block e.printStackTrace();
	 * fail("Parser Exception is thrown.Invalid Syntax for given policy."); }
	 * try { RadiusPacket requestPacket = new RadiusPacket();
	 * requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
	 * IRadiusAttribute radAttr1 =
	 * Dictionary.getInstance().getAttribute(RadiusAttributeConstants
	 * .USER_NAME); radAttr1.setStringValue("rahul@elitecore.com");
	 * requestPacket.addAttribute(radAttr1); IRadiusAttribute radAttr2 =
	 * Dictionary
	 * .getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
	 * radAttr2.setStringValue("192.168.10.11");
	 * requestPacket.addAttribute(radAttr2); IRadiusAttribute radAttr3 =
	 * Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_PORT);
	 * radAttr3.setIntValue(1402); requestPacket.addAttribute(radAttr3);
	 * IRadiusAttribute radAttr4 =
	 * Dictionary.getInstance().getAttribute(RadiusAttributeConstants
	 * .FRAMED_IPV6_PREFIX);
	 * radAttr4.setStringValue("0:0:0:0:0:ffff:192.1.56.10");
	 * requestPacket.addAttribute(radAttr4);
	 * 
	 * RadiusPacket responsePacket = new RadiusPacket();
	 * responsePacket.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
	 * responsePacket.refreshPacketHeader();
	 * 
	 * rpm.applyPolicies(requestPacket, responsePacket, "Test_Policy_5",null,
	 * null, null, true, true); } catch (Exception e) {fail(
	 * "Test Case: Check Item with only IPv6 Attribute( Framed IP address.) Failed!"
	 * ); } }
	 */

	// Test Case: Check Item Valid VSA Attribute(Cisco NAS Port)
	@Test public void testAccessPolicy134() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1009);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_130",0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("Test Case: Check Item with only VSA(Cisco NAS port no.) Failed!");
		}
	}

	// Test Case: Check Item Invalid VSA Attribute(Cisco NAS Port)
	public void testAccessPolicy135() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1890);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_130",0,null,
					null,null,true,
					true,true);

			fail("Test Case: Check Item with only VSA(Cisco NAS port no.) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Reject Item:Valid String Attribute(username)
	@Test public void testAccessPolicy136() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_131",0,null,
					null,null,true,
					true,true);

			fail("Test Case: Reject Item with only String Atributes Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Reject Item:Invalid String Attribute(username)
	@Test public void testAccessPolicy137() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_131",0,null,
					null,null,true,
					true,true);


		} catch (Exception exp) {
			fail("Test Case: Reject Item with only String Atributes Failed!");
		}
	}

	// Test Case: Reject Item:Valid Integer Attribute(NAS Port no.)
	@Test public void testAccessPolicy138() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_132",0,null,
					null,null,true,
					true,true);


			fail("Test Case: Reject Item with only Integer Atribute(NAS port no.) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Reject Item:Invalid Integer Attribute(NAS Port no.)
	@Test public void testAccessPolicy139() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1009);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_132",0,null,
					null,null,true,
					true,true);

		} catch (Exception e) {
			fail("Test Case: Reject Item with only Integer Atribute(NAS port no.) Failed!");
		}
	}

	// Test Case: Reject Item:Valid Octet Attribute(CHAP Password)
	@Test public void testAccessPolicy140() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_133",0,null,
					null,null,true,
					true,true);
			fail("Test Case: Reject Item with only Octet Atribute(CHAP Password) Failed!");
		} catch (Exception e) {

		}
	}

	// Test Case: Reject Item:Invalid Octet Attribute(CHAP Password)
	@Test public void testAccessPolicy141() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_133",0,null,
					null,null,true,
					true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with only Octet Atribute(CHAP Password) Failed!");
		}
	}

	// Test Case: Reject Item: Valid VSA Attribute(Cisco NAS Port)
	public void testAccessPolicy142() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);



			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1009);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_134",0,null,
					null,null,true,
					true,true);
			fail("Test Case: Reject Item with only VSA(Cisco NAS port no.) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Reject Item: Invalid VSA Attribute(Cisco NAS Port)
	@Test public void testAccessPolicy143() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1980);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_134",0,null,
					null,null,true,
					true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with only VSA(Cisco NAS port no.) Failed!");
		}
	}

	// Test Case: Reply Item: Valid String Attribute(Filter - uid)
	@Test public void testAccessPolicy144() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_135",0,null,
					null,null,true,
					true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item with String Atribute only Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item with sat only String Atributes Failed!");
		}
	}

	// Test Case: Reply Item: Valid Integer Attribute(Framed MTU)
	@Test public void testAccessPolicy145() {
		// It should return Allow user at any time.
		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_136",0,null,
					null,null,true,
					true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FRAMED_MTU);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item with  Integer Atribute only Failed!");
			} else {
				// System.out.println("0:12=" + replyAttrib.getStringValue());
			}

		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			fail("Test Case: Reply Item with only Integer Atributes Failed!");
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block
			fail("Test Case: Reply Item with only Integer Atributes Failed!");

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Reply Item: Valid Octet Attribute(Login-LAT-Group)
	@Test public void testAccessPolicy146() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_137",0,null,
					null,null,true,
					true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item with Octet Atribute only Failed!");
			} else {
				// System.out.println("0:36=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item with only Octet Atributes Failed!");
		}
	}

	/*
	 * 
	 * // Test Case: Reply Item: Valid VSA(CIsco NAS port) public void
	 * testAccessPolicy22() {
	 * 
	 * try { rpm.initCache("", "", true, true); } catch (ParserException e) { //
	 * TODO Auto-generated catch block e.printStackTrace();
	 * fail("Parser Exception is thrown.Invalid Syntax for given policy."); }
	 * try { RadiusPacket requestPacket = new RadiusPacket();
	 * requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
	 * IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
	 * RadiusAttributeConstants.USER_NAME);
	 * radAttr1.setStringValue("rahul@elitecore.com");
	 * requestPacket.addAttribute(radAttr1); IRadiusAttribute radAttr2 =
	 * Dictionary.getInstance().getAttribute(
	 * RadiusAttributeConstants.NAS_IP_ADDRESS);
	 * radAttr2.setStringValue("192.168.10.11");
	 * requestPacket.addAttribute(radAttr2); IRadiusAttribute radAttr3 =
	 * Dictionary.getInstance().getAttribute(
	 * RadiusAttributeConstants.NAS_PORT); radAttr3.setIntValue(1402);
	 * requestPacket.addAttribute(radAttr3);
	 * 
	 * RadiusPacket responsePacket = new RadiusPacket();
	 * responsePacket.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
	 * responsePacket.refreshPacketHeader();
	 * 
	 * rpm.applyPolicies(requestPacket, responsePacket, "Test_Policy_14", null,
	 * null, null, null, true, true);
	 * 
	 * IRadiusAttribute attrib = responsePacket .getVendorSpecificAttribute(9,
	 * 2); if (attrib == null) {
	 * fail("Test Case: Reply Item with VSA Atribute only Failed!"); } else {
	 * //System.out.println("9:2=" + radiusAttribute.getStringValue()); } }
	 * catch (ParserException pexp) { pexp.printStackTrace(); } catch
	 * (PolicyFailedException e) {
	 * fail("Test Case: Reply Item with only VSA(Cisco NAS port no.) Failed!");
	 * } }
	 */
	// Test Case: Check Item for Octet Attribute with OR operator(sat)
	// Test Case: Customer Level Check Item with only String
	// Attribute(username)(sat)
	@Test public void testAccessPolicy147() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_127",0,
					"$Test_Policy_127(0:1=*@elitecore.co.in)", null, null,
					true, true,true);
		} catch (Exception e) {
			fail("Test Case: Customer Level Check Item with only String Atributes Failed!");
		}
	}

	// Test Case: Customer Level Check Item with only String
	// Attribute(username)(unsat)
	@Test public void testAccessPolicy148() {
		// It should return Allow user at any time.
		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_127",0,
					"$Test_Policy_127(0:1=*@elitecore.co.in)",  null, null,
					true, true,true);
			fail("Test Case: Customer Level Check Item with only String Atributes Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Customer Level Check Item with only Integer Attribute(Port
	// No.)(sat)
	@Test public void testAccessPolicy149() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1420);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_128",0,
					"$Test_Policy_128(0:5=1420)", null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Customer Level Check Item with only Integer Atribute(NAS port no.) Failed!");
		}
	}

	// Test Case: Customer Level Check Item with only Integer Attribute(Port
	// No.)(unsat)
	@Test public void testAccessPolicy150() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_128",0,
					"$Test_Policy_128(0:5=1420)", null, null, true, true,true);
			fail("Test Case: Customer Level Check Item with only Integer Atribute(NAS port no.) Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Valid Octet Attribute(CHAP Password)
	@Test public void testAccessPolicy151() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xEEEE");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_129",0,
					"$Test_Policy_129(0:3=0xEEEE)", null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Customer Level Check Item with only Octet Atribute(CHAP Password) Failed!");
		}
	}

	// Test Case: Invalid Octet Attribute(CHAP Password)
	@Test public void testAccessPolicy152() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_129",0,
					"$Test_Policy_129(0:3=0xEEEE)", null, null, true, true,true);
			fail("Test Case: Check Item with only Octet Atribute(CHAP Password) Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Customer Level Check Item Valid VSA Attribute(Cisco NAS Port)
	@Test public void testAccessPolicy153() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1010);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_130",0,
					"$Test_Policy_130(9:2=1010)", null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Customer LEvel Check Item with only VSA(Cisco NAS port no.) Failed!");
		}
	}

	// Test Case: Customer Level Check Item Invalid VSA Attribute(Cisco NAS
	// Port)
	public void testAccessPolicy154() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1890);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_130",0,
					"$Test_Policy_130(9:2=1010)", null, null, true, true,true);
			fail("Test Case: Customer Level Check Item with only VSA(Cisco NAS port no.) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Customer Level Reject Item:Valid String Attribute(username)
	@Test public void testAccessPolicy155() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_131",0,
					null, "$Test_Policy_131(0:1=*@elitecore.co.in)", null,
					true, true,true);
			fail("Test Case: Customer Level Reject Item with only String Atributes Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Customer Level Reject Item:Invalid String Attribute(username)
	@Test public void testAccessPolicy156() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_131",0,
					null, "$Test_Policy_131(0:1=*@elitecore.co.in)", null,
					true, true,true);

		} catch (Exception exp) {
			fail("Test Case: Customer Level Reject Item with only String Atributes Failed!");
		}
	}

	// Test Case: Customer Level Reject Item:Valid Integer Attribute(NAS Port
	// no.)
	@Test public void testAccessPolicy157() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1403);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_132",0,
					null, "$Test_Policy_132(0:5=1403)", null, true, true,true);
			fail("Test Case: Customer Level Reject Item with only Integer Atribute(NAS port no.) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Customer Level Reject Item:Invalid Integer Attribute(NAS Port
	// no.)
	@Test public void testAccessPolicy158() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_132",0,
					null, "$Test_Policy_132(0:5=1403)", null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Customer Level Reject Item with only Integer Atribute(NAS port no.) Failed!");
		}
	}

	// Test Case: Customer Level Reject Item:Valid Octet Attribute(CHAP
	// Password)
	@Test public void testAccessPolicy159() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x2222");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_133",0,
					null, "$Test_Policy_133(0:3=0x2222)", null, true, true,true);
			fail("Test Case: Customer Level Reject Item with only Octet Atribute(CHAP Password) Failed!");
		} catch (Exception e) {

		}
	}

	// Test Case: Customer Level Reject Item:Invalid Octet Attribute(CHAP
	// Password)
	@Test public void testAccessPolicy160() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_133",0,
					null, "$Test_Policy_133(0:3=0x2222)",  null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Customer Level Reject Item with only Octet Atribute(CHAP Password) Failed!");
		}
	}

	// Test Case: Customer Level Reject Item: Valid VSA Attribute(Cisco NAS
	// Port)
	public void testAccessPolicy161() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1010);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_134",0,
					null, "$Test_Policy_134(9:2=1010)", null, true, true,true);
			fail("Test Case: Customer Level Reject Item with only VSA(Cisco NAS port no.) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Customer Level Reject Item: Invalid VSA Attribute(Cisco NAS
	// Port)
	@Test public void testAccessPolicy162() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1009);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_134",0,
					null, "$Test_Policy_134(9:2=1010)", null,true, true,true);
		} catch (Exception e) {
			fail("Test Case: Customer Level Reject Item with only VSA(Cisco NAS port no.) Failed!");
		}
	}

	// Test Case: Customer Level Reply Item: Valid String Attribute(Filter -
	// uid)
	@Test public void testAccessPolicy163() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);


			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse,
					"Test_Policy_135",0, null,
					"$Test_Policy_135(0:11=trunc)", null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item with String Atribute only Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Customer Level Reply Item with sat only String Atributes Failed!");
		}
	}

	// Test Case: Reply Item: Valid Integer Attribute(Framed MTU)
	@Test public void testAccessPolicy164() {
		// It should return Allow user at any time.
		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();



			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_136",0,
					null, "$Test_Policy_136(0:12=4200)", null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FRAMED_MTU);
			if (replyAttrib == null) {
				fail("Test Case: Customer Level Reply Item with  Integer Atribute only Failed!");
			} else {
				// System.out.println("0:12=" + replyAttrib.getStringValue());
			}

		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			fail("Test Case: Reply Item with only Integer Atributes Failed!");
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block
			fail("Test Case: Reply Item with only Integer Atributes Failed!");

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Customer Level Reply Item: Valid Octet
	// Attribute(Login-LAT-Group)
	@Test public void testAccessPolicy165() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();
			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_137",0,
					null, "$Test_Policy_137(0:36=0x4200)", null, true,
					true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.LOGIN_LAT_GROUP);
			if (replyAttrib == null) {
				fail("Test Case: Customer Level Reply Item with Octet Atribute only Failed!");
			} else {
				// System.out.println("0:36=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item with only Octet Atributes Failed!");
		}
	}

	// Test Case: Check Item for String Attribute with OR operator(sat)
	@Test public void testAccessPolicy166() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("rahul");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_139",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with String Atribute and OR operator Failed!");
		}
	}

	// Test Case: Check Item for String Attribute with OR operator(unsat)
	@Test public void testAccessPolicy167() {
		// It should return Allow user at any time.
		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.us");
			requestPacket.addAttribute(radAttr1);
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("luhar");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_139",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with String Atribute and OR operator Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Test Case: Check Item for Integer Attribute with OR operator(sat)
	@Test public void testAccessPolicy168() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(1403);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_144",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with Integer Atribute(NAS port no.) and OR operator Failed!");
		}
	}

	// Test Case: Check Item for Integer Attribute with OR operator(unsat)
	@Test public void testAccessPolicy169() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(1404);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_140",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with Integer Atribute(NAS port no.) and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for Octet Attribute with OR operator(sat)
	@Test public void testAccessPolicy170() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x2222");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0016");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_141",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test Case: Check Item with only Octet Atribute(CHAP Password) Failed!");
		}
	}

	// Test Case: Check Item for Octet Attribute with OR operator(unsat)
	@Test public void testAccessPolicy171() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0017");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_141",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with only Octet Atribute(CHAP Password) Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for VSA with OR operator(sat)
	@Test public void testAccessPolicy172() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(1010);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_142",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with VSA and OR operator Failed!");
		}
	}

	// Test Case: Check Item for VSA with OR operator(unsat)
	public void testAccessPolicy173() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(1010);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("runc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_142",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with  VSA(Cisco NAS port no.) and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for String Attribute with AND operator(sat)
	@Test public void testAccessPolicy174() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("rahul");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_143",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with String Atribute and AND operator Failed!");
		}
	}

	// Test Case: Check Item for String Attribute with AND operator(unsat)
	@Test public void testAccessPolicy175() {
		// It should return Allow user at any time.
		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("luhar");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_143",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with String Atribute and AND operator Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Test Case: Check Item for Integer Attribute with AND operator(sat)
	@Test public void testAccessPolicy176() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1403);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(1403);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_140",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with Integer Atributes and AND operator Failed!");
		}
	}

	// Test Case: Check Item for Integer Attribute with AND operator(unsat)
	@Test public void testAccessPolicy177() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(1404);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_144",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with Integer Atributes and AND operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for Octet Attribute with AND operator(sat)
	@Test public void testAccessPolicy178() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0016");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_145",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test Case: Check Item with Octet Atributes and AND operator Failed!");
		}
	}

	// Test Case: Check Item for Octet Attribute with AND operator(unsat)
	@Test public void testAccessPolicy179() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0017");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_145",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with only Octet Atribute(CHAP Password) Failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for VSA with AND operator(sat)

	// Test Case: Check Item for VSAs AND operator(sat)
	@Test public void testAccessPolicy180() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(1009);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_146",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with VSA and AND operator Failed!");
		}
	}

	// Test Case: Check Item for VSA with OR operator(unsat)

	// Test Case: Check Item for VSAs AND operator(unsat)
	public void testAccessPolicy181() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(1010);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("runc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_146",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with  VSA and AND operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for standard attributes with OR operator(sat)
	@Test public void testAccessPolicy182() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_147",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with Standard Atributes and OR operator Failed!");
		}
	}

	// Test Case: Check Item for standard attributes with OR operator(unsat)
	@Test public void testAccessPolicy183() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_147",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with Standard Atributes and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case(8.16a): Check Item for all types of VSA and OR operator(sat)
	@Test public void testAccessPolicy184() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 194);
			radiusAttribute1.setIntValue(200);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("runc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_148",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item for all types of VSA and OR operator Failed!");
		}
	}

	// Test Case(8.16b): Check Item for all types of VSA and OR operator(unsat)
	public void testAccessPolicy185() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 194);
			radiusAttribute1.setIntValue(201);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("runc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_148",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item for all types of  VSA and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for standard attributes with AND operator(sat)
	@Test public void testAccessPolicy186() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x0111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_149",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with Standard Atributes and AND operator Failed!");
		}
	}

	// Test Case: Check Item for standard attributes with AND operator(unsat)
	@Test public void testAccessPolicy187() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_149",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with Standard Atributes and AND operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case(8.18a): Check Item for all types of VSA and AND operator(sat)
	@Test public void testAccessPolicy188() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 195);
			radiusAttribute1.setIntValue(2);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute2.setIntValue(1009);
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_150",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item for all types of VSA and AND operator Failed!");
		}
	}

	// Test Case(8.18b): Check Item for all types of VSA and AND operator(unsat)
	public void testAccessPolicy189() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 195);
			radiusAttribute1.setIntValue(201);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute2.setIntValue(1009);
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_150",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item for all types of  VSA and AND operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for standard attributes with AND and OR
	// operator(sat)
	@Test public void testAccessPolicy190() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x0111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_151",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with Standard Atributes and AND and OR operator Failed!");
		}
	}

	// Test Case: Check Item for standard attributes with AND and OR
	// operator(unsat)
	@Test public void testAccessPolicy191() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_151",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with Standard Atributes and AND and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: Check Item for Standard Attributes(OR) and VSA(AND)(sat)
	@Test public void testAccessPolicy192() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(1009);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_152",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with Standard Atributes(OR) and VSA(AND) Failed!");
		}
	}

	// Test Case: Check Item for Standard Attributes(OR) and
	// VSA(AND)(sat)(unsat)
	public void testAccessPolicy193() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(109);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_152",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with Standard Atributes(OR) and VSA(AND) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Test Case: Check Item for Standard Attributes(AND) and VSA(OR)(sat)
	@Test public void testAccessPolicy194() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x0111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(1009);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("runc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_153",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with Standard Atributes(OR) and VSA(AND) Failed!");
		}
	}

	// Test Case: Check Item for Standard Attributes(AND) and VSA(OR)(unsat)
	@Test public void testAccessPolicy195() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1403);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute1.setIntValue(109);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_153",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item with Standard Atributes(AND) and VSA(OR) Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Test Case(8.22): Check Item for all types of VSA with AND and OR
	// operator(sat)
	@Test public void testAccessPolicy196() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 194);
			radiusAttribute1.setIntValue(200);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("runc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			VendorSpecificAttribute vsaAttribute3 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute3.setVendorID(9);
			IVendorSpecificAttribute vsaType3 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute3 = Dictionary.getInstance()
					.getAttribute(9, 195);
			radiusAttribute3.setIntValue(2);
			vsaType3.addAttribute(radiusAttribute3);
			vsaAttribute3.setVendorTypeAttribute(vsaType3);
			requestPacket.addAttribute(vsaAttribute3);

			VendorSpecificAttribute vsaAttribute4 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute4.setVendorID(9);
			IVendorSpecificAttribute vsaType4 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute4 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute4.setIntValue(1009);
			vsaType4.addAttribute(radiusAttribute4);
			vsaAttribute4.setVendorTypeAttribute(vsaType4);
			requestPacket.addAttribute(vsaAttribute4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_154",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item for all types of VSA with AND and OR operator Failed!");
		}
	}

	// Test Case(8.22): Check Item for all types of VSA with AND and OR
	// operator(unsat)
	public void testAccessPolicy197() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute1 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute1.setVendorID(9);
			IVendorSpecificAttribute vsaType1 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance()
					.getAttribute(9, 194);
			radiusAttribute1.setIntValue(20);
			vsaType1.addAttribute(radiusAttribute1);
			vsaAttribute1.setVendorTypeAttribute(vsaType1);
			requestPacket.addAttribute(vsaAttribute1);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("runc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			VendorSpecificAttribute vsaAttribute3 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute3.setVendorID(9);
			IVendorSpecificAttribute vsaType3 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute3 = Dictionary.getInstance()
					.getAttribute(9, 195);
			radiusAttribute3.setIntValue(2);
			vsaType3.addAttribute(radiusAttribute3);
			vsaAttribute3.setVendorTypeAttribute(vsaType3);
			requestPacket.addAttribute(vsaAttribute3);

			VendorSpecificAttribute vsaAttribute4 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute4.setVendorID(9);
			IVendorSpecificAttribute vsaType4 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute4 = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute4.setIntValue(1009);
			vsaType4.addAttribute(radiusAttribute4);
			vsaAttribute4.setVendorTypeAttribute(vsaType4);
			requestPacket.addAttribute(vsaAttribute4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_154",0,
					null, null, null, true, true,true);
			fail("Test Case: Check Item for all types of VSA with AND and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}

	}

	// Test Case: (9.01)Reject Item for String Attributes with OR operator(sat)
	@Test public void testAccessPolicy198() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("rahul");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_155",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with String Atributes and OR oprator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: (9.01)Reject Item for String Attributes with OR
	// operator(unsat)
	@Test public void testAccessPolicy199() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("ahul");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_155",0,
					null, null, null, true, true,true);

		} catch (Exception exp) {
			fail("Test Case: Reject Item with String Atributes and OR operator Failed!");
		}
	}

	// Test Case(9.02): Reject Item: Integer Attribute and OR operator(sat)
	@Test public void testAccessPolicy200() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(1403);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_156",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with Integer Atribute and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case(9.02): Reject Item: Integer Attribute and OR operator(unsat)
	@Test public void testAccessPolicy201() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1009);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(140);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_156",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with Integer Atribute and OR operator Failed!");
		}
	}

	// Test Case(9.04): Reject Item: Octet Attribute and OR operator(sat)
	@Test public void testAccessPolicy202() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x0111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0016");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_157",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with Octet Atribute and OR operator Failed!");
		} catch (Exception e) {

		}
	}

	// Test Case(9.04): Reject Item: Octet Attribute and OR operator(unsat)
	@Test public void testAccessPolicy203() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0xFFFF");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0116");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_157",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with Octet Atribute and OR operator Failed!");
		}
	}

	// Test Case(9.07): Reject Item: VSA and OR operator(sat)
	public void testAccessPolicy204() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1008);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_158",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with VSA and OR operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case(9.07): Reject Item: VSA and OR operator(unsat)
	@Test public void testAccessPolicy205() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1980);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trun");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_158",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with VSA and OR operator Failed!");
		}
	}

	// Test Case: (9.08)Reject Item for String Attributes with AND operator(sat)
	@Test public void testAccessPolicy206() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("rahul");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_159",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with String Atributes and AND operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case: (9.08)Reject Item for String Attributes with AND
	// operator(unsat)
	@Test public void testAccessPolicy207() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_PASSWORD);
			radAttr1a.setStringValue("rahul");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_159",0,
					null, null, null, true, true,true);

		} catch (Exception exp) {
			fail("Test Case: Reject Item with String Atributes and OR operator Failed!");
		}
	}

	// Test Case(9.09): Reject Item: Integer Attribute and AND operator(sat)
	@Test public void testAccessPolicy208() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(1403);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_160",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with Integer Atribute and AND operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case(9.09): Reject Item: Integer Attribute and AND operator(unsat)
	@Test public void testAccessPolicy209() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(140);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.FRAMED_MTU);
			radAttr4.setIntValue(1403);
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_160",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with Integer Atribute and AND operator Failed!");
		}
	}

	// Test Case(9.11): Reject Item: Octet Attribute and AND operator(sat)
	@Test public void testAccessPolicy210() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0016");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_161",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with Octet Atribute and AND operator Failed!");
		} catch (Exception e) {

		}
	}

	// Test Case(9.11): Reject Item: Octet Attribute and AND operator(unsat)
	@Test public void testAccessPolicy211() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1b = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1b.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1b);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.LOGIN_LAT_GROUP);
			radAttr4.setStringValue("0x0116");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_161",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with Octet Atribute and AND operator Failed!");
		}
	}

	// Test Case(9.14): Reject Item: VSA and AND operator(sat)
	public void testAccessPolicy212() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1009);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_162",0,
					null, null, null, true, true,true);
			fail("Test Case: Reject Item with VSA and AND operator Failed!");
		} catch (ParserException pexp) {
			pexp.printStackTrace();
		} catch (PolicyFailedException e) {

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	// Test Case(9.14): Reject Item: VSA and AND operator(unsat)
	@Test public void testAccessPolicy213() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(109);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);

			VendorSpecificAttribute vsaAttribute2 = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute2.setVendorID(9);
			IVendorSpecificAttribute vsaType2 = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute2 = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute2.setStringValue("trunc");
			vsaType2.addAttribute(radiusAttribute2);
			vsaAttribute2.setVendorTypeAttribute(vsaType2);
			requestPacket.addAttribute(vsaAttribute2);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_162",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Reject Item with VSA and AND operator Failed!");
		}
	}

	// Test Case: Reply Item(10.01): Equal Check on String Attribute
	@Test public void testAccessPolicy214() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_163",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for equal check on String Atribute Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for equal check on String Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.02): Equal Check on Integer Attribute
	@Test public void testAccessPolicy215() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_164",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for equal check on Integer Atribute Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for equal check on Integer Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.04): Equal Check on Octet Attribute
	@Test public void testAccessPolicy216() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1a.setStringValue("0x1111");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_165",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for equal check on Octet Atribute Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for equal check on Octet Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.06): Equal Check on tag Attribute
	@Test public void testAccessPolicy217() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.ACCT_TUNNEL_CONNECTION);
			radAttr4.setStringValue("trunk");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);

			System.out.println("for 217 response :"+radAuthResponse);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_166",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			System.out.println("for 217 response :"+radAuthResponse);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for equal check on tag Atribute Failed!");
			} else {
				//System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for equal check on tag Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.07): Equal Check on VSA
	public void testAccessPolicy218() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(1009);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_167",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for equal check on VSA Failed!");
			} else {
				//System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for equal check on VSA Failed!");
		}
	}

	// Test Case: Reply Item(10.08): Not Equal Check on String Attribute
	@Test public void testAccessPolicy219() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.co.in");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1008);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_168",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for equal check on String Atribute Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for equal check on String Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.09): Not Equal Check on Integer Attribute
	@Test public void testAccessPolicy220() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(140);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_169",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for not equal check on Integer Atribute Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for not equal check on Integer Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.11:Not Equal Check on Octet Attribute
	@Test public void testAccessPolicy221() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr1a = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.CHAP_PASSWORD);
			radAttr1a.setStringValue("0x111");
			requestPacket.addAttribute(radAttr1a);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_170",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for not equal check on Octet Atribute Failed!");
			} else {
				// System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for not equal check on Octet Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.13): Not Equal Check on tag Attribute
	@Test public void testAccessPolicy222() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);
			IRadiusAttribute radAttr4 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.ACCT_TUNNEL_CONNECTION);
			radAttr4.setStringValue("runk");
			requestPacket.addAttribute(radAttr4);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_171",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for not equal check on tag Atribute Failed!");
			} else {
				//System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for not equal check on tag Atribute Failed!");
		}
	}

	// Test Case: Reply Item(10.07): Not Equal Check on VSA
	public void testAccessPolicy223() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 2);
			radiusAttribute.setIntValue(109);
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_172",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttrib = radAuthResponse
					.getRadiusAttribute(RadiusAttributeConstants.FILTER_ID);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for not equal check on VSA Failed!");
			} else {
				System.out.println("0:11=" + replyAttrib.getStringValue());
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for not equal check on VSA Failed!");
		}
	}

	//	 Test Case: Check Item for AVPair, valid case
	@Test public void testAccessPolicy224() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("key=value");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_173",0,
					null, null, null, true, true,true);

		} catch (Exception e) {
			fail("Test Case: Check item for AVPair attribute failed!");
		}
	}

	//	 Test Case: Check Item for AVPair, invalid case
	public void testAccessPolicy225() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("key=test");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_173",0,
					null, null, null, true, true,true);
			fail("Test Case: Check item for AVPair attribute failed!");
		} catch (ParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (PolicyFailedException e3) {
			// TODO Auto-generated catch block

		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	//	 Test Case: AVPair as reply item
	public void testAccessPolicy226() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("key=test");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_174",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttribute = radAuthResponse.getRadiusAttribute(9, 1);
			if(replyAttribute == null){
				fail("Test case: Reply Item for AVPair attribute failed.");
			}else{
				assertEquals("key=value", replyAttribute.getStringValue());
			}
		} catch (Exception e) {
			fail("Test case: Reply Item for AVPair attribute failed.");
		}

	}

	//	 Test Case: AVPair as dynamic reply item
	public void testAccessPolicy227() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("key=test");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_175",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttribute = radAuthResponse.getRadiusAttribute(9, 1);
			if(replyAttribute == null){
				fail("Test case: Reply Item for AVPair attribute failed.");
			}else{
				assertEquals("k1=test", replyAttribute.getStringValue());
			}
		} catch (Exception e) {
			fail("Test case: Reply Item for AVPair attribute failed.");
		}

	}
	//	 Test Case: AVPair as conditional reply item
	public void testAccessPolicy228() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("k1=v1");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_176",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttribute = radAuthResponse.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
			if(replyAttribute == null){
				fail("Test case: Conditional reply Item for AVPair attribute failed.");
			}else{
				assertEquals("success", replyAttribute.getStringValue());
			}
		} catch (Exception e) {
			fail("Test case: Reply Item for AVPair attribute failed.");
		}

	}

	//	 Test Case: AVPair as conditional reply item
	@Test public void testAccessPolicy229() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("k1=v2");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_176",0,
					null, null, null, true, true,true);

			IRadiusAttribute replyAttribute = radAuthResponse.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
			if(replyAttribute != null)
				fail("Test case: Conditional reply Item for AVPair attribute failed.");

		} catch (Exception e) {
			fail("Test case: Conditional reply Item for AVPair attribute failed.");
		}

	}

	// Test Case: Either_Or Operator Support
	@Test public void testAccessPolicy230() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("elitecore");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_177 ^ Test_Policy_178",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			ArrayList<IRadiusAttribute> replyAttrib = (ArrayList<IRadiusAttribute>) radAuthResponse
					.getRadiusAttributes(RadiusAttributeConstants.REPLY_MESSAGE);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for Either Or Operator failed");
			} else if(replyAttrib.size()!=1){
				fail("Test Case: Reply Item for Either Or Operator failed");
			} else if(!replyAttrib.get(0).getStringValue().equals("test_policy_177")){
				fail("Test Case: Reply Item for Either Or Operator failed");
			}
			IRadiusAttribute userNameAttr = radAuthRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
			userNameAttr.setStringValue("eliteaaa");
			requestPacket.refreshPacketHeader();
			//	responsePacket = new RadiusPacket();
			radAuthResponse  = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);
			satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_177 ^ Test_Policy_178",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			replyAttrib = (ArrayList<IRadiusAttribute>) radAuthResponse
					.getRadiusAttributes(RadiusAttributeConstants.REPLY_MESSAGE);
			if (replyAttrib == null) {
				fail("Test Case: Reply Item for Either Or Operator failed");
			} else if(replyAttrib.size()!=1){
				fail("Test Case: Reply Item for Either Or Operator failed");
			} else if(!replyAttrib.get(0).getStringValue().equals("test_policy_178")){
				fail("Test Case: Reply Item for Either Or Operator failed");
			}

		} catch (Exception e) {
			fail("Test Case: Reply Item for not equal check on VSA Failed!");
		}
	}

	//	 Test Case: AVPair as conditional reply item
	public void testAccessPolicy231() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("k1=v1");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			List<String> satisfiedPolicies = rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_179",0,
					null, null, null, true, true,true);

			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			IRadiusAttribute replyAttribute = radAuthResponse.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
			if(replyAttribute == null){
				fail("Test case: Conditional reply Item for AVPair attribute failed.");
			}else{
				assertEquals("success", replyAttribute.getStringValue());
			}
		} catch (Exception e) {
			fail("Test case: Reply Item for AVPair attribute failed.");
		}

	}

	//	 Test Case: AVPair as conditional reply item
	@Test public void testAccessPolicy232() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("t1=v1");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_179",0,
					null, null, null, true, true,true);

			IRadiusAttribute replyAttribute = radAuthResponse.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
			if(replyAttribute != null){
				fail("Test case: Conditional reply Item for AVPair attribute failed.");
			}
		} catch (Exception e) {
			fail("Test case: Reply Item for AVPair attribute failed.");
		}

	}
	// Test Case: Valid Integer Info Attribute(NAS Port no.)
	@Test public void testAccessPolicy233() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);
			IRadiusAttribute radAttr3 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_PORT);
			radAttr3.setIntValue(1402);
			requestPacket.addAttribute(radAttr3);

			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_128",0,
					null, null, null, true, true,true);
		} catch (Exception e) {
			fail("Test Case: Check Item with only Integer Atribute(NAS port no.) Failed!");
		}
	}

	//	 Test Case: AVPair as conditional reply item on Info Attribute
	@Test public void testAccessPolicy234() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("t1=v1");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addInfoAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = formServiceSpecificRequest(requestPacket);

			RadAuthResponse radAuthResponse = new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);


			rpm.applyPolicies(radAuthRequest, radAuthResponse, "Test_Policy_179",0,
					null, null, null, true, true,true);

			IRadiusAttribute replyAttribute = radAuthResponse.getRadiusAttribute(RadiusAttributeConstants.REPLY_MESSAGE);
			if(replyAttribute != null){
				fail("Test case: Conditional reply Item for AVPair attribute failed.");
			}
		} catch (Exception e) {
			fail("Test case: Reply Item for AVPair attribute failed.");
		}

	}

	@Test public void test_reply_Item_for_multiple_occurance_of_same_attribute() {

		try {
			rpm.initCache(serverContext, false);
		} catch (ManagerInitialzationException e) {
			fail("Parser Exception is thrown.Invalid Syntax for given policy.");
		}
		try {
			RadiusPacket requestPacket = new RadiusPacket();
			requestPacket.setPacketType(RadiusConstants.ACCESS_REQUEST_MESSAGE);
			IRadiusAttribute radAttr1 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.USER_NAME);
			radAttr1.setStringValue("rahul@elitecore.com");
			requestPacket.addAttribute(radAttr1);
			IRadiusAttribute radAttr2 = Dictionary.getInstance().getAttribute(
					RadiusAttributeConstants.NAS_IP_ADDRESS);
			radAttr2.setStringValue("192.168.10.11");
			requestPacket.addAttribute(radAttr2);

			VendorSpecificAttribute vsaAttribute = (VendorSpecificAttribute) Dictionary
					.getInstance().getAttribute(
							RadiusAttributeConstants.VENDOR_SPECIFIC);
			vsaAttribute.setVendorID(9);
			IVendorSpecificAttribute vsaType = Dictionary.getInstance()
					.getVendorAttributeType(9);

			IRadiusAttribute radiusAttribute = Dictionary.getInstance()
					.getAttribute(9, 1);
			radiusAttribute.setStringValue("t1=v1");
			vsaType.addAttribute(radiusAttribute);
			vsaAttribute.setVendorTypeAttribute(vsaType);
			requestPacket.addInfoAttribute(vsaAttribute);
			requestPacket.refreshPacketHeader();

			RadAuthRequest radAuthRequest = new BaseRadiusAuthService.RadiusAuthRequestImpl(requestPacket.getBytes(), InetAddress.getLocalHost(), 0, serverContext, new SocketDetail(InetAddress.getLocalHost().getHostAddress(),0));

			RadAuthResponse radAuthResponse = createResponse(requestPacket);

			List<String> satisfiedPolicies = new ArrayList<String>();
			satisfiedPolicies.add("Test_Policy_180");
			rpm.applyReplyItems(radAuthRequest, radAuthResponse,satisfiedPolicies, null,true,true,true);

			Collection<IRadiusAttribute> replyAttribute = radAuthResponse.getRadiusAttributes(1);
			if(replyAttribute == null){
				fail("Test case: Conditional reply Item for AVPair attribute failed.");
			}
			assertEquals(2, replyAttribute.size());
		} catch (Exception e) {
			fail("Test case: Reply Item for AVPair attribute failed.");
		}

	}

	private RadAuthResponse createResponse(RadiusPacket requestPacket) {
		RadAuthResponse radAuthResponse = (RadAuthResponse)new BaseRadiusAuthService.RadiusAuthResponseImpl(requestPacket.getAuthenticator(), 0, serverContext);
		return radAuthResponse;
	}

	@Test
	public void testNPE() throws ManagerInitialzationException, UnknownHostException, ParserException, PolicyFailedException {
		rpm = new RadiusPolicyManager("Test") {
			
			@Override
			protected List<IPolicyData> readFromDB() throws LoadCacheFailedException {
				ArrayList<IPolicyData> list = new ArrayList<IPolicyData>();
				
				PolicyData policyData = new PolicyData();
				policyData.setPolicyName("NPE");
				policyData.setCheckItem("0:8=[*,*]");
				
				list.add(policyData);
				return list;
			}
			
			@Override
			protected List<PolicyGroupData> readPolicyGroupFromDB() throws LoadCacheFailedException {
				return new ArrayList<PolicyGroupData>();
			}
		};
		
		rpm.initCache(serverContext, false);
		
		RadiusPacket requestPacket = new RadiusPacket();
		
		RadAuthRequest request = formServiceSpecificRequest(requestPacket);
		RadAuthResponse response = createResponse(requestPacket);
		
		rpm.applyPolicies(request, response, "NPE", 1, null, null, null, false, false, true);
	}
}
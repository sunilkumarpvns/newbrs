package com.elitecore.aaa.core.radius.service.auth.handlers;

import static com.elitecore.aaa.core.radius.service.auth.handlers.RadEapHandlerValuesProvider.defaultRSABuilder;

import com.elitecore.coreeap.util.constants.EapTypeConstants;

public class EapAKADataProvider {

	public static Object[] provideDEMO() throws Exception{
		RadEapHandlerValues values = 
				new RadEapHandlerValues.RadEapHandlerValuesBuilder(defaultRSABuilder)
				.withEapMethod(EapTypeConstants.AKA.typeId)
				.withDefaultNegotiationMethod(EapTypeConstants.AKA.typeId)
				.withScenario("EAP_AKA")
				.withUser("0404979998282715@wlan.mnc097.mcc404.3gppnetwork.org", "0:1f464155cf5212b909299b703c65a58b,7b212c31d95393df,bf6e7b9f9a11a1ba7896ed744d2e9da9,e87ec0fd3d887d79ac80c60617986051,badb6841682c000040bda9a41f3964ac")
				.build();
		
		return new Object[][] {
				{ new String[] {
						"0x011901880b04f62bfaf52070454d26099b44c90e01353034303439373939393832383237313540776c616e2e6d6e633039372e6d63633430342e336770706e6574776f726b2e6f72675903008306000000011f1337342d65322d38632d36612d39632d32301e2b31632d65362d63372d35622d32332d37303a61697274656c2068616e676f757420617070206f6e6c790506000000011a3100000009012b61756469742d73657373696f6e2d69643d3061306332633635303031383732306635396433383464382c2435396433383464382f37343a65323a38633a36613a39633a32302f3230353938393204060a0c2c65201d42414c2d4e435243414d5055532d44432d353530382d574c4330311a0c0000376301060000000a0606000000020c06000005143d060000001340060000000d410600000006510439384f3a02020038013034303439373939393832383237313540776c616e2e6d6e633039372e6d63633430342e336770706e6574776f726b2e6f72675012e4c8b75cb56153b0e8e17523ed82a2e2",
						"0x0123022826c0c20a44391c48bbee8131aebc540601353034303439373939393832383237313540776c616e2e6d6e633039372e6d63633430342e336770706e6574776f726b2e6f72675903008306000000011f1337342d65322d38632d36612d39632d32301e2b31632d65362d63372d35622d32332d37303a61697274656c2068616e676f757420617070206f6e6c790506000000011a3100000009012b61756469742d73657373696f6e2d69643d3061306332633635303031383732306635396433383464382c2435396433383464382f37343a65323a38633a36613a39633a32302f3230353938393204060a0c2c65201d42414c2d4e435243414d5055532d44432d353530382d574c4330311a0c0000376301060000000a0606000000020c06000005143d060000001340060000000d410600000006510439384f2a0203002817010000030300407b212c31d95393df0b0500003a6e6af2c159950abb32bffdd7ad43315012fec7eb08ea6e77b65418b7099c82954b",
					},
					new String[]{
						"0x0b190128472a7899d98881e6b0080ecdb552202418b037343a65323a38633a36613a39633a3230203034303439373939393832383237313540776c616e2e6d6e633039372e6d63633430342e336770706e6574776f726b2e6f72672031302e31322e34342e3130312042414c2d4e435243414d5055532d44432d353530382d574c4330312031203020576972656c6573732d3830322e31312031632d65362d63372d35622d32332d37303a61697274656c2068616e676f757420617070206f6e6c792030370659d38bfa4f460103004417010000010500001f464155cf5212b909299b703c65a58b02050000badb6841682c000040bda9a41f3964ac0b050000924190fe0f23d577580c9df39ef3cb5150127221b5ce579c161cb2c1f883a1d812901b0600000bb8",
						"0x022301e34468c7017a79d297ce12230971b98324121841757468656e7469636174696f6e205375636365737306060000000218b037343a65323a38633a36613a39633a3230203034303439373939393832383237313540776c616e2e6d6e633039372e6d63633430342e336770706e6574776f726b2e6f72672031302e31322e34342e3130312042414c2d4e435243414d5055532d44432d353530382d574c4330312031203020576972656c6573732d3830322e31312031632d65362d63372d35622d32332d37303a61697274656c2068616e676f757420617070206f6e6c7920301a3a000001371134802d9f336d24e4072b1b7d5f3ef558e2e2ffeb72b7f187447bc49d29a28cdf035db1fb96c896b430d2506a7d2508314c5ec01a3a000001371034802ecdceeb2a295dbdf9dfcf0a382b629b02d670bb3cf4c01679044eafd940b7af3e97e96844020244f06efd620b2b60738b4f06030300040111343034393739393938323832373135590e3931393731373938333833321a0c000000c1e106000000011a44000000c1e23e01000003e8000003e8007c09000004e200000004e200000000000000000000000001000e0d61697274656c68616e676f7574040a033629040a03362950129eab9ab70cb10fec91ee9bb359aaf1df1b0600000258",
					},
					values
				}
		};
	}
	
/*	
	public static Object[] provideDEMO() throws Exception{
		RadEapHandlerValues values = 
				new RadEapHandlerValues.RadEapHandlerValuesBuilder(defaultRSABuilder)
				.withEapMethod(EapTypeConstants.AKA.typeId)
				.withDefaultNegotiationMethod(EapTypeConstants.AKA.typeId)
				.withScenario("EAP_AKA")
				.withUser("", "")
				.build();
		
		return new Object[][] {
				{ new String[] {
						"",
						"",
						"",
						
					},
					new String[]{
						"",
						"",
						""
					},
					values
				}
		};
	}
*/

}

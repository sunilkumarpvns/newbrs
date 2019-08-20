package com.elitecore.corenetvertex.constants;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CommonConstantsTest {

	@Test
	public void valueOfUndefined() {
		assertThat(CommonConstants.QUOTA_UNDEFINED, is(equalTo(-999999999999999999L)));
	}
	
	@Test
	public void valueOfUnlimited() {
		assertThat(CommonConstants.QUOTA_UNLIMITED, is(equalTo(999999999999999999L)));
	}
}

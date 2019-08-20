package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.config.core.CaseInsensitiveEnumAdapter;

/**
 * Represents the action that policy takes in case when policy is unresponsive.
 *   
 * @author vicky.singh
 * @author narendra.pathai
 */
public  abstract class DefaultResponseBehavior {

	@XmlEnum
	public enum DefaultResponseBehaviorType {
		@XmlEnumValue(value = "REJECT")
		REJECT,
		
		@XmlEnumValue(value = "DROP")
		DROP,
		
		@XmlEnumValue(value = "HOTLINE")
		HOTLINE;
		
		/**
		 * Case in-sensitive enum adapter for {@link DefaultResponseBehaviorType}
		 *  
		 * @author vicky.singh
		 * @author narendra.pathai
		 *
		 */
		public static class DefaultResponseBehaviorTypeAdapter extends CaseInsensitiveEnumAdapter<DefaultResponseBehaviorType> {

			public DefaultResponseBehaviorTypeAdapter() {
				super(DefaultResponseBehaviorType.class, DefaultResponseBehaviorType.REJECT);
			}
		}
	}
	
	public abstract void apply(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse);
	
	/**
	 * @return new instance of {@link DefaultResponseBehavior} based on type
	 */
	public static DefaultResponseBehavior create(@Nonnull DefaultResponseBehaviorType responseBehaviorType, 
			@Nullable String responseBehaviorParameter) {
		
		switch (responseBehaviorType) {
		case REJECT:
			return new RejectBehavior(responseBehaviorParameter);
		case DROP:
			return new DropBehavior();
		case HOTLINE:
			return new HotlineBehavior(responseBehaviorParameter);
		default:
			throw new UnsupportedOperationException(responseBehaviorType + " is not supported.");
		}
	}

}

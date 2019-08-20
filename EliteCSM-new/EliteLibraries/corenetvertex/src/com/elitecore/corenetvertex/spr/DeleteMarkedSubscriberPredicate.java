package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.data.SPRInfo;

public class DeleteMarkedSubscriberPredicate implements Predicate<SPRInfo> {

	private static DeleteMarkedSubscriberPredicate deleteMarkedSubscriberPredicate;
	
	private DeleteMarkedSubscriberPredicate() { }
	
	static {
		deleteMarkedSubscriberPredicate = new DeleteMarkedSubscriberPredicate();
	}
	
	public static DeleteMarkedSubscriberPredicate getInstance() {
		return deleteMarkedSubscriberPredicate;
	}
	
	@Override
	public boolean apply(SPRInfo sprInfo) {
		return SubscriberStatus.DELETED.name().equals(sprInfo.getStatus());
			
	}
}

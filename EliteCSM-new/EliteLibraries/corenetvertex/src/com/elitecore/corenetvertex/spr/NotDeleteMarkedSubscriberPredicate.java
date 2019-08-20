package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Predicate;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.data.SPRInfo;

/**
 * Filters subscriber with status: DELETE
 * 
 * @author Chetan.Sankhala
 */
public class NotDeleteMarkedSubscriberPredicate implements Predicate<SPRInfo> {

	private static NotDeleteMarkedSubscriberPredicate markedForDeleteSubcriberFilter;
	
	private NotDeleteMarkedSubscriberPredicate() { }
	
	static {
		markedForDeleteSubcriberFilter = new NotDeleteMarkedSubscriberPredicate();
	}
	
	public static NotDeleteMarkedSubscriberPredicate getInstance() {
		return markedForDeleteSubcriberFilter;
	}
	
	@Override
	public boolean apply(SPRInfo sprInfo) {
		if(SubscriberStatus.DELETED.name().equals(sprInfo.getStatus()) == false){
			return true;
		}
		return false;
	}
}

package com.elitecore.corenetvertex.pm.pkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

public class GroupManageOrderStore {
	
	private @Nonnull Map<String, List<GroupManageOrder>> emergencyOrderByGroupId;
	private @Nonnull Map<String, List<GroupManageOrder>> promotionalOrderByGroupId;
	private @Nonnull Map<String, GroupManageOrder> groupManageOrderById;
	
	public GroupManageOrderStore() {
		this.emergencyOrderByGroupId = new HashMap<String, List<GroupManageOrder>>();
		this.promotionalOrderByGroupId = new HashMap<String, List<GroupManageOrder>>();
		this.groupManageOrderById = new HashMap<String, GroupManageOrder>();
	}
	

	public void add(GroupManageOrder groupManageOrder) {
		groupManageOrderById.put(groupManageOrder.getId(), groupManageOrder);
		if (PkgType.EMERGENCY == groupManageOrder.getPackageType()) {
			addEmergencyOrder(groupManageOrder);
		} else if (PkgType.PROMOTIONAL == groupManageOrder.getPackageType()) {
			addPromotionOrder(groupManageOrder);
		}
	}

	private void addPromotionOrder(GroupManageOrder groupManageOrder) {

		String groupId = groupManageOrder.getGroupId();
		if (promotionalOrderByGroupId.get(groupId) == null) {
			promotionalOrderByGroupId.put(groupId, new ArrayList<GroupManageOrder>());
		}

		promotionalOrderByGroupId.get(groupId).add(groupManageOrder);
		
	}
	
	private void addEmergencyOrder(GroupManageOrder groupManageOrder) {
		
		String groupId = groupManageOrder.getGroupId();
		if (emergencyOrderByGroupId.get(groupId) == null) {
			emergencyOrderByGroupId.put(groupId, new ArrayList<GroupManageOrder>());
		}

		emergencyOrderByGroupId.get(groupId).add(groupManageOrder);
	}
	
	public GroupManageOrder getById(String id) {
		return groupManageOrderById.get(id); 
	}
	
	public @Nonnull Map<String, List<GroupManageOrder>> getEmergencyOrderByGroupId() {
		return emergencyOrderByGroupId;
	}
	
	public @Nonnull Map<String, List<GroupManageOrder>> getPromotionalOrderByGroupId() {
		return promotionalOrderByGroupId;
	}

	public void removeById(String id) {
		GroupManageOrder removedElement = groupManageOrderById.remove(id);
		
		if (removedElement == null) {
			return;
		}
		
		removeFromMap(removedElement, emergencyOrderByGroupId);
		removeFromMap(removedElement, promotionalOrderByGroupId);
	}


	private void removeFromMap(GroupManageOrder removedElement, Map<String, List<GroupManageOrder>> groupManageOrderByGroupId) {
		for (Entry<String, List<GroupManageOrder>> entry : groupManageOrderByGroupId.entrySet()) {
			List<GroupManageOrder> groupOrders = entry.getValue();
			Iterator<GroupManageOrder> iterator = groupOrders.iterator();
			while (iterator.hasNext()) {
				GroupManageOrder groupManageOrder = iterator.next();
				if (removedElement.getId().equals(groupManageOrder.getId())) {
					iterator.remove();
					break;
				}
			}
		}
	}

	public Iterator<GroupManageOrder> iterator() {
		return groupManageOrderById.values().iterator();
	}

	public void merge(GroupManageOrderStore existingStore) {
		Iterator<GroupManageOrder> existingStoreIterator = existingStore.iterator();
		
		while (existingStoreIterator.hasNext()) {
			GroupManageOrder groupManageOrder = existingStoreIterator.next();
			if (getById(groupManageOrder.getId()) == null) {
				add(groupManageOrder);
			}
		}
	}


	@Override
	public String toString() {
		
		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE);
		
		toStringBuilder.append("Emergency Package Manage Order");
		toStringBuilder.append("\t");
		
		for (Entry<String, List<GroupManageOrder>> entry : emergencyOrderByGroupId.entrySet()) {
			toStringBuilder.append("\t");
			toStringBuilder.append("GroupId", entry.getKey());
			toStringBuilder.append("\t");
			
			for (GroupManageOrder order : entry.getValue()) {
				toStringBuilder.append("\tPackage Id", order.getPackageId());
				toStringBuilder.append("\tOrder No", order.getOrderNumber());
			}
		}
		toStringBuilder.append("\t");
		toStringBuilder.append("Promotional Package Manage Order");
		toStringBuilder.append("\t");
		
		for (Entry<String, List<GroupManageOrder>> entry : promotionalOrderByGroupId.entrySet()) {
			toStringBuilder.append("\t");
			toStringBuilder.append("GroupId", entry.getKey());
			toStringBuilder.append("\t");
			
			for (GroupManageOrder order : entry.getValue()) {
				toStringBuilder.append("\tPackage Id", order.getPackageId());
				toStringBuilder.append("\tOrder No", order.getOrderNumber());
			}
		}
		
		return toStringBuilder.toString();
	}
}

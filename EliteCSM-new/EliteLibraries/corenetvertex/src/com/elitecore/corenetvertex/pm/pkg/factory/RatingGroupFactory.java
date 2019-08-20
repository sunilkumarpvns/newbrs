package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;

public class RatingGroupFactory {

	private PackageFactory packageFactory;

	public RatingGroupFactory(PackageFactory packageFactory) {
		this.packageFactory = packageFactory;
		
	}


	public List<RatingGroup> createRatingGroup(List<RatingGroupData> ratingGroupDatas) {

		List<RatingGroup> ratingGroups = new ArrayList<RatingGroup>();
		for (RatingGroupData serviceRatingGroupRelData : ratingGroupDatas) {

			if (serviceRatingGroupRelData == null) {
				continue;
			}

			ratingGroups.add(packageFactory.createRatingGroup(serviceRatingGroupRelData.getId()
					, serviceRatingGroupRelData.getName()
					, serviceRatingGroupRelData.getDescription()
					, serviceRatingGroupRelData.getIdentifier()));
		}
		return ratingGroups;
	}
	
	public RatingGroup createRatingGroup(RatingGroupData ratingGroupData) {
		
		return packageFactory.createRatingGroup(ratingGroupData.getId()
				, ratingGroupData.getName()
				, ratingGroupData.getDescription()
				, ratingGroupData.getIdentifier());
	}

	
}

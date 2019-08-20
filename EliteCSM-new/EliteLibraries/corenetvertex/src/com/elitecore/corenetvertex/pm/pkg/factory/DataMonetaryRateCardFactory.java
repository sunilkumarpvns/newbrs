package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.DataRateCardConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataMonetaryRateCardFactory {

	private PackageFactory packageFactory;

	public DataMonetaryRateCardFactory(PackageFactory packageFactory) {
		this.packageFactory = packageFactory;
	}
	
	public DataRateCard createDataMonetaryRateCard(DataRateCardData data, List<String> dataMonetaryRateCardFailReasons) {

		List<DataRateCardVersionRelationData> rateCardVersionRelation = data.getDataRateCardVersionRelationData();
		/// Sorting will be required when multiple version are supported
		String id= data.getId();
		String name = data.getName();

		if (rateCardVersionRelation.isEmpty()) {
			dataMonetaryRateCardFailReasons.add("Monetary rate card (" + name
					+ " parsing fail. Cause by: No rate card version found");
		}
		DataRateCardConf dataRateCardConf = null;

		Uom rateUom = Uom.fromVaue(data.getRateUnit());

		Uom pulseUom = Uom.fromVaue(data.getPulseUnit());

		List<RateCardVersion> dataRateCardVersions = new ArrayList<>();

		if (rateUom == null) {
			dataMonetaryRateCardFailReasons.add("Monetary rate card (" + name
					+ ") parsing fail. Cause by: rate Uom is not configurred");
		} else if (pulseUom == null) {
			dataMonetaryRateCardFailReasons.add("Monetary rate card (" + name
					+ ") parsing fail. Cause by: pulse Uom is not configurred");
		} else if (Uom.EVENT == rateUom) {
			dataMonetaryRateCardFailReasons.add("Monetary rate card (" + name
					+ ") parsing fail. Cause by: Unsupported rate UOM: " + rateUom);
		} else {
			dataRateCardConf = new DataRateCardConf(id,
					name,
					data.getLabelKey1(),
					data.getLabelKey2(),
                    dataRateCardVersions,
                    pulseUom,
                    rateUom);

			for (DataRateCardVersionRelationData versionRelation: rateCardVersionRelation) {
				List<VersionDetail> versionDetails = new ArrayList<>();

				List<DataRateCardVersionDetailData> rateCardVersionDetails = versionRelation.getDataRateCardVersionDetailDataList();
				for (DataRateCardVersionDetailData rateCardVersionDetail : rateCardVersionDetails) {

					if (rateCardVersionDetail.getSlab1() == null) {
						rateCardVersionDetail.setSlab1(CommonConstants.SLAB_UNLIMITED);
					}

					List<RateSlab> rateSlabs = new ArrayList<>();

					if(TierRateType.FLAT.name().equals(rateCardVersionDetail.getRateType())) {
						VersionDetail versionDetail = null;
						createAndAddFlatSlabs(rateSlabs
								, rateCardVersionDetail
								, pulseUom
								, rateUom);


						String keyValueOne = rateCardVersionDetail.getLabelKey1();
						String keyValueTwo = rateCardVersionDetail.getLabelKey2();
						RevenueDetailData revenueDetailData = rateCardVersionDetail.getRevenueDetail();
						keyValueOne = (Strings.isNullOrBlank(keyValueOne) ? "": keyValueOne);
						keyValueTwo = (Strings.isNullOrBlank(keyValueTwo) ? "": keyValueTwo);
						String revenueDetail = (Objects.nonNull(revenueDetailData) ? revenueDetailData.getName():"");
						if (rateCardVersionDetail.getRateType().equals(TierRateType.FLAT.name())) {
							versionDetail = packageFactory.createFlatRatingVersionDetail(keyValueOne, keyValueTwo, rateSlabs, revenueDetail);
						}

						versionDetails.add(versionDetail);
					} else  {
						dataMonetaryRateCardFailReasons.add("Monetary rate card (" + name
								+ ") parsing fail. Cause by: rate type is not defined");
					}
				}
				dataRateCardVersions.add(packageFactory.createDataRateCardVersion(id, name, versionRelation.getVersionName(), versionDetails ));
			}

		}

		if (dataMonetaryRateCardFailReasons.isEmpty() == false) {
			return null;
		}

		return packageFactory.createDataRateCard(dataRateCardConf);
	}

	private void createAndAddFlatSlabs(List<RateSlab> rateSlabs, DataRateCardVersionDetailData rateCardVersionDetail,
									   Uom pulseUom, Uom rateUom) {
		RateSlab rateSlabOne;

		if (rateCardVersionDetail.getSlab1() != null) {

			rateSlabOne = new FlatSlab(rateCardVersionDetail.getSlab1()
					, rateCardVersionDetail.getPulse1()
					, rateCardVersionDetail.getRate1()
					, pulseUom
					, rateUom);

			rateSlabs.add(rateSlabOne);
		}
	}

}

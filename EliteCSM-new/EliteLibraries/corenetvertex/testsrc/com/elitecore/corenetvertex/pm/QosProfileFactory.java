package com.elitecore.corenetvertex.pm;


import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.acesstime.exception.InvalidTimeSlotException;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.factory.QoSProfileDetailFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QosProfileFactory {
	
	
	
	public static Builder createQosProfile() {
		return new Builder();
	}
	
	
	public static class Builder{
		
		private List<QoSProfileDetail> qoSProfileDetails;
		private QoSProfileDetail hsqLevelQoSProfileDetail;
		private QuotaProfile quotaProfile;
		private DataRateCard dataRateCard;
		
		
		public Builder hasHSQLevelQoSProfileDetail(QoSProfileDetail hsqLevelQoSProfileDetail) {
			this.hsqLevelQoSProfileDetail = hsqLevelQoSProfileDetail;
			return this;
		}
		
		public Builder withQuotaProfile(QuotaProfile quotaProfile) {
			this.quotaProfile = quotaProfile;
			return this;
		}

        public Builder withDataRateCard(DataRateCard dataRateCard) {
            this.dataRateCard = dataRateCard;
            return this;
        }
		
		
		public QoSProfile build() {

			if (hsqLevelQoSProfileDetail == null) {
				hsqLevelQoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().build();
			}

            QoSProfileBuilder builder = new QoSProfileBuilder(UUID.randomUUID().toString(), UUID.randomUUID().toString()).withHSQPQoSProfileDetail(hsqLevelQoSProfileDetail);
					
			if (quotaProfile != null) {
				builder.withQuotaProfile(quotaProfile);
			}

			if (dataRateCard != null) {
			    builder.withDataRateCard(dataRateCard);
            }
		
			if(Collectionz.isNullOrEmpty(qoSProfileDetails) == false) {
				builder.withFUPLevelQosDetails(qoSProfileDetails);
			}
			return builder.build();
		}

        public static class QoSProfileBuilder {
            private final String name;
            private final String packageName;
            private com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail hsqLevelQoSDetail;
            @Nullable
            private QuotaProfile quotaProfile;
            private DataRateCard dataRateCard;
            @Nullable private List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail> fupLevelQoSDetails;
            @Nullable private String additionCondition;
            @Nullable private List<String> timeBaseCondition;
            @Nullable private List<String> accessNetwork;
            private int duration;


            public QoSProfileBuilder(String name, String packageName) {
                this.name = name;
                this.packageName = packageName;
            }

            public QoSProfileBuilder withHSQPQoSProfileDetail(com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail hsqLevelQoSDetail) {
                this.hsqLevelQoSDetail = hsqLevelQoSDetail;
                return this;
            }

            public QoSProfileBuilder withDuration(int duration) {
                this.duration = duration;
                return this;
            }

            public QoSProfileBuilder withAccessNetwork(List<String> accessNetwork) {
                this.accessNetwork = accessNetwork;
                return this;
            }

            public QoSProfileBuilder withQuotaProfile(QuotaProfile quotaProfile) {
                this.quotaProfile = quotaProfile;
                return this;
            }

            public QoSProfileBuilder withDataRateCard(DataRateCard dataRateCard) {
                this.dataRateCard = dataRateCard;
                return this;
            }

            public QoSProfileBuilder withFUPLevelQosDetail(com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail qosProfileDetail) {
                if(fupLevelQoSDetails == null) {
                    this.fupLevelQoSDetails = new ArrayList<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail>();
                }

                fupLevelQoSDetails.add(qosProfileDetail);

                return this;
            }

            public QoSProfileBuilder withFUPLevelQosDetails(List<QoSProfileDetail> qosProfileDetails) {
                if(fupLevelQoSDetails == null) {
                    this.fupLevelQoSDetails = new ArrayList<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail>();
                }

                fupLevelQoSDetails.addAll(qosProfileDetails);

                return this;
            }

            public QoSProfileBuilder withAdditionalCondition(String additionCondition) {
                this.additionCondition = additionCondition;
                return this;
            }

            public QoSProfileBuilder withTimeBaseCondition(List<String> timeBaseConditions) {
                this.timeBaseCondition = timeBaseConditions;
                return this;
            }

            public com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile build() throws RuntimeException {

                LogicalExpression logicalExpression = null;
                if(additionCondition != null) {
                    try {
                        logicalExpression = Compiler.getDefaultCompiler().parseLogicalExpression(additionCondition);
                    } catch (InvalidExpressionException e) {
                        throw new RuntimeException(e);
                    }
                }

                AccessTimePolicy accessTimePolicy = null;
                if(timeBaseCondition != null) {

                    accessTimePolicy = new AccessTimePolicy();
                    for(String condition : timeBaseCondition) {
                        try {
                            accessTimePolicy.addTimeSlot(TimeSlot.getTimeSlot(null, null, null, condition));
                        } catch (InvalidTimeSlotException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }


                return new com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile(name, name, packageName, packageName,
                        quotaProfile,
                        dataRateCard, accessNetwork,
                        duration,
                        hsqLevelQoSDetail,
                        fupLevelQoSDetails,
                        logicalExpression,
                        additionCondition,
                        accessTimePolicy);
            }



        }
	}
	


	public static QoSProfile createSimpleProfile() {
		return new Builder().build();
	}


	public static QoSProfile createQosProfileHasHigherQoSThan(IPCANQoS ipcanQoS) {
		
		
		QoSProfileDetail qoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().withSessionQoSHigherThan(ipcanQoS).build();
		return new Builder().hasHSQLevelQoSProfileDetail(qoSProfileDetail).build();
	}
	
	public static QoSProfile createQosProfileHasLowerQoSThan(IPCANQoS ipcanQoS) {
		QoSProfileDetail qoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().withSessionQoSLowerThan(ipcanQoS).build();
		return new Builder().hasHSQLevelQoSProfileDetail(qoSProfileDetail).build();
	}
	
	public static QoSProfile createQosProfileHasQoSEqualTo(IPCANQoS ipcanQoS) {
		QoSProfileDetail qoSProfileDetail = QoSProfileDetailFactory.createQoSProfile().withSessionQoSEqualTo(ipcanQoS).build();
		return new Builder().hasHSQLevelQoSProfileDetail(qoSProfileDetail).build();
	}

}

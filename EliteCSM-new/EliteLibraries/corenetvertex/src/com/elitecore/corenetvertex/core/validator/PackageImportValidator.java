package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.util.MessageResult;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;

/**
 * this class will used to validate uniqueness of entity within package.
 * It will validate the uniqueness of entity within package and provide MessageResult.
 */
public class PackageImportValidator {

    public static MessageResult validateUniquenessOfQoSProfileWithinPackage(List<QosProfileData> qosProfileDatas) {
        MessageResult messageResult = new MessageResult(true, null);
        if (Collectionz.isNullOrEmpty(qosProfileDatas) == false) {
            if (qosProfileDatas.size() == 1) {
                return messageResult;
            }
            HashSet<String> qosProfileNames = Collectionz.newHashSet();
            for (QosProfileData qosProfileData : qosProfileDatas) {
                boolean isUniqueNameAdded = qosProfileNames.add(qosProfileData.getName().toLowerCase());
                if (isUniqueNameAdded == false) {
                    messageResult.setFlag(false);
                    messageResult.setMessage(qosProfileData.getName());
                    return messageResult;
                }
            }
        }
        return messageResult;
    }

    public static MessageResult validateUniquenessOfPCCRuleWithinPackage(List<QosProfileData> qosProfileDatas) {
        HashSet<String> pccRulesByName = Collectionz.newHashSet();
        MessageResult messageResult = new MessageResult(true, null);
        for (QosProfileData qosProfileData : qosProfileDatas) {
            List<QosProfileDetailData> qosProfileDetailDatas = qosProfileData.getQosProfileDetailDataList();
            if (Collectionz.isNullOrEmpty(qosProfileDetailDatas) == false) {
                for (QosProfileDetailData detailData : qosProfileDetailDatas) {
                    List<PCCRuleData> pccRules = detailData.getPccRules();
                    if (Collectionz.isNullOrEmpty(pccRules) == false) {
                        if (Collectionz.isNullOrEmpty(qosProfileDatas) == false) {
                            for (PCCRuleData pccRuleData : detailData.getPccRules()) {
                                boolean isUniqueNameAdded = pccRulesByName.add(pccRuleData.getName());
                                if (isUniqueNameAdded == false) {
                                    messageResult.setFlag(false);
                                    messageResult.setMessage(pccRuleData.getName());
                                    return messageResult;
                                }
                            }
                        }
                    }
                }
            }
        }
        return messageResult;
    }

    public static MessageResult validateUniquenessOfUsageMeteringQuotaProfileWithinPackage(List<QuotaProfileData> quotaProfileDatas) {
        MessageResult messageResult = new MessageResult(true, null);
        if (Collectionz.isNullOrEmpty(quotaProfileDatas) == false) {
            if (quotaProfileDatas.size() == 1) {
                return messageResult;
            }
            HashSet<String> quotaProfileNames = Collectionz.newHashSet();
            for (QuotaProfileData quotaProfileData : quotaProfileDatas) {
                boolean isUniqueNameAdded = quotaProfileNames.add(quotaProfileData.getName().toLowerCase());
                if (isUniqueNameAdded == false) {
                    messageResult.setFlag(false);
                    messageResult.setMessage(quotaProfileData.getName());
                    return messageResult;
                }
            }
        }
        return messageResult;
    }


    public static MessageResult validateUniquenessOfSyQuotaProfileWithinPackage(List<SyQuotaProfileData> syQuotaProfileDatas) {
        MessageResult messageResult = new MessageResult(true, null);
        if (Collectionz.isNullOrEmpty(syQuotaProfileDatas) == false) {
            if (syQuotaProfileDatas.size() == 1) {
                return messageResult;
            }
            HashSet<String> syQuotaProfileNames = Collectionz.newHashSet();
            for (SyQuotaProfileData syQuotaProfileData : syQuotaProfileDatas) {
                boolean isUniqueNameAdded = syQuotaProfileNames.add(syQuotaProfileData.getName().toLowerCase());
                if (isUniqueNameAdded == false) {
                    messageResult.setFlag(false);
                    messageResult.setMessage(syQuotaProfileData.getName());
                    return messageResult;
                }
            }
        }
        return messageResult;
    }

    public static MessageResult validateUniquenessOfMonitoringKeyWithinPackage(List<QosProfileData> qosProfileDatas) {
        HashSet<String> monitoringKeyByName = Collectionz.newHashSet();
        MessageResult messageResult = new MessageResult(true, null);
        if (Collectionz.isNullOrEmpty(qosProfileDatas) == false) {
            for (QosProfileData qosProfileData : qosProfileDatas) {
                List<QosProfileDetailData> qosProfileDetailDatas = qosProfileData.getQosProfileDetailDataList();
                if (Collectionz.isNullOrEmpty(qosProfileDetailDatas) == false) {
                    for (QosProfileDetailData detailData : qosProfileDetailDatas) {
                        List<PCCRuleData> pccRules = detailData.getPccRules();
                        if (Collectionz.isNullOrEmpty(pccRules) == false) {
                            for (PCCRuleData pccRuleData : detailData.getPccRules()) {
                                boolean isUniqueNameAdded = monitoringKeyByName.add(pccRuleData.getMonitoringKey());
                                if (isUniqueNameAdded == false) {
                                    messageResult.setFlag(false);
                                    messageResult.setMessage(pccRuleData.getMonitoringKey());
                                    return messageResult;
                                }
                            }
                        }
                    }
                }
            }
        }
        return messageResult;
    }

    public static @Nonnull List<PkgGroupOrderData> getPkgGroupOrderDatas(String groups,String pkgType, String existingPkgId, SessionProvider sessionProvider) throws Exception {
        List<String> groupList = CommonConstants.COMMA_SPLITTER.split(groups);
        List<PkgGroupOrderData> pkgGroupOrderDatas = Collectionz.newArrayList();
        for (String groupId : groupList) {
            int orderNo;
            if(Strings.isNullOrBlank(existingPkgId)){
                orderNo = ImportExportCRUDOperationUtil.getGroupWiseMaxOrder(groupId, pkgType, sessionProvider)+1;
            }else {
                PkgData pkgData = new PkgData();
                pkgData.setId(existingPkgId);
                pkgData.setType(pkgType);
                orderNo = ImportExportCRUDOperationUtil.getPkgGroupOrderNo(pkgData, groupId, sessionProvider);
            }
            PkgGroupOrderData pkgGroupOrderData = new PkgGroupOrderData();
            pkgGroupOrderData.setGroupId(groupId);
            pkgGroupOrderData.setType(pkgType);
            pkgGroupOrderData.setOrderNumber(orderNo);
            pkgGroupOrderDatas.add(pkgGroupOrderData);
        }
        return pkgGroupOrderDatas;
    }

    public static MessageResult validateUniquenessOfQuotaBalanceBasedQuotaProfileWithinPackage(List<QuotaProfileData> quotaProfileDatas) {
        MessageResult messageResult = new MessageResult(true, null);
        //TODO -Dhyani Need to validate uniquness when Quota Balance Based configuration will be added
        return messageResult;
    }
}

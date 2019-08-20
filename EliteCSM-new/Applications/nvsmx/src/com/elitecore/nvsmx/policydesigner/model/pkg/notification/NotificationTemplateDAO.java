package com.elitecore.nvsmx.policydesigner.model.pkg.notification;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pd.notification.TopUpNotificationData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.stream.Collectors.joining;

public class NotificationTemplateDAO {

    private static final String MODULE = "Notification-Template-DAO";


    public static String attachedWithPackages(NotificationTemplateData notificationTemplateData) {
        DetachedCriteria associatedPackagesOfUsageNotification = DetachedCriteria.forClass(UsageNotificationData.class)
                .createAlias("pkgData", "pkg")
                .add(Restrictions.ne(CRUDOperationUtil.STATUS_PROPERTY, CommonConstants.STATUS_DELETED))
                .add(Restrictions.ne("pkg.status", CommonConstants.STATUS_DELETED))
                .add(Restrictions.or(Restrictions.eq("emailTemplateData.id", notificationTemplateData.getId()), Restrictions.eq("smsTemplateData.id", notificationTemplateData.getId())))
                .setProjection(Projections.property("pkg.name"));
        DetachedCriteria associatedPkgOfQuotaNotification = DetachedCriteria.forClass(QuotaNotificationData.class)
                .createAlias("pkgData", "pkg")
                .add(Restrictions.ne(CRUDOperationUtil.STATUS_PROPERTY, CommonConstants.STATUS_DELETED))
                .add(Restrictions.ne("pkg.status", CommonConstants.STATUS_DELETED))
                .add(Restrictions.or(Restrictions.eq("emailTemplateData.id", notificationTemplateData.getId()), Restrictions.eq("smsTemplateData.id", notificationTemplateData.getId())))
                .setProjection(Projections.property("pkg.name"));
        DetachedCriteria associatedDataTopUps = DetachedCriteria.forClass(TopUpNotificationData.class)
                .createAlias("dataTopUpData", "topup")
                .add(Restrictions.or(Restrictions.eq("emailTemplateData.id", notificationTemplateData.getId()), Restrictions.eq("smsTemplateData.id", notificationTemplateData.getId())))
                .setProjection(Projections.property("topup.name"));
        DetachedCriteria associatedDataRnCPackages = DetachedCriteria.forClass(RncNotificationData.class)
                .createAlias("rncPackageData", "rncpackage")
                .add(Restrictions.or(Restrictions.eq("emailTemplateData.id", notificationTemplateData.getId()), Restrictions.eq("smsTemplateData.id", notificationTemplateData.getId())))
                .setProjection(Projections.property("rncpackage.name"));
        try {
            String pkgNamesOfUsageNotification = CRUDOperationUtil.findAllByDetachedCriteria(associatedPackagesOfUsageNotification)
                    .stream().map(Object::toString)
                    .collect(joining(NVSMXCommonConstants.COMMA));
            String pkgNamesOfQuotaNotification = CRUDOperationUtil.findAllByDetachedCriteria(associatedPkgOfQuotaNotification)
                    .stream().map(Object::toString)
                    .collect(joining(NVSMXCommonConstants.COMMA));
            String dataTopUps = CRUDOperationUtil.findAllByDetachedCriteria(associatedDataTopUps)
                    .stream().map(Object::toString)
                    .collect(joining(NVSMXCommonConstants.COMMA));
            String rncPackages = CRUDOperationUtil.findAllByDetachedCriteria(associatedDataRnCPackages)
                    .stream().map(Object::toString)
                    .collect(joining(NVSMXCommonConstants.COMMA));

            StringBuilder associations = new StringBuilder();
            if (StringUtils.isNotEmpty(pkgNamesOfUsageNotification)) {
                associations.append("Data Packages(").append(pkgNamesOfUsageNotification).append(")\n");
            }
            if (StringUtils.isNotEmpty(pkgNamesOfQuotaNotification)) {
                associations.append("RnC Base Data Packages(").append(pkgNamesOfQuotaNotification).append(")\n");
            }
            if (StringUtils.isNotEmpty(dataTopUps)) {
                associations.append("Data Top-Up(").append(dataTopUps).append(")\n");
            }
            if (StringUtils.isNotEmpty(rncPackages)) {
                associations.append("RnC Packages(").append(rncPackages).append(")\n");
            }
            return associations.toString();
        } catch (HibernateDataException hex) {
            getLogger().error(MODULE, "Failed to get packages for NotificationTemplateData. Reason: " + hex.getMessage());
            getLogger().trace(MODULE, hex);
            throw new HibernateDataException(hex.getMessage(), hex);

        } catch (Exception e) {
            getLogger().error(MODULE, "Failed to get packages for NotificationTemplateData. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            throw new HibernateDataException(e.getMessage(), e);
        }
    }


}

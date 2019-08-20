package com.elitecore.corenetvertex.pkg.importimspkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgPCCAttributeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.util.SessionProvider;
import org.hibernate.HibernateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * A class that provides import operation on ims package and its hierarchy
 */
public class IMSPkgImportOperation implements ImportOperation<IMSPkgData, IMSPkgData, IMSPkgData> {

    @Override
    public void importData(@Nonnull IMSPkgData imsPkgData, @Nullable IMSPkgData parentObject, @Nullable IMSPkgData superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {
        try {

            IMSPkgData imsPkgToBeImported = new IMSPkgData();
            importIMSPackageData(imsPkgToBeImported, imsPkgData, session);

            if(Collectionz.isNullOrEmpty(imsPkgData.getImsPkgServiceDatas()) == false) {
                importImsServices(imsPkgToBeImported, imsPkgData, session);
            }


        } catch (HibernateException e) {
            throw new ImportOperationFailedException("Failed to import ims package: " + imsPkgData.getName() + ". Reason: " + e.getMessage(), e);

        }
    }

    private void importImsServices(IMSPkgData imsPkgToBeImported, IMSPkgData imsPkgData, SessionProvider session) {
        for (IMSPkgServiceData imsPkgService : imsPkgData.getImsPkgServiceDatas()) {

            List<IMSPkgPCCAttributeData> imsPkgPCCAttributes = imsPkgService.getImsPkgPCCAttributeDatas();
            imsPkgService.setImsPkgData(imsPkgToBeImported);
            imsPkgService.setImsPkgPCCAttributeDatas(null);

            if(Strings.isNullOrBlank(imsPkgService.getId())){
                imsPkgService.setId(null);
                session.getSession().persist(imsPkgService);
            }else {
                session.getSession().merge(imsPkgService);
            }
            if(Collectionz.isNullOrEmpty(imsPkgPCCAttributes) == false) {
                importImsPCCAttributes(imsPkgPCCAttributes, imsPkgService, session);
            }

        }
    }

    private void importImsPCCAttributes(List<IMSPkgPCCAttributeData> imsPCCAttributes, IMSPkgServiceData imsPkgService, SessionProvider session) {
        for (IMSPkgPCCAttributeData imsPkgPCCAttribute : imsPCCAttributes) {
            imsPkgPCCAttribute.setImsPkgServiceData(imsPkgService);
            if (Strings.isNullOrBlank(imsPkgPCCAttribute.getId())) {
                imsPkgPCCAttribute.setId(null);
                session.getSession().persist(imsPkgPCCAttribute);
            } else {
                session.getSession().merge(imsPkgPCCAttribute);
            }
        }
    }



    private void importIMSPackageData(IMSPkgData imsPkgToBeImported, IMSPkgData imsPkgData, SessionProvider session) {
        imsPkgToBeImported.setId(imsPkgData.getId());
        imsPkgToBeImported.setName(imsPkgData.getName());
        imsPkgToBeImported.setDescription(imsPkgData.getDescription());
        imsPkgToBeImported.setPrice(imsPkgData.getPrice());
        imsPkgToBeImported.setCreatedDateAndStaff(imsPkgData.getCreatedByStaff());
        imsPkgToBeImported.setModifiedDateAndStaff(imsPkgData.getModifiedByStaff());
        imsPkgToBeImported.setType(imsPkgData.getType());
        imsPkgToBeImported.setPackageMode(imsPkgData.getPackageMode());
        imsPkgToBeImported.setGroups(imsPkgData.getGroups());
        imsPkgToBeImported.setStatus(imsPkgData.getStatus());

        if (Strings.isNullOrBlank(imsPkgToBeImported.getId())) {
            imsPkgToBeImported.setId(null);
            session.getSession().persist(imsPkgToBeImported);
        } else {
            session.getSession().merge(imsPkgToBeImported);
        }
    }

}

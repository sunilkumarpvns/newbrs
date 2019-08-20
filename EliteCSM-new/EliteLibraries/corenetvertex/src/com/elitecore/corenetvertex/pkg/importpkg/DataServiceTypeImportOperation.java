package com.elitecore.corenetvertex.pkg.importpkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.pkg.dataservicetype.DefaultServiceDataFlowExt;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.util.SessionProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Provides Import operation on Service Type and its sub-hierarchy
 * Created by Ishani on 15/9/16.
 */
public class DataServiceTypeImportOperation implements ImportOperation<DataServiceTypeDataExt, DataServiceTypeDataExt, DataServiceTypeDataExt> {


    @Override
    public void importData(@Nonnull DataServiceTypeDataExt dataServiceTypeDataImported, @Nullable DataServiceTypeDataExt parentObject, @Nullable DataServiceTypeDataExt superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {
        List<RatingGroupData> ratingGroups = dataServiceTypeDataImported.getRatingGroupDatas();
        List<DefaultServiceDataFlowExt> defaultServiceDataFlowDatas = dataServiceTypeDataImported.getDefaultServiceDataFlows();
        dataServiceTypeDataImported.setRatingGroupDatas(null);
        dataServiceTypeDataImported.setDefaultServiceDataFlows(null);
        if (Strings.isNullOrBlank(dataServiceTypeDataImported.getId())) {
            dataServiceTypeDataImported.setId(null);
            session.getSession().persist(dataServiceTypeDataImported);
        } else {
            session.getSession().merge(dataServiceTypeDataImported);
        }

        if (Collectionz.isNullOrEmpty(ratingGroups) == false) {
            dataServiceTypeDataImported.setRatingGroupDatas(ratingGroups);
            session.getSession().merge(dataServiceTypeDataImported);
        }
        if(Collectionz.isNullOrEmpty(defaultServiceDataFlowDatas) == false) {
            setDefaultServiceDataFlow(dataServiceTypeDataImported, defaultServiceDataFlowDatas, session);
        }

    }

    private void setDefaultServiceDataFlow(DataServiceTypeDataExt dataServiceTypeDataImported, List<DefaultServiceDataFlowExt> defaultServiceDataFlowDatas, SessionProvider session) {
        for (DefaultServiceDataFlowExt defaultServiceDataFlow : defaultServiceDataFlowDatas) {
            defaultServiceDataFlow.setDataServiceTypeExt(dataServiceTypeDataImported);
            if (Strings.isNullOrBlank(defaultServiceDataFlow.getServiceDataFlowId())) {
                defaultServiceDataFlow.setServiceDataFlowId(null);
                session.getSession().persist(defaultServiceDataFlow);
            } else {
                session.getSession().merge(defaultServiceDataFlow);
            }
        }
    }


}

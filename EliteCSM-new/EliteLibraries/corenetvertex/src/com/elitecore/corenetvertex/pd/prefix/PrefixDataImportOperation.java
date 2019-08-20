package com.elitecore.corenetvertex.pd.prefix;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import org.hibernate.criterion.Order;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PrefixDataImportOperation implements ImportOperation<PrefixDataExt, PrefixDataExt, PrefixDataExt> {

    private String MODULE = "Prefix-Import-Operation";
    @Override
    public void importData(@Nonnull PrefixDataExt prefixImported, @Nullable PrefixDataExt parentObject, @Nullable PrefixDataExt superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {

        try
        {
            List<PrefixDataExt> prefixDataExtList= ImportExportCRUDOperationUtil.get(PrefixDataExt.class, Order.asc("prefix"),session);
            if (Strings.isNullOrBlank(prefixImported.getId())) {
                Boolean isPrefixAlreadyExists=false;
                for (int i=0;i<prefixDataExtList.size();i++)
                {
                    if (prefixDataExtList.get(i).getPrefix().equalsIgnoreCase(prefixImported.getPrefix()))
                    {
                        isPrefixAlreadyExists = true;
                    }
                }
                if(!isPrefixAlreadyExists)
                {
                    prefixImported.setId(null);
                    session.getSession().persist(prefixImported);
                }
            } else {
                session.getSession().merge(prefixImported);
            }
        }
        catch (Exception e)
        {
            LogManager.getLogger().error(MODULE, "Error while importing prefix: " + prefixImported.getPrefix());
            LogManager.getLogger().trace(MODULE, e);
        }
    }
}

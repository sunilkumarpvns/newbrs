package com.elitecore.corenetvertex.pkg.importpkg;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.util.SessionProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Ishani on 22/9/16.
 */
public class RatingGroupImportOperation implements ImportOperation<RatingGroupDataExt, RatingGroupDataExt, RatingGroupDataExt> {

    @Override
    public void importData(@Nonnull RatingGroupDataExt ratingGroupImported, @Nullable RatingGroupDataExt parentObject, @Nullable RatingGroupDataExt superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException {
        if (Strings.isNullOrBlank(ratingGroupImported.getId())) {
            ratingGroupImported.setId(null);
            session.getSession().persist(ratingGroupImported);
        } else {
            session.getSession().merge(ratingGroupImported);
        }


    }
}
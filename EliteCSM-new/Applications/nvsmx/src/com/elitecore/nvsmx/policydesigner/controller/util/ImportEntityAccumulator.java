package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This class will be used to accumulate common functions of import export functionality
 * Created by dhyani on 1/6/17.
 */
public class ImportEntityAccumulator<T> {

    private @Nonnull List<T> entityList;
    private @Nonnull String commaSeparatedSelectedEntityIndexes;

    public ImportEntityAccumulator(@Nonnull List<T> entityList, @Nonnull String commaSeparatedSelectedEntityIndexes) {
        this.entityList = entityList;
        this.commaSeparatedSelectedEntityIndexes = commaSeparatedSelectedEntityIndexes;
    }

    public @Nonnull List<T> get(){

        List<T> importEntities = Collectionz.newArrayList();

        List<String> indexes = CommonConstants.COMMA_SPLITTER.split(commaSeparatedSelectedEntityIndexes);

        for (String indexAsString : indexes) {
            int index = Integer.valueOf(indexAsString);

            if(index >= entityList.size()) {
                throw new ArrayIndexOutOfBoundsException("Index " + indexAsString + " is greater then list size");
            }

            T entity = entityList.get(index);

            if(entity == null) {
                throw new ArrayIndexOutOfBoundsException("Value not found at index " + indexAsString);
            }

            importEntities.add(entity);
        }
        return importEntities;
    }
}

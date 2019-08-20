package com.elitecore.corenetvertex.core.imports;

import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.util.SessionProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface ImportOperation<T,PT, RT> {

	public void importData(@Nonnull T t, @Nullable PT parentObject, @Nullable RT superObject, @Nonnull SessionProvider session) throws ImportOperationFailedException;
}

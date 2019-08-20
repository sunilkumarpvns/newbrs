package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.driverx.cdr.deprecated.FileParametersResolvers;
import com.elitecore.core.driverx.cdr.deprecated.NullTextResolver;

import java.util.Objects;
import java.util.function.Function;

public class PCRFKeyBaseFileParameterResolver implements FileParametersResolvers<ValueProviderExtImpl> {

    private Function<ValueProviderExtImpl, String> filePrefixKey;
    private Function<ValueProviderExtImpl, String> folderName;


    public PCRFKeyBaseFileParameterResolver(Function<ValueProviderExtImpl, String> filePrefixKey,
                                            Function<ValueProviderExtImpl, String> folderName) {
        this.filePrefixKey = filePrefixKey;
        this.folderName = folderName;
    }


    @Override
    public String getFilePrefix(ValueProviderExtImpl valueProviderExt) {
        return filePrefixKey.apply(valueProviderExt);
    }

    @Override
    public String getFolderName(ValueProviderExtImpl valueProviderExt) {
        return folderName.apply(valueProviderExt);
    }


    public static PCRFKeyBaseFileParameterResolver create(String fileNamePrefixParameter, String folderNameParameter) {


        Function<ValueProviderExtImpl, String> filePrefixNameResolver = createResolver(fileNamePrefixParameter);
        Function<ValueProviderExtImpl, String> folderNameResolver = createResolver(folderNameParameter);

        return new PCRFKeyBaseFileParameterResolver(filePrefixNameResolver, folderNameResolver);
    }

    private static Function<ValueProviderExtImpl, String> createResolver(String parameter) {

        if(Objects.isNull(parameter)) {
            return new NullTextResolver<>();
        }
        Function<ValueProviderExtImpl, String> folderNameResolver = new ValueProviderBaseParameterResolver(parameter);
        return folderNameResolver;
    }
}

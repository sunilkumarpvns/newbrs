package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;

import javax.annotation.Nullable;

/**
 * Created by aditya on 4/24/17.
 */
public class SuccessRMIResponse<T> extends RMIResponse<T> {


    public T getResponse() {
        return response;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isErrorOccurred() {
        return false;
    }

    @Nullable
    @Override
    public Exception getError() {
        return null;
    }

    private T response;

    public SuccessRMIResponse(T response, ServerInformation instanceGroupData, ServerInformation instanceData) {
        super(instanceGroupData,instanceData);
        this.response = response;

    }
}

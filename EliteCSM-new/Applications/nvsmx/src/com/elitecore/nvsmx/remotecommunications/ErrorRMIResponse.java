package com.elitecore.nvsmx.remotecommunications;



import com.elitecore.nvsmx.commons.model.sessionmanager.NetServerInstanceData;
import com.elitecore.nvsmx.remotecommunications.data.ServerInformation;
import com.elitecore.nvsmx.remotecommunications.data.ServerInstanceGroupData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;

/**
 * Created by aditya on 4/24/17.
 */
public class ErrorRMIResponse<T> extends RMIResponse<T> {
    private Exception exception;

    public ErrorRMIResponse(Exception exception, ServerInformation instanceGroupData, ServerInformation instanceData) {
        super(instanceGroupData, instanceData);
        this.exception = exception;
    }



    @Nullable
    @Override
    public T getResponse() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isErrorOccurred() {
        return true;
    }

    @Nonnull
    @Override
    public Exception getError() {
        return exception;
    }
}

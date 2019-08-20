package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class RemoteMessageCommunicator{

	private static final String MODULE = "REMOTE-MSG-COMM";
	private static final  RemoteMessageCommunicator remoteMessageCommunicator;

	static {
		remoteMessageCommunicator = new RemoteMessageCommunicator();
	}

	private RemoteMessageCommunicator(){

	}

	public static RemoteMessageCommunicator getInstance() {
		return remoteMessageCommunicator;
	}

	@Nonnull public static  <T> RMIResponse<T> callSync(@Nonnull RemoteMethod remoteMethod, @Nonnull RMIGroup rmiGroup, @Nonnull String serverInstanceId) {
		return rmiGroup.call(remoteMethod, 2, TimeUnit.SECONDS, serverInstanceId);
	}

	@Nullable public static  <T> RMIResponse<T> callSync(@Nonnull List<RMIGroup> rmiGroups, @Nonnull RemoteMethod method, Predicate<RMIResponse<T>> predicate) {

		for (int groupIndex = 0; groupIndex < rmiGroups.size(); groupIndex++) {
			RMIGroup rmiGroup = rmiGroups.get(groupIndex);

			RMIResponse<T> result = rmiGroup.call(method, 2, TimeUnit.SECONDS);

			if (result != null && predicate.apply(result)) {
				return result;
			}

		}


        return null;

	}



	@Nonnull public static BroadCastCompletionResult broadcast(List<? extends EndPoint> endPoints, RemoteMethod remoteMethod) {

        if (getLogger().isDebugLogLevel())
            getLogger().debug(MODULE, "Calling caller: " + remoteMethod.getName());

        BroadCastCompletionResult broadCastCompletionResult = new BroadCastCompletionResult(remoteMethod.getName());
        for (int endPointIndex = 0; endPointIndex < endPoints.size(); endPointIndex++) {
            EndPoint endPoint = endPoints.get(endPointIndex);
            broadCastCompletionResult.add(endPoint.submit(remoteMethod), endPoint);
        }


        return broadCastCompletionResult;
    }
}
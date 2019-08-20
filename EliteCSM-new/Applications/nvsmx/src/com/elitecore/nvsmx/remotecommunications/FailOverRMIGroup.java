package com.elitecore.nvsmx.remotecommunications;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;
import com.elitecore.nvsmx.remotecommunications.exception.CommunicationException;

import javax.annotation.Nonnull;

public class FailOverRMIGroup extends ESCommunicatorGroupImpl<EndPoint> implements RMIGroup {

	private final String MODULE;
	private @Nonnull EndPoint primaryEndPoint;
    private @Nonnull EndPoint secondaryEndPoint;
	private @Nonnull Map<String,EndPoint> serverInstanceIdToEndPoint;


	public FailOverRMIGroup(@Nonnull EndPoint primaryEndPoint,
                            @Nonnull EndPoint secondaryEndPoint){

		this.MODULE = "FAILOVER-RMI-GRP-" + primaryEndPoint.getGroupData().getName();
		this.primaryEndPoint = primaryEndPoint;
		this.secondaryEndPoint = secondaryEndPoint;
        serverInstanceIdToEndPoint = Maps.newHashMap();
        serverInstanceIdToEndPoint.put(primaryEndPoint.getInstanceData().getNetServerCode(),primaryEndPoint);
        serverInstanceIdToEndPoint.put(secondaryEndPoint.getInstanceData().getNetServerCode(),secondaryEndPoint);
	}


	public void init(){
		addCommunicator(this.primaryEndPoint);
		addCommunicator(this.secondaryEndPoint,0);
	}


	@Override
	public <V> Future<RMIResponse<V>> call(RemoteMethod method) {


		EndPoint rmiInstance = getCommunicator();

		if(rmiInstance == null) {
			SettableFuture<RMIResponse<V>> errorResponse = SettableFuture.create();
			errorResponse.set(new ErrorRMIResponse<V>(new CommunicationException("No live instance found from group: " + getName()), primaryEndPoint.getGroupData(), primaryEndPoint.getInstanceData()));
			return errorResponse;
		}
		return rmiInstance.submit(method);
	}

	@Override
	public <V> RMIResponse<V> call(RemoteMethod method, long time, TimeUnit timeUnit) {

		EndPoint rmiInstance = getCommunicator();

		if (rmiInstance == null) {
			return new ErrorRMIResponse<V>(new CommunicationException("No live instance found from group: " + getName()), primaryEndPoint.getGroupData(), primaryEndPoint.getInstanceData());
		}
		try {
			Future<RMIResponse<V>> responseFuture = rmiInstance.submit(method);
			return responseFuture.get(time, timeUnit);
		} catch (Exception e) {
			return new ErrorRMIResponse<V>(e, rmiInstance.getGroupData(), rmiInstance.getInstanceData());

		}
	}


	/** This method is used to call remote method from given server instance id.<br/>
	 * Its flow will be,<br>
	 * Fetch communicator from given instance id & check its aliveness<br/>
	 * if Yes<br/><p>
	 *   Call its {@code submit} method & fetch response from it , if any exception occur
	 *   then call the same method with secondary instance.</p>
	 *   <p>
	 * if No <br/>
	 *   Call same method to secondary instance.
	 *   </p> while calling to secondary instance if any exception occur then throw it.
	 *
	 * @param method {@code RemoteMethod} which is being called
	 * @param time the maximum time to wait
	 * @param timeUnit time unit of the timeout argument
	 * @param serverInstanceId instanceId of the server on which this operation will be pe performed
	 * @param <V> the computed result
	 * @return {@code RMIResponse}
	 * @throws InterruptedException
	 * @throws TimeoutException
	 * @throws CommunicationException
	 */
	@Override
	public <V> RMIResponse<V> call(RemoteMethod method, long time, TimeUnit timeUnit, String serverInstanceId) {

		EndPoint endPoint = getCommunicator(serverInstanceId);

        if(endPoint.isAlive()==false) {
			EndPoint secondaryEndPoint = getSecondaryCommunicator(endPoint);
			if (secondaryEndPoint == null) {
               return new ErrorRMIResponse<V>(new CommunicationException(endPoint.getName() + " not live from group: " + getName()), endPoint.getGroupData(), endPoint.getInstanceData());
			}

			getLogger().warn(MODULE, "Try on another communicator: " + secondaryEndPoint.getName() + ". Reason: " + endPoint.getName() + " is not alive");

			return callToSecondaryInstance(method, time, timeUnit, secondaryEndPoint);
		} else {
			try{
				Future<RMIResponse<V>> future = endPoint.submit(method);
				return future.get(time, timeUnit);
			} catch (ExecutionException e) {
				EndPoint secondaryEndPoint = getSecondaryCommunicator(endPoint);

				if (secondaryEndPoint == null) {
					return new ErrorRMIResponse<V>(e,endPoint.getGroupData(), endPoint.getInstanceData());

				}

				getLogger().warn(MODULE, "Try on another communicator: " + secondaryEndPoint.getName() + ". Reason: " + e.getCause().getMessage());

				return callToSecondaryInstance(method, time, timeUnit, secondaryEndPoint);
			} catch (Exception ex) {
                return new ErrorRMIResponse<V>(ex,endPoint.getGroupData(),endPoint.getInstanceData());
            }
		}
	}

	private <V> RMIResponse<V> callToSecondaryInstance(RemoteMethod method, long time, TimeUnit timeUnit, EndPoint secondaryEndPoint) {
		Future<RMIResponse<V>> future = secondaryEndPoint.submit(method);
		try {
            return future.get(time, timeUnit);
        } catch (Exception e) {
			return new ErrorRMIResponse<V>(e, secondaryEndPoint.getGroupData(), secondaryEndPoint.getInstanceData());
        }
	}

	@Override
	public String getName() {
		return primaryEndPoint.getGroupData().getName();
	}

	@Override
	public String id() {
		return primaryEndPoint.getGroupData().getId();
	}

	private EndPoint getCommunicator(String netServerCode){
		   return serverInstanceIdToEndPoint.get(netServerCode);
    }


}
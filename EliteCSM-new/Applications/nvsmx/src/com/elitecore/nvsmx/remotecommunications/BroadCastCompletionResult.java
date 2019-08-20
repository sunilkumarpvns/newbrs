package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * Created by harsh on 4/27/17.
 */
public class BroadCastCompletionResult<V> {

    private static final String MODULE = "BROADCAST-COMP-RESULT";
    @Nonnull private final LinkedHashMap<Future<RMIResponse<V>>, EndPoint> futures;
    @Nonnull private final String methodCalled;

    @Nullable private Predicate<RMIResponse<V>> predicate;
    @Nullable private Comparator<RMIResponse<V>> comparator;

    public <T> BroadCastCompletionResult(@Nonnull String methodCalled) {
        this.methodCalled = methodCalled;
        this.futures = new LinkedHashMap<Future<RMIResponse<V>>, EndPoint>();
    }

    public void add(@Nonnull Future<RMIResponse<V>> future, @Nonnull EndPoint endPoint) {
        futures.put(future, endPoint);
    }


    public @Nonnull Collection<RMIResponse<V>> getAll(int waitTime, @Nonnull java.util.concurrent.TimeUnit unitOfTime) {

        long endTime = System.currentTimeMillis() + unitOfTime.toMillis(3);

        Collection<RMIResponse<V>> results = null;
        if (comparator != null) {
            results = new TreeSet<RMIResponse<V>>(comparator);
        } else {
            results = new ArrayList<RMIResponse<V>>(futures.size());
        }

        if(Maps.isNullOrEmpty(futures)){
            return results;
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Error while waiting for 10 ms for async response for "
                        + methodCalled + ". Reason: " + e.getMessage());
            }
            getLogger().trace(MODULE, e);
        }

        do {

            Iterator<Map.Entry<Future<RMIResponse<V>>, EndPoint>> futureIterator = futures.entrySet().iterator();
            while(futureIterator.hasNext()) {
                Map.Entry<Future<RMIResponse<V>>, EndPoint> entry = futureIterator.next();
                final Future<RMIResponse<V>> future = entry.getKey();
                final EndPoint endPoint = entry.getValue();
                if (future.isDone()) {
                    RMIResponse<V> result;
                    try {
                        result = future.get();
                    } catch (Exception e) {
                        result = new ErrorRMIResponse<V>(e, endPoint.getGroupData(), endPoint.getInstanceData());
                    }

                    if(predicate == null || predicate.apply(result)) {
                        results.add(result);
                    }

                    futureIterator.remove();
                }
            }

            if (futures.isEmpty()) {
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Error while waiting for 100 ms for async response for "
                                + methodCalled + ". Reason: " + e.getMessage());
                    }
                    getLogger().trace(MODULE, e);
                }
            }

        } while(System.currentTimeMillis() < endTime && futures.isEmpty() == false);


        if (futures.isEmpty() == false) {
            for (Map.Entry<Future<RMIResponse<V>>, EndPoint> future : futures.entrySet()) {
                future.getKey().cancel(true);
                ErrorRMIResponse<V> errorRMIResponse = new ErrorRMIResponse<V>(new TimeoutException("Timeout while waiting for async response"), future.getValue().getGroupData(), future.getValue().getInstanceData());
                if(predicate == null || predicate.apply(errorRMIResponse)) {
                    results.add(errorRMIResponse);
                }
            }
        }

        return results;
    }

    public @Nullable RMIResponse<V> getFirst(int waitTime, @Nonnull java.util.concurrent.TimeUnit unitOfTime) {
        Collection<RMIResponse<V>> results = getAll(waitTime, unitOfTime);

        if (Collectionz.isNullOrEmpty(results)) {
            return null;
        } else {
            return results.iterator().next();
        }

    }



    /*
        provide filter which execute while iterating over future collections.
         Purpose of this method is to avoid addition iteration to filter result after receiving the collection of RMI result
     */
    public BroadCastCompletionResult<V> filter(Predicate<RMIResponse<V>> predicate) {
        this.predicate = predicate;
        return this;
    }

    /*
        provide comparator which sort result while iterating over future collections.
         Purpose of this method is to avoid addition iteration to sort result after receiving the collection of RMI result
     */
    public BroadCastCompletionResult<V> sort(Comparator<RMIResponse<V>> comparator) {
        this.comparator = comparator;
        return this;
    }



}
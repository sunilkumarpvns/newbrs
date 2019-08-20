package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Predicate;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EndPointStore {
    private final @Nonnull List<EndPoint> netvertexEndPoints;
    private final @Nonnull List<NVSMXEndPoint> nvsmxEndPoints;
    private final @Nonnull Map<String, EndPoint> netEngineServerCodeToEndPoint;
    private final @Nonnull Map<String, NVSMXEndPoint> idToPDEndPoint;

    public EndPointStore() {
        this.netvertexEndPoints = Collectionz.newArrayList();
        this.nvsmxEndPoints = Collectionz.newArrayList();
        this.netEngineServerCodeToEndPoint = Maps.newHashMap();
        this.idToPDEndPoint = Maps.newHashMap();
    }


    public void addNetVertexEndPoint(@Nonnull EndPoint endPoint) {
        netvertexEndPoints.add(endPoint);
        netEngineServerCodeToEndPoint.put(endPoint.getInstanceData().getNetServerCode(), endPoint);
    }

    public void addNVSMXEndPointList(@Nonnull NVSMXEndPoint endPoint) {
        nvsmxEndPoints.add(endPoint);
        idToPDEndPoint.put(endPoint.getInstanceData().getId(), endPoint);

    }

    public void addIfAbsentNetVertexEndPoint(@Nonnull Collection<EndPoint> toBeAddedNetVertexEndPoints) {
        Predicate<EndPoint> netvertexEndPointPredicate = new Predicate<EndPoint>() {
            @Override
            public boolean apply(EndPoint netvertexEndPoint) {
                if (netEngineServerCodeToEndPoint.containsKey(netvertexEndPoint.getInstanceData().getNetServerCode())) {
                    return false;
                }
                return true;
            }
        };
        addAll(this.netvertexEndPoints, toBeAddedNetVertexEndPoints, netvertexEndPointPredicate);
        addNetVertexEndPointsAll(this.netEngineServerCodeToEndPoint, toBeAddedNetVertexEndPoints);
    }

    public void addIfAbsentNvsxmEndPoint(@Nonnull Collection<NVSMXEndPoint> toBeAddedNVSMXEndPoints) {
        Predicate<EndPoint> nvsmxEndPointPredicate = new Predicate<EndPoint>() {
            @Override
            public boolean apply(EndPoint nvsmxEndPoint) {
                if (idToPDEndPoint.containsKey(nvsmxEndPoint.getInstanceData().getId())) {
                    return false;
                }

                return true;
            }
        };
        addAllNVSMXEndPoints(this.nvsmxEndPoints, toBeAddedNVSMXEndPoints, nvsmxEndPointPredicate);
        addNVSMXEndPoints(this.idToPDEndPoint, toBeAddedNVSMXEndPoints);
    }


    private void addNetVertexEndPointsAll(Map<String, EndPoint> netEngineServerCodeToEndPoint, Collection<EndPoint> toBeAddedNetVertexEndPoints) {
        for (EndPoint endPoint : toBeAddedNetVertexEndPoints) {
            if (netEngineServerCodeToEndPoint.containsKey(endPoint.getInstanceData().getNetServerCode()) == false) {
                netEngineServerCodeToEndPoint.put(endPoint.getInstanceData().getNetServerCode(), endPoint);
            }
        }

    }

    private void addNVSMXEndPoints(Map<String, NVSMXEndPoint> idToPDEndPoint, Collection<NVSMXEndPoint> toBeAddedNVSMXEndPoints) {
        for (NVSMXEndPoint endPoint : toBeAddedNVSMXEndPoints) {
            if (idToPDEndPoint.containsKey(endPoint.getInstanceData().getId()) == false) {
                idToPDEndPoint.put(endPoint.getInstanceData().getId(), endPoint);
            }
        }
    }


    private void addAll(List<EndPoint> existingEndPoints, Collection<EndPoint> tobeAdded, Predicate<EndPoint> endPointPredicate) {
        for (EndPoint endPoint : tobeAdded) {
            if (endPointPredicate.apply(endPoint)) {
                existingEndPoints.add(endPoint);
            }
        }
    }
    private void addAllNVSMXEndPoints(List<NVSMXEndPoint> existingEndPoints, Collection<NVSMXEndPoint> tobeAdded, Predicate<EndPoint> endPointPredicate) {
        for (NVSMXEndPoint endPoint : tobeAdded) {
            if (endPointPredicate.apply(endPoint)) {
                existingEndPoints.add(endPoint);
            }
        }
    }


    public @Nonnull List<EndPoint> getNetvertexEndPoints() {
        return netvertexEndPoints;
    }


    public @Nonnull List<NVSMXEndPoint> getNvsmxEndPoints() { return nvsmxEndPoints; }


    public @Nonnull Map<String, EndPoint> getNetEngineServerCodeToEndPoint() {
        return netEngineServerCodeToEndPoint;
    }


    public @Nonnull Map<String, NVSMXEndPoint> getIdToPDEndPoint() {
        return idToPDEndPoint;
    }
}
package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy.PackageSelectionState;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy.ServiceSelectionState;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.collections.CollectionUtils;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class RatingGroupSelectionState {

    private static final String MODULE = "QOS-SELECTION-STATE";
    public static final String SUBSCRIPTION_ID = "sid";
    public static final String PACKAGE_ID = "pid";
    public static final String QOS_ID = "qid";
    public static final String LEVEL = "level";
    public static final String STATES = "states";
    public static final String SERVICE = "service";
    public static final String RATING_GROUP = "rg";

    private Map<Long, ServiceSelectionState> packageSelectionStates;


    public RatingGroupSelectionState() {
        this.packageSelectionStates = new HashMap<>();
    }

    @VisibleForTesting
    RatingGroupSelectionState(Map<Long, ServiceSelectionState> packageSelectionStates) {
        this.packageSelectionStates = packageSelectionStates;
    }

    public void add(String subscriptionId, UserPackage aPackage, QoSProfile qoSProfile, int fupLevel, long ratingGroup, long serviceIdentifier) {

        Objects.requireNonNull(aPackage, "Package is null");
        Objects.requireNonNull(qoSProfile, "qos profile is null");

        if(aPackage.getQuotaProfileType() != QuotaProfileType.RnC_BASED && CollectionUtils.isEmpty(aPackage.getQuotaProfiles())) {
            return;
        }


        ServiceSelectionState serviceSelectionState = packageSelectionStates.get(ratingGroup);
        if(Objects.isNull(serviceSelectionState)) {
            serviceSelectionState = new ServiceSelectionState();
            packageSelectionStates.put(ratingGroup, serviceSelectionState);
        }

        serviceSelectionState.add(serviceIdentifier, new PackageSelectionState(subscriptionId, aPackage, qoSProfile, fupLevel, ratingGroup, serviceIdentifier));

    }


    public ServiceSelectionState getServiceSelectionState(long ratingGroupId){
        return packageSelectionStates.get(ratingGroupId);
    }

    public Map<Long, ServiceSelectionState> getPackageSelectionStates() {
        return packageSelectionStates;
    }

    public String  toJson() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{'" + STATES + "':[");


        packageSelectionStates.forEach((aLong, serviceSelectionState) -> serviceSelectionState.getAll().forEach((aLong1, packageSelectionState) -> {
                toJson(packageSelectionState, stringBuilder);
                stringBuilder.append(',');
        }));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("]}");

        return stringBuilder.toString();
    }

    private void toJson(PackageSelectionState packageSelectionState, StringBuilder stringBuilder) {
        String packageId = packageSelectionState.getPackage().getId();
        String qosProfileId = packageSelectionState.getQoSProfile().getId();

        stringBuilder.append(CommonConstants.OPENING_BRACES);
        if(packageSelectionState.getSubscriptionId() != null) {
            addKey(SUBSCRIPTION_ID, packageSelectionState.getSubscriptionId(), stringBuilder);
            stringBuilder.append(CommonConstants.COMMA);
        }
        addKey(PACKAGE_ID, packageId, stringBuilder);
        stringBuilder.append(CommonConstants.COMMA);
        addKey(QOS_ID, qosProfileId, stringBuilder);
        stringBuilder.append(CommonConstants.COMMA);
        addKey(LEVEL, packageSelectionState.getLevel(), stringBuilder);
        stringBuilder.append(CommonConstants.COMMA);
        addKey(RATING_GROUP, packageSelectionState.getRatingGroup(), stringBuilder);
        stringBuilder.append(CommonConstants.COMMA);
        addKey(SERVICE, packageSelectionState.getServiceIdentifier(), stringBuilder);
        stringBuilder.append(CommonConstants.CLOSING_BRACES);

    }

    private void addKey(String key, String value, StringBuilder builder) {
        builder.append(CommonConstants.SINGLE_QUOTE).append(key).append(CommonConstants.SINGLE_QUOTE).append(CommonConstants.COLON);
        builder.append(CommonConstants.SINGLE_QUOTE).append(Objects.toString(value)).append(CommonConstants.SINGLE_QUOTE);
    }

    private void addKey(String key, int value, StringBuilder builder) {
        builder.append(CommonConstants.SINGLE_QUOTE).append(key).append(CommonConstants.SINGLE_QUOTE).append(CommonConstants.COLON);
        builder.append(CommonConstants.SINGLE_QUOTE).append(Objects.toString(value)).append(CommonConstants.SINGLE_QUOTE);
    }

    private void addKey(String key, long value, StringBuilder builder) {
        builder.append(CommonConstants.SINGLE_QUOTE).append(key).append(CommonConstants.SINGLE_QUOTE).append(CommonConstants.COLON);
        builder.append(CommonConstants.SINGLE_QUOTE).append(Objects.toString(value)).append(CommonConstants.SINGLE_QUOTE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RatingGroupSelectionState that = (RatingGroupSelectionState) o;

        return Objects.equals(packageSelectionStates, that.packageSelectionStates);
    }

    @Override
    public int hashCode() {

        return Objects.hash(packageSelectionStates);
    }

    public static RatingGroupSelectionState fromJson(PolicyRepository policyRepository, String json) {

        JsonObject asJsonArray =  GsonFactory.defaultInstance().fromJson(json, JsonElement.class).getAsJsonObject();

        JsonElement rootElement = asJsonArray.get(STATES);

        if(rootElement == null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Unable to parse PCC profile selection state json. Reason: Invalid json:" + json);
            }

            return null;
        }


        JsonArray jsonArray = rootElement.getAsJsonArray();

        if(jsonArray.size() == 0) {
            return null;
        }

        Iterator<JsonElement> statesIterator = jsonArray.iterator();

        RatingGroupSelectionState pccProfileSelectionState = new RatingGroupSelectionState();

        while (statesIterator.hasNext()) {
            JsonElement next = statesIterator.next();

            JsonObject packageStateAsJson = next.getAsJsonObject();

            String subscriptionId = null;
            JsonElement subscriptionIdJsonElement = packageStateAsJson.get(SUBSCRIPTION_ID);

            if (subscriptionIdJsonElement != null) {
                subscriptionId = subscriptionIdJsonElement.getAsString();
            }

            String packageId = packageStateAsJson.get(PACKAGE_ID).getAsString();
            String qosId = packageStateAsJson.get(QOS_ID).getAsString();
            int level = packageStateAsJson.get(LEVEL).getAsInt();
            long serviceIdentifier = packageStateAsJson.get(SERVICE).getAsLong();
            long ratingGroup = packageStateAsJson.get(RATING_GROUP).getAsLong();


            UserPackage aPackage = policyRepository.getPkgDataById(packageId);

            if (aPackage == null) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to add package status for json:" + next.toString() + ". Reason: package:" + packageId + " not found");
                }
                continue;
            }

            if (aPackage.getStatus() == PolicyStatus.FAILURE) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to add package status for json:"
                            + next.toString() + ". Reason: package:" + packageId
                            + "(" + aPackage.getName() + ") is in failure state. Reason: " + aPackage.getFailReason());
                }
                continue;
            }

            QoSProfile qoSProfile = (QoSProfile) aPackage.getQoSProfile(qosId);


            if (qoSProfile == null) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Unable to add package status for json:"
                            + next.toString() + ". Reason: QoS Profile:" + qosId + " not found from package:"
                            + packageId + "(" + aPackage.getName() + ")");
                }
                continue;
            }


            pccProfileSelectionState.add(subscriptionId, aPackage, qoSProfile, level, ratingGroup, serviceIdentifier);
        }


        return pccProfileSelectionState;
    }

    public boolean hasRnCQuotaProfilePackage() {

        if(Maps.isNullOrEmpty(this.packageSelectionStates)) {
            return false;
        }

        for (ServiceSelectionState serviceSelectionState: packageSelectionStates.values()) {
            for(PackageSelectionState packageSelectionState : serviceSelectionState.getAll().values()) {
                if(QuotaProfileType.RnC_BASED == packageSelectionState.getPackage().getQuotaProfileType()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void toString(IndentingWriter indentingWriter){
        if(packageSelectionStates.values().isEmpty()){
            indentingWriter.println("No package selection state");
        } else {
            for(Map.Entry<Long, ServiceSelectionState> packageSelectionEntry: packageSelectionStates.entrySet()){
                ServiceSelectionState serviceSelectionState = packageSelectionEntry.getValue();
                indentingWriter.println("Rating Group = "+packageSelectionEntry.getKey());
                indentingWriter.incrementIndentation();
                serviceSelectionState.toString(indentingWriter);
                indentingWriter.decrementIndentation();
                indentingWriter.println();
            }
        }
    }

    public String toString(){
        StringWriter stringBuffer=new StringWriter();
        PrintWriter out=new PrintWriter(stringBuffer);
        toString(new IndentingPrintWriter(out));
        return stringBuffer.toString();
    }
}



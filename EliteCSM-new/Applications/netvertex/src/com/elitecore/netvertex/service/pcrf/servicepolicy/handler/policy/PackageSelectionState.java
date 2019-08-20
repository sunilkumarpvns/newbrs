package com.elitecore.netvertex.service.pcrf.servicepolicy.handler.policy;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.netvertex.pm.QoSProfile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

public class  PackageSelectionState {

        private final String subscriptionId;
        private final UserPackage aPackage;
        private final QoSProfile qoSProfile;
        private int level;
        private long ratingGroup;
        private long serviceIdentifier;


    public PackageSelectionState(String subscriptionId, UserPackage aPackage, QoSProfile qoSProfile, int level, long ratingGroup, long serviceIdentifier) {
        this.subscriptionId = subscriptionId;
        this.aPackage = aPackage;
        this.qoSProfile = qoSProfile;
        this.level = level;
        this.ratingGroup = ratingGroup;
        this.serviceIdentifier = serviceIdentifier;
    }

    public String getSubscriptionId() {
            return subscriptionId;
        }

        public UserPackage getPackage() {
            return aPackage;
        }

        public QoSProfile getQoSProfile() {
            return qoSProfile;
        }

        public int getLevel() {
            return level;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PackageSelectionState that = (PackageSelectionState) o;
            return level == that.level &&
                    serviceIdentifier == that.serviceIdentifier &&
                    ratingGroup == that.ratingGroup &&
                    Objects.equals(subscriptionId, that.subscriptionId) &&
                    Objects.equals(aPackage, that.aPackage) &&
                    Objects.equals(qoSProfile, that.qoSProfile);

        }

        @Override
        public int hashCode() {

            return Objects.hash(subscriptionId, aPackage, qoSProfile, level, ratingGroup, serviceIdentifier);
        }

    public long getServiceIdentifier() {
        return serviceIdentifier;
    }

    public long getRatingGroup() {
        return ratingGroup;
    }

    public void toString(IndentingWriter indentingWriter){
        indentingWriter.println("Subscription Id = "+ subscriptionId);
        indentingWriter.println("Package = "+ aPackage.getName()+"("+aPackage.getId()+")");
        indentingWriter.println("QOS Profile = "+ qoSProfile.getName()+"("+qoSProfile.getId()+")");
        indentingWriter.println("Level = "+ level);
    }

    public String toString(){
        StringWriter stringBuffer=new StringWriter();
        PrintWriter out=new PrintWriter(stringBuffer);
        toString(new IndentingPrintWriter(out));
        return stringBuffer.toString();
    }
}
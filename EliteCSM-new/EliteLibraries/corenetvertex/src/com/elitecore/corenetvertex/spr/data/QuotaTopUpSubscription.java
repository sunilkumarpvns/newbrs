package com.elitecore.corenetvertex.spr.data;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.SubscriptionState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Calendar;

public class QuotaTopUpSubscription {

    private String id;
    private String subscriberIdentity;
    private String packageId;
    private Timestamp startTime;
    private Timestamp endTime;
    private SubscriptionState status;
    private String parameter1;
    private String parameter2;

    private QuotaTopUpSubscription(){

    }

    public QuotaTopUpSubscription(String id,
                        String subscriberIdentity, String packageId, Timestamp startTime, Timestamp endTime, SubscriptionState status,
                        String parameter1, String parameter2) {
        this.id = id;
        this.subscriberIdentity = subscriberIdentity;
        this.packageId = packageId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
    }

    public QuotaTopUpSubscription(Subscription addOnSubscription) {
        this(null, addOnSubscription.getSubscriberIdentity(),
                addOnSubscription.getPackageId(),
                addOnSubscription.getStartTime(),
                addOnSubscription.getEndTime(),
                addOnSubscription.getStatus(),
                addOnSubscription.getParameter1(),
                addOnSubscription.getParameter2());
    }

    public void setSubscriberIdentity(String subscriberIdentity) {
        this.subscriberIdentity = subscriberIdentity;
    }

    public String getSubscriberIdentity() {
        return subscriberIdentity;
    }

    public String getPackageId() {
        return packageId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public SubscriptionState getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getParameter1() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public boolean isExpired(long currentTimeInMillis) {
        return endTime.getTime() < currentTimeInMillis;
    }

    public boolean isExpired(Calendar currentTimeCalendar) {
        return endTime.getTime() < currentTimeCalendar.getTimeInMillis();
    }

    public static class QuotaTopUpSubscriptionBuilder{

        private QuotaTopUpSubscription quotaTopUpSubscription;

        public QuotaTopUpSubscriptionBuilder(){
            this.quotaTopUpSubscription = new QuotaTopUpSubscription();
        }


        public QuotaTopUpSubscriptionBuilder withSubscriberIdentity(String subscriberIdentity) {
            quotaTopUpSubscription.subscriberIdentity = subscriberIdentity;
            return this;
        }

        public QuotaTopUpSubscriptionBuilder withPackageId(String packageId) {
            quotaTopUpSubscription.packageId = packageId;
            return this;
        }

        public QuotaTopUpSubscriptionBuilder withStartTime(Timestamp startTime) {
            quotaTopUpSubscription.startTime = startTime;
            return this;
        }

        public QuotaTopUpSubscriptionBuilder withEndTime(Timestamp endTime) {
            quotaTopUpSubscription.endTime = endTime;
            return this;
        }

        public QuotaTopUpSubscriptionBuilder withStatus(SubscriptionState status) {
            quotaTopUpSubscription.status = status;
            return this;
        }

        public QuotaTopUpSubscriptionBuilder withId(String id) {
            quotaTopUpSubscription.id = id;
            return this;
        }

        public QuotaTopUpSubscription buildQuotaTopUp() {
            return quotaTopUpSubscription;
        }

    }


    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out = new IndentingPrintWriter(new PrintWriter(stringWriter));
        toString(out);
        out.close();
        return stringWriter.toString();
    }

    public void toString(IndentingWriter indentingWriter) {
        indentingWriter.println("Subscription [id=" + id + ", subscriberIdentity=" + subscriberIdentity + ", packageId=" + packageId + ", startTime=" + startTime
                + ", endTime=" + endTime + ", status=" + status + ", parameter1="
                + parameter1 + ", parameter2=" + parameter2 + "]");
    }

}

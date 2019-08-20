package com.elitecore.netvertex.core.conf;

import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by harsh on 6/20/16.
 */
public class NetvertexServerGroupConfiguration implements ToStringable{
    @Nonnull private final String groupName;
    @Nonnull private final String groupId;
    @Nonnull private final NetvertexServerGroupInstanceConfiguration primaryInstance;
    @Nullable private final NetvertexServerGroupInstanceConfiguration secondaryInstance;
    @Nonnull private final NetvertexServerGroupInstanceConfiguration runningServerInstance;
    @Nonnull  private DBDataSource sessionDS;
    @Nonnull private DBDataSource notificationDS;
    private boolean syncSessionEnabled;


    public NetvertexServerGroupConfiguration(@Nonnull String groupId,
                                             @Nonnull String groupName,
                                             boolean syncSessionEnabled,
                                             @Nonnull NetvertexServerGroupInstanceConfiguration primaryInstance,
                                             @Nullable NetvertexServerGroupInstanceConfiguration secondaryInstance,
                                             @Nonnull NetvertexServerGroupInstanceConfiguration runningServerInstance,
                                             @Nonnull DBDataSource sessionDS,
                                             DBDataSource notificationDS) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.syncSessionEnabled = syncSessionEnabled;
        this.primaryInstance = primaryInstance;
        this.secondaryInstance = secondaryInstance;
        this.runningServerInstance = runningServerInstance;
        this.sessionDS = sessionDS;
        this.notificationDS = notificationDS;
    }

    @Nonnull
    public String getGroupName() {
        return groupName;
    }

    @Nonnull
    public String getGroupId() {
        return groupId;
    }

    @Nonnull
    public NetvertexServerGroupInstanceConfiguration getPrimaryInstanceConfiguration() {
        return primaryInstance;
    }

    @Nullable
    public NetvertexServerGroupInstanceConfiguration getSecondaryInstanceConfiguration() {
        return secondaryInstance;
    }

    @Nonnull
    public NetvertexServerGroupInstanceConfiguration getRunningServerInstance() {
        return runningServerInstance;
    }

    public NetvertexServerGroupInstanceConfiguration getFailOverInstance() {
        if(primaryInstance != runningServerInstance) {
            return primaryInstance;
        } else {
            return secondaryInstance;
        }
    }

    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- NetVertex server group -- ");
        builder.incrementIndentation();
        toString(builder);
        builder.decrementIndentation();
        return builder.toString();
    }


    public boolean isSyncSessionEnabled() {
        return syncSessionEnabled;
    }

    public DBDataSource getSessionDS() {
        return sessionDS;
    }

    @Nullable
    public DBDataSource getNotificationDS() {
        return notificationDS;
    }

    @Override
    public void toString(IndentingToStringBuilder builder) {
        builder.append("Id", groupId);
        builder.append("Name", groupName);
        builder.append("Session Data Source", sessionDS.getDataSourceName());
        builder.appendChildObject("-- Primary Instance --" + (primaryInstance == runningServerInstance ? "(Currently Running Instance)" : "") , primaryInstance);
        builder.appendChildObject("-- Secondary Instance --" + (secondaryInstance == runningServerInstance ? "(Currently Running Instance)" : "") , secondaryInstance);
    }
}

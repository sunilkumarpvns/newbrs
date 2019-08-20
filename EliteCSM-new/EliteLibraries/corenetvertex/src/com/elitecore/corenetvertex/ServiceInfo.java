package com.elitecore.corenetvertex;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

public class ServiceInfo implements ToStringable {

    @Nonnull private final String name;
    @Nonnull private final String status;
    @Nullable private final Date startDate;
    @Nullable private final String remarks;

    public ServiceInfo(@Nonnull String name, @Nonnull String status,
                       @Nullable Date startDate,
                       @Nullable String remarks) {
        this.name = checkNotNull(name, "name is null");
        this.status = checkNotNull(status, "status is null");
        this.startDate = startDate;
        this.remarks= remarks;
    }

    public @Nonnull String getName() {
        return name;
    }

    public @Nonnull String getStatus() {
        return status;
    }

    public @Nullable Date getStartDate(){
        return startDate;
    }

    public @Nullable String getRemarks(){
        return remarks;
    }

    @Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Service Information -- ");
        toString(builder);
        return builder.toString();
    }


    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.append("Name", name);
        out.append("Status", status);
        out.append("Start Date", startDate);
        out.append("Remarks", remarks);
        out.decrementIndentation();
    }
}

package com.elitecore.corenetvertex;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class DataSourceInfo implements ToStringable {
    private String dataSourceType;
    private String dataSourceName;
    private int noOfActiveconnection;
    private int minimumPoolSize;
    private int maximumPoolSize;
    private String status;

    public DataSourceInfo(String dataSourceType, String dataSourceName, int noOfActiveconnection, int minimumPoolSize, int maximumPoolSize, String status) {
        this.dataSourceType = dataSourceType;
        this.dataSourceName = dataSourceName;
        this.noOfActiveconnection = noOfActiveconnection;
        this.minimumPoolSize = minimumPoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.status = status;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public int getNoOfActiveconnection() {
        return noOfActiveconnection;
    }

    public void setNoOfActiveconnection(int noOfActiveconnection) {
        this.noOfActiveconnection = noOfActiveconnection;
    }

    public int getMinimumPoolSize() {
        return minimumPoolSize;
    }

    public void setMinimumPoolSize(int minimumPoolSize) {
        this.minimumPoolSize = minimumPoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getDataSourceType() {
		return dataSourceType;
	}

	@Override
    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.appendHeading(" -- Data Source Information -- ");
        toString(builder);
        return builder.toString();
    }


    @Override
    public void toString(IndentingToStringBuilder out) {
        out.incrementIndentation();
        out.append("Data Source Type", dataSourceType);
        out.append("Data Source Name", dataSourceName);
        out.append("Status Check Duration", noOfActiveconnection);
        out.append("Minimum Pool Size", minimumPoolSize);
        out.append("Maximum Pool Size", maximumPoolSize);
        out.append("Status ", status);
        out.decrementIndentation();
    }
}

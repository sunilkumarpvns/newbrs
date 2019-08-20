package com.elitecore.netvertex.restapi;

import com.elitecore.corenetvertex.DataSourceInfo;

import java.util.List;
import java.util.Map;

public interface DataSourceInfoProvider {

    Map<String, DataSourceInfo> getDataSourceInfo();
    Map<String, List<DataSourceInfo>> getDataSourceInfoByType();
}

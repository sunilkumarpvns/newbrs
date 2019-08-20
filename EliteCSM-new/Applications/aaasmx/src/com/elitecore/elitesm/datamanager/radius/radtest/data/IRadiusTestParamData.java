package com.elitecore.elitesm.datamanager.radius.radtest.data;


public interface IRadiusTestParamData {

	public String getName( );
	public void setName( String name );

	public String getNtRadParamId();

	public void setNtRadParamId(String ntRadParamId);

	public String getNtradId();

	public void setNtradId(String ntradId);

	public String getValue( );
	public void setValue( String value );
	
	public Integer getOrderNumber();
	public void setOrderNumber(Integer orderNumber);
}

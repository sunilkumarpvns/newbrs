package com.elitecore.elitesm.web.livemonitoring.client.auth;

import java.util.Arrays;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.elitecore.elitesm.web.livemonitoring.client.AbstractChartPanel;
import com.elitecore.elitesm.web.livemonitoring.client.GraphConstants;

public class AuthRejectReasonsChartPanel extends AbstractChartPanel{
	


	private static final String  title = "<b> Authentication Errors</b>";
	private static final String MODULE = "AuthRejectReasonsChartPanel";
	private Long startTimestamp = null;
	private Long lastTimestamp = null;
	
	private Curve userNotFoundCurve;
	private Curve invalidPasswordCurve;
	private Curve eapFailureCurve;
	private Curve inactiveAccountCurve;
	private Curve accountExpiredCurve;
	private Curve rmCommTimeOutCurve;
	
	public AuthRejectReasonsChartPanel(){
		super();
		try{
			this.setBorderColor("#000000");
			setChartTitle(title);
			
			userNotFoundCurve = addCurve("User Not Found");
			invalidPasswordCurve   = addCurve("Invalid Password");
			eapFailureCurve = addCurve("EAP Failure");
			inactiveAccountCurve = addCurve("Inactive Account");
			accountExpiredCurve = addCurve("Account Expired");
			rmCommTimeOutCurve = addCurve("RM Comm Time Out");
			
			getXAxis().setHasGridlines(true);
			getYAxis().setAxisLabel("N<br>u<br>m<br>b<br>e<br>r<br>s");
			getYAxis().setTickLabelFormat("# ");
			setChartTitle(title);

			this.update();
			this.setVisible(true);
			Log.info("AuthRejectReasonsChartPanel created.");
		}catch(Exception e){
			Log.error("Error in creating AuthRejectReasonsChartPanel", e);
		}
	}
	
	
	public void setData(List<Long[]>result){
		try{
			if(result!=null){
				int size = result.size();
				for(int i=0;i<size;i++){
					Long[] data= result.get(i);
					
					Log.debug(MODULE+" :"+Arrays.toString(data));
					if(data!=null){
						Long timestamp = data[GraphConstants.TIMESTAMP_DATA_INDEX];
						Long userNotFound = data[GraphConstants.IDX_AUTH_REJECT_USER_NOT_FOUND];
						Long invalidPassword = data[GraphConstants.IDX_AUTH_REJECT_INVALID_PASSWORD];
						Long eapFailure = data[GraphConstants.IDX_AUTH_REJECT_EAP_FAILURE];
						Long inactiveAccount = data[GraphConstants.IDX_AUTH_REJECT_ACCOUNT_NOT_ACTIVE];
						Long accountExpired = data[GraphConstants.IDX_AUTH_REJECT_ACCOUNT_EXPIRED];
						Long rmCommTimeOut = data[GraphConstants.IDX_AUTH_REJECT_RMCOMM_TIMEOUT];
						
						userNotFoundCurve.addPoint(timestamp,userNotFound);
						invalidPasswordCurve.addPoint(timestamp,invalidPassword);
						eapFailureCurve.addPoint(timestamp,eapFailure);
						inactiveAccountCurve.addPoint(timestamp,inactiveAccount);
						accountExpiredCurve.addPoint(timestamp,accountExpired);
						rmCommTimeOutCurve.addPoint(timestamp,rmCommTimeOut);
						
						lastTimestamp=timestamp;
					}
				}
			}
			if(startTimestamp==null && lastTimestamp!=null){
				startTimestamp = lastTimestamp-GRAPH_TIMESPAN;
			}else if(startTimestamp!=null && (lastTimestamp-startTimestamp)>=GRAPH_TIMESPAN){
				startTimestamp = startTimestamp + ((lastTimestamp - startTimestamp)-GRAPH_TIMESPAN);
			}
			if(startTimestamp!=null && lastTimestamp!=null){
				getXAxis().setAxisMin(startTimestamp);
				clearPointsForAllCurve(startTimestamp);
				getXAxis().setAxisMax(lastTimestamp);
			}
			this.update();
		}catch(Exception e){
			Log.error("Error in setdata for AuthRejectReasonsChartPanel", e);
		}

	}	
	
	public void updateData(List<Long[]> result) throws Exception{
		setData(result);
	}
	
	protected void finalize() throws Throwable {
		super.finalize();
		
	}
	public String getGroupKey(){
		return this.AUTH_STATISTICS_GROUP;
	}
}

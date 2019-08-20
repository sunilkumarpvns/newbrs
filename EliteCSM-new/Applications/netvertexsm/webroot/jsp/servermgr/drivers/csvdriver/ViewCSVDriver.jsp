<%@page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVDriverData"%>
<%@ page import="java.util.*" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData" %>
<%@page import="com.elitecore.netvertexsm.util.constants.TimeUnits"%>
<%

	CSVDriverData csvDriverData = null;
	Set<CSVDriverData> csvDriverDataSet = driverInstanceData.getCsvDriverDataSet();
	if(csvDriverDataSet!=null && !csvDriverDataSet.isEmpty()){
		for(Iterator<CSVDriverData> iterator= csvDriverDataSet.iterator();iterator.hasNext();){
			csvDriverData = iterator.next();
		}
	}
	if(csvDriverData!=null){
		pageContext.setAttribute("csvDriverData",csvDriverData);
	}
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >

	<%if(csvDriverData!=null){%>
    
    <tr> 
      <td valign="top" align="right"> 
      
        <table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" align="right" >
        <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2">
			<bean:message bundle="driverResources" key="driver.csvdriver.cdrdetails"/></td>
		</tr>
         <tr> 
            <td class="tbllabelcol" width="30%" height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.header" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="csvDriverData" property="header" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.delimeter" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="delimiter" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.cdrtimestampformat" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="cdrTimestampFormat" />&nbsp;</td>
          </tr>
           <tr>
			<td align="left" class="tblheader-bold" valign="top"  colspan="2" style=" ">
			<bean:message bundle="driverResources" key="driver.filedetail"/></td>
		</tr>
		    <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.filename" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="fileName" />&nbsp;</td>
          </tr>
         <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.location" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="fileLocation" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.prefixFileName" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="prefixFileName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.defaultdirname" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="defaultDirName" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.foldername" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="folderName" />&nbsp;</td>
          </tr>
			<tr>
				<td align="left" class="tblheader-bold" valign="top" colspan="2" style=" ">
				<bean:message bundle="driverResources" key="driver.filerollingdetail"/></td>
			</tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.filerollingtype" /></td>
            <td class="tblcol"  height="20%">
            	<%if(csvDriverData.getFileRollingType()==1){%>
            	Time-Based(min)&nbsp;
            	<%}else if(csvDriverData.getFileRollingType()==2){%>
            		Size-Based(kb)&nbsp;
            	<%}else{%>
            		Record-Based(No. of records)&nbsp;
            	<%}%>
             </td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.rollingUnit" /></td>
            <td class="tblcol"  height="20%">
            	<%if(csvDriverData.getFileRollingType()==1){
            		for(TimeUnits unit : TimeUnits.values()){
            			if(csvDriverData.getRollingUnit()== Long.parseLong(unit.key)){
            			%> <%= unit.val%> &nbsp; 
            			<%}
            		}
            			            	  		
            	  }else{%>
            		<bean:write name="csvDriverData" property="rollingUnit" />&nbsp;
            		<%}%>
            </td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.range" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="range" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.pos" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="sequencePosition" />&nbsp;</td>
          </tr>
          <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.global" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="globalization" />&nbsp;</td>
          </tr>
          <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2" style=" ">
			<bean:message bundle="driverResources" key="driver.filetransferdetails"/></td>
		  </tr>
      	  <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.allocatingprotocol" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="allocatingProtocol" />&nbsp;</td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.address" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="address" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.remoteLocation" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="remoteLocation" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.username" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="userName" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.password" /></td>
            <td class="tblcol"  height="20%">*****&nbsp;</td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.postoperation" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="postOperation" />&nbsp;</td>
          </tr>
             <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.archiveloc" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="archiveLocation" />&nbsp;</td>
          </tr>
             <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.failovertime" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="failOvertime" />&nbsp;</td>
          </tr>
          
          <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2" style=" ">
			<bean:message bundle="driverResources" key="driver.csvdriver.usagefields"/></td>
		  </tr>
      	  <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.reportingtype" /></td>
            <td class="tblcol"  height="20%">
            	<%if(csvDriverData.getReportingType().equalsIgnoreCase("QR")){%>
            		Quota Reporting&nbsp;
            	<%}else if(csvDriverData.getReportingType().equalsIgnoreCase("UM")){%>
            		Usage Monitoring&nbsp;
            	<%}else{%>
            		&nbsp;
            	<%}%>
            </td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.inputoctetsheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="inputOctetsHeader" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.outputoctetsheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="outputOctetsHeader" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.totalOctetsheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="totalOctetsHeader" />&nbsp;</td>
          </tr>
           <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.usageTimeheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="usageTimeHeader" />&nbsp;</td>
          </tr>
            <tr> 
            <td class="tbllabelcol"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.usagekeyheader" /></td>
            <td class="tblcol"  height="20%"><bean:write name="csvDriverData" property="usageKeyHeader" />&nbsp;</td>
          </tr>         
		  <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2" style=" ">
			 <bean:message bundle="driverResources" key="driver.csvdriver.csvfieldmap"/></td>
		  </tr>
		  <tr> 
			<td colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<tr>
						<td class="tblheader-bold"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.ordernumber" /> </td>
					    <td class="tblheader-bold"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.header" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" /></td>
            		</tr>
					  <%if(csvDriverData.getCsvFieldMapSet()!=null && !csvDriverData.getCsvFieldMapSet().isEmpty()){ %>
						<logic:iterate id="csvFieldBean" name="csvDriverData" property="csvFieldMapSet" scope="page" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVFieldMapData">					  
				 		  <tr> 
				 		  	<td class="tblrows" width="10%" height="20%"><bean:write name="csvFieldBean" property="orderNumber" />&nbsp;</td>
				            <td class="tblrows" width="30%" height="20%"><bean:write name="csvFieldBean" property="header" />&nbsp;</td>
				            <td class="tblrows" width="60%" height="20%"><bean:write name="csvFieldBean" property="pcrfKey" />&nbsp;</td>
				          </tr>
						</logic:iterate>
					  <%}else{%>		
						<tr> 
			            <td class="tblfirstcol" align="center" height="20%" colspan="2">No Record Found</td>
			           </tr>
			          <%}%>
				</table>
			</td>	  
          </tr>          
          
          <tr>
			<td align="left" class="tblheader-bold" valign="top" colspan="2" style=" ">
			 <bean:message bundle="driverResources" key="driver.csvstripmapping" /></td>
		  </tr>
          <tr> 
			<td colspan="2">
				<table cellpadding="0" cellspacing="0" border="0" width="100%" class="box">
					<tr>
					    <td class="tblheader-bold"  height="20%"><bean:message bundle="driverResources" key="driver.csvdriver.pcrfkey" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="driver.pattern" /></td>
            			<td class="tblheader-bold" height="20%"><bean:message bundle="driverResources" key="driver.seperator" /></td>
            		</tr>
					  <%if(csvDriverData.getCsvStripFieldMapDataSet()!=null && !csvDriverData.getCsvStripFieldMapDataSet().isEmpty()){ %>
						<logic:iterate id="csvStripFieldBean" name="csvDriverData" property="csvStripFieldMapDataSet" scope="page" type="com.elitecore.netvertexsm.datamanager.servermgr.drivers.csvdriver.data.CSVStripFieldMapData">					  
				 		  <tr> 
				            <td class="tblrows" width="30%" height="20%"><bean:write name="csvStripFieldBean" property="pcrfKey" />&nbsp;</td>
				            <td class="tblrows" width="35%" height="20%"><bean:write name="csvStripFieldBean" property="pattern" />&nbsp;</td>
				            <td class="tblrows" width="35%" height="20%"><bean:write name="csvStripFieldBean" property="separator" />&nbsp;</td>
				          </tr>
						</logic:iterate>
					  <%}else{%>		
						<tr> 
			            	<td class="tbllabelcol"  align="center" height="20%" colspan="3">No Record Found</td>
			           </tr>
			          <%}%>
				</table>
			</td>	  
          </tr>
          </table>
	</td>
   </tr>
   <%}%>
</table>
	








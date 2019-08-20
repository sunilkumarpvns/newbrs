<%@page import="com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyConstant"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData"%>
<%@page import="com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData"%>
<%@page import="com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager"%>
<%@page import="com.elitecore.elitesm.util.constants.ServiceTypeConstants"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>
<%@taglib prefix="ec" uri="/elitetags" %>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=request.getContextPath()%>/css/popcalendar.css" >
<html>
<head>
<%
	String widgetId=request.getParameter("widgetId");

   String orderNumber=request.getParameter("orderNumber");
   String handlerTable = request.getParameter("handlerTable");
   
   DriverBLManager driverManager = new DriverBLManager();
   String[] cacheableDriverInstIds = (String[])request.getAttribute("cacheableDriverInstIds");
   String[][] cacheableDriverInstanceNames = (String[][])request.getAttribute("cacheableDriverInstanceNames");
   List<DriverInstanceData> additionalableDriverInstDataList = driverManager.getCacheableDriverData();
   List<DriverInstanceData> listOfDriver = driverManager.getDriverInstanceList(ServiceTypeConstants.NAS_AUTH_APPLICATION);
	
	String[] driverInstanceIds = new String [listOfDriver.size()];
	String[][] driverInstanceNames = new String[listOfDriver.size()][3]; 
	
	for(int i=0;i<listOfDriver.size();i++){
		DriverInstanceData data = listOfDriver.get(i);				
	driverInstanceNames[i][0] = String.valueOf(data.getName());
	driverInstanceNames[i][1] = String.valueOf(data.getDescription());
	driverInstanceNames[i][2] = String.valueOf(data.getDriverTypeData().getDisplayName());
	driverInstanceIds[i] = String.valueOf(data.getName());
	}
%>
<script type="text/javascript">
	
	var driverListArray = [];
	
	<%for(int i=0;i<listOfDriver.size();i++){
		DriverInstanceData data = listOfDriver.get(i);%>	
		driverListArray.push({'driverInstanceId':'<%=String.valueOf(data.getName())%>','driverName':'<%=String.valueOf(data.getName())%>'});
	<%}%>

	var currentList_<%=widgetId%>;
	var mainDriverList_<%=widgetId%>;
	var mypopupwindow_<%=widgetId%>;
	
	var jdriverNames_<%=widgetId%> = new Array();
	var jdriverInstanceIds_<%=widgetId%> = new Array();
	var count_<%=widgetId%>=0;
	
	var jAcctDriverName_<%=widgetId%> = new Array();
	var jAcctDriverInstacneIds_<%=widgetId%> = new Array();
	var acctCount_<%=widgetId%>=0;

	jdriverNames_<%=widgetId%>.length = <%=listOfDriver.size()%>;				
	jdriverInstanceIds_<%=widgetId%>.length= <%=listOfDriver.size()%>;

	<%int j,k=0;
	for(j =0;j<listOfDriver.size();j++){%>		
		jdriverNames_<%=widgetId%>[<%=j%>] = new Array(3);		
			<%for(k=0;k<3;k++){%>												
				jdriverNames_<%=widgetId%>[<%=j%>][<%=k%>] = '<%=driverInstanceNames[j][k]%>';				
			<%}%>
		jdriverInstanceIds_<%=widgetId%>[<%=j%>] = '<%=driverInstanceIds[j]%>';
		count_<%=widgetId%> ++;							
	<%}%>	 

	var headersArr_<%=widgetId%> = new Array(5);
	headersArr_<%=widgetId%>[0] = '';
	headersArr_<%=widgetId%>[1] = 'Driver Name';
	headersArr_<%=widgetId%>[2] = 'Driver Description';
	headersArr_<%=widgetId%>[3] = 'Driver Type';
	headersArr_<%=widgetId%>[4] = 'Weightage';
	
	initializeData(jdriverInstanceIds_<%=widgetId%>,jdriverNames_<%=widgetId%>,'addDriver<%=widgetId%>','driverDataCheckBoxId<%=widgetId%>',headersArr_<%=widgetId%>,'true',jdriverInstanceIds_<%=widgetId%>.length,0,10);

	var cacheHeadersArr_<%=widgetId%> = new Array(4);
	cacheHeadersArr_<%=widgetId%>[0] = '';
	cacheHeadersArr_<%=widgetId%>[1] = 'Secondary Driver Name';
	cacheHeadersArr_<%=widgetId%>[2] = 'Driver Description';
	cacheHeadersArr_<%=widgetId%>[3] = 'Driver Type';
	
	initializeCacheData(jdriverInstanceIds_<%=widgetId%>,jdriverNames_<%=widgetId%>,'addCacheDriver<%=widgetId%>','driverDataRadioId<%=widgetId%>',cacheHeadersArr_<%=widgetId%>,jdriverInstanceIds_<%=widgetId%>.length);
	
	var additionalHeadersArr_<%=widgetId%> = new Array(4);
	additionalHeadersArr_<%=widgetId%>[0] = '';
	additionalHeadersArr_<%=widgetId%>[1] = 'Additional Driver Name';
	additionalHeadersArr_<%=widgetId%>[2] = 'Driver Description';
	additionalHeadersArr_<%=widgetId%>[3] = 'Driver Type';
	
	initializeAdditionalData(jdriverInstanceIds_<%=widgetId%>,jdriverNames_<%=widgetId%>,'additionalDriver<%=widgetId%>','additionalDriverCheckBoxId<%=widgetId%>',additionalHeadersArr_<%=widgetId%>,jdriverInstanceIds_<%=widgetId%>.length);
	
	function initializeData(listOfId,listOfdrNm,tableId,checkbxId,headerArr,isWeightage,count,startValue,endValue){		
		
		if(startValue==null && endValue==null){
			startValue=0;
			endValue=10;
		}
		
		var table = document.getElementById(tableId);		
		var rowid = table.insertRow(0);
		var tempWeightage;
		
		if(startValue > endValue) {
			tempWeightage = startValue;
			startValue = endValue;
			endValue = tempWeightage;
		}
		
		for(var z=0;z<headerArr.length;z++){
			var cells = rowid.insertCell(z);					
			cells.innerHTML = headerArr[z];			
			cells.className="tblheader-bold";						
		}
		var temp = 1;
		
		for(var i = 0;i<count;i++){
			
			var row = table.insertRow(temp);		
			row.id = "row" + checkbxId + listOfId[i];
			row.style.visibility = 'visible';
	            
			    var cell1 = row.insertCell(0);
			    var element1 = document.createElement("input");
			    element1.type = "checkbox";
			    element1.name = checkbxId;
			    element1.id = checkbxId+listOfId[i];		    		    		    
			    element1.value = listOfdrNm[i][0];
			    
			    cell1.appendChild(element1);
			    cell1.className="tblfirstcol";
			    cell1.width="5%";
			    
				
				var cellNumber = 1;
				
					for(var m=0;m<listOfdrNm[i].length;m++){
						var cell = row.insertCell(cellNumber);					
						
						if( cellNumber == 1 ){
							cell.innerHTML = '<span class="view-details-css" onclick="openViewDetails(this,'+listOfId[i]+',\''+ listOfdrNm[i][m] +'\',\''+'DRIVERS\');">' + listOfdrNm[i][m] +"</span>";
						}else{
							cell.innerHTML = listOfdrNm[i][m];
						}
						
						cell.className="tblrows";
						cellNumber++;
					}
	
				if(isWeightage == 'true'){
					
					var cell3 = row.insertCell(cellNumber);
				    var element2 = document.createElement("select");
				    for(var e=startValue;e<=endValue;e++){
				    	var innerele2 = document.createElement("option");
				    	innerele2.text = e;
				    	innerele2.value = e;
				    	if(e==1){
				    		innerele2.selected="selected";
				    	}
				    	element2.options.add(innerele2, e);
					}
					
					element2.id = checkbxId+listOfId[i]+"w";		    	
					cell3.appendChild(element2);
					cell3.className="tblrows";
					cell3.width="15%";	
				}
			temp++;
		}
	  }
	
	function initializeCacheData(listOfCacheId,listOfCachedrNm,cachetableId,cachecheckbxId,cacheheaderArr,cachecount){		
		var cachetable = document.getElementById(cachetableId);		
		var cacherowid = cachetable.insertRow(0);
		
		for(var d=0;d<cacheheaderArr.length;d++){
			var cells = cacherowid.insertCell(d);					
			cells.innerHTML = cacheheaderArr[d];			
			cells.className="tblheader-bold";						
		}
		
		var cachetemp = 1;
		for(var a = 0;a<cachecount;a++){
			var cacherow = cachetable.insertRow(cachetemp);		
			cacherow.id = "cacherow" + cachecheckbxId + listOfCacheId[a];
			cacherow.style.visibility = 'visible';
            
		    var cachecell1 = cacherow.insertCell(0);
		    var cacheelement1 = document.createElement("input");
		    cacheelement1.type = "checkbox";
		    cacheelement1.name = cachecheckbxId;
		    cacheelement1.id = cachecheckbxId+listOfCacheId[a];		    		    		    
		    cacheelement1.value = listOfCachedrNm[a][0];
		    
		    cachecell1.appendChild(cacheelement1);
		    cachecell1.className="tblfirstcol";
		    cachecell1.width="5%";
			
			var cachecellNumber = 1;
			for(var b=0;b<listOfCachedrNm[a].length;b++){
				var cell = cacherow.insertCell(cachecellNumber);	
				
				if( cachecellNumber == 1 ){
					cell.innerHTML = '<span class="view-details-css" onclick="openViewDetails(this,'+listOfCacheId[a]+',\''+ listOfCachedrNm[a][b] +'\',\''+'DRIVERS\');">' + listOfCachedrNm[a][b] +"</span>";
				}else{
					cell.innerHTML = listOfCachedrNm[a][b];
				}
				
				cell.className="tblrows";
				cachecellNumber++;
			}
			cachetemp++;
		}

	}
	
	function initializeAdditionalData(listOfAdditionalId,listOfAdditionaldrNm,additionaltableId,additionalcheckbxId,additionalheaderArr,additionalcount){		
		
		var additionaltable = document.getElementById(additionaltableId);		
		var additionalrowid = additionaltable.insertRow(0);
		for(var d=0;d<additionalheaderArr.length;d++){
			var cells = additionalrowid.insertCell(d);					
			cells.innerHTML = additionalheaderArr[d];			
			cells.className="tblheader-bold";						
		}
		var additionaltemp = 1;
		
		for(var a = 0;a<additionalcount;a++){
			
			var additionalrow = additionaltable.insertRow(additionaltemp);		
			additionalrow.id = "additionalrow"+ additionalcheckbxId + listOfAdditionalId[a];
			additionalrow.style.visibility = 'visible';
	            
			    var additionalcell1 = additionalrow.insertCell(0);
			    var additionalelement1 = document.createElement("input");
			    additionalelement1.type = "checkbox";
			    additionalelement1.name = additionalcheckbxId;
			    additionalelement1.id =additionalcheckbxId+listOfAdditionalId[a];		    		    		    
			    additionalelement1.value = listOfAdditionaldrNm[a][0];
			    additionalcell1.appendChild(additionalelement1);
			    additionalcell1.className="tblfirstcol";
			    additionalcell1.width="5%";
			    
			    var additionalcellNumber = 1;
				
				for(var b=0;b<listOfAdditionaldrNm[a].length;b++){
					var cell = additionalrow.insertCell(additionalcellNumber);					
					
					if( additionalcellNumber == 1 ){
						cell.innerHTML = '<span class="view-details-css" onclick="openViewDetails(this,'+listOfAdditionalId[a]+',\''+ listOfAdditionaldrNm[a][b] +'\',\''+'DRIVERS\');">' + listOfAdditionaldrNm[a][b] +"</span>";
					}else{
						cell.innerHTML = listOfAdditionaldrNm[a][b];
					}
					
					cell.className="tblrows";
					additionalcellNumber++;
				}
				additionaltemp++;
			}
	}
	
	function driverpopup(callerObj){
		openpopup(callerObj,'driverPopup<%=widgetId%>','driverDataCheckBoxId',jdriverInstanceIds_<%=widgetId%>,jdriverNames_<%=widgetId%>,'selecteddriverIds','true');
	}
	
	function cacheDriverpopup(callerObj){
		openCachePopup(callerObj,'cacheDriverPopup<%=widgetId%>','driverDataRadioId',jdriverInstanceIds_<%=widgetId%>,jdriverNames_<%=widgetId%>,'selectedCacheDriverIds');
	}
	
	function additionalDriverpopup(callerObj){
		openAdditionalPopup(callerObj,'additionalDriverPopup<%=widgetId%>','additionalDriverCheckBoxId',jdriverInstanceIds_<%=widgetId%>,jdriverNames_<%=widgetId%>,'selectedAdditionalDriverIds');
	}

	function openpopup(callerObj,divId,checkBxId,listOfIds,listOfNms,componentId,isWeightage){	
		
		var selectObjIds=$(callerObj).attr('id');
		var widgetId="";
		
		if(selectObjIds.indexOf("_") > 0 ) {
			var start =  selectObjIds.indexOf("_") + 1;
			widgetId = selectObjIds.substring(start,selectObjIds.length);
		}
		var selectObj=$('#'+selectObjIds).closest('form').find('.selecteddriverIds');
		var selectObjAttrIds=$(selectObj).attr('id');
		
		$('#'+selectObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');

			$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		//selectedCacheDriverIds
		var selectCacheObj = $('#'+selectObjIds).closest('form').find('.selectedCacheDriverIds');
		var selectCacheObjAttrIds=$(selectCacheObj).attr('id');
		
		$('#'+selectCacheObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');

			$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		//selectedAdditionalDriverIds
		var selectAdditionalObj = $('#'+selectObjIds).closest('form').find('.selectedAdditionalDriverIds');
		var selectAdditionalObjAttrIds=$(selectAdditionalObj).attr('id');
		
		$('#'+selectAdditionalObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');

			$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		var id = "#driverPopup" + widgetId;
	
		checkBxId=checkBxId+widgetId;
		
		$(id).show();
	
		if(isWeightage==null || isWeightage=='true'){
		    var jItems=$("*[name='"+checkBxId+"']"); 
			for(var i=0;i<jItems.length;i++){
			   $("#"+checkBxId+listOfIds[i]+"w").val("1");
			}
		 }
				
		$(id).dialog({
			modal: true,
			autoOpen: false,
			minHeight: 200,
			height: 250,
			width: 650,
			position: 'top',
			buttons:{					
				        'Add': function() {
	                       	var selectedItems=$("*[name='"+checkBxId+"']");
	                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
	                                
	                        		if(isWeightage==null || isWeightage=='true'){	 
										var optionsval =$("#"+checkBxId+listOfIds[0]+"w").val();	
	                                	var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
	                                	$(selectObj).append("<option id="+ listOfIds[0] +" value="+ labelVal + "(" + optionsval +") class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
	                               		
									}else if(isWeightage=='false'){
		                                var labelVal=$("#"+checkBxId+listOfIds[0]).val();                                
		                                $(selectObj).append("<option id="+ listOfIds[0] +" value="+ labelVal  +" selected=selected class=labeltext> "+labelVal +" </option>");
									}
	                               	$("#addDriver"+widgetId+" #row"+checkBxId+listOfIds[0]).hide();
	                               	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+listOfIds[0]).hide();	
	                                $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+listOfIds[0]).hide();
	                            	selectedItems[0].checked=false;
									
									         
	                        }else if(selectedItems.length>1 ){
		                        for(var i=0;i<selectedItems.length;i++)
		                        {
									if(selectedItems[i].checked == true)
		                            {   	                         
										if(isWeightage==null || isWeightage=='true'){	 
											var optionsval =$("#"+checkBxId+listOfIds[i]+"w").val();
		                                	var labelVal=$("#"+checkBxId+listOfIds[i]).val();  
		                                	$(selectObj).append("<option id="+ listOfIds[0] +" value="+ labelVal + "(" + optionsval +") class=labeltext> "+labelVal+"-W-" + optionsval +" </option>");
										}else if(isWeightage=='false'){
			                                var labelVal=$("#"+checkBxId+listOfIds[i]).val();                                
			                                $(selectObj).append("<option id="+ listOfIds[i] +" value="+ labelVal  +" selected=selected class=labeltext> "+labelVal +" </option>");
										}
										$("#addDriver"+widgetId+" #row"+checkBxId+listOfIds[i]).hide();
		                                $("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+listOfIds[i]).hide();		
		                            	$("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+listOfIds[i]).hide();
										selectedItems[i].checked=false;         
		                            }                             
		                        }
	                        }
	                        $(this).dialog('close');
						},                
						Cancel: function() {
							$(this).dialog('close');
						}
					},
			open: function() {},
			close: function() {}				
			});
			$(id).dialog("open");
	 }
	
	function openCachePopup(callerObj,divId,checkBxId,listOfIds,listOfNms,componentId){
		var selectObjIds=$(callerObj).attr('id');
		var widgetId="";
		
		if(selectObjIds.indexOf("_") > 0 ) {
			var start =  selectObjIds.indexOf("_") + 1;
			widgetId = selectObjIds.substring(start,selectObjIds.length);
		}
		var selectObj=$('#'+selectObjIds).closest('form').find('.selectedCacheDriverIds');
		var selectObjAttrIds=$(selectObj).attr('id');
		
		$('#'+selectObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');
			
		   	$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		//selecteddriverIds
		var selectCacheObj = $('#'+selectObjIds).closest('form').find('.selecteddriverIds');
		var selectCacheObjAttrIds=$(selectCacheObj).attr('id');
		
		$('#'+selectCacheObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');

			$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		//selectedAdditionalDriverIds
		var selectAdditionalObj = $('#'+selectObjIds).closest('form').find('.selectedAdditionalDriverIds');
		var selectAdditionalObjAttrIds=$(selectAdditionalObj).attr('id');
		
		$('#'+selectAdditionalObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');

			$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		
		var id = "#cacheDriverPopup" + widgetId;
	
		checkBxId=checkBxId+widgetId;
		
		$(id).show();
		$(id).dialog({
			modal: true,
			autoOpen: false,
			minHeight: 200,
			height: 250,
			width: 700,
			position: 'top',
			buttons:{					
				        'Add': function() {
	                       	var selectedItems=$("*[name='"+checkBxId+"']");
	                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
	                        	var supportedDriverList=document.getElementById("selectedCacheDriverIds"+widgetId);	
	                        									
	                            var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
	                           	$(selectObj).append("<option id="+ listOfIds[0] +" value="+ labelVal + " selected=selected class=labeltext> "+labelVal +" </option>");
	                           		                                                        
	                           	$("#addDriver"+widgetId+" #row"+"driverDataCheckBoxId"+widgetId+listOfIds[0]).hide();
	                           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+listOfIds[0]).hide();		
	                        	$("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+listOfIds[0]).hide();
								selectedItems[0].checked=false;	
									        
	                        }else if(selectedItems.length>1 ){
	                        	for(var i=0;i<selectedItems.length;i++)
		                        {
									if(selectedItems[i].checked == true)
		                            {   	
										var supportedDriverList=document.getElementById("selectedCacheDriverIds"+widgetId);	
			    				
		                                var labelVal=$("#"+checkBxId+listOfIds[i]).val();       
		                               	$(selectObj).append("<option id="+ listOfIds[i] +" value="+ labelVal + " selected=selected class=labeltext> "+labelVal +" </option>");	

		                            	$("#addDriver"+widgetId+" #row"+"driverDataCheckBoxId"+widgetId+listOfIds[i]).hide();
		                               	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+listOfIds[i]).hide();		
		                            	$("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+listOfIds[i]).hide();
		                            	
										selectedItems[i].checked=false;         
		                            }                          
		                        }
	                        }
	                        $(this).dialog('close');
						},                
						Cancel: function() {
							$(this).dialog('close');
						}
					},
			open: function() {},
			close: function() {}				
			});
			$(id).dialog("open");	
	 }
	
	function openAdditionalPopup(callerObj,divId,checkBxId,listOfIds,listOfNms,componentId){
		var selectObjIds=$(callerObj).attr('id');
		var widgetId="";
		
		if(selectObjIds.indexOf("_") > 0 ) {
			var start =  selectObjIds.indexOf("_") + 1;
			widgetId = selectObjIds.substring(start,selectObjIds.length);
		}
		var selectObj=$('#'+selectObjIds).closest('form').find('.selectedAdditionalDriverIds');
		var selectObjAttrIds=$(selectObj).attr('id');
		
		$('#'+selectObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');
			
		   	$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		
		//selectedCacheDriverIds
		var selectCacheObj = $('#'+selectObjIds).closest('form').find('.selectedCacheDriverIds');
		var selectCacheObjAttrIds=$(selectCacheObj).attr('id');
		
		$('#'+selectCacheObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');

			$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		//selectedAdditionalDriverIds
		var selectAdditionalObj = $('#'+selectObjIds).closest('form').find('.selecteddriverIds');
		var selectAdditionalObjAttrIds=$(selectAdditionalObj).attr('id');
		
		$('#'+selectAdditionalObjAttrIds + ' option').each(function(){
			var optionIds = $(this).attr('id');

			$("#addDriver"+widgetId+" #row"+checkBxId+widgetId+optionIds).hide();
           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+optionIds).hide();	
            $("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+optionIds).hide();
		});
		
		
		var id = "#additionalDriverPopup" + widgetId;
	
		checkBxId=checkBxId+widgetId;
		
		$(id).show();
		
		$(id).dialog({
			modal: true,
			autoOpen: false,
			minHeight: 200,
			height: 250,
			width: 600,
			position: 'top',
			buttons:{					
				        'Add': function() {
	                       	var selectedItems=$("*[name='"+checkBxId+"']");
	                        if(selectedItems.length==1 && selectedItems[0].checked  == true){
	                        	var supportedDriverList=document.getElementById("selectedAdditionalDriverIds"+widgetId);
	                            var labelVal=$("#"+checkBxId+listOfIds[0]).val();       
	                           	$(selectObj).append("<option id="+ listOfIds[0] +" value="+ labelVal +" class=labeltext> "+labelVal+" </option>");
	                               		                                                        
	                           	$("#addDriver"+widgetId+" #row"+"driverDataCheckBoxId"+widgetId+listOfIds[0]).hide();
	                           	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+listOfIds[0]).hide();		
	                        	$("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+listOfIds[0]).hide();
								selectedItems[0].checked=false;	
									        
	                        }else if(selectedItems.length>1 ){
	                        	for(var i=0;i<selectedItems.length;i++)
		                        {
									if(selectedItems[i].checked == true)
		                            {   	
										var supportedDriverList=document.getElementById("selectedAdditionalDriverIds"+widgetId);	
										//var optionsval =$("#"+checkBxId+listOfIds[i]+"w").val();	
		                                var labelVal=$("#"+checkBxId+listOfIds[i]).val();       
		                               	$(selectObj).append("<option id="+ listOfIds[i] +" value="+ labelVal +" selected=selected class=labeltext> "+labelVal+" </option>");	

		                               	$("#addDriver"+widgetId+" #row"+"driverDataCheckBoxId"+widgetId+listOfIds[i]).hide();
		                               	$("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+listOfIds[i]).hide();		
		                            	$("#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+listOfIds[i]).hide();
		                                selectedItems[i].checked=false;         
		                            }                             
		                        }
	                        }
	                        $(this).dialog('close');
						},                
						Cancel: function() {
							$(this).dialog('close');
						}
					},
			open: function() {
			
			},
			close: function() {
				
			}				
			});
			$(id).dialog("open");	
	 }
	
	function removeData(componentId,checkboxid,currentObj){
		var id = "#" + componentId +" option:selected";	
		var selectObjIds=$(currentObj).attr('id');
		var widgetId="";
		if(selectObjIds.indexOf("_") > 0 ) {
			var start =  selectObjIds.indexOf("_") + 1;
			widgetId = selectObjIds.substring(start,selectObjIds.length);
		}
		
		$(id).each(function(){
			 var mainValue =$(this).attr('id');
// 			 var strippedVal = mainValue.split('-');
			 var rowid="#addDriver"+widgetId +" #row"+checkboxid+mainValue;
			 var cacheRowId="#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+mainValue;
		     var addtionalId ="#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+mainValue; 
			 $(rowid).show();
			 $(cacheRowId).show();
			 $(addtionalId).show();
			 $(this).remove();	
		});
	}
	
	function removeCacheData(componentId,checkboxid,currentObj){
		 var id = "#" + componentId + " " +"option:selected";
		 
		 var selectObjIds=$(currentObj).attr('id');
			var widgetId="";
			if(selectObjIds.indexOf("_") > 0 ) {
				var start =  selectObjIds.indexOf("_") + 1;
				widgetId = selectObjIds.substring(start,selectObjIds.length);
			}
			
		 
		 $(id).each(function(){
			 var mainValue = $(this).attr('id');
// 			 var strippedVal = mainValue.split('-');
		     var rowid="#addCacheDriver"+widgetId+" #cacherow"+checkboxid+mainValue;
		     var primaryRowId="#addDriver"+widgetId+" #row"+"driverDataCheckBoxId"+widgetId+mainValue;
		     var addtionalId = "#additionalDriver"+widgetId+" #additionalrow"+"additionalDriverCheckBoxId"+widgetId+mainValue;
		     $(rowid).show();
		     $(primaryRowId).show();
		     $(addtionalId).show();
		     $(this).remove();				      
	   });
	}
	function removeAdditionalDriverData(componentId,checkboxid,currentObj){
 		 var id = "#" + componentId + " " +"option:selected";
		 
		 var selectObjIds=$(currentObj).attr('id');
		 var widgetId="";
		 if(selectObjIds.indexOf("_") > 0 ) {
			var start =  selectObjIds.indexOf("_") + 1;
			widgetId = selectObjIds.substring(start,selectObjIds.length);
		}
		 
		 $(id).each(function(){
			 var mainValue = $(this).val();
		     $("#addDriver"+widgetId+" #row"+"driverDataCheckBoxId"+widgetId+mainValue).show();
		     $("#addCacheDriver"+widgetId+" #cacherow"+"driverDataRadioId"+widgetId+mainValue).show();
		     $("#additionalDriver"+widgetId+" #additionalrow"+checkboxid+mainValue).show();
		     $(this).remove();				      
	  });
	}

</script>
</head>
<body>
<form id="frm_<%=widgetId%>" name="frm_<%=widgetId%>"  class="form_profilelookup">
<input type="hidden" value="<%=orderNumber%>" name="orderNumber" id="orderNumber"/>
<input type="hidden" value="<%=TGPPAAAPolicyConstant.PROFILE_LOOKUP_HANDLER%>" name="handlerType" id="handlerType" />
<input type="hidden" value="<%=widgetId %>" name="widgetId" id="widgetId_<%=widgetId%>" />
<input type="hidden" value="ProfileLookupDriver" name="handlerJsName" id="handlerJsName" />

<table width="100%" border="0" style="background-color: white;" class="handler-class" cellspacing="0" cellpadding="0">
<tbody>
	<tr style="cursor: pointer;">
		<td class="sortableClass">
			<table name="c_tblCrossProductList" width="100%" border="0" style="background-color: white;" class="" cellspacing="0" cellpadding="0">
				<tr>
					<td align="left" class="tbl-header-bold" valign="top" colspan="4">
						<table border="0" cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td width="96%" align="left" class="tbl-header-bold" valign="top">
									<div class="handler-label"><bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.profilelookupdhandler.title' /></div>
									<input type="text" id="handlerName_<%=widgetId%>" name="handlerName" class="handler-name-txt" size="17" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.profilelookupdhandler.title' />" onkeyup="expand(this);" onkeypress="keyPressedForHandler(event,this);" onload="expand(this);" disabled="disabled" style="display: none;"/>
									<input type="hidden" id="profileLookuphiddenHandlerName_<%=widgetId%>" name="hiddenHandlerName" class="hidden-handler-name" value="<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.profilelookupdhandler.title' />" />
									<span class="handler-type">[<bean:message bundle='servicePolicyProperties' key='tgppaaapolicy.profilelookupdhandler.title' />]</span>
								</td>
								<td align="left" class="tbl-header-bold" valign="top">
									<span class="edit_handler_icon"  title="Edit Handler Name" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %> onclick="changeHandlerName(this);" <%}%>  ></span>
									<span class="save_handler_icon" onclick="saveHandlerName(this);"  title="Save Handler Name" style="display: none;"></span>
								</td>
								<td width="1%" align="left" valign="middle"  class="tbl-header-bold" style="padding-right: 2px;line-height: 9px;">
									<div class="switch">
									  <input id="toggle-profilelookup_<%=widgetId%>" name="isHandlerEnabled" class="is-handler-enabled is-handler-enabled-round" type="checkbox" value="true" checked="checked" onclick="changeValueOfFlow(this);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%>/>
									  <label for="toggle-profilelookup_<%=widgetId%>"></label>
									</div>
								</td>
								<td width="1%" align="left" valign="middle" class="tbl-header-bold" style="padding-right: 5px;">
									<img alt="Delete" class="delele_proxy"  src="<%=request.getContextPath()%>/images/delete_proxy.png" title="Delete"   style="cursor: pointer;" <%if("true".equals(request.getParameter("isViewPage").toString()) == false){ %>  onclick="deleteHandler(this);" <%}%>  />
								</td>
								<td width="2%" align="left" style="padding-right: 10px;" class="tbl-header-bold" onclick="expandCollapse(this);">
									<img alt="Expand" class="expand_class" id="profileDriver"  title="Expand"  src="<%=request.getContextPath()%>/images/bottom.ico"  style="cursor: pointer;"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<div id="profileLookUpDriver" class="toggleDivs">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="bottom-border left-border right-border">
					<tr>
						<td align="left" class="captiontext" valign="top" style="padding-top:10px;" width="50%">
							<table  width="100%" border="0" style="background-color: white;" cellspacing="0" cellpadding="0">
								<tr>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;" width="40%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.updateuseridentity" />
										<ec:elitehelp  header="tgppaaapolicy.profilelookupdhandler.updateuseridentity" headerBundle="servicePolicyProperties" text="tgppaaapolicy.profilelookupdhandler.updateuseridentity" ></ec:elitehelp>
									</td>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;" width="60%">
										<table width="100%" border="0" style="background-color: white;border-style: solid; border-width: 1px; border-color: #CCCCCC;padding: 5px;width: 250px;" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td align="left" class="labeltext" valign="top">
													<table width="100%" cellspacing="0" cellpadding="0" border="0">
														<tr>
															<td align="left" class="labeltext" valign="top" style="padding-top:5px;">
																<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.stripidentity" /> 
															</td>
															<td align="left" class="labeltext" valign="top" style="padding-top:5px;">
																<select class="stripIdentity labeltext" id="stripIdentity" name="stripIdentity" style="width:100px;"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
																	<option value="None">None</option>
																	<option value="Prefix">Prefix</option>
																	<option value="Suffix">Suffix</option>
																</select>
															</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top" style="padding-top:5px;">
																<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.realmseparator" /> 
															</td>
															<td align="left" class="labeltext" valign="top" style="padding-top:5px;">
																<input class="separator" type="text" name="separator" id="separator" size="5" maxlength="1" style="width:100px;"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
															</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top" style="padding-top:5px;">
																<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.casesensitiveuid" />
															</td>
															<td align="left" class="labeltext" valign="top" style="padding-top:5px;">
																<select id="iCase" name="iCase" style="width:100px;" class="iCase labeltext"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
																	<option value="No Change">No Change</option>
																	<option value="Lower Case">Lower Case</option>
																	<option value="Upper Case">Upper Case</option>
																</select>
															</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top" colspan="2" style="padding-top:5px;">
																<input class="trimUserIdentity" type="checkbox" id="trimUserIdentity" name="trimUserIdentity" value="true" onchange="changeDiameterPolicyVal(this);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
																<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.trimuid" />
															</td>
														</tr>
														<tr>
															<td align="left" class="labeltext" valign="top" colspan="2" style="padding-top:5px;">
																<input type="checkbox" id="trimPassword" name="trimPassword" value="true" class="trimPassword" onchange="changeDiameterPolicyVal(this);"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
																<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.trimpassword" />
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" align="left" style="padding-top:10px;" width="40%">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.anonymousidentity" />
										<ec:elitehelp  header="tgppaaapolicy.profilelookupdhandler.anonymousidentity" headerBundle="servicePolicyProperties" text="tgppaaapolicy.profilelookupdhandler.anonymousidentity" ></ec:elitehelp>
									</td>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;" width="60%">
										<input class="anonymousProfileIdentity" type="text" id="anonymousProfileIdentity" name="anonymousProfileIdentity" size="25" style="width: 250px" maxlength="300"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
									</td>
								</tr>
							</table>
						</td>
						<td align="left" class="labeltext" valign="top" style="padding-top:10px;" width="50%">
							<table  width="100%" border="0" style="background-color: white;" cellspacing="0" cellpadding="0">
								<tr>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.primarygroup" /> 
										<font color="#FF0000">*</font> 
										<ec:elitehelp header="tgppaaapolicy.profilelookupdhandler.primarygroup" example="tgppaaapolicy.profilelookupdhandler.primarygroup" headerBundle="servicePolicyProperties" text="tgppaaapolicy.profilelookupdhandler.primarygroup" ></ec:elitehelp>
									</td>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;">
										<table width="50%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td align="left" class="labeltext" valign="top"> 
													<select class="labeltext select-box-style selecteddriverIds" name="selecteddriverIds" id="selecteddriverIds<%=widgetId%>" multiple="multiple" size="3" style="width: 200;" tabindex="1" <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
													</select>
												</td>
												<td align="left" class="labeltext" valign="top">
													<table cellspacing="0" cellpadding="0" border="0" width="20%">
														<tr>
															<td>
																<input type="button" id="addBtn_<%=widgetId%>" value="Add " onClick="driverpopup(this);" class="light-btn driver-popup" style="width: 75px" tabindex="2"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /><br />
															</td>
														</tr>
														<tr>
															<td style="padding-top: 5px;">
																<input type="button" id="removeBtn_<%=widgetId%>"  value="Remove " onclick="removeData('selecteddriverIds<%=widgetId%>','driverDataCheckBoxId<%=widgetId%>',this);" class="light-btn" style="width: 75px" tabindex="3"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.secondarygroup" /> 
										<ec:elitehelp  header="tgppaaapolicy.profilelookupdhandler.secondarygroup" headerBundle="servicePolicyProperties" text="tgppaaapolicy.profilelookupdhandler.secondarygroup" ></ec:elitehelp>
									</td>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;">
										<table width="50%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td align="left" class="labeltext" valign="top"> 
													<select class="labeltext select-box-style selectedCacheDriverIds" name="selectedCacheDriverIds" id="selectedCacheDriverIds<%=widgetId%>" multiple="multiple" size="3" style="width: 200;" tabindex="7"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
													</select>
												</td>
												<td align="left" class="labeltext" valign="top">
													<table cellspacing="0" cellpadding="0" border="0" width="20%">
														<tr>
															<td>
																<input type="button" id="addCacheBtn_<%=widgetId%>" value="Add " onClick="cacheDriverpopup(this);" class="light-btn" style="width: 75px" tabindex="5"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /><br />
															</td>
														</tr>
														<tr>
															<td style="padding-top: 5px;">
																<input type="button" id="removeCacheBtn_<%=widgetId%>" value="Remove " onclick="removeCacheData('selectedCacheDriverIds<%=widgetId%>','driverDataRadioId<%=widgetId%>',this);" class="light-btn" style="width: 75px" tabindex="6"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.additionalgroup" />
										<ec:elitehelp  header="tgppaaapolicy.profilelookupdhandler.additionalgroup" headerBundle="servicePolicyProperties" text="tgppaaapolicy.profilelookupdhandler.additionalgroup" ></ec:elitehelp>
									</td>
									<td align="left" class="labeltext" valign="top" style="padding-top:10px;">
										<table width="50%" cellspacing="0" cellpadding="0" border="0">
											<tr>
												<td align="left" class="labeltext" valign="top"> 
													<select class="labeltext select-box-style selectedAdditionalDriverIds" name="selectedAdditionalDriverIds" id="selectedAdditionalDriverIds<%=widgetId%>" multiple="multiple" size="3" style="width: 200px;" tabindex="7"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> >
												</td>
												<td align="left" class="labeltext" valign="top" >
													<table cellspacing="0" cellpadding="0" border="0" width="20%">
														<tr>
															<td>
																<input type="button" value="Add " id="addAdditionalBtn_<%=widgetId%>" onClick="additionalDriverpopup(this);" class="light-btn" style="width: 75px" tabindex="8"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> /> 
															</td>
														</tr>
														<tr>
															<td style="padding-top: 5px;">
																<input type="button" value="Remove " id="removeAdditionalBtn_<%=widgetId%>" onclick="removeAdditionalDriverData('selectedAdditionalDriverIds<%=widgetId%>','additionalDriverCheckBoxId<%=widgetId%>',this);" class="light-btn" style="width: 75px;" tabindex="9"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="left" class="labeltext" valign="top" style="padding-top: 10px;">
										<bean:message bundle="servicePolicyProperties" key="tgppaaapolicy.profilelookupdhandler.driverscript" />
										<ec:elitehelp  header="tgppaaapolicy.profilelookupdhandler.driverscript" headerBundle="servicePolicyProperties" text="tgppaaapolicy.profilelookupdhandler.driverscript" ></ec:elitehelp>
									</td>
									<td class="labeltext" valign="top" style="padding-top: 10px;padding-left: 5px;">
										<input class="driverScript scriptInstAutocomplete" type="text" name="driverScript" id="driverScript" size="30" style="width:200px" maxlength="255"  <%if("true".equals(request.getParameter("isViewPage").toString())){ %>  disabled=" disabled" <%}%> />
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
</tbody>
</table>
<%--START :Main Driver Template  --%>
<div id="driverPopup<%=widgetId%>"  title="Add Drivers" style="display: none;">
	<table id="addDriver<%=widgetId%>" name="addDriver" cellpadding="0" cellspacing="0" width="100%" class="box">
	</table>
</div>
<%--END  :Main Driver Template  --%>
<%--START :Cache Driver Template  --%>
<div id="cacheDriverPopup<%=widgetId%>" style="display: none;" title="Add Cache Drivers">
	<table id="addCacheDriver<%=widgetId%>" name="addCacheDriver" cellpadding="0" cellspacing="0" width="100%" class="box">
	</table>
</div>
<%--END  :Cache Driver Template  --%>
<%--START :Additional Driver Template  --%>
<div id="additionalDriverPopup<%=widgetId%>" style="display: none;" title="Add Additional Drivers">
	<table id="additionalDriver<%=widgetId%>" name="additionalDriver" cellpadding="0" cellspacing="0" width="100%" class="box">
	</table>
</div>
<%--END  :Additional Driver Template  --%>
</form>
</body>
</html>
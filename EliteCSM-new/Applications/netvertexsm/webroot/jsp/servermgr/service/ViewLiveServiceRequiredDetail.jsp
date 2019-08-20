<jsp:directive.page import="java.util.LinkedHashMap"/>
<jsp:directive.page import="java.util.Iterator"/>
<jsp:directive.page import="java.util.HashMap"/>
<jsp:directive.page import="java.lang.Double"/>
<jsp:directive.page import="java.lang.Object"/>
<jsp:directive.page import="java.text.SimpleDateFormat"/>
<jsp:directive.page import="java.util.Date"/>
<jsp:directive.page import="com.elitecore.netvertexsm.web.servermgr.service.form.ViewLiveServiceRequiredDetailForm"/>






<%@ page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"%>
<%@ page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"%>


<%
	String localBasePath = request.getContextPath();
	String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
	LinkedHashMap liveServiceSummaryMap = (LinkedHashMap)request.getAttribute("liveServiceSummaryMap");
	ViewLiveServiceRequiredDetailForm viewLiveServiceRequiredDetailForm = (ViewLiveServiceRequiredDetailForm)request.getAttribute("viewLiveServiceRequiredDetailForm");
%>

<script> 

	var dFormat;
	dFormat = '<%=dateFormat%>';
		
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 
	}
	
	function send() {
		if(document.forms[0].startDate.value == "") {
			alert("Plz select start date..")
		}else if(document.forms[0].endDate.value == "") {
			alert("Plz select end date..")
		} else {
			document.forms[0].checkAction.value = 'Send';
			document.forms[0].submit();
		}	
	}
	
	function navigate(direction, pageNumber ){
		document.forms[0].pageNumber.value = pageNumber;
		document.forms[0].submit();
	}
</script>


<%
	 long pageNo = viewLiveServiceRequiredDetailForm.getPageNumber();
     long totalPages = viewLiveServiceRequiredDetailForm.getTotalPages();
     long totalRecord = viewLiveServiceRequiredDetailForm.getTotalRecords();
	 int count=1;
     String strTotalPages = String.valueOf(totalPages);
     String strTotalRecords = String.valueOf(totalRecord);
 %>
 
<html:form action="/viewRequiredAttribute">
	<html:hidden styleId="checkAction" property="checkAction" />
	<html:hidden styleId="serviceId" property="serviceId" />
	<html:hidden name="viewLiveServiceRequiredDetailForm" styleId="pageNumber" property="pageNumber"/>
	<html:hidden name="viewLiveServiceRequiredDetailForm" styleId="totalPages" property="totalPages" value="<%=strTotalPages%>"/>
	<html:hidden name="viewLiveServiceRequiredDetailForm" styleId="totalRecords" property="totalRecords" value="<%=strTotalRecords%>"/>
		
	<table width="97%" name="c_tblCrossProductLis id="c_tblCrossProductList align="right" border="0">
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td align="left" class="labeltext" valign="top">Hour</td>
			<td align="left" class="labeltext" valign="top">Minute</td>
		</tr>
		<tr>
			<td align="left" class="labeltext" valign="top" width="15%">
				Start Date
			</td>
			
			<td align="left" class="labeltext" valign="top" width="30%">
				<html:text styleId="startDate" property="startDate" size="15" maxlength="15" readonly="true"/>
				<font color="#FF0000"> *</font>
				<a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].startDate)"> 
					<img src="<%=localBasePath%>/images/calendar.jpg" border="0" tabindex="6">
				</a>
			</td>

			<td align="left" class="labeltext" valign="top" width="20%">
				<html:select name="viewLiveServiceRequiredDetailForm" styleId="startHour" property="startHour" size="1">
					<html:option value="00">00</html:option>
					<html:option value="01">01</html:option>
					<html:option value="02">02</html:option>
					<html:option value="03">03</html:option>
					<html:option value="04">04</html:option>
					<html:option value="05">05</html:option>
					<html:option value="06">06</html:option>
					<html:option value="07">07</html:option>
					<html:option value="08">08</html:option>
					<html:option value="09">09</html:option>
					<html:option value="10">10</html:option>
					<html:option value="11">11</html:option>
					<html:option value="12">12</html:option>
					<html:option value="13">13</html:option>
					<html:option value="14">14</html:option>
					<html:option value="15">15</html:option>
					<html:option value="16">16</html:option>
					<html:option value="17">17</html:option>
					<html:option value="18">18</html:option>
					<html:option value="19">19</html:option>
					<html:option value="20">20</html:option>
					<html:option value="21">21</html:option>
					<html:option value="22">22</html:option>
					<html:option value="23">23</html:option>
				</html:select>
			</td>

			<td align="left" class="labeltext" valign="top" width="20%">
				<html:select name="viewLiveServiceRequiredDetailForm" styleId="startMinute" property="startMinute" size="1">
					<html:option value="00">00</html:option>
					<html:option value="01">01</html:option>
					<html:option value="02">02</html:option>
					<html:option value="03">03</html:option>
					<html:option value="04">04</html:option>
					<html:option value="05">05</html:option>
					<html:option value="06">06</html:option>
					<html:option value="07">07</html:option>
					<html:option value="08">08</html:option>
					<html:option value="09">09</html:option>
					<html:option value="10">10</html:option>
					<html:option value="11">11</html:option>
					<html:option value="12">12</html:option>
					<html:option value="13">13</html:option>
					<html:option value="14">14</html:option>
					<html:option value="15">15</html:option>
					<html:option value="16">16</html:option>
					<html:option value="17">17</html:option>
					<html:option value="18">18</html:option>
					<html:option value="19">19</html:option>
					<html:option value="20">20</html:option>
					<html:option value="21">21</html:option>
					<html:option value="22">22</html:option>
					<html:option value="23">23</html:option>
					<html:option value="24">24</html:option>
					<html:option value="25">25</html:option>
					<html:option value="26">26</html:option>
					<html:option value="27">27</html:option>
					<html:option value="28">28</html:option>
					<html:option value="29">29</html:option>
					<html:option value="30">30</html:option>
					<html:option value="31">31</html:option>
					<html:option value="32">32</html:option>
					<html:option value="33">33</html:option>
					<html:option value="34">34</html:option>
					<html:option value="35">35</html:option>
					<html:option value="36">36</html:option>
					<html:option value="37">37</html:option>
					<html:option value="38">38</html:option>
					<html:option value="39">39</html:option>
					<html:option value="40">40</html:option>
					<html:option value="41">41</html:option>
					<html:option value="42">42</html:option>
					<html:option value="43">43</html:option>
					<html:option value="44">44</html:option>
					<html:option value="45">45</html:option>
					<html:option value="46">46</html:option>
					<html:option value="47">47</html:option>
					<html:option value="48">48</html:option>
					<html:option value="49">49</html:option>
					<html:option value="50">50</html:option>
					<html:option value="51">51</html:option>
					<html:option value="52">52</html:option>
					<html:option value="53">53</html:option>
					<html:option value="54">54</html:option>
					<html:option value="55">55</html:option>
					<html:option value="56">56</html:option>
					<html:option value="57">57</html:option>
					<html:option value="58">58</html:option>
					<html:option value="59">59</html:option>

				</html:select>
			</td>

		</tr>

		<tr>
			<td align="left" class="labeltext" valign="top" width="15%">
				End Date
			</td>
			<td align="left" class="labeltext" valign="top" width="30%">
				<html:text styleId="endDate" property="endDate" size="15" maxlength="15"  readonly="true"/>
				<font color="#FF0000"> *</font>
				<a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].endDate)">
				 	<img src="<%=localBasePath%>/images/calendar.jpg" border="0" tabindex="6">
				</a>
			</td>

			<td align="left" class="labeltext" valign="top" width="20%">
				<html:select name="viewLiveServiceRequiredDetailForm" styleId="endHour" property="endHour" size="1">
					<html:option value="00">00</html:option>
					<html:option value="01">01</html:option>
					<html:option value="02">02</html:option>
					<html:option value="03">03</html:option>
					<html:option value="04">04</html:option>
					<html:option value="05">05</html:option>
					<html:option value="06">06</html:option>
					<html:option value="07">07</html:option>
					<html:option value="08">08</html:option>
					<html:option value="09">09</html:option>
					<html:option value="10">10</html:option>
					<html:option value="11">11</html:option>
					<html:option value="12">12</html:option>
					<html:option value="13">13</html:option>
					<html:option value="14">14</html:option>
					<html:option value="15">15</html:option>
					<html:option value="16">16</html:option>
					<html:option value="17">17</html:option>
					<html:option value="18">18</html:option>
					<html:option value="19">19</html:option>
					<html:option value="20">20</html:option>
					<html:option value="21">21</html:option>
					<html:option value="22">22</html:option>
					<html:option value="23">23</html:option>
				</html:select>
			</td>

			<td align="left" class="labeltext" valign="top" width="20%">
				<html:select name="viewLiveServiceRequiredDetailForm" styleId="endMinute" property="endMinute" size="1">
					<html:option value="00">00</html:option>
					<html:option value="01">01</html:option>
					<html:option value="02">02</html:option>
					<html:option value="03">03</html:option>
					<html:option value="04">04</html:option>
					<html:option value="05">05</html:option>
					<html:option value="06">06</html:option>
					<html:option value="07">07</html:option>
					<html:option value="08">08</html:option>
					<html:option value="09">09</html:option>
					<html:option value="10">10</html:option>
					<html:option value="11">11</html:option>
					<html:option value="12">12</html:option>
					<html:option value="13">13</html:option>
					<html:option value="14">14</html:option>
					<html:option value="15">15</html:option>
					<html:option value="16">16</html:option>
					<html:option value="17">17</html:option>
					<html:option value="18">18</html:option>
					<html:option value="19">19</html:option>
					<html:option value="20">20</html:option>
					<html:option value="21">21</html:option>
					<html:option value="22">22</html:option>
					<html:option value="23">23</html:option>
					<html:option value="24">24</html:option>
					<html:option value="25">25</html:option>
					<html:option value="26">26</html:option>
					<html:option value="27">27</html:option>
					<html:option value="28">28</html:option>
					<html:option value="29">29</html:option>
					<html:option value="30">30</html:option>
					<html:option value="31">31</html:option>
					<html:option value="32">32</html:option>
					<html:option value="33">33</html:option>
					<html:option value="34">34</html:option>
					<html:option value="35">35</html:option>
					<html:option value="36">36</html:option>
					<html:option value="37">37</html:option>
					<html:option value="38">38</html:option>
					<html:option value="39">39</html:option>
					<html:option value="40">40</html:option>
					<html:option value="41">41</html:option>
					<html:option value="42">42</html:option>
					<html:option value="43">43</html:option>
					<html:option value="44">44</html:option>
					<html:option value="45">45</html:option>
					<html:option value="46">46</html:option>
					<html:option value="47">47</html:option>
					<html:option value="48">48</html:option>
					<html:option value="49">49</html:option>
					<html:option value="50">50</html:option>
					<html:option value="51">51</html:option>
					<html:option value="52">52</html:option>
					<html:option value="53">53</html:option>
					<html:option value="54">54</html:option>
					<html:option value="55">55</html:option>
					<html:option value="56">56</html:option>
					<html:option value="57">57</html:option>
					<html:option value="58">58</html:option>
					<html:option value="59">59</html:option>
				</html:select>
			</td>
			<td>&nbsp;</td>
		</tr>
		
		<tr>
			<td align="center" colspan="6">
				<input type="button" name="get"  value="       Get      " class="light-btn" onclick="send()">
			</td>
		</tr>
	</table>
	
	<br/><br/><br/><br/><br/><br/>
	
	<table width="100%" cols="8" border="1" cellpadding="0" cellspacing="0">
	
									<tr > 
										<% if(liveServiceSummaryMap.keySet().size() > 0) { %>
										
			    			              <td class="table-header" colspan="5" >Service Summary List</td>  
										  <td align="right" class="blue-text" valign="middle" width="14%" colspan="4" >
												<% if(pageNo == totalPages+1) { %>
													    [<%=((pageNo-1)*10)+1%>-<%=totalRecord%>] of <%= totalRecord %>
												<% } else if(pageNo == 1) { %>
													    [<%=(pageNo-1)*10+1%>-<%=(pageNo-1)*10+10%>] of <%= totalRecord %>
												<% } else { %>
													    [<%=((pageNo-1)*10)+1%>-<%=((pageNo-1)*10)+10%>] of <%= totalRecord %>
												<% } %>
								    	  </td>
								    	  
								    	  <% } else { %>
								    	  	<td class="table-header" colspan="6" >Service Summary List</td>  
								    	  <% } %>
									 </tr>
									 
									 
									  <tr>
						    				<td class="btns-td" align="right" colspan="6">
											  	<% if(totalPages >= 1) { %>
												  	<% if(pageNo == 1){ %>
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%=pageNo+1%>)"  onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" />
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
												  	<% } %>
											  	
												  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
												  		<%  if(pageNo-1 == 1){ %>
																<img  src="<%=localBasePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)"   onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
																<img  src="<%=localBasePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
												  		<% } else if(pageNo == totalPages){ %>
																<img  src="<%=localBasePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
																<img  src="<%=localBasePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"   onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
												  		<% } else { %>
																<img  src="<%=localBasePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)"  onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
													  			<img  src="<%=localBasePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
														<% } %>
												  	<% } %>
												 	<% if(pageNo == totalPages+1) { %>
																<img  src="<%=localBasePath%>/images/first.jpg"  name="Image511"  onclick="navigate('first',<%=1%>)"onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
																<img  src="<%=localBasePath%>/images/previous.jpg"  name="Image5"  onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
												  	<% } %>
												  <% } %>
							 				</td>
									</tr>
									
									
									 
									<tr>
										<td align="center" class="tblheader" valign="top" width="5%">													
											Sr.No.
										</td>
										<td align="center" class="tblheader" valign="top" width="15%">													
											Date
										</td>
										<td align="left" class="tblheader" valign="top" width="10%">
											Access Request
										</td>
										<td align="left" class="tblheader" valign="top" width="10%">
											Access Accept
										</td>
										<td align="left" class="tblheader" valign="top" width="10%">
											Access Reject
										</td>
										<td align="left" class="tblheader" valign="top" width="10%">
											Access Dropped
										</td>
									</tr>
		
		
								<%if(liveServiceSummaryMap.keySet().size() > 0) { %>
		
								<% int index = 0; %>		
							
								<%	Iterator iter = liveServiceSummaryMap.keySet().iterator();
								
							 		while(iter.hasNext()) {
							 		
									 Object key = iter.next();
									 HashMap elementMap = (HashMap)liveServiceSummaryMap.get(key);
							        
										if(elementMap != null) {
											double accessRequest = ((Double)elementMap.get("ACCESS_REQUEST")).doubleValue();
											double accessAccept = ((Double)elementMap.get("ACCESS_ACCEPT")).doubleValue();
											double accessReject = ((Double)elementMap.get("ACCESS_REJECT")).doubleValue();
											double accessDropped = ((Double)elementMap.get("ACCESS_DROPPED")).doubleValue();
											
											if(accessRequest == Double.NaN) { accessRequest = 0.0; }
											if(accessAccept == Double.NaN) { accessAccept = 0.0; }
											if(accessReject == Double.NaN) { accessReject = 0.0; }
											if(accessDropped == Double.NaN) { accessDropped = 0.0; }

								 			long yourmilliseconds = Long.parseLong(key.toString());
											SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
							 				Date resultdate = new Date(yourmilliseconds*1000); %>	
							 			
										<tr>
											<td align="left" class="tblrows" valign="top">												
												<%=(index + 1)%>
											</td>
											<td align="left" class="tblrows" valign="top">
												<%=sdf.format(resultdate)%>
											</td>
											<td align="left" class="tblrows" valign="top">
												<%=accessRequest%>
											</td>
											<td align="left" class="tblrows" valign="top">
												<%=accessAccept%>
											</td>
											<td align="left" class="tblrows" valign="top">
												<%=accessReject%>
											</td>
											<td align="left" class="tblrows" valign="top">
												<%=accessDropped%>
											</td>
									  </tr>
									
									<% } index = index + 1; } %>	
									
									<% } %>
						<tr>
			   				<td class="btns-td" align="right" colspan="6">
			   					<% if(totalPages >= 1) { %>
												  	<% if(pageNo == 1){ %>
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%=pageNo+1%>)"  onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" />
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
												  	<% } %>
												  	<% if(pageNo>1 && pageNo!=totalPages+1) {%>
												  		<%  if(pageNo-1 == 1){ %>
																<img  src="<%=localBasePath%>/images/first.jpg" name="Image511" onclick="navigate('first',<%= 1%>)"   onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
																<img  src="<%=localBasePath%>/images/previous.jpg" onclick="navigate('next',<%= pageNo-1%>)" name="Image5"  onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
												  		<% } else if(pageNo == totalPages){ %>
																<img  src="<%=localBasePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)" onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
																<img  src="<%=localBasePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%= pageNo-1%>)" onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61"  onclick="navigate('next',<%= pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%= totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"   onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
												  		<% } else { %>
																<img  src="<%=localBasePath%>/images/first.jpg"  name="Image511" onclick="navigate('previous',<%= 1%>)"  onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
													  			<img  src="<%=localBasePath%>/images/previous.jpg"  name="Image5" onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
																<img  src="<%=localBasePath%>/images/next.jpg"  name="Image61" onclick="navigate('next',<%=pageNo+1%>)" onmouseover="MM_swapImage('Image61','','<%=localBasePath%>/images/next-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Next"  border="0" >
																<img  src="<%=localBasePath%>/images/last.jpg"  name="Image612" onclick="navigate('last',<%=totalPages+1%>)" onmouseover="MM_swapImage('Image612','','<%=localBasePath%>/images/last-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Last"  border="0" >
														<% } %>
												  	<% } %>
												 	<% if(pageNo == totalPages+1) { %>
																<img  src="<%=localBasePath%>/images/first.jpg"  name="Image511"  onclick="navigate('first',<%=1%>)"onmouseover="MM_swapImage('Image511','','<%=localBasePath%>/images/first-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="First"  border="0" >
																<img  src="<%=localBasePath%>/images/previous.jpg"  name="Image5"  onclick="navigate('previous',<%=pageNo-1%>)"onmouseover="MM_swapImage('Image5','','<%=localBasePath%>/images/previous-hover.jpg',1)"  onmouseout="MM_swapImgRestore()"  alt="Previous"  border="0" >
												  	<% } %>
												  <% } %>
				   			</td>
					</tr>

	</table>											
</html:form>

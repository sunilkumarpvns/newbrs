
<jsp:directive.page import="com.elitecore.netvertexsm.util.constants.ConfigConstant"/>
<jsp:directive.page import="com.elitecore.netvertexsm.web.core.system.cache.ConfigManager"/>





<% 
	String basePath = request.getContextPath();
	String dateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT); %>

<script language="javascript">
	var dFormat;
	dFormat = '<%=dateFormat%>';
		
	function popUpCalendar(ctl,	ctl2, datestyle)
	{
		datestyle = dFormat;
		jsPopUpCalendar( ctl, ctl2, datestyle ); 
	}
	
	function purgeNow() {
		if(document.forms[0].purgeDate.value == "") {
			alert("Date Field should not be empty");
			return;
		}
		document.forms[0].checkAction.value="purgeNow";
		document.forms[0].submit();
	}
	
	function clearData() {
		document.forms[0].purgeDate.value = "";
		document.forms[0].purgeHour.value = "00";
		document.forms[0].purgeMinute.value = "00";
	}
	
</script>

<html:form action="/purgeConfigForMediation">	
 <html:hidden styleId="checkAction" property="checkAction" />
   <html:hidden styleId="serverId" property="serverId" />
   
 	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    	<tr> 
      		<td valign="top" align="right"> 
      		
        		<table width="97%" border="0" cellspacing="0" cellpadding="0" height="15%" >
				<br>
				  <tr> 
					<td class="tblheader-bold" colspan="2">Purge Interval</td>
				  </tr>
				  
				   <tr> 
					<td class="tblfirstcol" colspan="2">&nbsp;</td>
				  </tr>
				  
				  <tr>
					<td align="left" class="tblfirstcol" valign="top" width="5%"><strong>Date</strong></td>
					<td align="left" class="tblrows" valign="top" width="18%" >
						<html:text styleId="purgeDate" property="purgeDate" size="18" maxlength="15" readonly="true"/>
						<a href="javascript:void(0)" onclick="popUpCalendar(this, document.forms[0].purgeDate)"> 
							<img src="<%=basePath%>/images/calendar.jpg" border="0" tabindex="6">
						</a>
						<font color="#FF0000"> *</font>						
					</td>
				  </tr>
				  <tr>
					<td align="left" class="tblfirstcol" valign="top" width="5%"><strong>Time</strong></td>
					<td align="left" class="tblrows" valign="top" width="18%" >
						<html:select styleId="purgeHour" property="purgeHour" style="width:50;">
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
						Hours
						&nbsp;&nbsp;&nbsp;&nbsp;
						<html:select styleId="purgeMinute" property="purgeMinute" style="width:50;">
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
						Minutes
					</td>
				  </tr>
				  
				  <tr> 
					<td class="tblfirstcol" colspan="2">&nbsp;</td>
				  </tr>
				  
				  <tr>
					<td align="left" class="tblfirstcol" valign="top" width="5%">&nbsp;</td>
					<td align="left" class="tblrows" valign="top" width="18%" >
						<input type="button" name="btn_purgeNow" value="Purge Now" class="light-btn" onclick="purgeNow()"/>
						<input type="button" name="btn_clear" value="Reset" class="light-btn" onclick="clearData()"/>
					</td>
				  </tr>
				</table>
			</td>
		</tr>		  
 	</table>
</html:form>

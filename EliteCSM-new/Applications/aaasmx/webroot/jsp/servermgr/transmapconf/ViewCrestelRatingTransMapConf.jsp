<%@page import="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingInstData"%>
<%@page import="com.elitecore.elitesm.util.constants.TranslationMappingConfigConstants"%>
<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ page import="com.elitecore.commons.base.Collectionz"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/elitecore" prefix="elitecore" %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

   <bean:define id="translationMapppingConfigBean" name="translationMappingConfData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData" />

    <tr> 
      <td valign="top" align="right" colspan="2"> 
    	 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="translationmapconf.view"/></td>
          </tr>
          </table>
       </td>
     </tr>
     <tr>
     	<td class='tblfirstcol'><bean:message bundle="servermgrResources" key="translationmapconf.basetranslationmapping" /></td>
     	<td class='tblcol'  width="70%" height="20%">
     		<logic:notEmpty property="baseTranslationMappingConfData" name="viewCrestelRatingTransMapConfigForm">
     			<bean:write property="baseTranslationMappingConfData.name" name="viewCrestelRatingTransMapConfigForm"/>
     		</logic:notEmpty>
     		<logic:empty property="baseTranslationMappingConfData" name="viewCrestelRatingTransMapConfigForm">
     			No Base Translation Mapping Configured 
     		</logic:empty>
     	</td>
     </tr>
     <tr>
     	<td colspan="2">
     	<%int index=1; %>
 		<logic:iterate id="translationMappingInstDataBean"  name="translationMapppingConfigBean" property="translationMappingInstDataList" type="TranslationMappingInstData">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader" colspan="2" height="20%">Mapping-<%=index%></td>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="servermgrResources" key="translationmapconf.mappingname"/>  (<bean:write name="translationMapppingConfigBean" property="translatorTypeFrom.name"/>)</td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="translationMappingInstDataBean" property="mappingName"/>&nbsp;</td>
         </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="servermgrResources" key="translationmapconf.inmessage"/>  (<bean:write name="translationMapppingConfigBean" property="translatorTypeFrom.name"/>)</td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="translationMappingInstDataBean" property="inMessage"/>&nbsp;</td>
         </tr>
         <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="servermgrResources" key="translationmapconf.outmessage"/>  (<bean:write name="translationMapppingConfigBean" property="translatorTypeTo.name"/>)</td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="translationMappingInstDataBean" property="outMessage"/>&nbsp;</td>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1><bean:message bundle="servermgrResources" key="translationmapconf.defaultmapping"/></td>
            <td class="tblcol" width="70%" height="20%" >
	            <logic:equal value="N" name="translationMappingInstDataBean" property="defaultMapping">
	            No
	            </logic:equal>
	            <logic:equal value="Y" name="translationMappingInstDataBean" property="defaultMapping">
	            Yes
	            </logic:equal>
            </td>
          </tr>
           <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1><bean:message bundle="servermgrResources" key="translationmapconf.dummyresponse" /></td>
            <td class="tblcol" width="70%" height="20%" >
	            <logic:equal value="false" name="translationMappingInstDataBean" property="dummyResponse">
	            No
	            </logic:equal>
	            <logic:equal value="true" name="translationMappingInstDataBean" property="dummyResponse">
	            Yes
	            </logic:equal>
            </td>
          </tr>
          <logic:equal value="N" name="translationMappingInstDataBean" property="defaultMapping">
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> <bean:message bundle="servermgrResources" key="translationmapconf.reqparams"/></td>
            <td class="tblcol" width="70%" height="20%" >
            	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
            		<tr>
            			<td class="tblheader" height="20%"><bean:message key="general.serialnumber"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.checkexpression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.mappingexression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.defaultvalue"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.valuemapping"/></td>
            		</tr>
            		<% int tempIndex = 1; %>
            		<% if (Collectionz.isNullOrEmpty(translationMappingInstDataBean.getTranslationMappingInstDetailDataList()) == false) { %>
            		<logic:iterate id="translationMappingInstDetailDataBean"  name="translationMappingInstDataBean" property="translationMappingInstDetailDataList">
            		<logic:equal value="<%=TranslationMappingConfigConstants.REQUEST_PARAMETERS%>"  name="translationMappingInstDetailDataBean" property="mappingTypeId">
            		<tr>
            			<td class="tblfirstcol" height="20%"><%=tempIndex%></td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="checkExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="mappingExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="defaultValue"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="valueMapping"/>&nbsp;</td>
            		</tr>
            		<%tempIndex++; %>
            		</logic:equal>
            		
            		</logic:iterate>
            		<%if(tempIndex==1){ %>
            				<tr>
				            	<td class="tblfirstcol" colspan="5" align="center" valign=top>No Records Found.</td>
				            </tr>
            		<% }%>
            		<%} %>
            	</table>
            </td>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1><bean:message bundle="servermgrResources" key="translationmapconf.respparams"/></td>
            <td class="tblcol" width="70%" height="20%" >
            	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
            		<tr>
            			<td class="tblheader" height="20%"><bean:message key="general.serialnumber"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.checkexpression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.mappingexression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.defaultvalue"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.valuemapping"/></td>
            		</tr>
            		<% int resTempIndex = 1; %>
            		<% if(Collectionz.isNullOrEmpty(translationMappingInstDataBean.getTranslationMappingInstDetailDataList()) == false){ %>
            		<logic:iterate id="translationMappingInstDetailDataBean"  name="translationMappingInstDataBean" property="translationMappingInstDetailDataList">
            		<logic:equal value="<%=TranslationMappingConfigConstants.RESPONSE_PARAMETERS%>"  name="translationMappingInstDetailDataBean" property="mappingTypeId">
            		<tr>
            			<td class="tblfirstcol" height="20%"><%=resTempIndex%></td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="checkExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="mappingExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="defaultValue"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="valueMapping"/>&nbsp;</td>
            		</tr>
            		<%resTempIndex++; %>
            		</logic:equal>
            		
            		</logic:iterate>
            		<%if(resTempIndex==1){ %>
            				<tr>
				            	<td class="tblfirstcol" colspan="5" align="center" valign=top>No Records Found.</td>
				            </tr>
            		<% }%>
            		<%} %>
            	</table>
            </td>
          </tr>
          </logic:equal>
        </table> 
		<%index++;%>
		</logic:iterate>
		
		</td>
    </tr>
    <tr>
    <td align=left class=tblheader-bold valign=top colspan="2">
    	<bean:message bundle="servermgrResources" key="translationmapconf.defaultmapping"/>
    </td>
    </tr>
    <tr>
    <td colspan="2">
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
         <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> Request Parameters</td>
            <td class="tblcol" width="70%" height="20%" >
            	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
            		<tr>
            			<td class="tblheader" height="20%"><bean:message key="general.serialnumber"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.checkexpression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.mappingexression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.defaultvalue"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.valuemapping"/></td>
            		</tr>
            		<% int tempIndex = 1; %>
            		
            		<logic:iterate id="translationMappingInstDetailDataBean"  name="translationMapppingConfigBean" property="defaultTranslationMappingDetailDataList">
            		<logic:equal value="<%=TranslationMappingConfigConstants.REQUEST_PARAMETERS%>"  name="translationMappingInstDetailDataBean" property="mappingTypeId">
            		<tr>
            			<td class="tblfirstcol" height="20%"><%=tempIndex%></td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="checkExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="mappingExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="defaultValue"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="valueMapping"/>&nbsp;</td>
            		</tr>
            		<%tempIndex++; %>
            		</logic:equal>
            		</logic:iterate>
            		<%if(tempIndex==1){ %>
            				<tr>
				            	<td class="tblfirstcol" colspan="5" align="center" valign=top>No Records Found.</td>
				            </tr>
            		<% }%>
            		
            		
            	</table>
            </td>
          </tr>
          <tr> 
			<td  align=left class='tblfirstcol' valign=top colspan=1> Response Parameters</td>
            <td class="tblcol" width="70%" height="20%" >
            	<table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
            		<tr>
            			<td class="tblheader" height="20%"><bean:message key="general.serialnumber"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.checkexpression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.mappingexression"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.defaultvalue"/></td>
						<td align=left class=tblheader valign=top><bean:message bundle="servermgrResources" key="translationmapconf.valuemapping"/></td>
            		</tr>
            		<% int resTempIndex = 1; %>
            		
            		<logic:iterate id="translationMappingInstDetailDataBean"  name="translationMapppingConfigBean" property="defaultTranslationMappingDetailDataList">
            		<logic:equal value="<%=TranslationMappingConfigConstants.RESPONSE_PARAMETERS%>"  name="translationMappingInstDetailDataBean" property="mappingTypeId">
            		<tr>
            			<td class="tblfirstcol" height="20%"><%=resTempIndex%></td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="checkExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="mappingExpression"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="defaultValue"/>&nbsp;</td>
						<td align=left class=tblcol valign=top><bean:write name="translationMappingInstDetailDataBean" property="valueMapping"/>&nbsp;</td>
            		</tr>
            		<%resTempIndex++; %>
            		</logic:equal>
            		</logic:iterate>
            		<%if(resTempIndex==1){ %>
            				<tr>
				            	<td class="tblfirstcol" colspan="5" align="center" valign=top>No Records Found.</td>
				            </tr>
            		<% }%>
            		
            	</table>
            </td>
          </tr>
          </table>
          
          </td>
     </tr>
     
     <tr>
    	<td align=left class=tblheader-bold valign=top colspan="2">
    		<bean:message bundle="servermgrResources" key="translationmapconf.dummyresponseparams"/> 
       	</td>
    </tr>
    <tr>
    	<td colspan="2">
    		<table width="100%" border="0" cellspacing="0" cellpadding="0" >
				    <tr>
					    	<td  align=left class='tblfirstcol' valign=top colspan=1><bean:message bundle="servermgrResources" key="translationmapconf.dummyresponseparams"/> </td>
					    	<td class="tblcol" width="70%" height="20%" >
				    	    <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
				    	    	<tr>
				    	    		<td class="tblheader"  height="20%"><bean:message key="general.serialnumber"/></td>
									<td class="tblheader"  height="20%"><bean:message bundle="servermgrResources" key="translationmapconf.outfield"/></td>
									<td class="tblheader"  height="20%"><bean:message bundle="servermgrResources" key="translationmapconf.value"/></td>
				    	    	</tr>
								<%
								int dummyIndex=1;
								if(Collectionz.isNullOrEmpty(translationMapppingConfigBean.getDummyResponseParameterDataList()) == false) {%>		    	    
				    	    	<logic:iterate id="dummyResponseParameterDataBean"  name="translationMapppingConfigBean" property="dummyResponseParameterDataList">
				            		<tr>
				            			<td class="tblfirstcol" align=left valign=top><%=dummyIndex%></td>
										<td align=tblrows class="tblrows" valign=top><bean:write name="dummyResponseParameterDataBean" property="outField"/>&nbsp;</td>
										<td align=tblrows class="tblrows" valign=top><bean:write name="dummyResponseParameterDataBean" property="value"/>&nbsp;</td>
				            		</tr>
				            		<%dummyIndex++; %>
				           		</logic:iterate>
				           		<%}else{%>
				           			<tr>
				            			<td class="tblfirstcol" colspan="3" align="center" valign=top>No Records Found.</td>
				            		</tr>
				            	<%} %>
				           		
				        	</table>
				     	</td>
				     </tr>
    		</table> 
       	</td>
    </tr>
   
    <tr>
		<td class="small-gap" colspan="2">&nbsp;</td>
   </tr>
</table>

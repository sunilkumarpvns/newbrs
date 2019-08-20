<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   
   <bean:define id="translationMapppingConfigBean" name="translationMappingConfData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.transmapconf.data.TranslationMappingConfData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="translationmapconf.view"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.name" /></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="translationMapppingConfigBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message  key="general.description" /></td>
            <td class="tblcol" width="70%" height="20%"><%=EliteUtility.formatDescription(translationMapppingConfigBean.getDescription()) %>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="translationmapconf.script" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="translationMapppingConfigBean" property="script"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="translationmapconf.translationtype" /></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="translationMapppingConfigBean" property="translatorTypeFrom.name"/>-<bean:write name="translationMapppingConfigBean" property="translatorTypeTo.name"/></td>
          </tr>
		</table>
		</td>
    </tr>
</table>

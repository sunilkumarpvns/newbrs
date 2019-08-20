<%@page import="com.elitecore.elitesm.util.EliteUtility"%>
<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0"
	<bean:define id="copyPacketConfigBean" name="copyPacketMappingConfData" scope="request" type="com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData" />
    <tr> 
      <td valign="top" align="right"> 
        <table width="100%" border="0" cellspacing="0" cellpadding="0" height="15%" >
          <tr> 
            <td class="tblheader-bold" colspan="2" height="20%"><bean:message bundle="servermgrResources" key="copypacket.viewcopypacketbasicdetail"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="copypacket.name"/></td>
            <td class="tblcol" width="70%" height="20%" ><bean:write name="copyPacketConfigBean" property="name"/></td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="copypacket.description"/></td>
            <td class="tblcol" width="70%" height="20%"><%=EliteUtility.formatDescription(copyPacketConfigBean.getDescription()) %>&nbsp;</td>
          </tr>
          <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="copypacket.view.basicdetail.script"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="copyPacketConfigBean" property="script"/>&nbsp;</td>
          </tr>
           <tr> 
            <td class="tblfirstcol" width="30%" height="20%"><bean:message bundle="servermgrResources" key="copypacket.view.basicdetail.type"/></td>
            <td class="tblcol" width="70%" height="20%"><bean:write name="copyPacketConfigBean" property="translatorTypeFrom.name"/>-<bean:write name="copyPacketConfigBean" property="translatorTypeTo.name"/></td>
          </tr>
		</table>
		</td>
    </tr>
</table>

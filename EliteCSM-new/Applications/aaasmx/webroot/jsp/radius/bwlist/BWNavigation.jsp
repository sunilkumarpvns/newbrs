<%@page import="com.elitecore.elitesm.datamanager.radius.bwlist.data.BWListData"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td colspan="2" valign="top">
         <% String navigationBasePath = request.getContextPath(); 
         	
     		String updateBWList  = navigationBasePath+"/updateBWList.do?bwId="+bwListData.getBwId();
    		String viewBWList	 = navigationBasePath+"/viewBWList.do?bwId="+bwListData.getBwId();
    		String viewHistory   = navigationBasePath+"/viewBWListHistory.do?bwId="+bwListData.getBwId()+"&auditUid="+bwListData.getAuditUid()+"&name="+bwListData.getAttributeId();

         %>
         <table border="0" width="100%" cellspacing="0" cellpadding="0">
            <tr id=header1>
               <td class="subLinksHeader" width="87%">
                  <bean:message key="general.action" />
               </td>
               <td class="subLinksHeader" width="13%">
                  <a href="javascript:void(0)" onClick="STB('UpdateBW');swapImages()">
                 	 <img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow" />
                  </a>
               </td>
            </tr>
            <tr valign="top">
               <td colspan="2" id="backgr1">
                  <div>
                     <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                           <td class="subLinks">
                              <a href="<%=updateBWList%>">
                                 <bean:message bundle="radiusResources" key="radius.bwlist.updatebasicdetails" />
                              </a>
                           </td>
                        </tr>
                     </table>
                  </div>
               </td>
            </tr>
            <tr id=header1>
               <td class="subLinksHeader" width="87%">
                  <bean:message key="general.view" />
               </td>
               <td class="subLinksHeader" width="13%">
               	  <a href="javascript:void(0)" onClick="STB('ViewBW');swapImages()">
               	  	<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg"  border="0" name="arrow">
               	  </a>
               </td>
            </tr>
            <tr valign="top">
               <td colspan="2" id="backgr1">
                  <div>
                     <table width="100%" border="0" cellspacing="0" cellpadding="0">
                         <tr>
                           <td class="subLinks">
                              <a href="<%=viewBWList%>">
                                 <bean:message bundle="radiusResources"  key="radius.bwlist.viewbasicdetails" />
                              </a>
                           </td>
                        </tr>
                        <tr>
                           <td class="subLinks">
                              <a href="<%=viewHistory%>">
                                 <bean:message bundle="radiusResources" key="radius.bwlist.viewhistory" />
                              </a>
                           </td>
                        </tr> 
                     </table>
                  </div>
               </td>
            </tr>
         </table>
      </td>
   </tr>
</table>


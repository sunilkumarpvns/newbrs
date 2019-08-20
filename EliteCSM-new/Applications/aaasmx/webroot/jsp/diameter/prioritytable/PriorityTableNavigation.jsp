<table width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td colspan="2" valign="top">
         <%
            String navigationBasePath = request.getContextPath();
            String updatePriorityTable = navigationBasePath + "/updatePriorityTableConfig.do";
            String viewPiorityTableHistory = navigationBasePath+"/viewPriorityTableHistory.do";
          %>
         <table border="0" width="100%" cellspacing="0" cellpadding="0">
            <tr id=header1>
               <td class="subLinksHeader" width="87%">
                  <bean:message key="general.action" />
               </td>
               <td class="subLinksHeader" width="13%">
                  <a href="javascript:void(0)">
                  		<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
                  </a>
               </td>
            </tr>
            <tr valign="top">
               <td colspan="2" id="backgr1">
                  <div>
                     <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                           <td class="subLinks">
                              <a href=<%=updatePriorityTable%> class="subLink">
                                 <bean:message bundle="servicePolicyProperties" key="servicepolicy.authpolicy.update.prioritytable" />
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
                  <a href="javascript:void(0)" onClick="STB('ViewRadiusPolicy');swapImages()">
                  		<img src="<%=navigationBasePath%>/images/sublinks-dnarrow.jpg" border="0" name="arrow">
                  </a>
               </td>
            </tr>
            <tr valign="top">
               <td colspan="2" id="backgr1">
                  <div>
                     <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                           <td class="subLinks">
                              <a href=<%=viewPiorityTableHistory %> class="subLink">
                                 <bean:message key="view.history" />
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
<script language="javascript" src="<%=request.getContextPath()%>/js/commonfunctions.js"></script>
<%@page import="com.sun.swing.internal.plaf.basic.resources.basic"%>
<% String dashboardInitTime=(String)session.getAttribute("dashboardInitTime");%>
<tr id="frameHeader">
	<td colspan="6" >
		<div> 
        <table cellpadding="0" cellspacing="0" border="0" width="100%" >
          	<tr>
          		<td style="padding-left: 7px;padding-bottom: 5px;"></td>
	      		<td width="100%" align="left" style="min-width: 780px;padding-bottom: 5px;">
	      		      <table width="98%" border="0" cellspacing="0" cellpadding="0"> 
				        <tr> 
				          <td width="26" valign="top" rowspan="2"><img src="<%=basePath%>/images/left-curve.jpg"></td> 
				          <td width="133" rowspan="2" align="center" >
				       			<div id="headerTitle" class="page-header" align="middle" style="background-image: url('<%=basePath%>/images/header-gradient.jpg');display:inline-block;height:31px !important;width:133px;vertical-align: middle;line-height: 28px;">
				       			</div>
				          </td> 
				          <td width="32" rowspan="2"><img src="<%=basePath%>/images/right-curve.jpg"></td> 
				         <td width="*"></td> 
				        </tr> 
				        <tr> 
				          	<td width="*" valign="bottom"><img src="<%=basePath%>/images/line.jpg" style="width:100%;"  height="7" ></td> 
				        </tr> 
				      </table> 
	      		</td>
			</tr>
        </table>
        </div>
      </td>
	 </tr>	

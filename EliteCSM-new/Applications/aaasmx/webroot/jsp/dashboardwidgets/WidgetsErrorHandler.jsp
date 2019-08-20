<html>
<%String errorClass="",errorImage="";
 String traceLog=request.getParameter("traceLog");
 String widgetIds=request.getParameter("widgetIds");
 String widgetDivId=request.getParameter("widgetDivId");
 
 System.out.println("widget Div Id : " +widgetDivId);
 
 if(traceLog == null){
	 traceLog = "Unknown Error";
 }
 String errorMsg=request.getParameter("errorMessgae");
 if(errorMsg == null){
	 errorMsg = "Unknown Error";
 }
%> 
<head>
<script type="text/javascript">
var errorCase='<%=request.getParameter("errorPriority")%>';
var errorClass='',errorImagePath='';
switch(errorCase)
{
	case 'INFO':
		errorClass="priority-info";
		errorImagePath="<%=request.getContextPath()%>/images/Info-bubble.png";
		break;
	case 'BLOCKER':
		errorClass="priority-blocker";
		errorImagePath="<%=request.getContextPath()%>/images/icons/priority_blocker.gif";
		break;
	case 'MAJOR':
		errorClass="priority-major";
		errorImagePath="<%=request.getContextPath()%>/images/icons/priority_major.gif";
		break;
	case 'MINOR':
		errorClass="priority-minor";
		errorImagePath="<%=request.getContextPath()%>/images/icons/priority_minor.gif";
		break;
	case 'TRIVAL':
		errorClass="priority-trival";
		errorImagePath="<%=request.getContextPath()%>/images/icons/priority_trivial.gif";
		break;
	default:
		errorClass="priority-major";
		errorImagePath="<%=request.getContextPath()%>/images/icons/priority_major.gif";
		break;
};

$('.errorTable').removeClass("errorClass");
$('.errorTable').addClass(errorClass);
$('.errorImage').attr("src", errorImagePath);

</script>
</head>
<body>
<div id="errorClassDiv" style="width: 100%;display: block;overflow: auto;" align="left" class="<%=errorClass%>">
<table  id="errorTable" class="errorTable" width="100%" style="text-align: left;" align="left">
<tr>
	<td style="vertical-align: top;" width="2%">
		<img alt="Severity" class="errorImage"/>
	</td>
	<td class="error-Header-Text" width="98%" align="left" style="margin-left: 0;">
		<span style="margin-left: 0;text-align: left;">
			<%=errorMsg%>
		</span>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
</tr>
<tr>
	<td class="priority-style" colspan="2">
		<ul>
			<li>
				<%=traceLog %>
			</li>
		</ul>
	</td>
</tr>
 </table>
 </div>
 </body>
 </html>

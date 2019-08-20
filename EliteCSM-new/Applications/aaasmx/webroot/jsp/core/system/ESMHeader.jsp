<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm" %>
<%@ page import="com.elitecore.elitesm.web.core.system.cache.ConfigManager" %>
<%@ page import="com.elitecore.elitesm.util.constants.ConfigConstant" %>

<%
	String strStaffName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
    String basePath = request.getContextPath();
%>
<html>
<head>
<title>EliteCSM - Server Manager</title>
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/mllnstyles.css" >
<LINK REL ="stylesheet" TYPE="text/css" HREF="<%=basePath%>/css/mllnbillling.css" >

<script language="javascript" src="<%=basePath%>/js/commonfunctions.js"></script>
<script language="javascript" src="<%=basePath%>/js/openhelp.js"></script>
<script language="JavaScript">
<!--
//alert(history.length);
var initlen=history.length;
function backbtn(src,clicked)
{
	if (history.length>2)
	{
		src.style.cursor="hand";
		MM_swapImage('Image2','','<%=basePath%>/images/back-hover.jpg',1)
		
		if (clicked)
		{
			history.go(-1);
		}
	}
}

function forwardbtn(src,clicked)
{
	if (history.length>3)
	{
		src.style.cursor="hand";
		MM_swapImage('Image3','','<%=basePath%>/images/forward-hover.jpg',1)
	}
	if (clicked)
	{
		history.go(1);
	}
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

function MM_callJS(jsStr) { //v2.0
  return eval(jsStr)
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}
function isDSConfigured(){
    var response;
    $.ajax({url:'<%=basePath%>/dashboard.do?method=isDSConfigured',
          type:'POST',
          async:false,
          success: function(transport){
             response=transport;
         }
   	});
	if(response!=null){
		if(response == 'true'){
			$("#dashboardHomeLink").attr("href", "<%=basePath%>/dashboard.do?method=getDashboardData");
		}else if(response == 'false'){
			alert('Kindly Configure datasource in Dashboard Configuration and try again..');
			$("#dashboardHomeLink").attr("href", "<%=basePath%>/dashboardConfiguration.do?method=viewDashboardConfiguration");
		}
	}
}
</script>
</head>
<body bgcolor="#FFFFFF" text="#000000" style="width: 100%;" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="MM_preloadImages('<%=basePath%>/images/print.jpg','<%=basePath%>/images/aboutus.jpg','<%=basePath%>/images/help.jpg','<%=basePath%>/images/home-hover.jpg','<%=basePath%>/images/back-hover.jpg','<%=basePath%>/images/forward-hover.jpg','<%=basePath%>/images/refresh-hover.jpg','<%=basePath%>/images/stop-hover.jpg','<%=basePath%>/images/print-hover.jpg','<%=basePath%>/images/aboutus-hover.jpg','<%=basePath%>/images/help-hover.jpg','<%=basePath%>/images/home-hover.jpg','<%=basePath%>/images/forward-hover.jpg','<%=basePath%>/images/refresh-hover.jpg','<%=basePath%>/images/stop-hover.jpg','<%=basePath%>/images/print-hover.jpg','<%=basePath%>/images/aboutus-hover.jpg','<%=basePath%>/images/help-hover.jpg')" onhelp="openHelpPage();return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0"  >
  <tr> 
    <td background="<%=basePath%>/images/header-bkgd.jpg" width="44" valign="top" align="left" height="20"><img src="<%=basePath%>/images/header-curve.jpg"></td>
	<td background="<%=basePath%>/images/header-bkgd.jpg"  valign="bottom" align="center" class="blue-text-bold" height="20">Welcome <%=strStaffName%></td>
	<td valign="top" width="34" height="20"><img src="<%=basePath%>/images/left-headercurve.jpg"></td>
    <td background="<%=basePath%>/images/header-curve-bkgd.jpg" width="222" height="20"> 
      <table width="98%" border="0" cellspacing="3" cellpadding="0">
        <tr> 
          <td align="center"><a id="dashboardHomeLink" href="" onclick="isDSConfigured();" TARGET=mainFrame ><img src="<%=basePath%>/images/home.jpg" alt="Home" border="0" name="Image1" onMouseOver="MM_swapImage('Image1','','<%=basePath%>/images/home-hover.jpg',1)" onMouseOut="MM_swapImgRestore()" ></a></td>
          <td><img src="<%=basePath%>/images/back.jpg" alt="Back" name="Image2" border="0" onMouseOver="backbtn(this,false)" onMouseOut="MM_swapImgRestore()" onClick="history.go(-1);"></td>
          <td><img src="<%=basePath%>/images/forward.jpg" alt="Forward" border="0" name="Image3"  onMouseOver="forwardbtn(this,false)" onMouseOut="MM_swapImgRestore()" onClick="history.go(1);"></td>
          <td><a href="javascript:void(0)" onclick="top.mainFrame.location.reload()"><img src="<%=basePath%>/images/refresh.jpg" alt="Refresh" border="0" name="Image4" onMouseOver="MM_swapImage('Image4','','<%=basePath%>/images/refresh-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"></a></td>
          <td><a href="#"><img src="<%=basePath%>/images/stop.jpg" alt="Stop" border="0" name="Image5" onMouseOver="MM_swapImage('Image5','','<%=basePath%>/images/stop-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"></a></td>
          <td><a href="javascript:void(0)" onclick="top.mainFrame.window.focus();top.mainFrame.window.print()"><img src="<%=basePath%>/images/print.jpg" alt="Print" border="0" name="Image6" onMouseOver="MM_swapImage('Image6','','<%=basePath%>/images/print-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"></a></td>
          <td><a href="<%=basePath%>/jsp/core/includes/common/About.jsp" target="mainFrame"><img src="<%=basePath%>/images/aboutus.jpg" alt="About" border="0" name="Image7" onMouseOver="MM_swapImage('Image7','','<%=basePath%>/images/aboutus-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"></a></td>
          <td><a href="<%=basePath%>/initHelp.do" target="mainFrame" ><img src="<%=basePath%>/images/help.jpg" alt="Help" border="0" name="Image8" onMouseOver="MM_swapImage('Image8','','<%=basePath%>/images/help-hover.jpg',1)" onMouseOut="MM_swapImgRestore()"></a></td>    
        </tr>
      </table>
    </td>
  </tr>
  <%-- 	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%> --%>
</table>
</body>
</html>

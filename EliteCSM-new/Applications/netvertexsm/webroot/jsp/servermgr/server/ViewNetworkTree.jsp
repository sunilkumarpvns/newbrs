<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.tree.VNetworkTreeHelper" %>
<%@ page import="com.elitecore.netvertexsm.datamanager.servermgr.data.tree.*" %>
<%@page import="java.util.TreeMap" %>
<%@taglib uri="treetag.tld" prefix="tree" %>
<%@ page import="java.util.HashMap" %>

<!--[if lt IE 8]>
<script src="<%=request.getContextPath()%>/javascript/ie8.js" type="text/javascript"></script>
<![endif]-->

<script type="text/javascript" src="<%=request.getContextPath()%>/javascript/ballons/js/balloon.config.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/javascript/ballons/js/balloon.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/javascript/ballons/js/box.js"></script>
 <script type="text/javascript" src="<%=request.getContextPath()%>/javascript/ballons/js/yahoo-dom-event.js"></script> 
 <script type="text/javascript" src="<%=request.getContextPath()%>/javascript/ballons/js/toggle.js"></script>
 
 <script type="text/javascript">
   // white balloon with default configuration
   // (see http://www.wormbase.org/wiki/index.php/Balloon_Tooltips)
   var balloon    = new Balloon;
   //balloon.allowEventHandlers = true;
   //balloon.allowScripts       = true;
   // plain balloon tooltip
   var tooltip  = new Balloon;
   BalloonConfig(tooltip,'GPlain');

   // fading balloon
   var fader = new Balloon;
   BalloonConfig(fader,'GFade'); 
   //fader.allowEventHandlers = true; 
   //fader.allowScripts = true;

   // a plainer popup box
   var box         = new Box;
   BalloonConfig(box,'GBox');

   // a box that fades in/out
   var fadeBox     = new Box;
   BalloonConfig(fadeBox,'GBox');
   fadeBox.bgColor     = 'black';
   fadeBox.fontColor   = 'white';
   fadeBox.borderStyle = 'none';
   fadeBox.delayTime   = 50;
   fadeBox.allowFade   = true;
   fadeBox.fadeIn      = 750;
   fadeBox.fadeOut     = 200;

 </script>

<script language="JavaScript" src="<%=request.getContextPath()%>/javascript/textarealimit.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/javascript/utilities.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/javascript/cyberoam.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/javascript/multiselectpicklist.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/javascript/treeview.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/javascript/ajax.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/css/cyberoam.css">
<script language="JavaScript"> 
	var t;	
	var isIE = /*@cc_on!@*/false; // IE detector
	
	function getCapacity(){
		addCapacityToPage("15");				
	}
	
	function f1(){
			t=setInterval("getCapacity()",60000);	
		}
			
	//function for add,update,delete and detialview of treenode
	function checkAdd(nodeid,locationid,nodename){		 
			window.location.href="<%=request.getContextPath()%>/webpages/networkrepomgt/adddevicenode.jsp?nodeid="+nodeid+"&locationid="+locationid;		
	}
	
	function checkUpdate(nodeid,locationid){		 
			window.location.href="<%=request.getContextPath()%>/webpages/networkrepomgt/editdevicenode.jsp?nodeid="+nodeid+"&locationid="+locationid;		
	}
	
	function checkDelete(nodeid,locationid,nodename){						
		frm = document.frmtree;
		var con = confirm("Are you sure to Delete Node - "+nodename+" ?");
		if (con){ 
			frm.id.value=nodeid;
			frm.submit();
			return true ;
		}else{ 
			return false;
		}
	}
		
	function viewDetails(Nodeid){	
		var page = 	'<%=request.getContextPath()%>/webpages/networkrepomgt/treenodedetails.jsp?nodeid='+Nodeid;
		window.open(page, 'NodeDetails', 'width=400,height=450,resizable=o,scrollbars=yes');
	}	
	
	//function uploadConfig(Nodeid){	
	//	var page = 	'<%=request.getContextPath()%>/webpages/networkrepomgt/uploadConfig.jsp?nodeid='+Nodeid;
	//	window.open(page, 'Upload SCE ConfigFile', 'width=400,height=450,resizable=o,scrollbars=yes');
	//}
	
	function test(){
		var temp = new Array();
		var i =0;
		<%
				for(int j=0;j<2;j++){
					
		%>
					temp[i++] = 'ahmedabad' + i;
		<%
					}
		%>
		var attr = new Array();
		var l=0;
		var ele = document.getElementsByTagName('td');
		
		for (var j = 0; j < ele.length; j++) { 
    		for(k=0;k<temp.length;k++){
    			if((ele[j].innerHTML).indexOf('&nbsp;'+temp[k]) != -1){
    				if(ele[j].getElementsByTagName('img')[0].getAttribute('id') != null
    					&& ele[j].getElementsByTagName('img')[0].getAttribute('id').indexOf('itrtcntr')!=-1){
    					var flag = 1;
    					for(var m=0;m<l;m++){
    						if(ele[j].getElementsByTagName('img')[0].getAttribute('id') == attr[m]){
    							flag = 0;
    							break;
    						}
    					}
    					if(flag == 1){
    						attr[l++] = ele[j].getElementsByTagName('img')[0].getAttribute('id');
    					}
    				}
    			}
    			//if(('&nbsp;'+temp[k]) == ele[j].getText()){
    			//	attr[l++] = ele[j].getElementsByTagName('img').getAttribute('id');
    			//}
    		}
		}
		for(var x=0;x<attr.length;x++){
			if(attr[x]!='itrtcntr1'){
				alert('called');
				swapFolder(attr[x]);
			}
		}
	}	
	
	var em;
	function getNodeDetails(nodeid,event){
		//if(!isIE)
			em = rebuildEvent(event);
			getDetailsRequest('a',nodeid);
	}	
	$(document).ready(function(){
		setTitle('<bean:message bundle="servermgrResources" key="servermgr.networktree"/>');
	});
</script>




<TITLE>tree</TITLE> 
<!-- MAIN TABLE START TAG-->
<!--<TABLE border="0" cellpadding="0" cellspacing="0" height=100% width=100%>


<TR>
<TD>

--><!-- PUT THE LOGIC OF YOUR PAGE HERE -->



	
<TABLE cellpadding="0" cellspacing="0" border="0" width="100%" >
<%
//Start : Message to display
String strFontclass = "note";

//End :  Message to display
%>
	<%@ include file="/jsp/core/includes/common/HeaderBar.jsp"%>
	<form name="frmtree" action="<%=request.getContextPath()%>/servlet/NetworkEquipmentsManager" method=post onsubmit="return validate();" >
	

<TR>
	
	<TD style="width: 100%" colspan="2" valign="top" class="box">	
	<table cellSpacing="0" cellPadding="0" width="100%" border="0">
    	<tr   >
    	<td class="table-header" colspan="5">Network Repository Current Summary
    	</td>
    	
        </tr>             		
<TR>
	
	<TD  >	

				<%
					String path=request.getContextPath();					
					
					//TreeMap<String,TreeBean> recordMap = VNetworkTreeHelper.getTreeViewMap(request);
					TreeMap<String,TreeBean> recordMap;
						recordMap = VNetworkTreeHelper.getTreeViewMap(request,null);
					/*if(GeneralUtilities.isSuperAdmin(strAdminLocationid)){
						recordMap = VNetworkTreeHelper.getTreeViewMap(request);
					}else{
						recordMap = VNetworkTreeHelper.getTreeViewMapForLocationWiseSearch(request,strAdminLocationid);
					}*/
					
					TreeBean root = recordMap.get("0-0");
					/*					
					//TreeBean nodes[] = new TreeBean[size];
					
					for(TreeBean infoNode : recordMap.values()){
						if(infoNode.get == null){
												
						} else {
					
							
						}
					
					}*/
										
				 %>

				 <tree:createTree defaultConnectors="false"
				 			treePicture='<%= path+"/images/tree/plus_arrow.gif"%>' 				 							 							 							 							 							 			
				 			openPicture='<%= path+"/images/tree/minus_arrow.gif"%>'
				 			dhtml="true"
							modelBean="<%=root%>" id="mytree" className="tabletext" 
							>
				</tree:createTree> 
 				
				
			</TD>
			</TR>
			
			
			<tr> 
   
    <td colspan="2"> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="26" valign="top" rowspan="2"></td>
          <td </td>
          <td </td>
          <td </td>
        </tr>
        <tr> 
          <td ></td>
        </tr>
      </table>
    </td>
  </tr>
			<tr>
		
			<td><center>
			<input type="button" name="c_btnPushConfiguration"
								onclick=""
								value="Push Policy" class="light-btn" />
			</center>
			</td>
			</tr>
    </table>
    </TD>	
    </tr>
	</form>
<!--</TD>
</TR>
-->


<%@ include file="/jsp/core/includes/common/Footerbar.jsp"%>
</table>

<!--  verticalConnector="<%--= path+"/images/tree/vertical.gif"--%>"  Content TABLE ENDS here -->
<!--</TD>
</TR>
</TABLE>-->
	<script type="text/javascript">
		f1();
	</script>
<!-- MAIN TABLE ENDS here -->


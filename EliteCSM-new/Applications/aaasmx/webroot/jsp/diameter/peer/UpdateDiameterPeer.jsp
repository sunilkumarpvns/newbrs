<%@ include file="/jsp/core/includes/common/Header.jsp"%>
<%@ page import="com.elitecore.elitesm.datamanager.radius.dictionary.data.DictionaryData"%>
<%@ page import="com.elitecore.elitesm.util.constants.InstanceTypeConstants"%>
<%@ page import="com.elitecore.elitesm.web.diameter.peer.forms.DiameterPeerForm"%>
<%@ page import="com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerData"%>
<%@ page import="java.util.List"%>
<%
DiameterPeerForm diameterPeerForm=(DiameterPeerForm)request.getAttribute("diameterPeerForm");
%>
<script type="text/javascript" language="javascript" src="<%=request.getContextPath()%>/expressionbuilder/expressionbuilder.nocache.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/js/expressionbuilder.js"></script>
<script>
setExpressionData("Diameter");
	var isValidName;
	var isValidHostIdentity;
	function validatePort(txt){
		// check for valid numeric port	 
		if(IsNumeric(txt) == true){
			if(txt >= 0 && txt<=65535)
				return(true);
		}else
			return(false);
	}

	function validateIpAddress(ipAddress){
		var flagIp=false,flagPort=false,validIP=false;
		
		var ipAds = ipAddress.split(":").length - 1;
		if(ipAds==1){
			 var strIpAddress=ipAddress.split(":");
			 if(! strIpAddress[0]){
					flag=false;
			 }else if(! strIpAddress[1]){
				    	flag=false;
			 }else{
			    	flagIp=validateIP(strIpAddress[0]);
			    	flagPort=validatePort(strIpAddress[1]);
			 }
		}else if(ipAds>1){
			var firstCut=null,secondCut=null,finalResult=null,validPort=null;
			
			   firstCut = ipAddress.split('[');
			    
			if(typeof firstCut[1] != 'undefined'){
			   	 secondCut = firstCut[1].split(']:'); 
			   	 finalResult = secondCut[0],validPort=secondCut[1];
		    }else{
			   	flagIp=false;
				flagPort=false;
			}
			
			 if(typeof firstCut[0] != 'undefined' && typeof firstCut[1] != 'undefined'  && typeof secondCut[0] != 'undefined' && typeof secondCut[1] != 'undefined'){
				flagIp=validateIP(finalResult);
		    	flagPort=validatePort(validPort);
			}else{
				flagIp=false;
				flagPort=false;
			}
		}
		    if(flagIp==true && flagPort==true){
		    	validIP=true;
		    }
		   return validIP;
	}
	
	function chkIpAddress(){
		var remoteAdd=document.forms[0].remoteAddress.value;
		var localAdd=document.forms[0].localAddress.value;
		var verifyIp=false;
		
		if(remoteAdd != ""){
			var validIp=validateIP(remoteAdd);
			if(validIp==false){
				var validIpPort=validateIpAddress(remoteAdd);
				if(validIpPort==false){
					var splitAddressCnt = remoteAdd.split("/").length - 1;
					var splitRange=remoteAdd.split("-").length -1;
					if(splitAddressCnt==1){
						var splitIp=remoteAdd.split("/");
						var validateSplitIP=validateIP(splitIp[0]);
						if(validateSplitIP == true){
							var verifyIpv4=validateIPv4(splitIp[0]);   
							if(verifyIpv4==true){
								var chkMask=validateIPv4NetMask(splitIp[1]);
								if(chkMask == true){
									verifyIp=true;
								}else{
									invalidRemoteAddress();
					return false;
								}
				}else{
								var chkMask=validateIPv6NetMask(splitIp[1]);
								if(chkMask == true){
					verifyIp=true;
								}else{
									invalidRemoteAddress();
									return false;
				}
							}
							
			}else{
							invalidRemoteAddress();
							return false;
						}
					}else if(splitRange >=1){
						if(splitRange == 1){
							var splitRangeAddress=remoteAdd.split("-"),verifyFirstRange,verifySecondRange;
							verifyFirstRange=validateIP(splitRangeAddress[0]);
							verifySecondRange=validateIP(splitRangeAddress[1]);
							if(verifyFirstRange == false || verifySecondRange == false){
								invalidRemoteAddress();
								return false;
							}else{
				verifyIp=true;
			}
						}else{
							verifyIp=true;
		}
					}else{
						invalidRemoteAddress();
						return false;
					}
				}else{
					verifyIp=true;
				}
			}else{
				verifyIp=true;
			}
		}else{
			verifyIp=true;
	  }
		
		
		if(localAdd != ""){
			var validIp=validateIP(localAdd);
			if(validIp==false){
				var validIpPort=validateIpAddress(localAdd);
				if(validIpPort==false){
					var splitAddressCnt = localAdd.split("/").length - 1;
					var splitRange=localAdd.split("-").length -1;
					if(splitAddressCnt==1){
						var splitIp=localAdd.split("/");
						var validateSplitIP=validateIP(splitIp[0]);
						if(validateSplitIP == true){
							var verifyIpv4=validateIPv4(splitIp[0]);   
							if(verifyIpv4==true){
								var chkMask=validateIPv4NetMask(splitIp[1]);
								if(chkMask == true){
									verifyIp=true;
								}else{
									invalidLocalAddress();
					return false;
								}
				}else{
								var chkMask=validateIPv6NetMask(splitIp[1]);
								if(chkMask == true){
					verifyIp=true;
								}else{
									invalidLocalAddress();
									return false;
				}
							}
							
			}else{
							invalidLocalAddress();
							return false;
						}
					}else if(splitRange >=1){
						if(splitRange == 1){
							var splitRangeAddress=localAddress.split("-"),verifyFirstRange,verifySecondRange;
							verifyFirstRange=validateIP(splitRangeAddress[0]);
							verifySecondRange=validateIP(splitRangeAddress[1]);
							if(verifyFirstRange == false || verifySecondRange == false){
								invalidLocalAddress();
								return false;
							}else{
				verifyIp=true;
			}
						}else{
							verifyIp=true;
		}
					}else{
						invalidLocalAddress();
						return false;
					}
				}else{
					verifyIp=true;
				}
			}else{
				verifyIp=true;
			}
		}else{
			verifyIp=true;
		}
	
		if(localAdd == "" && remoteAdd == ""){
			verifyIp=true;
		}
		
		function invalidRemoteAddress(){
			alert("Invalid Remote Address");
			document.forms[0].remoteAddress.focus();
			verifyIp=false;
		}
		
		function invalidLocalAddress(){
			alert("Invalid Local Address");
			document.forms[0].localAddress.focus();
			verifyIp=false;
		}
		return verifyIp;
	}


	function validateIPv4NetMask(mask)
	{
		if(mask>=0 && mask<=32){
			return true;
		}else{
			return false;
		}
	}

	function validateIPv6NetMask(mask){
		if(mask>=0 && mask<=128){
			return true;
		}else{
			return false;
		}
	}
	
	
	function validateIP(ipaddress){
		var ip=/((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;
			if(ip.test(ipaddress)){
				   return true;
			   }else{
				   return false;
			   }
	}
	function validateIPv4(ipaddress){
		var ipre=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)[.](25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
			if(ipre.test(ipaddress)){
				   return true;
		  	}else{
				  return false;
		}
	}

	function validateIPv4WithNetMask(ipaddress){
		var ip=(/((25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)/);
		if(ip.test(ipaddress)){
			   return true;
		   }else{
			   return false;
		   }
	}
function customValidate(form)
{
		var requestTimeout = $("#requestTimeout").val();
		if(isNull(document.forms[0].name.value)){
			alert("Peer name must be specified.");
			return false;	
		}else if(isNull(document.forms[0].realmName.value)){
			alert("Realm name must be specified.");
			return false;
		}else if(document.forms[0].hostIdentity.value == "" && document.forms[0].remoteAddress.value == ""){
			alert("Either Host Identity or Remote Address Must be Specified.");
			return false;	
		}else if(isNaN(Number(requestTimeout))){
			alert("Request Timeout must be Numeric.");
			document.forms[0].requestTimeout.focus();
			return false;
		}else if(isNull(requestTimeout)){
			alert("Request Timeout must be specified.");
			document.forms[0].requestTimeout.focus();
			return false;
		}else if(isNaN(Number($("#retransmissionCount").val()))){
			alert("Retransmission Count must be Numeric.");
			document.forms[0].retransmissionCount.focus();
			return false;
		}else if(((parseInt(requestTimeout) < 1000) || ((parseInt(requestTimeout) > 10000)))){
			alert("Request Time-out value should be between 1000 to 10000 .");
			document.forms[0].requestTimeout.focus();
			return false;
		}else if(((parseInt($("#retransmissionCount").val())) != 0) && ((parseInt($("#retransmissionCount").val()) < 0) || ((parseInt($("#retransmissionCount").val()) > 3)))){
			alert("Invalid value in Retransmission Count...!\nPossible values are : 0 or 0-3.");
			document.forms[0].retransmissionCount.focus();
			return false; 
		}else{
			$('#requestTimeout').val(parseInt(requestTimeout.trim()));
			
			if(!isValidName){
				alert('Enter Valid Peer Name');
				document.forms[0].name.focus();
				return false;
			}else if(validateDiameterPeerForm(form))
		    {
				var varifyIP=chkIpAddress();
				var remoteAddressString=document.forms[0].remoteAddress.value;
				if (remoteAddressString.indexOf('-') != -1) {
					if(!(isNull(document.forms[0].hostIdentity.value))){
						alert("Host Identity is not allowed with IP Range");
						document.forms[0].hostIdentity.focus();
					}else{
						if(varifyIP == true){
							return true; 
						}
					}
				}else if(remoteAddressString.indexOf('/') != -1) {
						if(!(isNull(document.forms[0].hostIdentity.value))){
							alert("Host Identity is not allowed with Subnetmask");
							document.forms[0].hostIdentity.focus();
						}else{
							if(varifyIP == true){
								return true; 
							}
						}
				}else{
					if(varifyIP == true){
						return true; 
					}
				}
				
				return false;
		    }
			else
			{
				return false;
			}	
		}
	}
	 function verifyName() {
			var searchName = document.getElementById("name").value;
			isValidName = verifyInstanceName('<%=InstanceTypeConstants.DIAMETER_PEER%>',searchName,'update','<%=diameterPeerForm.getPeerUUID()%>','verifyNameDiv');
	}
	 function verifyHostIdentity() {
			var searchHostIdentity = document.getElementById("hostIdentity").value;
			isValidHostIdentity = verifyHostIdentityName('<%=InstanceTypeConstants.DIAMETER_HOST_IDENTITY%>',searchHostIdentity,'update','<%=diameterPeerForm.getPeerUUID()%>','verifyHostIdentityDiv');
	}

</script>

<html:javascript formName="diameterPeerForm" />
<html:form action="/updateDiameterPeer"
	onsubmit="return customValidate(this);">
	<html:hidden name="diameterPeerForm" styleId="peerUUID" property="peerUUID" />
	<html:hidden name="diameterPeerForm" styleId="auditUId" property="auditUId" />
	<table cellpadding="0" cellspacing="0" border="0" width="100%"
		align="right">
		<tr>
			<td class="small-gap" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td valign="middle" colspan="5">
				<table cellpadding="0" cellspacing="0" border="0" width="100%"
					height="30%">
					<tr>
						<td align="left" class="tblheader-bold" valign="top" colspan="3">
							<bean:message bundle="diameterResources"
								key="diameterpeer.updatepeer" />
						</td>
					</tr>
					<tr>
						<td colspan="100%">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeer.name" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.peername" header="diameterpeer.name"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="1"
								styleId="name" property="name" size="30"  onblur="verifyName();"
								maxlength="256" style="width:250px" /><font color="#FF0000">
								*</font><div id="verifyNameDiv" class="labeltext">
						</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeer.hostidentity" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.hostidentity" header="diameterpeer.hostidentity"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="2"
								styleId="hostIdentity" property="hostIdentity" size="30" onblur="verifyHostIdentity();"
								maxlength="256" style="width:250px" /><font color="#FF0000"> *</font><div id="verifyHostIdentityDiv" class="labeltext"></div>
						</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" " valign="top" width="25%">
							<bean:message bundle="diameterResources"
							key="diameterpeer.realmname" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.realmname" header="diameterpeer.realmname"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="3"
								styleId="realmName" property="realmName" size="30"
								maxlength="256" style="width:250px" /><font color="#FF0000">
								*</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" " valign="top" width="25%">
							<bean:message bundle="diameterResources"
							key="diameterpeer.remoteaddress" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.remoteaddress" header="diameterpeer.remoteaddress"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="4"
								styleId="remoteAddress" property="remoteAddress" size="30"
								maxlength="100" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" " valign="top" width="25%">
							<bean:message bundle="diameterResources"
							key="diameterpeer.localaddress" />
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.localaddress" header="diameterpeer.localaddress" />
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="5"
								styleId="localAddress" property="localAddress" size="30"
								maxlength="100" style="width:250px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeer.uriformat" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.diameteteruriformat" header="diameterpeer.uriformat"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="6" styleId="diameterURIFormat" property="diameterURIFormat" size="30" maxlength="256"  style="width:500px" />
						</td>
				   </tr>
				   	<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeer.requesttimeout" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.requesttimeout" header="diameterpeer.requesttimeout"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="7" styleId="requestTimeout" property="requestTimeout" size="30" maxlength="100" style="width:100px" /><font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeer.retransmissioncount" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.retransmissioncount" header="diameterpeer.retransmissioncount"/>
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<html:text name="diameterPeerForm" tabindex="8" styleId="retransmissionCount" property="retransmissionCount" size="30" maxlength="100" style="width:100px" />
						</td>
					</tr>
					<tr>
						<td align="left" class="captiontext" valign="top" width="25%">
							<bean:message bundle="diameterResources" key="diameterpeer.secondarypeer" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.secondarypeer" header="diameterpeer.secondarypeer"/>
						</td>
						 
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<bean:define id="secondaryList" name="diameterPeerForm" property="secondaryPeerList"></bean:define>
							<html:select name="diameterPeerForm" tabindex="9" styleId="secondaryPeerName" property="secondaryPeerName" size="1"  style="min-width:120px;">
								<html:option value="">--Select--</html:option>
								<html:options collection="secondaryList" property="name" labelProperty="name" />
							</html:select> 
						</td>
					</tr>
					<tr>
						<td align="left" class="btns-td" " valign="top" width="25%">
							<bean:message bundle="diameterResources"
								key="diameterpeer.profilename" /> 
							<ec:elitehelp headerBundle="diameterResources" 
							text="diameterpeer.name" header="diameterpeer.profilename"/>	
						</td>
						<td align="left" class="labeltext" valign="top" nowrap="nowrap">
							<bean:define id="profileList" name="diameterPeerForm" property="peerProfileList"></bean:define> 
							<html:select name="diameterPeerForm" tabindex="10" styleId="peerProfileId" property="peerProfileId" size="1"  style="min-width:120px;">
								<html:option value="0">-----Select-----</html:option>
								<html:options collection="profileList" property="peerProfileId"
									labelProperty="profileName" />
							</html:select> <font color="#FF0000"> *</font>
						</td>
					</tr>
					<tr>
						<td class="btns-td" valign="middle">&nbsp;</td>
						<td class="btns-td" valign="middle" colspan="2"><input
							type="submit" name="c_btnUpdate" tabindex="11" id="c_btnUpdate"
							value="  Update  " class="light-btn"> <input
							type="button" name="c_btnCancel" tabindex="12"
							onclick="javascript:location.href='<%=basePath%>/viewDiameterPeer.do?peerUUID='+document.diameterPeerForm.peerUUID.value"
							value="  Cancel  " class="light-btn"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>

<div id="popupExpr" style="display: none;" title="ExpressionBuilder">
	<div id="expBuilderId" align="center"></div>
</div>
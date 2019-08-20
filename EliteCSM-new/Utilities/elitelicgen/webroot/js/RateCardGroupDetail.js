//This function will that all Combos are selected at the time of addition.
function checkAll()
{
	if(document.frmRateCardGroup.c_strAccounttype.selectedIndex == 0)
	{
		alert("Account Type Must be Selected for adding Parameters.");
		document.frmRateCardGroup.c_strAccounttype.focus();
		return false;
	}
	else if(document.frmRateCardGroup.c_strRadiusPolicy.selectedIndex == 0)
	{
		alert("Radius Policy Must be Selected for adding Parameters.");
		document.frmRateCardGroup.c_strRadiusPolicy.focus();
		return false;
	}
	else if(document.frmRateCardGroup.c_strRateCard.selectedIndex == 0)
	{
		alert("Rate Card Group Must be Selected for adding Parameters.");
		document.frmRateCardGroup.c_strRateCard.focus();
		return false;
	}
	return true;
}

function isDuplicateRadiusPolicy()
{
	strRadiusPolicyId = document.frmRateCardGroup.c_strRadiusPolicy.value;
	strRadiusPolicyName = document.frmRateCardGroup.c_strRadiusPolicy.options[document.frmRateCardGroup.c_strRadiusPolicy.selectedIndex].text;
	
	//Get the handle to Radius Policy Column for Radius policy and compare corrosponding 
	//Policy id with one selected.
	var TblRoot = document.getElementById("tblRCRPCombination").children[0];	
	for(i=1;i<document.frmRateCardGroup.SerialNumber.value;i++)
	{
		if(strRadiusPolicyId == TblRoot.children[i].children[1].children[0].value)
		{
			alert("Radius Policy already bounded.");
			return false;
		}
	}
	return true;
}


function removeRow(tblRow)
{
	var rowToRemove = tblRow.parentNode;
	var currValue = tblRow.parentNode.children[3].children[0].value;
	//alert("Current Value : " + currValue);
	var removeFrom = document.getElementById("tblRCRPCombination").children[0];
	removeFrom.removeChild(rowToRemove);
	//alert("Child node removed Successfully.");
	
	//Now reset the values for Respective child Nodes
	//Get each TR from the table and change the Name and Id Attributes for each
	newChildArray = removeFrom.childNodes;
	for(i=1;i<newChildArray.length;i++)
	{
		//Get the Tr at this position
		var currTR = newChildArray[i];
		var currTD;
		var currInput;
		//Now Get Each TD for this TR and modify it.
		
		//Serial Number
		currTD = currTR.children[0];
		//Now Get the input type text from this td
		currInput = currTD.children[0];
		currInput.name = 'SerialTxt' + i;
		currInput.id = 'SerialTxt' + i;
		currInput.value = i;

		//Radius Policy
		currTD = currTR.children[1];
		//Get input type Hidden for Radius Policy.
		currInput = currTD.children[0];
		currInput.name = 'c_strRCGRadiusPolicyId' + i;
		currInput.id = 'c_strRCGRadiusPolicyId' + i;
		//Get input type Text for Radius Policy.
		currInput = currTD.children[1];
		currInput.name = 'RCGRadPolTxt' + i;
		currInput.id = 'RCGRadPolTxt' + i;
		
		//Rate Card Column
		currTD = currTR.children[2];
		//Get input type Hidden for rate card.
		currInput = currTD.children[0];
		currInput.name = 'c_strRCGRateCardId' + i;
		currInput.id = 'c_strRCGRateCardId' + i;
		//Get input type Text for Rate Card.
		currInput = currTD.children[1];
		currInput.name = 'RCGRCName' + i;
		currInput.id = 'RCGRCName' + i;
		
/*		
		//Account Type
		currTD = currTR.children[3];
		//Get input type Hidden for Account Type.
		currInput = currTD.children[0];
		currInput.name = 'c_strRCGAccTypeId' + i;
		currInput.id = 'c_strRCGAccTypeId' + i;
		//Get input type Text for Account Type.
		currInput = currTD.children[1];
		currInput.name = 'RCGAccTypeTxt' + i;
		currInput.id = 'RCGAccTypeTxt' + i;
	*/	
		//Application Order.
		currTD = currTR.children[3];
		//Now Get the input type text from this td
		currInput = currTD.children[0];
		currInput.name = 'AppOrderTxt' + i;
		currInput.id = 'AppOrderTxt' + i;
		currInput.tabIndex=i;
		currInput.value = ((currValue<currInput.value?currInput.value-1:currInput.value));
		//alert("Setting This Value To" + currInput.value);
		
		//alert(removeFrom.innerHTML);
	}
	document.frmRateCardGroup.SerialNumber.value--;
}

function addRCGDetail()
{
	if(checkAll() && isDuplicateRadiusPolicy())
	{
		//alert("Hello World");
		strAccTypeId = document.frmRateCardGroup.c_strAccounttype.value;
		strAccTypeName = document.frmRateCardGroup.c_strAccounttype.options[document.frmRateCardGroup.c_strAccounttype.selectedIndex].text;
		//alert(strAccTypeId + "," + strAccTypeName);

		strRateCardId = document.frmRateCardGroup.c_strRateCard.value;
		strRateCardName = document.frmRateCardGroup.c_strRateCard.options[document.frmRateCardGroup.c_strRateCard.selectedIndex].text;
		//alert(strRateCardId + "," + strRateCardName);

		strRadiusPolicyId = document.frmRateCardGroup.c_strRadiusPolicy.value;
		strRadiusPolicyName = document.frmRateCardGroup.c_strRadiusPolicy.options[document.frmRateCardGroup.c_strRadiusPolicy.selectedIndex].text;
		//alert(strRadiusPolicyId + "," + strRadiusPolicyName);

		//Now get the handler for the Table 
		var tblRCRPCombo = document.getElementById("tblRCRPCombination");
		var currSerial = document.frmRateCardGroup.SerialNumber.value;
		var newTR = document.createElement("TR");
		var newTD;
/*		
		var newAT  =	"<input type='hidden' name='c_strRCGAccTypeId" + currSerial + 
							"' id='c_strRCGAccType" + currSerial + "' value='"+strAccTypeId+"' />" + 
							"<input type='text' name='RCGAccTypeTxt" + currSerial + 
							"' id='RCGAccTypeTxt" + currSerial + "' value='" + strAccTypeName + 
							"' class='text-noborders' onFocus='this.blur()' />";
*/		
		var newRP  = "<input type='hidden' name='c_strRCGRadiusPolicyId" + currSerial + 
							"' id='c_strRCGRadiusPolicy" + currSerial + "' value='"+strRadiusPolicyId+"' />" + 
							"<input type='text' name='RCGRadPolTxt" + currSerial + 
							"' id='RCGRadPolTxt" + currSerial + "' value='" + strRadiusPolicyName + 
							"' class='text-noborders' onFocus='this.blur()' size='30' />";
							
		var newRC  = "<input type='hidden' name='c_strRCGRateCardId" + currSerial + 
							"' id='c_strRCGRateCard" + currSerial + "' value='" + strRateCardId + "' />" + 
							"<input type='text' name='RCGRCName" + currSerial + 
							"' id='RCGRCName" + currSerial + "' value='" + strRateCardName + 
							"' class='text-noborders' onFocus='this.blur()' size='30' />";
		
		//First append Serial Number
		//alert(currSerial);
		newTD = document.createElement("TD");
		newTD.width = "12%"
		newTD.innerHTML = "<input type='text' name='SerialTxt" + currSerial + 
							"' id='SerialTxt" + currSerial + "' value='" + currSerial + 
							"' class='text-noborders' onFocus='this.blur()' size='3'/>";
		//alert(newTD.innerHTML);
		newTR.appendChild(newTD);

		//Add Radius Policy
		newTD = document.createElement("TD");
		newTD.width = "28%"
		//alert(newTD.innerHTML);
		newTD.innerHTML = newRP;
		//Now append TD to the tr.
		newTR.appendChild(newTD);
		
		//Add Rate Card
		newTD = document.createElement("TD");
		newTD.width = "25%"
		//Add Rate Card
		newTD.innerHTML = newRC;
		//alert(newTD.innerHTML);
		newTR.appendChild(newTD);
/*		
		//Add Account Type
		newTD = document.createElement("TD");
		newTD.innerHTML = newAT;
		//alert(newTD.innerHTML);
		newTR.appendChild(newTD);
*/		
		//Add Appliation Order.
		newTD = document.createElement("TD");
		newTD.width = "22%"
		newTD.innerHTML = "<input type='text' name='AppOrderTxt" + currSerial + 
							"' id='AppOrderTxt" + currSerial + "' value='" + currSerial + 
							"' size='3' maxlength='3' + class='labeltext' tabindex='" + currSerial + "'/>";
		//alert(newTD.innerHTML);
		newTR.appendChild(newTD);
		
		//Add Remove Button.
		newTD = document.createElement("TD");
		newTD.width = "5%"
		newTD.innerHTML = "<a href='javascript:void(0)' onclick='removeRow(this.parentNode)'>" + 
						"<img src='/images/minus.jpg' alt='Remove' border='0' align='center'>" +
						"</a>";
							
		//alert(newTD.innerHTML);
		newTR.appendChild(newTD);
		
		//alert(newTR.innerHTML);
		tblRCRPCombo.children[0].appendChild(newTR);
		
		document.frmRateCardGroup.SerialNumber.value = ++currSerial;
	}
}

function validateApplicationOrder()
{
	//Get the handle to Radius Policy Column for Radius policy and compare corrosponding 
	//Policy id with one selected.
	var TblRoot = document.getElementById("tblRCRPCombination").children[0];	
	var numString = '';
	var strMsg = '';
	//alert(document.frmRateCardGroup.SerialNumber.value);
	for(i=1;i<document.frmRateCardGroup.SerialNumber.value;i++)
	{
		var currOrder = TblRoot.children[i].children[3].children[0].value;
		//if currOrder is a valid number
		//if currOrder is less then curent value of serial number
		//if currOrder is not repeated previously in any other field.
		//if app. Order is 1 then Radius Policy must be default.
		//then return true
		//alert(parseInt(currOrder) > 0 && parseInt(currOrder < (document.frmRateCardGroup.SerialNumber.value)));
		if(!isNaN(currOrder) && (currOrder.lastIndexOf(".")==-1))//Valid Number
		{
			if(parseInt(currOrder) > 0 && (parseInt(currOrder) < parseInt(document.frmRateCardGroup.SerialNumber.value)))//Not Greater then max Allowed
			{
				if(numString.lastIndexOf(currOrder)!=-1)//App. Order is unique.
				{
					strMsg += "Sr. no.  " + i + " :  This Application Order is already applied.\n";
				}
				else
				{
					//Check that default Radius Policy has Application Order 1.
					var TblRoot = document.getElementById("tblRCRPCombination").children[0];	
					if(isDefaultRadiusPolicy(TblRoot.children[i].children[1].children[0].value))//If policy is Default Policy
					{
						if(currOrder != 1)//If Application Order is 1
						{
							alert("Default Radius Policy must have Application Order 1.");
							TblRoot.children[i].children[3].children[0].focus();//Set Focus on Application Order text.
							return false;
						}
						else
							numString += '' + currOrder + ':';
					}
					else //Not a Default Policy
					{
						if(currOrder == 1)//Application Order Cannot be 1
						{
							alert("Only Default Radius Policy can have Application Order 1.");
							TblRoot.children[i].children[3].children[0].focus();//Set Focus on Application Order text.	
							return false;
						}
						else
							numString += '' + currOrder + ':';
					}
				}
			}
			else
			{
				strMsg += "Sr. no.  " + i + " :  Application Order must be between 0 to " + frmRateCardGroup.SerialNumber.value + "\n";
			}
		}
		else
		{
			strMsg += "Sr. no.  " + i + " : Application Order should be a valid Integer.\n";
		}
	}
	
	if(strMsg == '')
	{
		return true;
	}
	else
	{
		alert(strMsg);
		return false;
	}
}

function validateApplicationOrderForSSTP()
{
	//Get the handle to Radius Policy Column for Radius policy and compare corrosponding 
	//Policy id with one selected.
	var TblRoot = document.getElementById("tblRCRPCombination").children[0];	
	var numString = '';
	var strMsg = '';
	//alert(document.frmRateCardGroup.SerialNumber.value);
	for(i=1;i<document.frmRateCardGroup.SerialNumber.value;i++)
	{
		var currOrder = TblRoot.children[i].children[3].children[0].value;
		//if currOrder is a valid number
		//if currOrder is less then curent value of serial number
		//if currOrder is not repeated previously in any other field.
		//if app. Order is 1 then Radius Policy must be default.
		//then return true
		//alert(parseInt(currOrder) > 0 && parseInt(currOrder < (document.frmRateCardGroup.SerialNumber.value)));
		if(!isNaN(currOrder) && (currOrder.lastIndexOf(".")==-1))//Valid Number
		{
			if(parseInt(currOrder) > 0 && (parseInt(currOrder) < parseInt(document.frmRateCardGroup.SerialNumber.value)))//Not Greater then max Allowed
			{
				if(numString.lastIndexOf(currOrder)!=-1)//App. Order is unique.
				{
					strMsg += "Sr. no.  " + i + " :  This Application Order is already applied.\n";
				}
				else
				{
					//Check that default Radius Policy has Application Order 1.
					var TblRoot = document.getElementById("tblRCRPCombination").children[0];	
					if(isDefaultRadiusPolicy(TblRoot.children[i].children[1].children[0].value))//If policy is Default Policy
					{
						if(currOrder != 1)//If Application Order is 1
						{
							alert("Default Radius Policy must have Application Order 1.");
							TblRoot.children[i].children[3].children[0].focus();//Set Focus on Application Order text.
							return false;
						}
						else
							numString += '' + currOrder + ':';
					}
					else //Not a Default Policy
					{						
							numString += '' + currOrder + ':';
					}
				}
			}
			else
			{
				strMsg += "Sr. no.  " + i + " :  Application Order must be between 0 to " + frmRateCardGroup.SerialNumber.value + "\n";
			}
		}
		else
		{
			strMsg += "Sr. no.  " + i + " : Application Order should be a valid Integer.\n";
		}
	}
	
	if(strMsg == '')
	{
		return true;
	}
	else
	{
		alert(strMsg);
		return false;
	}
}

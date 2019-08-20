// JavaScript Document
nre= /^\d+$/;
var ParameterArray = new Array();
var SlaParameterName = "";
var SlaParameterId = "";
var Commitment = "";
var DiscountingPolicyName = "";
var DiscountingPolicyId = "";
var Weightage = "";
var totalRows =1;
var SMasterBaseItemArray = new Array();
var SMasterItemsArray = new Array();

//FOR UPDATE...................................

									function SMasterpopulateTable(strCalcMethod) //Read from the Data Structure and draw the table
									{
										alert('inside populate table');
										alert(strCalcMethod);
										SMasterDeleteAllRows();
										for(i=0;i<SMasterBaseItemArray.length;i++)
										{
											alert("Sla parameter :: "+SMasterBaseItemArray[i].u_slaparameter);
											u_srno=SMasterBaseItemArray[i].u_srno;
											u_slaparameter=SMasterBaseItemArray[i].u_slaparameter;
											u_slaparameterID=SMasterBaseItemArray[i].u_slaparameterID;
											u_commitment=SMasterBaseItemArray[i].u_commitment;
											u_dispolicy=SMasterBaseItemArray[i].u_dispolicy;
											u_dispolicyID=SMasterBaseItemArray[i].u_dispolicyID;
											u_weightage=SMasterBaseItemArray[i].u_weightage;
											getRowsForUpdate(i+1,strCalcMethod);	
										} 
									}

									function getRowsForUpdate(totalSMasterRows,strCalcMethod) //draws the content rows in the table
									{
											alert('getRowsforupdate : ' + totalSMasterRows );
											mynewrow = SMasterTableObject.insertRow(totalSMasterRows);
											for (c=0;c<6;c++)
											{
												mynewrow.insertCell(c);
												mynewrow.cells[c].innerHTML = "<td>&nbsp;</td>";
												mynewrow.cells[c].className="tblrows";
											}
											mynewrow.cells[5].className="tblcol";
											mynewrow.cells[5].align="center";
											//Sr. No. 
											SMasterTableObject.rows[totalSMasterRows].cells[0].innerHTML=totalSMasterRows+"."+"<input type='hidden' name='c_strSerialno"+totalSMasterRows+"' id='c_strSerialno"+totalSMasterRows+"' value='"+totalSMasterRows+"'>";
											//Sla Parameter
											SMasterTableObject.rows[totalSMasterRows].cells[1].innerHTML=u_slaparameter+"<input type='hidden' name='c_strSlaParameter"+totalSMasterRows+"' id='c_strSlaParameter"+totalSMasterRows+"' value='"+u_slaparameterID+"'>";
											//Commitment
											SMasterTableObject.rows[totalSMasterRows].cells[2].innerHTML=u_commitment+"<input type='hidden' name='c_strCommitment"+totalSMasterRows+"' id='c_strCommitment"+totalSMasterRows+"' value='"+u_commitment+"'>";
											//Discount Policy
											SMasterTableObject.rows[totalSMasterRows].cells[3].innerHTML=u_dispolicy+"<input type='hidden' name='c_strDiscountpolicy"+totalSMasterRows+"' id='c_strDiscountpolicy"+totalSMasterRows+"' value='"+u_dispolicyID+"'>";

											//Weightage
											alert(strCalcMethod);
											if(strCalcMethod=="Weighted")
												SMasterTableObject.rows[totalSMasterRows].cells[4].innerHTML="<input type='text' name='weightage"+totalSMasterRows+"' id='weightage"+totalSMasterRows+"' value='"+u_weightage+"'>";
											else
												SMasterTableObject.rows[totalSMasterRows].cells[4].innerHTML="<input type='text' name='weightage"+totalSMasterRows+"' id='weightage"+totalSMasterRows+"' value='"+u_weightage+"' readonly='true' >";
											//RemoveImage
											alert('Parent : ' + this.parentElement);
											SMasterTableObject.rows[totalSMasterRows].cells[5].innerHTML=" <a href='javascript:void(0)' onclick='removeRowUpdate(this.parentNode)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>"
											alert('TotalSMasterRows : ' + totalSMasterRows);
											document.frmUpdateSla.count.value=totalSMasterRows;
											alert(document.frmUpdateSla.count.value);
									}

									function addRowsForUpdate(checkWeight)
									{
										alert('addRowsForUpdate(checkWeight)')
										var SlaParameterName = document.frmUpdateSla.c_strSlaParameter.options[document.frmUpdateSla.c_strSlaParameter.options.selectedIndex].text;
										var SlaParameterId = document.frmUpdateSla.c_strSlaParameter.options[document.frmUpdateSla.c_strSlaParameter.options.selectedIndex].value;
										var Commitment = document.frmUpdateSla.c_strCommitment.value;
										var DiscountingPolicyName = document.frmUpdateSla.c_strDiscountingPolicy.options[document.frmUpdateSla.c_strDiscountingPolicy.options.selectedIndex].text;
										var DiscountingPolicyId = document.frmUpdateSla.c_strDiscountingPolicy.options[document.frmUpdateSla.c_strDiscountingPolicy.options.selectedIndex].value;
										var Weightage = "1" ;
										totalSMasterRows = SMasterTableObject.rows.length;
										alert("totalSMasterRows :"+totalSMasterRows);
										rowsPass=totalSMasterRows-1;
										MakeObject(rowsPass,SlaParameterName,SlaParameterId,Commitment,DiscountingPolicyName,DiscountingPolicyId,Weightage,rowsPass);

										alert(checkWeight.length);
										if(checkWeight=='Weighted')
										{		
											alert('true');
											SMasterpopulateTableWeight();
										}	
										else
										{
											alert('false');
											SMasterpopulateTable();		
										}
									}

									function removeRowUpdate(object) //Removes the row via DOM and also deletes the ITEM from the Data Structure
									{	
										if(confirm("Are You Sure you want to remove the Particular"))		
										{
											alert('in delete' + object );
											while (object.tagName !=  'TR')
											{
												object = object.parentNode;
											}
											RowNumber = object.rowIndex;
											ArrayIndex=RowNumber-1;
											SMasterTableObject.deleteRow(RowNumber); //Removes the row via DOM
											SMasterBaseItemArray.splice(ArrayIndex, 1); //deletes the ITEM from the Data Structure
											AssignIdsUpdate();
										}
									}

									function AssignIdsUpdate() //Regenerates the Serial Nos.
									{
										totalRows = SMasterTableObject.rows.length;
										ContentRows = totalRows-1;
										for(i=1;i<=ContentRows;i++)
										{
											SMasterTableObject.rows[i].cells[0].innerText=i+".";
										}
										document.frmUpdateSla.count.value=ContentRows;
										SMasterpopulateTable();
									}





//FOR VIEW..................

										function SMasterpopulateTableView() //Read from the Data Structure and draw the table
										{
											alert('inside populate table');
											SMasterDeleteAllRows();
											for(i=0;i<SMasterBaseItemArray.length;i++)
											{
												alert("Sla parameter :: "+SMasterBaseItemArray[i].u_slaparameter);
												u_srno=SMasterBaseItemArray[i].u_srno;
												u_slaparameter=SMasterBaseItemArray[i].u_slaparameter;
												u_slaparameterID=SMasterBaseItemArray[i].u_slaparameterID;
												u_commitment=SMasterBaseItemArray[i].u_commitment;
												u_dispolicy=SMasterBaseItemArray[i].u_dispolicy;
												u_dispolicyID=SMasterBaseItemArray[i].u_dispolicyID;
												u_weightage=SMasterBaseItemArray[i].u_weightage;
												getRowsForView(i+1);	
											} 
										}

										function getRowsForView(totalSMasterRows) //draws the content rows in the table
										{
												alert('getRowsforupdate : ' + totalSMasterRows );
												mynewrow = SMasterTableObject.insertRow(totalSMasterRows);
												for (c=0;c<5;c++)
												{
													mynewrow.insertCell(c);
													mynewrow.cells[c].innerHTML = "<td>&nbsp;</td>";
													mynewrow.cells[c].className="tblrows";
												}
												//Sr. No. 
												SMasterTableObject.rows[totalSMasterRows].cells[0].innerHTML=totalSMasterRows+"."+"<input type='hidden' name='c_strSerialno"+totalSMasterRows+"' id='c_strSerialno"+totalSMasterRows+"' value='"+totalSMasterRows+"'>";
												//Sla Parameter
												SMasterTableObject.rows[totalSMasterRows].cells[1].innerHTML=u_slaparameter+"<input type='hidden' name='c_strSlaParameter"+totalSMasterRows+"' id='c_strSlaParameter"+totalSMasterRows+"' value='"+u_slaparameterID+"'>";
												//Commitment
												SMasterTableObject.rows[totalSMasterRows].cells[2].innerHTML=u_commitment+"<input type='hidden' name='c_strCommitment"+totalSMasterRows+"' id='c_strCommitment"+totalSMasterRows+"' value='"+u_commitment+"'>";
												//Discount Policy
												SMasterTableObject.rows[totalSMasterRows].cells[3].innerHTML=u_dispolicy+"<input type='hidden' name='c_strDiscountpolicy"+totalSMasterRows+"' id='c_strDiscountpolicy"+totalSMasterRows+"' value='"+u_dispolicyID+"'>";
												//Weightage
												SMasterTableObject.rows[totalSMasterRows].cells[4].innerHTML=u_weightage+"<input type='hidden' name='c_strWeightage"+totalSMasterRows+"' id='c_strWeightage"+totalSMasterRows+"' value='"+u_weightage+"'>";
												alert('TotalSMasterRows : ' + totalSMasterRows);
												document.frmViewSla.count.value=totalSMasterRows;
												alert(document.frmViewSla.count.value);
										}




//FOR ADDING.................

										function AssignIds() //Regenerates the Serial Nos.
										{
											totalRows = TableObject.rows.length;
											ContentRows = totalRows-1;
											for(i=1;i<=ContentRows;i++)
											{
												TableObject.rows[i].cells[0].innerText=i+".";
											}
											document.frmCreateSla.count.value=ContentRows;
											SMasterpopulateTable();
										}

										function addRows() //draws the content rows in the table
										{
												index = TableObject.rows.length ;
												mynewrow = TableObject.insertRow(index);		
												var SlaParameterName = document.frmCreateSla.c_strSlaParameter.options[document.frmCreateSla.c_strSlaParameter.options.selectedIndex].text;
												var SlaParameterId = document.frmCreateSla.c_strSlaParameter.options[document.frmCreateSla.c_strSlaParameter.options.selectedIndex].value;
												var Commitment = document.frmCreateSla.c_strCommitment.value;
												var DiscountingPolicyName = document.frmCreateSla.c_strDiscountingPolicy.options[document.frmCreateSla.c_strDiscountingPolicy.options.selectedIndex].text;
												var DiscountingPolicyId = document.frmCreateSla.c_strDiscountingPolicy.options[document.frmCreateSla.c_strDiscountingPolicy.options.selectedIndex].value;
												var Weightage = "1" ;

												//Sr. No.
												mynewrow.insertCell();
												mynewrow.cells(0).className="tblrows";
												mynewrow.cells(0).innerHTML=index+"."+"<input type='hidden' name='C_Srno"+index+"' id='C_Srno"+index+"' value='"+index+"'>";
												
												//Sla Parameter		
												mynewrow.insertCell();
												mynewrow.cells(1).className="tblrows";
												mynewrow.cells(1).innerHTML=SlaParameterName+"<input type='hidden' name='slaparameter"+index+"' id='slaparameter"+index+"' value='"+SlaParameterId+"'>";

												//Commitment
												mynewrow.insertCell();
												mynewrow.cells(2).className="tblrows";
												mynewrow.cells(2).innerHTML=Commitment+"<input type='hidden' name='commitment"+index+"' id='commitment"+index+"' value='"+Commitment+"'>" ;

												//Discounting Policy
												mynewrow.insertCell();
												mynewrow.cells(3).className="tblrows";
												mynewrow.cells(3).innerHTML=DiscountingPolicyName+"<input type='hidden' name='discountingpolicy"+index+"' id='discountingpolicy"+index+"' value='"+DiscountingPolicyId+"'>";

												//Weightage
												mynewrow.insertCell();
												mynewrow.cells(4).className="tblrows";
												mynewrow.cells(4).innerHTML="<input type='text' name='weightage"+index+"' id='weightage"+index+"' value='"+Weightage+"' onFocus='this.blur()' >" ;
													
												//Remove
												mynewrow.insertCell();
												mynewrow.cells(5).className="tblrows";
												alert('Parent : ' + this.parentElement);
												mynewrow.cells(5).innerHTML=" <a href='javascript:void(0)' onclick='removeRow(this.parentElement)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>" ;

												document.frmCreateSla.count.value=index;
										}



/*FOR WEIGHT....................

	function SMasterpopulateTableWeight() //Read from the Data Structure and draw the table
	{
		alert('inside populate table');
		SMasterDeleteAllRows();
		for(i=0;i<SMasterBaseItemArray.length;i++)
		{
			alert("Sla parameter :: "+SMasterBaseItemArray[i].u_slaparameter);
			u_srno=SMasterBaseItemArray[i].u_srno;
			u_slaparameter=SMasterBaseItemArray[i].u_slaparameter;
			u_slaparameterID=SMasterBaseItemArray[i].u_slaparameterID;
			u_commitment=SMasterBaseItemArray[i].u_commitment;
			u_dispolicy=SMasterBaseItemArray[i].u_dispolicy;
			u_dispolicyID=SMasterBaseItemArray[i].u_dispolicyID;
			u_weightage=SMasterBaseItemArray[i].u_weightage;
			getRowsForWeight(i+1);	
		} 
	}

	function getRowsForWeight(totalSMasterRows) //draws the content rows in the table
	{
			alert('getRowsforupdate : ' + totalSMasterRows );
			mynewrow = SMasterTableObject.insertRow(totalSMasterRows);
			for (c=0;c<6;c++)
			{
				mynewrow.insertCell(c);
				mynewrow.cells[c].innerHTML = "<td>&nbsp;</td>";
				mynewrow.cells[c].className="tblrows";
			}
			mynewrow.cells[5].className="tblcol";
			mynewrow.cells[5].align="center";
			//Sr. No. 
			SMasterTableObject.rows[totalSMasterRows].cells[0].innerHTML=totalSMasterRows+"."+"<input type='hidden' name='c_strSerialno"+totalSMasterRows+"' id='c_strSerialno"+totalSMasterRows+"' value='"+totalSMasterRows+"'>";
			//Sla Parameter
			SMasterTableObject.rows[totalSMasterRows].cells[1].innerHTML=u_slaparameter+"<input type='hidden' name='c_strSlaParameter"+totalSMasterRows+"' id='c_strSlaParameter"+totalSMasterRows+"' value='"+u_slaparameterID+"'>";
			//Commitment
			SMasterTableObject.rows[totalSMasterRows].cells[2].innerHTML=u_commitment+"<input type='hidden' name='c_strCommitment"+totalSMasterRows+"' id='c_strCommitment"+totalSMasterRows+"' value='"+u_commitment+"'>";
			//Discount Policy
			SMasterTableObject.rows[totalSMasterRows].cells[3].innerHTML=u_dispolicy+"<input type='hidden' name='c_strDiscountpolicy"+totalSMasterRows+"' id='c_strDiscountpolicy"+totalSMasterRows+"' value='"+u_dispolicyID+"'>";
			//Weightage
			SMasterTableObject.rows[totalSMasterRows].cells[4].innerHTML="<input type='text' name='c_strWeightage"+totalSMasterRows+"' id='c_strWeightage"+totalSMasterRows+"' value='"+u_weightage+"'>";
			//RemoveImage
			alert('Parent : ' + this.parentElement);
			SMasterTableObject.rows[totalSMasterRows].cells[5].innerHTML=" <a href='javascript:void(0)' onclick='removeRowUpdate(this.parentNode)'><img src='/images/minus.jpg' border='0' alt='Remove' ></a>"
			alert('TotalSMasterRows : ' + totalSMasterRows);
			document.frmUpdateSla.count.value=totalSMasterRows;
			alert(document.frmUpdateSla.count.value);
	}

*/



function DeleteAllRows(rowAdd) // Deletes all rows of the table except the header
{
	
	for(i=1;i<ParameterArray.length+rowAdd;i++)
	{
		TableObject.deleteRow(1);
	}
}

function removeRow(object) //Removes the row via DOM and also deletes the ITEM from the Data Structure
{	
	if(confirm("Are You Sure you want to remove the Particular"))		
	{
		alert('in delete' + object );
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;
		ArrayIndex=RowNumber-1;
		TableObject.deleteRow(RowNumber); //Removes the row via DOM
		SMasterBaseItemArray.splice(ArrayIndex, 1); //deletes the ITEM from the Data Structure
		AssignIds();
	}
}

function SMasterDeleteAllRows() // Deletes all rows of the table except the header
{
	totalSMasterRows = SMasterTableObject.rows.length;
	for(i=1;i<totalSMasterRows;i++)
	{
		SMasterTableObject.deleteRow(1);
	}
}


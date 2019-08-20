/*
	The .js needs to be optimized
	Author  : Sumedha Jagushte
	Created : 1st August 2003
*/
	var RowNumber=0;
	var cellNumber=0;
	var maxLen,total,cellNumber,totalRows;
	var counter=0;
	var rowId=0;
	var RowsArray;
	var re = /\w{1,}/;  

	function selectedRow(object,cNumber)
	{
		while (object.tagName !=  'TR')
		{
			object = object.parentNode;
		}
		RowNumber = object.rowIndex;
		cellNumber = cNumber;
		enableAll();
		chkcounter();
	}



	function AssignIds()
	{
		rowId=0;
		totalRows=thisDoc.children.length;
		for(i=0;i<totalRows;i++)
		{
			if(thisDoc.childNodes[i].children.length=="1")
			{
				rowId = rowId+1;
				thisDoc.childNodes[i].id = rowId;
				thisDoc.childNodes[i].childNodes[0].innerHTML=
				"<input type='radio' name='level' value='"+rowId+"' onclick='selectedRow(this.parentElement)'>"+ rowId;
			}
			else
			{
				thisDoc.childNodes[i].id = rowId;
				for(j=1;j<=3;j++)
				{
					if(re.test(thisDoc.childNodes[i].childNodes[j].innerText))
					{
						orgVal=thisDoc.childNodes[i].childNodes[j].childNodes[0].value;
						//orgVal1=thisDoc.childNodes[i].childNodes[j].childNodes[2].value;
						chkVal=orgVal.split("$");
						thisDoc.childNodes[i].childNodes[j].childNodes[0].value = chkVal[0]+"$"+rowId;
						thisDoc.childNodes[i].childNodes[j].childNodes[2].value = chkVal[0]+"$"+rowId;
					}
				}
			}
		}
	}
	
	function validate()
	{
		totalRows=thisDoc.children.length;
		var curId=1;
		var prevId=1;
		var isEmpty=true;
		for(i=0;i<totalRows;)
		{
			isEmpty=true;
			prevId=curId;
			while(curId==prevId)
			{
				if(isEmpty)
				{
					if(thisDoc.childNodes[i].children.length!=1)
					{
						for(j=1;j<=3;j++)
						{
							if(re.test(thisDoc.childNodes[i].childNodes[j].innerText))
							{
								isEmpty=false;
							}
						}
					}
				}
				i++;
				if(i<totalRows)
				{
					prevId=curId;
					curId=thisDoc.childNodes[i].id;
				}
				else
				{
					curId=curId+1;
				}
			}
			if(isEmpty)
			{
				alert("Delete all empty levels");
				break;
			}
		}
		return !isEmpty;
	}
	
	function chkcounter()
	{
		maxLen = 0;
		chkSelected = 0;
		elementsLen=document.forms[0].elements.length;
		for(i=0;i<elementsLen;i++)
		{
			if (document.forms[0].elements[i].name=="tax")
			{
				maxLen += 1;
			}
		}
		
		for (var idx = 0; idx < maxLen; idx++)
		{
			if (eval("document.forms[0].tax[" + idx + "].checked") == true)
			{
				chkSelected += 1;
			}
		}
	}



	function RowAbove()
	{
		mynewrow = eval("document.all.myTable.insertRow(" + RowNumber + ")");
		for (i=0;i<4;i++)
		{
			mynewrow.insertCell();
			mynewrow.cells(i).innerHTML = "<td class='text'>&nbsp;</td>";
		}
	
		mynewrow = eval("document.all.myTable.insertRow(" + RowNumber + ")");
		mynewrow.insertCell();
		mynewrow.cells(0).innerHTML = "<td onclick='selectedRow(this.parentElement);disableCheckBoxes()'><input type='radio' name='level' value='level' onclick='selectedRow(this.parentElement)'>" + RowNumber + "</td>";

		var x=myTable.rows[RowNumber].cells;
		x[0].colSpan="4";
		
		disableAll();
		AssignIds();
	} // RowAbove()...



	
	function RowBelow()
	{

		curRowId=thisDoc.childNodes[RowNumber].id;
		totalRows=thisDoc.children.length;
		rowctr=0;
		for(i=0;i<totalRows;i++)
		{
			if(thisDoc.childNodes[i].id==curRowId)
			{
				rowctr++;
			}
		}
		RowNumber = RowNumber+rowctr;
		mynewrow = eval("document.all.myTable.insertRow(" + RowNumber + ")");
		for (i=0;i<4;i++)
		{
			mynewrow.insertCell();
			mynewrow.cells(i).innerHTML = "<td class='text'>&nbsp;</td>";
		}
		
		mynewrow = eval("document.all.myTable.insertRow(" + RowNumber + ")");
		mynewrow.insertCell();
		mynewrow.cells(0).innerHTML = "<td onclick='selectedRow(this.parentElement);disableCheckBoxes()'><input type='radio' name='level' value='level' onclick='selectedRow(this.parentElement)'>" + RowNumber + "</td>";
	
		var x=myTable.rows[RowNumber].cells;
		x[0].colSpan="4";

		disableAll();
		AssignIds();
	} // RowBelow()...



	function DeleteRow()
	{
		var bConfirm = confirm('Are You Sure you Want to Delete Level?');
		if(bConfirm)
		{
		NextRow=RowNumber+1;
		cellText="";
		for(j=1;j<=3;j++)
		{
			if(j==1)
			{
				cellText=thisDoc.childNodes[NextRow].childNodes[j].innerText;
			}
			else
			{
				cellText=cellText+","+thisDoc.childNodes[NextRow].childNodes[j].innerText;
			}
		}
		text=cellText.split(",");
		curRowId=thisDoc.childNodes[RowNumber].id;
//		if((text[0]==" ") && (text[1]==" ") && (text[2]==" "))
		if((!re.test(text[0])) && (!re.test(text[1])) && (!re.test(text[2])))
		{
			 for(i=0;i<totalRows;i++)
			 {
			 	if(thisDoc.childNodes[i].id==curRowId)
				{
			 		eval("document.all.myTable.deleteRow(" + i + ")");
					i--;
				}
				totalRows=thisDoc.children.length;
			 }
		}
		else
		{
			alert("Move all sub-levels");
		}
		disableAll();
		AssignIds();
		}
	} // DeleteRow()...


	
	function MoveUp()
	{
		totalRows=thisDoc.children.length;
		var orgRowCtr=1;
		var firstRowCtr=0;
		var lastRowCtr=0;
		var k=0;
		var rowAdded=false;
		chkcounter();
		rowsArray=new Array(chkSelected);
		for(i=1;i<totalRows;i++)
		{
			if(thisDoc.childNodes[i].children.length!="1")
			{
				for(j=1;j<=3;j++)	
				{
					if(thisDoc.childNodes[i].childNodes[j].childNodes[0].checked==true)
					{
						RNumber=thisDoc.childNodes[i].rowIndex;
						CNumber=thisDoc.childNodes[i].childNodes[j].cellIndex;

						var CellObject = new Object();
						CellObject.row=RNumber;
						CellObject.col=CNumber;
						rowsArray[k]=CellObject;
						k++;

					}
				}
			}
			
		}

		orgRow=rowsArray[0].row;
		orgID=thisDoc.childNodes[orgRow].id;
		for(rctr=0;rctr<rowsArray.length;rctr++)
		{
			IDsmatch=false;
			RowNumber =rowsArray[rctr].row;
			CellNumber =rowsArray[rctr].col;
			if(thisDoc.childNodes[RowNumber].id==orgID)			
			{
				IDsmatch=true;
				curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
			}
			
		}
		if(IDsmatch)		
		{
			if (curRowId!=1)
			{
				for(rctr=0;rctr<rowsArray.length;rctr++)
				{
					RowNumber =rowsArray[rctr].row;
					CellNumber =rowsArray[rctr].col;
					var nextRowId=0;
					var curRowId=0;
					var curRowNumber=0;
					var swapContent=false;
		
					orgRowNumber=RowNumber;
					if (rowAdded)
						orgRowNumber=orgRowNumber+1;
					orgCellNumber=CellNumber;
					totalRows=thisDoc.children.length;
					lastrow=totalRows-1;
					lastRowId=thisDoc.childNodes[lastrow].id;
		
					curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
					nextRowId=curRowId-1 ;
					
					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							firstRowCtr=firstRowCtr+1;
							firstCurRowNumber=thisDoc.childNodes[i].rowIndex;
							break;
						}
					}
					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							lastRowCtr=lastRowCtr+1;
							lastCurRowNumber=thisDoc.childNodes[i].rowIndex;
						}
					}

					loopCtr=lastCurRowNumber-firstCurRowNumber;
					if(rowAdded)
						//curRowNumber=lastCurRowNumber;
						curRowNumber=curRowNumber+1;
					else
						curRowNumber=firstCurRowNumber+1;

					for(i=0;i<loopCtr;i++)
					{
						if(i>0 && swapContent==false)
						{
							curRowNumber=curRowNumber+1;
						}
					
							for(j=1;j<=3;j++)
							{
								if(!re.test(thisDoc.childNodes[curRowNumber].childNodes[j].innerText))
								{
									swapContent=true;
					thisDoc.childNodes[curRowNumber].childNodes[j].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);
								}
							}
					}

					if(!swapContent)
					{
						curRowNumber=curRowNumber+1;
						mynewrow = eval("document.all.myTable.insertRow(" + curRowNumber + ")");
						for (i=0;i<4;i++)
						{
							mynewrow.insertCell();
							mynewrow.cells(i).innerHTML = "<td class='text'>&nbsp;</td>";
						}
							rowAdded=true;
							orgRowNumber=orgRowNumber+1;
		thisDoc.childNodes[curRowNumber].childNodes[1].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);						

					}
					AssignIds();
				}
			}
			else
			{
				alert("Create a level");
			}
		}
		else
		{
			alert("Select sub-levels of the same level");
		}
	
		for(i=0;i<document.forms[0].elements.length;i++)
			document.forms[0].elements[i].checked=false;
		disableMove();
		AssignIds();
		
	}//MoveUp()

	
	function MoveDown()
	{
		totalRows=thisDoc.children.length;
		var orgRowCtr=1;
		var firstRowCtr=0;
		var lastRowCtr=0;
		var k=0;
		var rowAdded=false;
		chkcounter();
		rowsArray=new Array(chkSelected);
		for(i=1;i<totalRows;i++)
		{
			if(thisDoc.childNodes[i].children.length!="1")
			{
				for(j=1;j<=3;j++)	
				{
					if(thisDoc.childNodes[i].childNodes[j].childNodes[0].checked==true)
					{
						RNumber=thisDoc.childNodes[i].rowIndex;
						CNumber=thisDoc.childNodes[i].childNodes[j].cellIndex;

						var CellObject = new Object();
						CellObject.row=RNumber;
						CellObject.col=CNumber;
						rowsArray[k]=CellObject;
						k++;

					}
				}
			}
			
		}
		orgRow=rowsArray[0].row;
		orgID=thisDoc.childNodes[orgRow].id;
		totalRows=thisDoc.children.length;
		lastrow=totalRows-1;
		lastRowId=thisDoc.childNodes[lastrow].id;

		for(rctr=0;rctr<rowsArray.length;rctr++)
		{
			IDsmatch=false;
			RowNumber =rowsArray[rctr].row;
			CellNumber =rowsArray[rctr].col;
			if(thisDoc.childNodes[RowNumber].id==orgID)			
			{
				IDsmatch=true;
				curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
			}
			
		}

		if(IDsmatch)		
		{
			if (curRowId!=lastRowId)
			{
				for(rctr=0;rctr<rowsArray.length;rctr++)
				{
					RowNumber =rowsArray[rctr].row;
					CellNumber =rowsArray[rctr].col;
					var nextRowId=0;
					var curRowId=0;
					var curRowNumber=0;
					var swapContent=false;

					orgRowNumber=RowNumber;
					orgCellNumber=CellNumber;
					totalRows=thisDoc.children.length;
					lastrow=totalRows-1;
					lastRowId=thisDoc.childNodes[lastrow].id;

					curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
					nextRowId=curRowId+1 ;
						
					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							firstRowCtr=firstRowCtr+1;
							firstCurRowNumber=thisDoc.childNodes[i].rowIndex;
							break;
						}
					}

					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							lastRowCtr=lastRowCtr+1;
							lastCurRowNumber=thisDoc.childNodes[i].rowIndex;
						}
					}
					
					loopCtr=lastCurRowNumber-firstCurRowNumber;
					if(rowAdded)
						//curRowNumber=lastCurRowNumber;
						curRowNumber=curRowNumber+1;
					else
						curRowNumber=firstCurRowNumber+1;

					for(i=0;i<loopCtr;i++)
					{
						if(i>0 && swapContent==false)
						{
							curRowNumber=curRowNumber+1;
						}
					
							for(j=1;j<=3;j++)
							{
								if(!re.test(thisDoc.childNodes[curRowNumber].childNodes[j].innerText))
								{
									swapContent=true;
					thisDoc.childNodes[curRowNumber].childNodes[j].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);
								}
							}
					}
					if(!swapContent)
					{
						curRowNumber=curRowNumber+1
						mynewrow = eval("document.all.myTable.insertRow(" + curRowNumber + ")");
						for (i=0;i<4;i++)
						{
							mynewrow.insertCell();
							mynewrow.cells(i).innerHTML = "<td class='text'>&nbsp;</td>";
						}
							rowAdded=true;
							thisDoc.childNodes[curRowNumber].childNodes[1].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);						

					}
					AssignIds();
				}//for
			}
			else
			{
				alert("Create a level");
			}
		}
		else
		{
			alert("Select sub-levels of the same level");
		}
	
		for(i=0;i<document.forms[0].elements.length;i++)
			document.forms[0].elements[i].checked=false;
		disableMove();
		AssignIds();
		
	}//MoveDown()




	function MoveFirst()
	{
		totalRows=thisDoc.children.length;
		var orgRowCtr=1;
		var firstRowCtr=0;
		var lastRowCtr=0;
		var k=0;
		var rowAdded=false;
		chkcounter();
		rowsArray=new Array(chkSelected);
		for(i=1;i<totalRows;i++)
		{
			if(thisDoc.childNodes[i].children.length!="1")
			{
				for(j=1;j<=3;j++)	
				{
					if(thisDoc.childNodes[i].childNodes[j].childNodes[0].checked==true)
					{
						RNumber=thisDoc.childNodes[i].rowIndex;
						CNumber=thisDoc.childNodes[i].childNodes[j].cellIndex;

						var CellObject = new Object();
						CellObject.row=RNumber;
						CellObject.col=CNumber;
						rowsArray[k]=CellObject;
						k++;

					}
				}
			}
			
		}

		orgRow=rowsArray[0].row;
		orgID=thisDoc.childNodes[orgRow].id;
		for(rctr=0;rctr<rowsArray.length;rctr++)
		{
			IDsmatch=false;
			RowNumber =rowsArray[rctr].row;
			CellNumber =rowsArray[rctr].col;
			if(thisDoc.childNodes[RowNumber].id==orgID)			
			{
				IDsmatch=true;
				curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
			}
			
		}
		if(IDsmatch)		
		{
			if (curRowId!=1)
			{
				for(rctr=0;rctr<rowsArray.length;rctr++)
				{
					RowNumber =rowsArray[rctr].row;
					CellNumber =rowsArray[rctr].col;
					var nextRowId=0;
					var curRowId=0;
					var curRowNumber=0;
					var swapContent=false;
		
					orgRowNumber=RowNumber;
					if (rowAdded)
						orgRowNumber=orgRowNumber+1;
					orgCellNumber=CellNumber;
					totalRows=thisDoc.children.length;
					lastrow=totalRows-1;
					lastRowId=thisDoc.childNodes[lastrow].id;
		
					curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
					nextRowId=1;
					
					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							firstRowCtr=firstRowCtr+1;
							firstCurRowNumber=thisDoc.childNodes[i].rowIndex;
							break;
						}
					}
					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							lastRowCtr=lastRowCtr+1;
							lastCurRowNumber=thisDoc.childNodes[i].rowIndex;
						}
					}

					loopCtr=lastCurRowNumber-firstCurRowNumber;
					if(rowAdded)
						//curRowNumber=lastCurRowNumber;
						curRowNumber=curRowNumber+1;
					else
						curRowNumber=firstCurRowNumber+1;

					for(i=0;i<loopCtr;i++)
					{
						if(i>0 && swapContent==false)
						{
							curRowNumber=curRowNumber+1;
						}
					
							for(j=1;j<=3;j++)
							{
								if(!re.test(thisDoc.childNodes[curRowNumber].childNodes[j].innerText))
								{
									swapContent=true;
					thisDoc.childNodes[curRowNumber].childNodes[j].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);
								}
							}
					}

					if(!swapContent)
					{
						curRowNumber=curRowNumber+1;
						mynewrow = eval("document.all.myTable.insertRow(" + curRowNumber + ")");
						for (i=0;i<4;i++)
						{
							mynewrow.insertCell();
							mynewrow.cells(i).innerHTML = "<td class='text'>&nbsp;</td>";
						}
							rowAdded=true;
							orgRowNumber=orgRowNumber+1;
		thisDoc.childNodes[curRowNumber].childNodes[1].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);						

					}
					AssignIds();
				}
			}
			else
			{
				alert("Create a level");
			}
		}
		else
		{
			alert("Select sub-levels of the same level");
		}
	
		for(i=0;i<document.forms[0].elements.length;i++)
			document.forms[0].elements[i].checked=false;
		disableMove();
		AssignIds();
		
	}




	function MoveLast()
	{
		totalRows=thisDoc.children.length;
		var orgRowCtr=1;
		var firstRowCtr=0;
		var lastRowCtr=0;
		var k=0;
		var rowAdded=false;
		chkcounter();
		rowsArray=new Array(chkSelected);
		for(i=1;i<totalRows;i++)
		{
			if(thisDoc.childNodes[i].children.length!="1")
			{
				for(j=1;j<=3;j++)	
				{
					if(thisDoc.childNodes[i].childNodes[j].childNodes[0].checked==true)
					{
						RNumber=thisDoc.childNodes[i].rowIndex;
						CNumber=thisDoc.childNodes[i].childNodes[j].cellIndex;

						var CellObject = new Object();
						CellObject.row=RNumber;
						CellObject.col=CNumber;
						rowsArray[k]=CellObject;
						k++;

					}
				}
			}
			
		}
		orgRow=rowsArray[0].row;
		orgID=thisDoc.childNodes[orgRow].id;
		totalRows=thisDoc.children.length;
		lastrow=totalRows-1;
		lastRowId=thisDoc.childNodes[lastrow].id;

		for(rctr=0;rctr<rowsArray.length;rctr++)
		{
			IDsmatch=false;
			RowNumber =rowsArray[rctr].row;
			CellNumber =rowsArray[rctr].col;
			if(thisDoc.childNodes[RowNumber].id==orgID)			
			{
				IDsmatch=true;
				curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
			}

			
		}

		if(IDsmatch)		
		{
			if (curRowId!=lastRowId)
			{
				for(rctr=0;rctr<rowsArray.length;rctr++)
				{
					RowNumber =rowsArray[rctr].row;
					CellNumber =rowsArray[rctr].col;
					var nextRowId=0;
					var curRowId=0;
					var curRowNumber=0;
					var swapContent=false;

					orgRowNumber=RowNumber;
					orgCellNumber=CellNumber;
					totalRows=thisDoc.children.length;
					lastrow=totalRows-1;
					lastRowId=thisDoc.childNodes[lastrow].id;

					curRowId=parseInt(thisDoc.childNodes[RowNumber].id);
					nextRowId=lastRowId;
						
					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							firstRowCtr=firstRowCtr+1;
							firstCurRowNumber=thisDoc.childNodes[i].rowIndex;
							break;
						}
					}

					for(i=0;i<totalRows;i++)
					{
						if(thisDoc.childNodes[i].id==nextRowId)
						{	
							lastRowCtr=lastRowCtr+1;
							lastCurRowNumber=thisDoc.childNodes[i].rowIndex;
						}
					}
					
					loopCtr=lastCurRowNumber-firstCurRowNumber;
					if(rowAdded)
						//curRowNumber=lastCurRowNumber;
						curRowNumber=curRowNumber+1;
					else
						curRowNumber=firstCurRowNumber+1;

					for(i=0;i<loopCtr;i++)
					{
						if(i>0 && swapContent==false)
						{
							curRowNumber=curRowNumber+1;
						}
					
							for(j=1;j<=3;j++)
							{
								if(!re.test(thisDoc.childNodes[curRowNumber].childNodes[j].innerText))
								{
									swapContent=true;
					thisDoc.childNodes[curRowNumber].childNodes[j].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);
								}
							}
						}
						if(!swapContent)
						{
							curRowNumber=curRowNumber+1
							mynewrow = eval("document.all.myTable.insertRow(" + curRowNumber + ")");
							for (i=0;i<4;i++)
							{
								mynewrow.insertCell();
								mynewrow.cells(i).innerHTML = "<td class='text'>&nbsp;</td>";
							}
								rowAdded=true;
								thisDoc.childNodes[curRowNumber].childNodes[1].swapNode(thisDoc.childNodes[orgRowNumber].childNodes[orgCellNumber]);						

						}
					AssignIds();
				}//for
			}
			else
			{
				alert("Create a level");
			}
		}
		else
		{
			alert("Select sub-levels of the same level");
		}
	
		for(i=0;i<document.forms[0].elements.length;i++)
			document.forms[0].elements[i].checked=false;
		disableMove();
		AssignIds();
		
	}//MoveLast()




	function enableMove()
	{
		document.all.c_btnMoveUp.disabled=false;
		document.all.c_btnMoveDown.disabled=false;
		document.all.c_btnMoveFirst.disabled=false;
		document.all.c_btnMoveLast.disabled=false;
		document.all.c_btnAddLevelAbove.disabled=true;
		document.all.c_btnAddLevelBelow.disabled=true;
		document.all.c_btnDelete.disabled=true;
		
		
	}

	function disableMove()
	{
		document.all.c_btnMoveUp.disabled=true;
		document.all.c_btnMoveDown.disabled=true;
		document.all.c_btnMoveFirst.disabled=true;
		document.all.c_btnMoveLast.disabled=true;
		
	}

	function enableAll()
	{
		document.all.c_btnAddLevelAbove.disabled=false;
		document.all.c_btnAddLevelBelow.disabled=false;
		document.all.c_btnDelete.disabled=false;
		disableMove();
	}



	function disableAll()
	{
		document.all.c_btnAddLevelAbove.disabled=true;
		document.all.c_btnAddLevelBelow.disabled=true;
		document.all.c_btnDelete.disabled=true;
		document.all.c_btnMoveUp.disabled=true;
		document.all.c_btnMoveDown.disabled=true;
		document.all.c_btnMoveFirst.disabled=true;
		document.all.c_btnMoveLast.disabled=true;
		disableCheckBoxes();
	}
	
	function disableCheckBoxes()
	{
		ctr=document.forms[0].tax.length;
		for(i=0;i<ctr;i++)
		{
			document.forms[0].tax[i].checked=false;
		}
	}
	
	
function viewGeneratedDOM(){
    x=window.open();
    x.document.write('<pre>&lt;html&gt;' + htmlEscape(document.documentElement.innerHTML) + '&lt;/html&gt;</pre>');
    x.document.close();
}

function htmlEscape(s){
    s=s.replace(/&/g,'&amp;');
    s=s.replace(/>/g,'&gt;');
    s=s.replace(/</g,'\n&lt;');
    return s;
}

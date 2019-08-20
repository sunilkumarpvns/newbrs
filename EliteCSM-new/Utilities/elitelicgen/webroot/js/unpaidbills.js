// JavaScript Document
nre= /^\d+$/;
var arrayDbDocData = new Array();
var BillListtRows =0;
var Counter=0;
var Columns=9;

function BillListpopulateTable(CurrencyId) //Read from the Data Structure and draw the table
{
	//alert(arrayDbDocData.length);
	if(arrayDbDocData.length != 0)
	{
		BillListDeleteAllRows();	
	}

	Counter=0;
		
	for(i=0;i<arrayDbDocData.length;i++)
	{
		if( arrayDbDocData[i].DocCurrencyId == CurrencyId )
		{
		Counter++;
		
		DocCurrencyId=arrayDbDocData[i].DocCurrencyId;
		DocCurrencyRate = arrayDbDocData[i].DocCurrencyRate;
		DocTypeAlias=arrayDbDocData[i].DocTypeAlias;
		DebitDocumentNo = arrayDbDocData[i].DebitDocumentNo; 
		DebitDocumentId=arrayDbDocData[i].DebitDocumentId;
		CurrencyAlias=arrayDbDocData[i].CurrencyAlias;
		DueDate=arrayDbDocData[i].DueDate;
		BillAmount=arrayDbDocData[i].BillAmount;
		PaidAmount=arrayDbDocData[i].PaidAmount;
		DueAmount = arrayDbDocData[i].DueAmount;
		strBillAmount=arrayDbDocData[i].strBillAmount;
		strPaidAmount=arrayDbDocData[i].strPaidAmount;
		strDueAmount = arrayDbDocData[i].strDueAmount;
		PayeeAccountName=arrayDbDocData[i].PayeeAccountName;	
		
		// Add Rows In the Table
		//alert("adding Row");
		BillListaddRow();
		
		}	
		
	}
	
	if(arrayDbDocData.length != 0)
	{
		if(Counter == 0)
		{		
			BillListaddNotFoundRow();
		}
		else
		{
			BillListaddTotalsRow();
		}
	}
		
}

function BillListaddRow() //Adds Row to the Table
{
	var RowCount=0;
	for(j=0;j<BillListTableObject.children.length;j++)
	{
		if(BillListTableObject.childNodes[j].nodeName == "TR") // Because Some Script elements are there
		{
			RowCount++;
			totalBillListRows =RowCount; 
		}
		else
		{
			//alert(BillListTableObject.childNodes[j].nodeName);
		}
		
	}
	
	mynewrow = BillListTableObject.insertRow(totalBillListRows);

	//alert(Columns);
		for (c=0;c<Columns;c++)
		{
			mynewrow.insertCell();
			mynewrow.cells(c).innerHTML = "<td>&nbsp;</td>";
			mynewrow.cells(c).className="tblrows";
			mynewrow.cells(c).align="right";
		}
		for(c=2;c<Columns-4;c++)
		{
			mynewrow.cells(c).align="left";
		}
		mynewrow.cells(0).align="center";
		mynewrow.cells(0).className="tblfirstcol";

		mynewrow.cells(0).width="3%";
		mynewrow.cells(1).width="4%";
		mynewrow.cells(2).width="5%";
		mynewrow.cells(3).width="3%";
			
		if(Columns == 9)
		{
			mynewrow.cells(4).width="15%";
			mynewrow.cells(5).width="10%";
			mynewrow.cells(6).width="10%";
			mynewrow.cells(7).width="10%";
			mynewrow.cells(8).width="8%";
		}
		else
		{
			mynewrow.cells(4).width="15%";
			mynewrow.cells(5).width="15%";
			mynewrow.cells(6).width="10%";
			mynewrow.cells(7).width="10%";
			mynewrow.cells(8).width="10%";
			mynewrow.cells(9).width="8%";
		}				
		
		//CheckBox
		BillListTableObject.childNodes[totalBillListRows].childNodes[0].innerHTML="<input type='checkbox' name='index' value='"+Counter+"' onclick='selectBill("+Counter+", this.checked ); Toggle(this,\"billlist_table\"," + Counter + ",this.checked)'>"+
																				  "<input type='hidden' name='c_strBillId"+Counter+"' value='"+DebitDocumentId+"'>"+
																				  "<input type='hidden' name='c_strBillCurrencyId"+Counter+"' value='"+DocCurrencyId+"'>"+
																				  "<input type='hidden' name='c_strBillExchangeRate"+Counter+"' value='"+DocCurrencyRate+"'>";
		//Sr. No.
		BillListTableObject.childNodes[totalBillListRows].childNodes[1].innerHTML=Counter+".";
		
		if(DocTypeAlias == "DEBIT_NOTE")
		{
			//Pass Debit Document ID.
			BillListTableObject.childNodes[totalBillListRows].childNodes[2].innerHTML="<a href='javascript:viewDocument(\""+DebitDocumentId+"\");' >"+DebitDocumentNo+"</a>";

		}
		else
		{
			//Pass Bill No.
			BillListTableObject.childNodes[totalBillListRows].childNodes[2].innerHTML="<a href='javascript:viewBill(\""+DebitDocumentNo+"\");' >"+DebitDocumentNo+"</a>";
		}
		
		//Bill Currency Alias 
		BillListTableObject.childNodes[totalBillListRows].childNodes[3].innerHTML=CurrencyAlias;

		if(Columns == 10)
		{		
			// Payee Account Name
			BillListTableObject.childNodes[totalBillListRows].childNodes[4].innerHTML=PayeeAccountName;

			//Bill Due Date
			BillListTableObject.childNodes[totalBillListRows].childNodes[5].innerHTML=DueDate;
						
			//Bill Amount
			BillListTableObject.childNodes[totalBillListRows].childNodes[6].innerHTML=strBillAmount;
				
			//Paid Amount
			BillListTableObject.childNodes[totalBillListRows].childNodes[7].innerHTML=strPaidAmount;
		
			//Due Amount
			BillListTableObject.childNodes[totalBillListRows].childNodes[8].innerHTML=strDueAmount;
		
			//Amount to be Paid
			BillListTableObject.childNodes[totalBillListRows].childNodes[9].innerHTML="<input type='text' class='amount-text' align='right' name='c_strPaidAmount"+Counter+"' value='"+strDueAmount+"' onblur='updateTotal( this ,this.value,"+Counter+");' onfocus='restore(this.value)' size='10'/>"
		}
		else if(Columns == 9)
		{
			//Bill Due Date
			BillListTableObject.childNodes[totalBillListRows].childNodes[4].innerHTML=DueDate;
						
			//Bill Amount
			BillListTableObject.childNodes[totalBillListRows].childNodes[5].innerHTML=strBillAmount;
				
			//Paid Amount
			BillListTableObject.childNodes[totalBillListRows].childNodes[6].innerHTML=strPaidAmount;
		
			//Due Amount
			BillListTableObject.childNodes[totalBillListRows].childNodes[7].innerHTML=strDueAmount;
		
			//Amount to be Paid
			BillListTableObject.childNodes[totalBillListRows].childNodes[8].innerHTML="<input type='text' class='amount-text' align='right' name='c_strPaidAmount"+Counter+"' value='"+strDueAmount+"' onblur='updateTotal( this ,this.value,"+Counter+");' onfocus='restore(this.value)' />"
		}	
		

}

function BillListaddNotFoundRow()
{
	BillListtRows = parseFloat(BillListTableObject.children.length);
	newrow = BillListTableObject.insertRow(BillListtRows);
	
	
	newrow.insertCell();
	newrow.cells(0).innerHTML = "<td><b>NO UNPAID BILLS FOUND</b></td>";
	newrow.cells(0).className="tblfirstcol";
	newrow.cells(0).align="center";

	newrow.cells(0).colSpan = Columns;


}

function BillListaddTotalsRow() //Updates the values of the Totals Row
{
		BillAmountTotal=0.00;
		PaidAmountTotal=0.00;
		DueAmountTotal=0.00;
		
		BillListtRows = parseFloat(BillListTableObject.children.length);
		for(r=1;r<BillListtRows;r++)
		{
			BillAmountTotal = BillAmountTotal+parseFloat(BillListTableObject.childNodes[r].childNodes[5].innerText);
			PaidAmountTotal = PaidAmountTotal+parseFloat(BillListTableObject.childNodes[r].childNodes[6].innerText);
			DueAmountTotal =  DueAmountTotal+parseFloat(BillListTableObject.childNodes[r].childNodes[7].innerText);			
		}
		
		newrow = BillListTableObject.insertRow(BillListtRows);
		
		for (c=0;c<6;c++)
		{
			newrow.insertCell();
			newrow.cells(c).innerHTML = "<td>&nbsp;</td>";
			newrow.cells(c).className="tblrows";
			newrow.cells(c).align="right";
		}

		newrow.cells(0).colSpan = Columns-5;
		newrow.cells(0).align ="left";
		newrow.cells(1).width = "15%";
		newrow.cells(2).width = "10%";
		newrow.cells(3).width = "10%";
		newrow.cells(4).width = "10%";
		newrow.cells(5).width = "10%";

		BillListTableObject.childNodes[BillListtRows].childNodes[0].innerHTML="<b>Total Amount<b>";
		BillListTableObject.childNodes[BillListtRows].childNodes[2].innerHTML="<b>"+roundDecimal(BillAmountTotal)+"</b>";
		BillListTableObject.childNodes[BillListtRows].childNodes[3].innerHTML="<b>"+roundDecimal(PaidAmountTotal)+"</b>";
		BillListTableObject.childNodes[BillListtRows].childNodes[4].innerHTML="<b>"+roundDecimal(DueAmountTotal)+"</b>";
		BillListTableObject.childNodes[BillListtRows].childNodes[5].innerHTML="<b>"+roundDecimal(DueAmountTotal)+"</b>";
		
}

function BillListDeleteAllRows() // Deletes all rows of the table except the header
{
	var RowCount=0;
	for(j=0;j<BillListTableObject.children.length;j++)
	{
		if(BillListTableObject.childNodes[j].nodeName == "TR") // Because Some Script elements are there
		{
			RowCount++;
			totalBillListRows =RowCount; 
		}
		
	}

	for(j=1;j<totalBillListRows;j++)
	{
		BillListTableObject.deleteRow(1);
	}
	
}




////viral 's treenode
var htmltags
var status;


function TreeNode()
{
	var ParentName;	
	var ParentID;		
	var ChildName;
	var ChildID;
	var ChildType;
	var ChildAccNo;
	var Root;
	var Checked;
	var ImageStatus;
    var DispStatus;
	var UseChkBox;
	var UseLnk;
	var Lnk;
	var UseBillableLnk;
	var BillableLnk;
	var UsePayableLnk;
	var PayableLnk;
    var UseRadio;
	var Billable;
	var Payable;
}



function treeparam()
{
	this.usetextlinks =1
	this.startallopen = 1
	this.useframes = 0
	this.useicons =0
	this.wraptext = 0
	this.perservestate = 1
	this.iconpath = '/images/'
	this.highlight = 1
	this.highlight_color = 'blue'
	this.highlight_bg    = 'white'
    this.buildall = 0
	this.iconsourcet = 'tick.jpg'
	this.iconsourcec = 'cross.jpg'
	this.iconsourceb = 'billable.jpg'
	this.iconsourcep = 'payable.jpg'
	this.iconsourcenb = 'nonbillable.jpg'
	this.iconsourcenp = 'nonpayable.jpg'
	this.usecheck = false
	this.useimg = false
	this.useradio = false
	this.billable = false
	this.payable = false

}


	function changeBillable(imgObj,nodeId)
	{
		//if(true)return;
		//alert(imgObj.src);
		imagename=imgObj.src;
		a=imagename.split('/');

		nodeObj = getNodeById(nodeId);

		if(a[a.length-1] == "billable.jpg")
		{
			a[a.length-1]="nonbillable.jpg";
			nodeObj.Billable=false;
		}
		else
		{
			a[a.length-1]="billable.jpg";
			nodeObj.Billable=true;
		}

		//alert(a.join('/'));
		imgObj.src=a.join('/');
		//alert(nodeId);
	}

	function changePayable(imgObj,nodeId)
	{
		//if(true)return;
		//alert(imgObj.src);
		imagename=imgObj.src;
		a=imagename.split('/');

		nodeObj = getNodeById(nodeId);

		if(a[a.length-1] == "payable.jpg")
		{
			a[a.length-1]="nonpayable.jpg";
			nodeObj.Payable=false;
		}
		else
		{
			a[a.length-1]="payable.jpg";
			nodeObj.Payable=true;
		}

		//alert(a.join('/'));
		imgObj.src=a.join('/');
		//alert(nodeId);
	}

	function getNodeById(nodeId)
	{
		totalNodes = TreeNodeArray.length;
		nodeObj = null ;
		for(i=0; i < totalNodes ; i++ )
		{
			if( TreeNodeArray[i].ChildID == nodeId )
			{
				nodeObj = TreeNodeArray[i];
				break;
			}
		}

		return nodeObj ;
	}



			function checkAll(flg,root)
			{
				//alert("chekcalla");
				if(flg==true)
				{
				
					for(var i=0;i<TreeNodeArray.length;i++)
					{
						if(TreeNodeArray[i].ChildID == root )
						{
							//alert("chekcalla");
							if(TreeNodeArray[i].Checked == "false")
							{
								status = true;
								TreeNodeArray[i].Checked = "true";
								break;
							}	
							else
							{
								status = false;
								TreeNodeArray[i].Checked = "false";
								break;

							}
						}
					
					}
				}
				for(var i=0;i<TreeNodeArray.length;i++)
				{
					if(TreeNodeArray[i].ParentID == root)
					{
					checkAll(false,TreeNodeArray[i].ChildID);		
						if(document.getElementById(TreeNodeArray[i].ChildID))
						{
							document.getElementById(TreeNodeArray[i].ChildID).checked = status;
						}

						if(status == true)
						{
							
							TreeNodeArray[i].Checked = "true";
						}
						else
						{
							
							TreeNodeArray[i].Checked = "false";
						}
					}
					
				
				}

			
				}
				
				
				function CreateTreeArray(type,original)
				{		
				var flag;
				var finaltree = null;
				finaltree = new Array();
					
				
				if(type == "NONE")
				{
					for(var i = 0;i<original.length;i++)
					{
						flag = true;
						for(var j=0;j<original.length;j++)
						{
							if(original[i].ChildID==original[j].ParentID)
							{
							flag = false;
							}
																	
						}
						original[i].UseChkBox = flag;
					
					}

				
				}
				else if(type == "LINK")
				{
					
				
					for(var i = 0;i<original.length;i++)
					{
						flag = true;
						for(var j=0;j<original.length;j++)
						{
							if(original[i].ChildID==original[j].ParentID)
							{
							flag = false;
							}
																	
						}
						original[i].UseChkBox = flag;
						
						if(flag == false)
						{
							
							original[i].Lnk = "javascript:checkAll(true,\""+original[i].ChildID+"\");"
						}

					
					}
				
				
				}
				else if(type == "CHECKBOX")
				{
					for(var i = 0;i<original.length;i++)
					{
						flag = true;
						original[i].UseChkBox = flag;
						
					
					}
				
				
				}
			
			}


function showTree(parentid,parentObj)
{
		//pobj = new treeparam()
	//	alert("hi there");
		//alert(TreeNodeArray[1].ChildName);
		var htmltags
		var TempArray = new Array();
		var count = 5
		var newcount = 0;
		
		initializeTree(pobj)
		
		for(var index= 0;index < TreeNodeArray.length ;index++)
		{
			if(TreeNodeArray[index].ParentID == parentid)
			{
			  	 TempArray[newcount] = new TreeNode();	
				 TempArray[newcount].ParentID = TreeNodeArray[index].ParentID;
				 TempArray[newcount].ChildName = TreeNodeArray[index].ChildName;
				 TempArray[newcount].ChildType = TreeNodeArray[index].ChildType;
				 TempArray[newcount].ChildID = TreeNodeArray[index].ChildID;
				 TempArray[newcount].Checked = TreeNodeArray[index].Checked;	
				 TempArray[newcount].ImageStatus = TreeNodeArray[index].ImageStatus;	
				 TempArray[newcount].UseChkBox = TreeNodeArray[index].UseChkBox;
				 TempArray[newcount].UseLnk = TreeNodeArray[index].UseLnk;
				 TempArray[newcount].Lnk = TreeNodeArray[index].Lnk;
				 TempArray[newcount].UseRadio = TreeNodeArray[index].UseRadio;
				 TempArray[newcount].Billable = TreeNodeArray[index].Billable;
				 TempArray[newcount].Payable = TreeNodeArray[index].Payable;
				 TempArray[newcount].UseBillableLnk = TreeNodeArray[index].UseBillableLnk;
				 TempArray[newcount].BillableLnk = TreeNodeArray[index].BillableLnk;
				 TempArray[newcount].UsePayableLnk = TreeNodeArray[index].UsePayableLnk;
				 TempArray[newcount].PayableLnk = TreeNodeArray[index].PayableLnk;

				 newcount++;

			}
		
		} 
		
		for(var i=0;i<newcount;i++)
		{
				
				if(parentid == -1)
				{
					gFldStr = gFld(TempArray[i].ChildName,"");
					foldersTree = gFldStr;
					showTree(TempArray[i].ChildID,foldersTree);	
				}
				else
				{	
					gFldStr = gFld(TempArray[i].ChildName,TempArray[i].Lnk);
				     parentObjectSub = insFld(parentObj,gFldStr);
					 htmltags = "<td valign=middle>" 
					 
					 if(pobj.usecheck==true  && TempArray[i].UseChkBox==true)
					 {
									 
							 if(TempArray[i].Checked=="true")
							 {
								htmltags = htmltags + "<input type=checkbox name='BOX' id="+TempArray[i].ChildID+" value =" + TempArray[i].ChildID +" checked >"
								
					    	 }
							 else
							 {
								htmltags = htmltags + "<input type=checkbox name='BOX' id="+TempArray[i].ChildID+" value =" + TempArray[i].ChildID + " >"
								
       						 }	
	
				    }
					
					if(pobj.useradio==true && TempArray[i].UseRadio == true)
					{
					
						   htmltags = htmltags + "<input type=radio name='treeradio' id = 'radio" +TempArray[i].ChildID + "'>"
										
					}
					
					if(pobj.useimg==true && TempArray[i].UseRadio==true)
					{
								
							if(TempArray[i].ImageStatus==true)
							{
								pobj.iconsource = pobj.iconsourcet;
							}
							else
							{
								pobj.iconsource = pobj.iconsourcec;
							}
									
							htmltags = htmltags + 	"<img src=" + ICONPATH + pobj.iconsource + ">"			
						
					}		
							

////
//alert("Billable : "+TempArray[i].Billable+ " : i : "+i);
//alert("Payable : "+TempArray[i].Payable+ " : i : "+i);

					if(TempArray[i].Billable==true)
					{
							pobj.iconsource = pobj.iconsourceb;
					}
					else
					{
							pobj.iconsource = pobj.iconsourcenb;
					}
					
					if(TempArray[i].UseBillableLnk)
					{
						htmltags = htmltags + 	"&nbsp;<img style='cursor:hand;' src=\"" + ICONPATH + pobj.iconsource + "\" onmousedown=\"" +  TempArray[i].BillableLnk + "\" border='0'></a>&nbsp;"
					}
					else
					{
						htmltags = htmltags + 	"&nbsp;<img src=\"" + ICONPATH + pobj.iconsource + "\" border='0'>&nbsp;"
					}
					
					
					if(TempArray[i].Payable==true)
					{
							pobj.iconsource = pobj.iconsourcep;
					}
					else
					{
							pobj.iconsource = pobj.iconsourcenp;
					}
					
					if(TempArray[i].UsePayableLnk)
					{
						htmltags = htmltags + 	"&nbsp;<img style='cursor:hand;'  src=\"" + ICONPATH + pobj.iconsource + "\" onmousedown=\"" +  TempArray[i].PayableLnk + "\" border='0'></a>&nbsp;"
					}
					else
					{
						htmltags = htmltags + 	"&nbsp;<img src=\"" + ICONPATH + pobj.iconsource + "\" border='0' >&nbsp;"
					}


////
							htmltags = htmltags +  "</td>"
							parentObjectSub.prependHTML = htmltags;					
					
					
					showTree(TempArray[i].ChildID,parentObjectSub);	
					
					}

					
					
					
				
				}
}


	/*	function ChangingStatus(id)
		{
			alert("changingstatus");
		}
	*/

	/*	function ChangingStatus(id)
		{
			alert("changingstatus");
			for(var index= 0;index<TreeNodeArray.length ;index++)
			{
				if(TreeNodeArray[index].ChildID ==	id)
				{
					if ((document.getElementById(TreeNodeArray[index].ChildID))!=null)
					{
					
						if(document.getElementById(TreeNodeArray[index].ChildID).checked  == true)
						{
							TreeNodeArray[index].Checked = "true";
								
						}
						else
						{
							TreeNodeArray[index].Checked = "false";
								
						}	
						
					}
				
				
				}
			}
		}



				
/*		function changingStatus(id)
{

		var  count = 0;
		var newcount = 0;
		
		//var chkbox = document.getElementById(id);
		var nwchkbox
		var flag=true;	
		alert("jgkdsa");
      	for(var index= 0;index<TreeNodeArray.length ;index++)
		{
			if ((document.getElementById(TreeNodeArray[index].ChildID))!=null)
			{
							
				if(TreeNodeArray[index].ChildID ==	id)
				{
					if(document.getElementById(TreeNodeArray[index].ChildID).checked  == true)
					{
						TreeNodeArray[index].Checked = "true";
						//alert("in chkstatus1 index = "+ index +TreeNodeArray[index].Checked);
							
					}
					else
					{
						TreeNodeArray[index].Checked = "false";
						//alert("in chkstatus1 index = "+ index +TreeNodeArray[index].Checked);
							
					}
				}
			}
		}
	   
		}

		/*if(chkbox.checked==true)
	   {
				for(var index= 0;index<TreeNodeArray.length ;index++)
				{
					if(TreeNodeArray[index].ParentID == id)
					{
			
							if ((document.getElementById(TreeNodeArray[index].ChildID))!=null)
							{
							
							document.getElementById(TreeNodeArray[index].ChildID).checked  = true;
							TreeNodeArray[index].Checked = "true" ;
						//	alert("in chkstatus index = "+ index +TreeNodeArray[index].Checked);
							changingStatus(TreeNodeArray[index].ChildID)
							
							
							}	

					
					}
				  
				   if(TreeNodeArray[index].ChildID == id)
				  {
					   
	   				  flag=true;			
	
					   for(var i=0;i<TreeNodeArray.length;i++)
					   {
							if(TreeNodeArray[i].ParentID==TreeNodeArray[index].ParentID)
						   {	
								if(document.getElementById(TreeNodeArray[i].ChildID).checked==false)
							   {
								   flag=false
							   }
						   
						   }
					 
					  }
				
					  if(flag==true)
					  {
				    		if(document.getElementById(TreeNodeArray[index].ParentID)!=null)
						   {
	
								document.getElementById(TreeNodeArray[index].ParentID).checked = true;	
								TreeNodeArray[index].Checked = "true" ;
							//alert("in chkstatus index = "+ index +TreeNodeArray[index].Checked);

								/*for(var i=0;i<TreeNodeArray.length;i++)
							    {
									if(TreeNodeArray[i].ChildID==TreeNodeArray[index].ParentID)
									{
										if(TreeNodeArray[i].ParentID!='-1')
										{
											changingStatus(TreeNodeArray[i].ParentID)
										}
		
									}
								
								}
						   
						   } 
					}				
			
				} 
				    		
				}	
	   
	   
	   
	   
	   }
	  
	   else
	   {
               	
							
	    		for(var index= 0;index < TreeNodeArray.length ;index++)
				{
					
					
					if(TreeNodeArray[index].ChildID == id)
					{
							if ((document.getElementById(TreeNodeArray[index].ParentID))!=null)
							{
							document.getElementById(TreeNodeArray[index].ParentID).checked  = false;
							TreeNodeArray[index].Checked = "false" ;
						//	alert("in chkstatuselse index = "+ index +TreeNodeArray[index].Checked);

								if(TreeNodeArray[index].ParentID!='-1')
			    				{
									changingStatus(TreeNodeArray[index].ParentID)
            					}
							}	
								
					
					}
				
				} 
				
	    }					
							
//showAdjustment();							
							
}		*/
		
	   



	   
	   
	   
	   
	   

	 


function MakingSubTree(id)
{
		
		var  count = 0;
		var newcount = 0;
		var inx
	   for(inx=0;inx<TreeNodeArray.length;inx++)
	   {
	    	if(TreeNodeArray[inx].ChildID == id)
		   {
				break;
		   }	   
	   
	   }
	   	      
	   TreeNodeArray[inx].DispStatus=true
	   
		for(var index=0;index<TreeNodeArray.length;index++)
		{
			if(TreeNodeArray[index].ParentID == id)
			{
				TreeNodeArray[index].DispStatus = true ;
				MakingSubTree(TreeNodeArray[index].ChildID);
			}
	
		}					

	 
}	 


function assignToArray()
{

	var TempnodeArray=new Array();
   var  newcount=0
	 for(var index=0;index<TreeNodeArray.length;index++)
	   {
	   
			if(TreeNodeArray[index].DispStatus == true)
		    {
	    		 TempnodeArray[newcount] = new TreeNode();	
				 TempnodeArray[newcount].ParentID = TreeNodeArray[index].ParentID;
				 TempnodeArray[newcount].ChildName = TreeNodeArray[index].ChildName;
				 TempnodeArray[newcount].ChildType = TreeNodeArray[index].ChildType;
				 TempnodeArray[newcount].ChildID = TreeNodeArray[index].ChildID;
				 TempnodeArray[newcount].Checked = TreeNodeArray[index].Checked;	
				 TempnodeArray[newcount].ImageStatus = TreeNodeArray[index].ImageStatus;	
				 TempnodeArray[newcount].DispStatus = TreeNodeArray[index].DispStatus;
				 TempnodeArray[newcount].UseChkBox = TreeNodeArray[index].UseChkBox;
				 TempnodeArray[newcount].UseLnk = TreeNodeArray[index].UseLnk;
				 TempnodeArray[newcount].Lnk = TreeNodeArray[index].Lnk;
				 TempnodeArray[newcount].Billable = TreeNodeArray[index].Billable;
				 TempnodeArray[newcount].Payable = TreeNodeArray[index].Payable;
				 TempnodeArray[newcount].UseBillableLnk = TreeNodeArray[index].UseBillableLnk;
				 TempnodeArray[newcount].BillableLnk = TreeNodeArray[index].BillableLnk;
				 TempnodeArray[newcount].UsePayableLnk = TreeNodeArray[index].UsePayableLnk;
				 TempnodeArray[newcount].PayableLnk = TreeNodeArray[index].PayableLnk;


				 newcount++;	
		   }
	   
	   }
	  
      TreeNodeArray = new Array();
			
		
     		
     		TreeNodeArray[0] = new TreeNode();
			TreeNodeArray[0].ParentName="";
			TreeNodeArray[0].ParentID="-1";		
			TreeNodeArray[0].ChildName="Root";
			TreeNodeArray[0].ChildID=strRootID;
			TreeNodeArray[0].ChildType="";
			TreeNodeArray[0].Root="";
			TreeNodeArray[0].Checked="true";
			TreeNodeArray[0].ImageStatus=false;

		   
		
		
		
		
		
		
		for(var index=0;index<newcount;index++)
	    {
	     
		         TreeNodeArray[index+1] = new TreeNode();	
				 TreeNodeArray[index+1].ParentID = TempnodeArray[index].ParentID;
				 TreeNodeArray[index+1].ChildName = TempnodeArray[index].ChildName; 
				 TreeNodeArray[index+1].ChildType = TempnodeArray[index].ChildType;
				 TreeNodeArray[index+1].ChildID = TempnodeArray[index].ChildID;
				 TreeNodeArray[index+1].Checked = TempnodeArray[index].Checked;	
        		 TreeNodeArray[index+1].ImageStatus =  TempnodeArray[index].ImageStatus;	
				 TreeNodeArray[index+1].DispStatus = TempnodeArray[index].DispStatus;
				 TreeNodeArray[index+1].UseChkBox = TempnodeArray[index].UseChkBox;
				 TreeNodeArray[index+1].UseLnk = TempnodeArray[index].UseLnk;
				 TreeNodeArray[index+1].Lnk = TempnodeArray[index].Lnk;
				 TreeNodeArray[index+1].Billable = TempnodeArray[index].Billable;
				 TreeNodeArray[index+1].Payable = TempnodeArray[index].Payable;
				 TreeNodeArray[index+1].UseBillableLnk = TempnodeArray[index].UseBillableLnk;
				 TreeNodeArray[index+1].BillableLnk = TempnodeArray[index].BillableLnk;
				 TreeNodeArray[index+1].UsePayableLnk = TempnodeArray[index].UsePayableLnk;
				 TreeNodeArray[index+1].PayableLnk = TempnodeArray[index].PayableLnk;
			
		}

}


function firstLevelTreeArray(root)
{
	//var TempnodeArray=new Array();
	var newcount=1
	TreeNodeArray[0] = new TreeNode();
	//TreeNodeArray[0].ParentName="";
	TreeNodeArray[0].ParentID='-1';		
	TreeNodeArray[0].ChildName="Root";
	TreeNodeArray[0].ChildID=root;
	TreeNodeArray[0].ChildType="";
	TreeNodeArray[0].Root="";
	TreeNodeArray[0].Checked="true";
	TreeNodeArray[0].ImageStatus=false;
	TreeNodeArray[0].UseChkBox = false;
	TreeNodeArray[0].UseLnk=false;
	TreeNodeArray[0].Billable=false;
	TreeNodeArray[0].Payable = false;
	TreeNodeArray[0].UseBillableLnk=false;
	TreeNodeArray[0].UsePayableLnk=false;

	for(var index=0;index<OriginalTree.length;index++)
	{
		if(OriginalTree[index].ParentID == root)
		{	
			//alert("root");
			TreeNodeArray[newcount] = new TreeNode();	
			TreeNodeArray[newcount].ParentID = OriginalTree[index].ParentID;
			TreeNodeArray[newcount].ChildName = OriginalTree[index].ChildName;
			TreeNodeArray[newcount].ChildType = OriginalTree[index].ChildType;
			TreeNodeArray[newcount].ChildID = OriginalTree[index].ChildID;
			TreeNodeArray[newcount].Checked = OriginalTree[index].Checked;	
			TreeNodeArray[newcount].ImageStatus = OriginalTree[index].ImageStatus;	
			TreeNodeArray[newcount].DispStatus = OriginalTree[index].DispStatus;
			TreeNodeArray[newcount].UseChkBox = OriginalTree[index].UseChkBox;
			TreeNodeArray[newcount].UseLnk = OriginalTree[index].UseLnk;
			TreeNodeArray[newcount].Lnk = OriginalTree[index].Lnk;
			TreeNodeArray[newcount].Billable = OriginalTree[index].Billable;
			TreeNodeArray[newcount].Payable = OriginalTree[index].Payable;
			TreeNodeArray[newcount].UseBillableLnk = OriginalTree[index].UseBillableLnk;
			TreeNodeArray[newcount].BillableLnk = OriginalTree[index].BillableLnk;
			TreeNodeArray[newcount].UsePayableLnk = OriginalTree[index].UsePayableLnk;
			TreeNodeArray[newcount].PayableLnk = OriginalTree[index].PayableLnk;

			newcount++;	

		}
	
	}

}


function expandTree(folderObj)
{
    var childObj;
    var i;

    //Open folder
    if (!folderObj.isOpen)
      parent.childFrame.clickOnNodeObj(folderObj)

    //Call this function for all folder children
    for (i=0 ; i < folderObj.nChildren; i++)  {
      childObj = folderObj.children[i]
      if (typeof childObj.setState != "undefined") {//is folder
        expandTree(childObj)
      }
    }
}

function collapseTree()
{
	//hide all folders
	parent.childFrame.clickOnNodeObj(parent.childFrame.foldersTree)
	//restore first level
	parent.childFrame.clickOnNodeObj(parent.childFrame.foldersTree)
}


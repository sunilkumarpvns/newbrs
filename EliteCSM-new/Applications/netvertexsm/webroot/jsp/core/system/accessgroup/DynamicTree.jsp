<%	
    String basePath = request.getContextPath();
%>
<html>
<head>
<style>
     BODY {background-color: white}
     TD {font-size: 12px; 
         font-family: arial, verdana,helvetica; 
  	   text-decoration: none;
  	   white-space:nowrap;}
     A  {text-decoration: none;
         color: black}
</style>
<link rel="stylesheet" href="<%=basePath%>/css/styles.css" type="text/css">
</head>
<body>

<form name='myform' >
<input type=button value="Expand All" onClick="expandTree(parent.childFrame.foldersTree)" class="light-btn">
<input type=button value="Collapse All" onClick="collapseTree()" class="light-btn">
<div style="position:absolute; top:0px; left:0px; "><table border=0><tr><td><font size=-2><a style="font-size:7pt;text-decoration:none;color:silver" href="http://www.treemenu.net/" target=_blank></a></font></td></table></div>
<div id="mydiv" name="mydiv" style="position:absolute; top:50px; left:0px; ">
</div>



<script language="javascript1.2" src="<%=basePath%>/js/ua.js" type="text/javascript"></script>
<script language="javascript1.2" src="<%=basePath%>/js/TreeNode.js" type="text/javascript"></script>
<script language="javascript1.2" src="<%=basePath%>/js/ftiens4-in.js" type="text/javascript" ></script>
<script language="javascript1.2">
<%@ page import="com.elitecore.billing.operationsmanager.accessright.AccessRightActionConstant" %>
<%@ page import = "com.elitecore.billing.operationsmanager.bill.chargehead.ChargeHeadActionConstant" %>
<%@ page import="java.util.Hashtable" %>
var TreeNodeArray;
var strRootID = parent.strRootID;
</script>


<script language = "javascript1.2">
	function test()
	{
	//	alert("in test onload");
	}

	function showAdjustment()
	{
		//alert("in showadjustment");
		if(TreeNodeArray == null)
			return;
		for(var index=0;index<TreeNodeArray.length;index++)
		{
			//alert("in for showadjustment");

			if ((document.getElementById(TreeNodeArray[index].ChildID))!=null)
			{
					
					if(document.getElementById(TreeNodeArray[index].ChildID).checked  == true)
					{
						TreeNodeArray[index].Checked = "true";
						//alert(TreeNodeArray[index].ChildName);
							
					}
					else
					{
						TreeNodeArray[index].Checked = "false";
						//alert("alert false" + TreeNodeArray[index].ChildName);
							
					}	
						
			}

		}
	}
	
	function setFinalTree()
	{
		//alert("in set final tree");
		//for(var k=0;k<parent.OriginalTree.length;k++)
		//{
			//parent.OriginalTree[k].Checked="false";
		//}
		if(TreeNodeArray == null)
		return;

		for(var i=0;i<TreeNodeArray.length;i++)
		{
			if(document.getElementById(TreeNodeArray[i].ChildID)!=null)
			{
				for(var j=0;j<parent.OriginalTree.length;j++)
				{

					if(parent.OriginalTree[j].ChildID == TreeNodeArray[i].ChildID)
					{
						if(document.getElementById(TreeNodeArray[i].ChildID).checked == true)
						{   
	//						alert("In true part");
							//alert(parent.OriginalTree[j].ChildName);
							parent.OriginalTree[j].Checked = "true";	
							//alert(TreeNodeArray[i].ChildName +  "true");
							break;
						}
						else
						{
							//alert(TreeNodeArray[i].ChildName + "false");
							parent.OriginalTree[j].Checked = "false";	
							break;
						}
					}

				}
			}			
		}
	
	}

	function initArray()
	{
		TreeNodeArray = null;
		TreeNodeArray=new Array();
		for(var index=0;index<parent.OriginalTree.length;index++)
		{
					 TreeNodeArray[index] = new TreeNode();	
					 TreeNodeArray[index].ParentID = parent.OriginalTree[index].ParentID;
					 TreeNodeArray[index].ChildName = parent.OriginalTree[index].ChildName;
					 TreeNodeArray[index].ChildType = parent.OriginalTree[index].ChildType;
					 TreeNodeArray[index].ChildID = parent.OriginalTree[index].ChildID;
					 TreeNodeArray[index].Checked = parent.OriginalTree[index].Checked;

					 TreeNodeArray[index].ImageStatus = parent.OriginalTree[index].ImageStatus;	
					 TreeNodeArray[index].DispStatus = parent.OriginalTree[index].DispStatus;
					 TreeNodeArray[index].UseLnk = parent.OriginalTree[index].UseLnk;
					 TreeNodeArray[index].UseChkBox = parent.OriginalTree[index].UseChkBox;
					 TreeNodeArray[index].UseRadio = parent.OriginalTree[index].UseRadio;
					 TreeNodeArray[index].Lnk = parent.OriginalTree[index].Lnk;	
		}
	//	alert('TreeNode length : ' + TreeNodeArray.length);

	}
</script> 






<script language="javascript1.2">	

pobj = new treeparam();
pobj.startallopen = 0;
pobj.iconpath ='<%=basePath%>/images/';

if(parent.document.forms[0].c_strActionMode.value == "" ){
	//alert("in update dynamic");
	pobj.usecheck = true;
	pobj.useimg = false;
}else if(parent.document.forms[0].c_strActionMode.value == "" ){
	pobj.usecheck = false;
	pobj.useimg = false;
	pobj.useradio = true;
}else{
	//alert("in view dynamic");
	pobj.usecheck = false;
	pobj.useimg = true;
}



</script>

<!-- <input type=button value="show value" onclick="" > -->


<script language="javascript1.2">	

function drawTree()
{

		//IDLen=parent.frmUpdateAccessGrp.IDLength.value;
		//IDValue=parent.frmUpdateAccessGrp.chkID.value;
		//alert(parent.forms[0].name);
		IDLen=parent.document.forms[0].IDLength.value;
		IDValue=parent.document.forms[0].chkID.value;
	
		initArray();

		showAdjustment();

		document.getElementById("mydiv").innerHTML="";
	
//CreateTreeArray("LINK",TreeNodeArray);
		if (IDLen==1)
		{
			//alert("IN IDLen = 1");
			MakingSubTree(IDValue);
		}
		else if (IDLen>1)
		{
			IDArray=IDValue.split(",")
			for(i=0;i<IDArray.length;i++)
			{
				MakingSubTree(IDArray[i]);
			}
		}
	assignToArray();

	if(TreeNodeArray.length>=0)
	{
		if(parent.document.forms[0].c_strActionMode.value == "")
		{
			CreateTreeArray("NONE",TreeNodeArray);
		}
		else
		{

			CreateTreeArray("LINK",TreeNodeArray);
		}
		

		showTree('-1',"");
		initializeDocument();
	}

}
	
	/*
	
	function drawTree(chID)
	{
		initArray();
		MakingSubTree(chID);
	//	MakingSubTree('ACT002');
		assignToArray();
		showTree('-1',"");
		//alert(document.myform.mydiv);
		//alert(mydiv);
		//alert(myform.mydiv);
		//alert(myform.mydiv.document);
		initializeDocument();
	}
	*/

</script>
<script language = "javascript1.2">

//alert("before initdrawtree");
if(parent.document.forms[0].c_strActionMode.value != "")
{

	parent.initDrawTree();
}
</script>
</form>
</body>
</html>

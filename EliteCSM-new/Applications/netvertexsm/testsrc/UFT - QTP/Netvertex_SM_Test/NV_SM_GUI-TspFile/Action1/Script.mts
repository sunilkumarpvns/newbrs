'Function Name : Repository1
'Created by : Crestel Automation Team using CATS [Crestel Automation Test Solution]
'Created on : 07-Feb-2015

Dim vFilePath
If DataTable.LocalSheet.GetRowCount = 0 Then
		vFilePath = PathFinder.Locate("Framework\TestData\Repository1.xls")
		Call DataTable.ImportSheet(vFilePath, "Sheet1", DataTable.LocalSheet.Name)
		DataTable.LocalSheet.SetCurrentRow(Environment("ActionIteration"))
End If
Call Repository1(DataTable.LocalSheet.Name)
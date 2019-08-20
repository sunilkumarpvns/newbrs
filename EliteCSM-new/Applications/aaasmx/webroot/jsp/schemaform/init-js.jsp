<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/jquery-ui.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/tv4.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/ace.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/angular.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/angular-sanitize.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/clipboard.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/moment.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/ZeroClipboard.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/ng-clip.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/swfobject.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/ui-bootstrap.min.js"></script>
<!-- <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.21/angular.min.js"></script> -->
<!-- <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.21/angular-sanitize.min.js"></script> -->


<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/sortable.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/ui-ace.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/ObjectPath.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/picker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/picker.date.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/nl_NL.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/tinymce.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/tx-tinymce.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/spectrum.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/jquery.spectrum-sv.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/angular-spectrum-colorpicker.min.js"></script>


<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/schema-form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/bootstrap-decorator.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/bootstrap-datepicker.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/bootstrap-colorpicker.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/schemaform/bootstrap-tinymce.js"></script>

<script type="text/javascript">
	
	function copyToClipboard(elementId) {
		SelectText("clipboard-div");
		document.execCommand('copy');
	}
	
	function SelectText(element) {
	    var doc = document
	        , text = doc.getElementById(element)
	        , range, selection
	    ;    
	    if (doc.body.createTextRange) {
	        range = document.body.createTextRange();
	        range.moveToElementText(text);
	        range.select();
	    } else if (window.getSelection) {
	        selection = window.getSelection();        
	        range = document.createRange();
	        range.selectNodeContents(text);
	        selection.removeAllRanges();
	        selection.addRange(range);
	    }
	}
	
</script>

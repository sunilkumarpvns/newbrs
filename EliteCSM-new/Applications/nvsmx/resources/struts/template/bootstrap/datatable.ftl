<#--
/*Author : Raj Kirpalsinh
 *This template is used to create html table's top section including the start tag of table,thead and tr elements.
 *it also inject the common.datatable.js which is a required java script for the usage of datatable java script framework.
 * <script type="text/javascript" src="/nvsmx/struts/datatables/js/commons.dataTables.js"></script>
 */

-->

<script src="${request.contextPath}/datatables/js/commons.dataTables.js"></script>
<#--<link rel="stylesheet" type="text/css" href="${request.contextPath}/datatables/bootstrap/commons.dataTables.css" /> -->
<#if (parameters.showFilter?default("false") == "true")>
<div class="row customFilter">
    <div class="col-xs-12">
        <div class="form-group ">
            <div class="  controls">
                <input name="customFilterForAjax"  maxlength="100" class="form-control" onkeyup="filterTableRows(this.value,'${parameters.id?default('')?html}')" placeholder="Filter" type="text">
            </div>
        </div>
    </div>
</div>


</#if>
<table id="${parameters.id?default("")?html}" style="${parameters.style?default("")?html}" class="${parameters.cssClass?default("")?html}" width="${parameters.width?default("")?html}" >
<#if parameters.caption??>
	<caption style="${parameters.captionStyle?default("")}" class="${parameters.captionCssClass?default("")}">
    	${parameters.caption}
	</caption> 
</#if> 
<thead>
<tr>

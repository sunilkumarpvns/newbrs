<%@taglib uri="/struts-tags/ec" prefix="s"%>
<%@taglib uri="http://www.elitecore.com/nvsmx/tags" prefix="nv" %>
<style type="text/css">
    .form-group {
        width: 100%;
        display: table;
        margin-bottom: 2px;
    }
</style>
<div class="panel panel-primary">
    <div class="panel-heading" style="padding: 8px 15px">
        <h3 class="panel-title" style="display: inline;">
            <s:property value="name"/>
        </h3>
        <div class="nv-btn-group" align="right">
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="Audit History"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/audit/audit/${id}?auditableResourceName=${name}&refererUrl=/sm/filelocation/file-location/${id}'">
                    <span class="glyphicon glyphicon-eye-open" ></span>
                </button>
			</span>
			<span class="btn-group btn-group-xs">
				<button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="edit"
                        onclick="javascript:location.href='${pageContext.request.contextPath}/sm/filelocation/file-location/${id}/edit'">
                    <span class="glyphicon glyphicon-pencil"></span>
                </button>
			</span>
            <span class="btn-group btn-group-xs" data-toggle="confirmation-singleton" onmousedown="deleteConfirm()" data-href="${pageContext.request.contextPath}/sm/filelocation/file-location/${id}?_method=DELETE">
			    <button type="button" class="btn btn-default header-btn" data-toggle="tooltip" data-placement="bottom" title="delete">
                    <span class="glyphicon glyphicon-trash"></span>
                </button>
			</span>
        </div>
    </div>
    <div class="panel-body">      
        <div class="row">
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="basic.detail" /></legend>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="row">
                            <s:label key="filelocation.description" value="%{description}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="filelocation.ratingtype" value="%{ratingType}" cssClass="control-label light-text" listValue="getValue()" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="filelocation.sortingtype" value="%{sortingType}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="filelocation.sortingcriteria" value="%{sortingCriteria}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />                            
                  		</div>
                   </div>
                   <div class="col-sm-4 leftVerticalLine">
                        <div class="row">
                            <s:hrefLabel key="filelocation.fileMapping" value="%{fileMappingData.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"
                                url="/sm/filemapping/file-mapping/%{fileMappingData.id}"/>
                            <s:label key="filelocation.inputpath" value="%{inputPath}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="filelocation.errorpath" value="%{errorPath}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="filelocation.archivepath" value="%{archivePath}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                            <s:label key="filelocation.outputpath" value="%{outputPath}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                  		</div>
                   </div>
                    <div class="col-sm-4 leftVerticalLine">	
                        <div class="row">
                            <%@include file="/WEB-INF/content/common/createdModifiedByUserDiv.jsp" %>
                        </div>
                    </div>
                </div>                
            </fieldset>
            
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="filelocation.output.config"/></legend>
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="filelocationDatas"
                                list="${FileOutputConfigDataListAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="Mapping" beanProperty="fileMappingData.name" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                            <nv:dataTableColumn title="Extension" beanProperty="extension" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                            <nv:dataTableColumn title="Field Separator" beanProperty="fieldSeparator" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                            <nv:dataTableColumn title="Location" beanProperty="location" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                        </nv:dataTable>	
                    </div>
                </div>
            </fieldset>
            
            <fieldset class="fieldSet-line">
                <legend align="top"><s:text name="filelocation.database.config"/></legend>
                	<div class="col-sm-6">
                        <div class="row">
                            <s:hrefLabel key="filelocation.db.datasource" value="%{databaseData.name}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7"
                                         url="/sm/database/database/%{databaseData.id}" />
                  			<s:label key="filelocation.table.name" value="%{tableName}" cssClass="control-label light-text" labelCssClass="col-xs-5" elementCssClass="col-xs-7" />
                  			
                  		</div>
                  	</div>
                  	
                <div class="row">
                    <div class="col-sm-12">
                        <nv:dataTable
                                id="columnMappingDatas"
                                list="${ColumnMappingDataListAsJson}"
                                width="100%"
                                showPagination="false"
                                showInfo="false"
                                cssClass="table table-blue">
                            <nv:dataTableColumn title="Source Keys" beanProperty="sourceKey" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                            <nv:dataTableColumn title="Column Name" beanProperty="columnName" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                            <nv:dataTableColumn title="Value Mapping" beanProperty="valueMapping" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                            <nv:dataTableColumn title="Default Value" beanProperty="defaultValue" tdCssClass="text-left text-middle" tdStyle="width:25%"/>
                        </nv:dataTable>	
                    </div>
                </div>
            </fieldset>
            <div class="row">
                  <div class="col-xs-12" align="center">                        
                      <button type="button" class="btn btn-primary btn-sm"  id="btnCancel" value="Cancel" onclick="javascript:location.href='${pageContext.request.contextPath}/sm/filelocation/file-location'"><span class="glyphicon glyphicon-backward" title="Back"></span>&nbsp;<s:text name="button.list"/></button>
                 </div>
            </div>            
        </div>
    </div>
</div>

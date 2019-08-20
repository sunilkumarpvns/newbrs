package com.elitecore.nvsmx.pd.controller.util;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;

import java.io.File;
import java.sql.Timestamp;

/**
 * @author jaidiptrivedi
 */
public abstract class ImportExportCTRL<T extends ResourceData> extends RestGenericCTRL<T> {


    protected static final ImportExportUtil importExportUtil = new ImportExportUtil(new SessionProviderImpl());
    private File importedFile;
    private String importedFileContentType;
    private String importedFileFileName;
    private String importAction;
    protected static final String XML = ".xml";
    protected static final String CSV = ".csv";
    protected static final String TEXT_XML = "text/xml";
    protected static final String TEXT_CSV = "text/csv";

    public void setImportedFileContentType(String importedFileContentType) {
        this.importedFileContentType = importedFileContentType;
    }

    public String getImportedFileContentType() {
        return importedFileContentType;
    }

    public String getImportedFileFileName() {
        return importedFileFileName;
    }

    public void setImportedFileFileName(String importedFileFileName) {
        this.importedFileFileName = importedFileFileName;
    }

    public String getImportAction() {
        return importAction;
    }

    public void setImportAction(String importAction) {
        this.importAction = importAction;
    }

    public void setImportedFile(File importedFile) {
        this.importedFile = importedFile;
    }

    public File getImportedFile() {
        return importedFile;
    }

    protected boolean isImportExportOperationInProgress() {
        return (Boolean) getRequest().getServletContext().getAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING);
    }

    protected void makeImportExportOperationInProgress(boolean inProgress) {
        getRequest().getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, inProgress);
    }

    protected String redirectError(String moduleName, String errorMessage) {
        LogManager.getLogger().error(moduleName, errorMessage);
        makeImportExportOperationInProgress(false);
        addActionError(errorMessage);
        return Results.LIST.getValue();
    }

    protected void setStaffInfo(ResourceData model) {
        model.setCreatedByStaff(getStaffData());
        model.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        model.setModifiedByStaff(model.getCreatedByStaff());
        model.setModifiedDate(model.getCreatedDate());
    }

    protected void setStaffInfoAPI(ResourceData model, StaffData staff) {
        model.setCreatedByStaff(staff);
        model.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        model.setModifiedByStaff(model.getCreatedByStaff());
        model.setModifiedDate(model.getCreatedDate());
    }

}

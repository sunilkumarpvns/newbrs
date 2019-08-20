package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;

import java.io.File;

/**
 * This class is the root Controller for related Import/Export functionality
 * Any Resource which can be Import or Export , its Controller should extends this Controller
 * Created by aditya on 12/8/16.
 */
public abstract class ImportExportCTRL<T> extends EliteGenericCTRL<T> {

    protected static final ImportExportUtil importExportUtil = new ImportExportUtil(new SessionProviderImpl());
    private File   importedFile;
    private String importedFileContentType;
    private String importedFileFileName;
    private String importAction;
    private boolean importExportOperationInProgress;
    protected static final String XML = ".xml";
    protected static final String TEXT_XML = "text/xml";

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
        return  (Boolean) request.getServletContext().getAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING);

    }
    protected void makeImportExportOperationInProgress(boolean inProgress){
        request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, inProgress);
    }
    public abstract String getIncludeProperties();

}

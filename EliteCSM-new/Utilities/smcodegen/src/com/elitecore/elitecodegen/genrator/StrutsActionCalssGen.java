/**
 * Copyright (C) Elitecore Technologies Ltd.
 * StrutsActionCalssGen.java
 * Created on Jul 27, 2007
 * Last Modified on 
 * @author : kaushik vira
 */
package com.elitecore.elitecodegen.genrator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitecodegen.base.ActionBean;
import com.elitecore.elitecodegen.base.TableDataBean;
import com.elitecore.elitecodegen.base.ActionBean.Forward;
import com.elitecore.elitecodegen.controller.form.PackageGenarationForm;
import com.elitecore.elitecodegen.genrator.base.BaseCodeGen;
import com.elitecore.elitecodegen.genrator.base.BaseJavaCodeGen;

public class StrutsActionCalssGen extends BaseJavaCodeGen {
    
    private ActionBean    actionBean;
    
    private static String prefix = "com.elitecore";
    
    public StrutsActionCalssGen( TableDataBean tableBean ,
                                 PackageGenarationForm packageForm ,
                                 ActionBean actionBean ,
                                 String workingDir ) {
        
        this.actionBean = actionBean;
        this.applicationName = packageForm.getStrApplicationName();
        this.subModualName = packageForm.getStrSubMoudalName().replace(File.separator, ".");
        this.modualName = packageForm.getStrModualName();
        BaseCodeGen.tableBean = tableBean;
        this.packageName = actionBean.getActionClassPath().replace(File.separator, ".");
        this.className = actionBean.getAcitonClassName();
        this.realPath = workingDir;
        this.log = Logger.getLogger(StrutsFormClassGen.class);
    }
    
    @Override
    public void genrateFile() throws FileNotFoundException {
        
        this.writer = getPrintWritter();
        genrateBaseComment();
        
        this.writer.println("package " + this.packageName + "; ");
        this.writer.println("  ");
        this.writer.println("import javax.servlet.http.HttpServletRequest;    ");
        this.writer.println("import javax.servlet.http.HttpServletResponse;  ");
        this.writer.println(" ");
        this.writer.println("import org.apache.struts.action.ActionForm;     ");
        this.writer.println("import org.apache.struts.action.ActionForward;  ");
        this.writer.println("import org.apache.struts.action.ActionMapping;  ");
        this.writer.println("import org.apache.struts.action.ActionMessage;  ");
        this.writer.println("import org.apache.struts.action.ActionMessages; ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.AuthorizationException;                ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.communication.CommunicationException;               ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;   ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;             ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.FormatInvalidException;              ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidFileExtensionException;       ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;               ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.NullValueException;                  ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.ValueOutOfRangeException;            ");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.environmentsupport.EnvironmentNotSupportedException;");
        writer.println("import com.elitecore.elitesm.datamanager.core.exceptions.opererationfailed.OperationFailedException;         ");
        
        this.writer.println(" ");
        
        if (this.subModualName.equals(""))
            this.writer.println("import " + StrutsActionCalssGen.prefix + "." + this.applicationName.toLowerCase() + ".blmanager." + this.modualName.toLowerCase() + "." + initCap(this.modualName) + "BLManager;");
        else this.writer.println("import " + StrutsActionCalssGen.prefix + "." + this.applicationName.toLowerCase() + ".blmanager." + this.subModualName.toLowerCase() + "." + this.modualName.toLowerCase() + "." + initCap(this.modualName)
                + "BLManager;");
        
     
        this.writer.println("import com.elitecore."+ applicationName.toLowerCase() +".web.core.base.BaseWebAction;");
        this.writer.println("import " + StrutsActionCalssGen.prefix + "." + this.applicationName.toLowerCase() + ".util.logger.Logger;");
        this.writer.println("                                                                               ");
        this.writer.println("public class " + this.className + " extends BaseWebAction { ");
        this.writer.println("	                                                                       ");
        this.writer.println("	private static final String SUCCESS_FORWARD = \"success\";               ");
        this.writer.println("	private static final String FAILURE_FORWARD = \"failure\";               ");
        this.writer.println("	private static final String MODULE =\""+modualName.toUpperCase()+"\";");
        
        Iterator itaction = this.actionBean.getForward().iterator();
        while (itaction.hasNext()) {
            Forward forward = (Forward) itaction.next();
            this.writer.println("	private static final String " + forward.getName().toUpperCase() + " = \"" + forward.getName() + "\"; ");
        }
        this.writer.println(" ");
        this.writer.println("	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{ ");
        
        this.writer.println("try {");
        this.writer.println("		 Logger.logInfo(MODULE, \"Entered execute method of \" + getClass().getName()); ");
        this.writer.println("		                                                                                ");
        this.writer.println("			" + this.actionBean.getFormClass() + " " + this.modualName + "Form = (" + this.actionBean.getFormClass() + ")form; ");
        this.writer.println("			" + initCap(this.modualName) + "BLManager " + this.modualName + "BLManager = new " + initCap(this.modualName) + "BLManager();");
        this.writer.println("			                                                                                                        ");
    	this.writer.println("       request.setAttribute(\"responseUrl\", \"~~~~%%%~~~~\");      ");
        this.writer.println("       ActionMessage message = new ActionMessage(\"~~~~%%%~~~~\");");
        this.writer.println("       ActionMessages messages = new ActionMessages();          ");
        this.writer.println("       messages.add(\"information\", message);                    ");
        this.writer.println("       saveMessages(request,messages);         				   ");
        this.writer.println("       Logger.logInfo(MODULE, \"Returning success forward from \" + getClass().getName()); ");
        this.writer.println("       return mapping.findForward(SUCCESS_FORWARD);             ");
       
        writer.println("}"); 
        writer.println("catch (AuthorizationException authExp) {                                                                                           ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + authExp.getMessage());                              ");
        writer.println("    Logger.logTrace(MODULE, authExp);                                                                                               ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (EnvironmentNotSupportedException envException) {                                                                             ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + envException.getMessage());                         ");
        writer.println("    Logger.logTrace(MODULE, envException);                                                                                          ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (CommunicationException connException) {                                                                                      ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + connException.getMessage());                        ");
        writer.println("    Logger.logTrace(MODULE, connException);                                                                                         ");
        writer.println("    ActionMessage message = new ActionMessage(\"dictionary.create.failure\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (OperationFailedException opException) {                                                                                      ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + opException.getMessage());                          ");
        writer.println("    Logger.logTrace(MODULE, opException);                                                                                           ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("                                                                                                                                    ");
        writer.println("catch (ConstraintViolationException conException) {                                                                                 ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + conException.getMessage());                         ");
        writer.println("    Logger.logTrace(MODULE, conException);                                                                                          ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (FormatInvalidException forException) {                                                                                       ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + forException.getMessage());                         ");
        writer.println("    Logger.logTrace(MODULE, forException);                                                                                          ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("                                                                                                                                    ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (NullValueException nullValueException) {                                                                                     ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason NullValueException:\" + nullValueException.getMessage()); ");             
        writer.println("    Logger.logTrace(MODULE, nullValueException);                                                                                    ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessage message1 = new ActionMessage(\"errors.required\", nullValueException.getSourceField());                             ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    messages.add(\"information\", message1);                                                                                          ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (InvalidFileExtensionException invException) {                                                                                ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason \" + invException.getMessage());                         ");
        writer.println("    Logger.logTrace(MODULE, invException);                                                                                          ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (InvalidValueException invalidValueException) {                                                                               ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + invalidValueException.getMessage());                ");
        writer.println("    Logger.logTrace(MODULE, invalidValueException);                                                                                 ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (ValueOutOfRangeException valueOutOfRangeException) {                                                                         ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + valueOutOfRangeException.getMessage());             ");
        writer.println("    Logger.logTrace(MODULE, valueOutOfRangeException);                                                                              ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (DataValidationException validationException) {                                                                               ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + validationException.getMessage());                  ");
        writer.println("    Logger.logTrace(MODULE, validationException);                                                                                   ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (DataManagerException mgrException) {                                                                                         ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason :\" + mgrException.getMessage());                         ");
        writer.println("    Logger.logTrace(MODULE, mgrException);                                                                                          ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("catch (Exception exp) {                                                                                                             ");
        writer.println("    Logger.logError(MODULE, \"Error during Data Manager operation , reason : \" + exp.getMessage());                                 ");
        writer.println("    ActionMessage message = new ActionMessage(\"~~~~key~~~\");                                                         ");
        writer.println("    ActionMessages messages = new ActionMessages();                                                                                 ");
        writer.println("    messages.add(\"information\", message);                                                                                           ");
        writer.println("    saveErrors(request, messages);                                                                                                  ");
        writer.println("}                                                                                                                                   ");
        writer.println("finally {                                                                                                                           ");
        writer.println("}                                                                                                                                   ");
        writer.println(" return mapping.findForward(FAILURE_FORWARD); ");
        writer.println("}");
        writer.println("}");
       this.writer.close();
    }
    
}

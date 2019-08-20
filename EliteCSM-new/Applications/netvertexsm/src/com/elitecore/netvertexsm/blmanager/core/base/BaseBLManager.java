package com.elitecore.netvertexsm.blmanager.core.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;

import com.elitecore.netvertexsm.util.constants.BaseConstant;
import org.w3c.dom.Document;

import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;


public abstract class BaseBLManager {
    
    private static final String MODULE = "BASE BL MANAGER";
    
    private Timestamp systemTimeStemp;
    
    
    /*
     * @author kaushikVira
     * @param IDataManagerSession
     * @purpose close Session Safe way.
     */
    protected void closeSession( IDataManagerSession session ) throws DataManagerException {
        if(session != null)
            session.close();
    }
    
    /*
     * @author kaushikVira
     * @param IDataManagerSession
     * @purpose rollBack Session Safe way.
     */
    protected void rollbackSession( IDataManagerSession session ) throws DataManagerException {
        if(session != null)
            session.rollback();
    }
    
    /*
     * @author kaushikVira
     * @param IDataManagerSession
     * @purpose commit Session Safe way.
     */
    protected void commit( IDataManagerSession session ) throws DataManagerException {
        if(session != null)
            session.commit();
    }
    
    /*
     * @author kaushikVira
     * @Return System TimeStemp,First time i will return current system TimeStemp but 
     * after first call it will return same timeStemp which is return in first Call,Basically 
     * use this method to get timeStemp value in one transaction. 
     */
    protected Timestamp getSystemTimeStemp(){
        if(systemTimeStemp == null)
            systemTimeStemp = getCurrentTimeStemp();
        return systemTimeStemp;
    }
    
    /*
     * @author kaushikVira
     * @Return it give System CurrentTime TimeStemp
     */
    protected Timestamp getCurrentTimeStemp(){
        return new Timestamp(new Date().getTime());
    }
    
	public void format(Document document,ByteArrayOutputStream outputStream) throws IOException {
		OutputFormat format = new OutputFormat(document);
		format.setLineWidth(0);
		format.setIndenting(false);
		format.setIndent(7);
		XMLSerializer serializer = new XMLSerializer(outputStream, format);
		serializer.serialize(document);
	}
	public String format(Document document) throws IOException {
		OutputFormat format = new OutputFormat(document);
		format.setLineWidth(0);
		format.setIndenting(false);
		format.setIndent(7);
		StringWriter writer = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(writer, format);
		serializer.serialize(document);
		return writer.toString();
	}
	protected SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session){
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager;
	}

    protected boolean isAdmin(String userName){
        return userName != null && (userName.equalsIgnoreCase(BaseConstant.ADMIN_USER_NAME) || userName.equalsIgnoreCase(BaseConstant.PROFILE_USER_NAME));
    }

}

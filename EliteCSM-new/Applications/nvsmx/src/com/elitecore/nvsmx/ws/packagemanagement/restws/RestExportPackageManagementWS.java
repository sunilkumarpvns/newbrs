package com.elitecore.nvsmx.ws.packagemanagement.restws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.pd.importexportutility.prefix.PrefixImportExportUtility;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import io.swagger.annotations.Api;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Path("/")
@Api(value = "/restful/export/export", description = "Apis for export packages ")
public class RestExportPackageManagementWS {

    private static final String MODULE = "SM-MGMT-REST-WS";
    @GET
    @Path("/prefix")
    public Response exportData() {
        long startTime = System.nanoTime();
        String fileName = NVSMXCommonConstants.EXPORT_PREFIX + PrefixImportExportUtility.getTime() + NVSMXCommonConstants.CSV;
        Session session = HibernateSessionFactory.getSession();
        Transaction transaction = session.beginTransaction();
        StringWriter stringWriter = PrefixImportExportUtility.prefixExportData();
        HibernateSessionUtil.commitTransaction(transaction);
        HibernateSessionUtil.closeSession(session);
        File f = null;
        FileOutputStream fos = null;
        try {
            f = File.createTempFile("temp", ".txt");
             fos= new FileOutputStream(f);
            String str = stringWriter.toString();
            if(str.equals(NVSMXCommonConstants.PREFIX_HEADER))
            {
                str = "Nothing to Export";
            }
            byte[] strToBytes = str.getBytes();
            fos.write(strToBytes);
            fos.flush();
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Error while Exporting Prefix data. Reason: " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        }finally {
            try
            {
                if (fos != null)
                {
                    fos.close();
                }
            }
            catch (Exception e)
            {
                LogManager.getLogger().error(MODULE, "Error while Exporting Prefix data. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }


        }
        Response.ResponseBuilder response = null;
        response = Response.ok((Object) f);
        response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        long endTime = System.nanoTime();
        LogManager.getLogger().debug(MODULE, "Total Execution time : " + (endTime - startTime));
        return response.build();
    }
}

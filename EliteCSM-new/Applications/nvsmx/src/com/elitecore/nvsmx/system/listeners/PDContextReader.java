package com.elitecore.nvsmx.system.listeners;


/**
 * Created by aditya on 3/3/17.
 */

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.ByteStreams;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.systeminformation.PDContextInformation;
import com.elitecore.corenetvertex.util.NetworkUtil;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.nvsmx.remotecommunications.data.EndPointStatus;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.util.TomcatUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.annotation.Nullable;
import javax.management.MalformedObjectNameException;
import java.io.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.util.NetworkUtil.INET_ADDRESS_IN_COMMA_STRING;
import static com.elitecore.corenetvertex.util.NetworkUtil.REMOVE_LOCAL_ADDRESSES;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.closeSession;
import static com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil.rollBackTransaction;

public class PDContextReader {

    private static final String MODULE = "PD-CNTXT-RDR";
    private static final String SYS_INFO = "WEB-INF/_sys.info";
    private static final Character PIPE_LINE_SEPARATOR = '|';
    private final String contextPath;
    private final String sysInfoFileLocation;


    private PDContextInformation localPDContextInformation;
    private SessionProvider sessionProvider;


    public PDContextReader(String deploymentPath, String contextPath, SessionProvider sessionProvider) {
        this.contextPath = contextPath;
        this.sessionProvider = sessionProvider;
        this.sysInfoFileLocation = deploymentPath + SYS_INFO;
    }


    /**
     * Flow of Read context Information will be like this<br/>
     * Read Current Context Information<br/>
     * Read Context Information from sysInfo file<br/>
     * Compare current information with sysInfo Information on following basis<br/>
     * <p>  HostName</p>
     * <p> Context Path</p>
     * <p>  IP (IP on sysInfo file should match with any of the IP in Current Context Information)</p><br/>
     * <p/>
     * If validation fail generate new UUID & update the information in sysInfor file & in database with new Information<br/>
     * If sysInfo file information is valid then read Context Information from DB based on UUID found in sysInfo file<br/>
     * Compare & Update the information found in DB with sysInfo file as well as with Current Information<br/>
     */

    public void read() throws InitializationFailedException {
        try {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Initializing Reading PD Context Information");
            }
            PDContextInformation currentPDContextInformation;
            PDContextInformation pdContextInformationFromDB;
            PDContextInformation pdContextInformationFromSysInfoFile;
            currentPDContextInformation = readCurrentPDContextInformation();

            pdContextInformationFromSysInfoFile = readFromSysInfo();

            //if context information in sysInfo file is null i.e new deployment generate new UUI
            if(pdContextInformationFromSysInfoFile == null){
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Generating new UUID.Reason: No information found in sysInfo file");
                }
                generateNewPDContextInfo(currentPDContextInformation);
                return;
            }

            localPDContextInformation = pdContextInformationFromSysInfoFile;
            //compare information from sysInfo file with current Information
            if (isContextInformationSame(currentPDContextInformation, pdContextInformationFromSysInfoFile) == false) {
                // generate new UUID & save it into sysInfo file & database
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Generating new UUID.Reason: sysInfo file information is not same with current Context Information");
                }
                generateNewPDContextInfo(currentPDContextInformation);
                return;

            }

            //fetch Information from DB by UUID found in sysInfo file
            pdContextInformationFromDB = fetchFromDB(pdContextInformationFromSysInfoFile.getId());
            if (pdContextInformationFromDB == null) {
                // no context Information found in DB
                // generate new UUID & save it into sysInfo file & database
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Generating new UUID.Reason: No Information found in DB for available system Information id: " + pdContextInformationFromSysInfoFile.getId());
                }
                generateNewPDContextInfo(currentPDContextInformation);
                return;
            }

            // compare DB information with current information
            if (isContextInformationSame(currentPDContextInformation, pdContextInformationFromDB) == false) {
                // db has invalid information then
                // update information in DB with current Information
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Updating DB with system Information.Reason: PD context Information from DB is not same as current context Information");
                }
                updateContextInformation(pdContextInformationFromDB, pdContextInformationFromSysInfoFile);
                return;
            }

            if (isContextInformationSame(pdContextInformationFromDB, pdContextInformationFromSysInfoFile) == false) {
                // update information in sysInfo file
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Updating system Information with context Information in DB");
                }
                writeInformationToSysInfo(pdContextInformationFromDB);
            }
        }catch (Exception e){
            throw new InitializationFailedException("Error while reading PD context Information ",e);
        }
    }

    private void generateNewPDContextInfo(PDContextInformation currentPDContextInformation) throws IOException {
        saveContextInformation(currentPDContextInformation);
        writeInformationToSysInfo(currentPDContextInformation);
        localPDContextInformation = currentPDContextInformation;
    }


    private void updateContextInformation(PDContextInformation pdContextInformation, PDContextInformation pdContextInformationFromDB){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Saving PD ContextInformation with details :" + pdContextInformation.toString());
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateSessionFactory.getSession();
            transaction = session.beginTransaction();
            pdContextInformationFromDB.setIpAddresses(pdContextInformation.getIpAddresses());
            pdContextInformationFromDB.setHostName(pdContextInformation.getHostName());
            pdContextInformationFromDB.setPort(pdContextInformation.getPort());
            pdContextInformationFromDB.setDeploymentPath(pdContextInformation.getDeploymentPath());
            pdContextInformationFromDB.setContextPath(pdContextInformation.getContextPath());
            session.update(pdContextInformationFromDB);
            transaction.commit();
        } catch (HibernateException e) {
            rollBackTransaction(transaction);
            getLogger().error(MODULE, "Error while updating context information.Reason: " + e.getMessage());
            throw e;
        } finally {
            closeSession(session);
        }
    }


    private PDContextInformation fetchFromDB(String id){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Reading context information from DB for Id: " + id);
        }
        Session session = null;
        try {
            session = sessionProvider.getSession();
            session.beginTransaction();
            return (PDContextInformation) session.get(PDContextInformation.class, id);
        } finally {
            closeSession(session);
        }

    }

    private void saveContextInformation(PDContextInformation pdContextInformation) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Saving PD ContextInformation with details: " + pdContextInformation.toString());
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionProvider.getSession();
            transaction = session.beginTransaction();
            session.save(pdContextInformation);
            transaction.commit();
        } catch (HibernateException e) {
            rollBackTransaction(transaction);
            getLogger().error(MODULE, "Error while saving context information.Reason: " + e.getMessage());
            throw e;
        } finally {
            closeSession(session);
        }
    }

    private PDContextInformation readCurrentPDContextInformation() throws SocketException, MalformedObjectNameException, UnknownHostException {

        String ipAddresses = NetworkUtil.getAllIPAddresses(REMOVE_LOCAL_ADDRESSES, INET_ADDRESS_IN_COMMA_STRING);

        if (Strings.isNullOrEmpty(ipAddresses)) {
            return null;
        }

        //TODO can return multiple port need to discuss - ADITYA
        String port = TomcatUtil.getContextPort();
        if (Strings.isNullOrBlank(port)) {
            return null;
        }

        String hostName = InetAddress.getLocalHost().getHostName();
        PDContextInformation pdContextInformation = new PDContextInformation(hostName, ipAddresses, port, contextPath);
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Current PD Context Information is: " + pdContextInformation);
        }

        return pdContextInformation;
    }



    private void writeInformationToSysInfo(PDContextInformation pdContext) throws IOException {
        if (Strings.isNullOrBlank(SYS_INFO)) {
            return;
        }
        StringBuilder pdContextBuilder = new StringBuilder();

        pdContextBuilder.append(pdContext.getId()).append(PIPE_LINE_SEPARATOR)
                .append(pdContext.getHostName()).append(PIPE_LINE_SEPARATOR)
                .append(pdContext.getIpAddresses()).append(PIPE_LINE_SEPARATOR)
                .append(pdContext.getPort()).append(PIPE_LINE_SEPARATOR)
                .append(pdContext.getContextPath());

        try (OutputStream outputStream = new FileOutputStream(sysInfoFileLocation)) {
            outputStream.write(pdContextBuilder.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            getLogger().error(MODULE, "Unable to write into file. Reason: " + e.getMessage());
            throw e;
        }
    }


    /**
     * This method will read context Information from sysInfo file
     *
     * @return password
     */
    @Nullable private PDContextInformation readFromSysInfo() throws IOException {

        getLogger().info(MODULE, "Method called readFromSysInfo()");
        try (InputStream inputStream = new FileInputStream(sysInfoFileLocation)) {
            String systemInformation = new String(ByteStreams.readFully(inputStream));

            if (Strings.isNullOrBlank(systemInformation)) {
                return null;
            }

            List<String> contextInformationList = Strings.splitter(PIPE_LINE_SEPARATOR).trimTokens().split(systemInformation);

            if (contextInformationList.size() != 5) {
                return null;
            }

            PDContextInformation pdContextInformation = new PDContextInformation();

            pdContextInformation.setId(contextInformationList.get(0));
            pdContextInformation.setHostName(contextInformationList.get(1));
            pdContextInformation.setIpAddresses(contextInformationList.get(2));
            pdContextInformation.setPort(contextInformationList.get(3));
            pdContextInformation.setContextPath(contextInformationList.get(4));

            return pdContextInformation;
        } catch (IOException e) {
            getLogger().error(MODULE, "Unable to read file from location " + sysInfoFileLocation + " .Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            throw e;
        }
    }


    private boolean isContextInformationSame(PDContextInformation pdContext1, PDContextInformation pdContext2) {

        if (pdContext1 == null || pdContext2 == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "PD Context not found for comparison");
            }
            return false;
        }
        //compare host name
        if (pdContext1.getHostName().equalsIgnoreCase(pdContext2.getHostName()) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Different PD Context host name found for PD context1: " + pdContext1.getHostName() + " & PD Context2: " + pdContext2.getHostName()); //NOSONAR
            }
            return false;
        }
        // compare context path
        if (pdContext1.getContextPath().equalsIgnoreCase(pdContext2.getContextPath()) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Different PD Context path found for PD context1: " + pdContext1.getContextPath() + " & PD Context2: " + pdContext2.getContextPath()); //NOSONAR
            }
            return false;
        }
        //compare port
        if (pdContext1.getPort().equalsIgnoreCase(pdContext2.getPort()) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Different PD Context port found for PD context1: " + pdContext1.getPort() + " & PD Context2: " + pdContext2.getPort());
            }
            return false;
        }
        // compare ip address
        //any ip address in one Context should match with should match with any ip address another
        return isPDContextIPsSame(pdContext1, pdContext2);
    }

    private boolean isPDContextIPsSame(PDContextInformation pdContext1, PDContextInformation pdContext2) {
        List<String> pdContext1ips = CommonConstants.COMMA_SPLITTER.split(pdContext1.getIpAddresses());
        List<String> pdContext2ips = CommonConstants.COMMA_SPLITTER.split(pdContext2.getIpAddresses());
        for (String pdContext2IP : pdContext2ips) {
            for (String pdContext1IP : pdContext1ips) {
                if (pdContext2IP.equals(pdContext1IP)) {
                    return true;
                }

            }
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Different PD Context IPs found for PD context1: " + pdContext1.getIpAddresses() + " & PD Context2: " + pdContext2.getIpAddresses());
        }
        return false;
    }

    public PDContextInformation getLocalPDContextInformation() {
        return localPDContextInformation;
    }

    public void updateStatus(EndPointStatus status){

        Session session = null;
        try{
            session = sessionProvider.getSession();
            Transaction transaction = session.beginTransaction();
            localPDContextInformation.setStatus(status.getVal());
            localPDContextInformation.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
            session.update(localPDContextInformation);
            transaction.commit();
        }catch (Exception e){
            getLogger().error(MODULE, "Error While updating PD Context Information from DB. Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
        }finally {
            closeSession(session);
        }

    }

}
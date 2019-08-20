package com.elitecore.elitesm.web.core.system.servlet;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.elitecore.aaa.core.conf.impl.ClusterGroupData;
import com.elitecore.aaa.core.conf.impl.ImdgConfigData;
import com.elitecore.aaa.core.conf.impl.MemberData;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.blmanager.inmemorydatagrid.InMemoryDataGridBLManager;
import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.elitesm.util.communicationmanager.remotecommunication.RemoteCommunicationManagerFactory;
import com.elitecore.elitesm.util.constants.CommunicationConstant;
import com.elitecore.elitesm.util.constants.ServermgrConstant;
import com.elitecore.passwordutil.PasswordEncryption;

import net.sf.json.JSONArray;

public class DownServerNodeDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "In-Memory-Data-Grid";
	private IRemoteCommunicationManager remoteCommunicationManager;
       
    public DownServerNodeDataServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	InMemoryDataGridBLManager inMemoryDataGridBLManager = new InMemoryDataGridBLManager();
    	JSONArray instanceNames = new JSONArray();

    	try {
    		String xmlDatas = new String(inMemoryDataGridBLManager.getInMemoryDatagridConfiguration().getImdgXml());
    		if(Strings.isNullOrBlank(xmlDatas.trim()) == false){
    			StringReader stringReader =new StringReader(xmlDatas.trim());

    			//Convert into relevant POJO 
    			JAXBContext context = JAXBContext.newInstance(ImdgConfigData.class);
    			Unmarshaller unmarshaller = context.createUnmarshaller();
    			ImdgConfigData imdgConfigData = (ImdgConfigData) unmarshaller.unmarshal(stringReader);

    			for (ClusterGroupData clusterGroup : imdgConfigData.getClusterGroups()) {
    				for (MemberData member : clusterGroup.getMemberDatas()) {

    					NetServerBLManager netServerBLManager = new NetServerBLManager();
    					NetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstanceByName(member.getName());
    					String netServerCode = PasswordEncryption.getInstance().crypt(netServerInstanceData.getNetServerCode(),ServermgrConstant.SERVER_ID_ENCRYPTION_MODE);

    					remoteCommunicationManager = RemoteCommunicationManagerFactory.getRemoteCommunicationManager(CommunicationConstant.JMX_RMI_JINI);

    					try {
    						remoteCommunicationManager.init(netServerInstanceData.getAdminHost(),netServerInstanceData.getAdminPort(), netServerCode);
    					} catch (Exception e) {
    						instanceNames.add(netServerInstanceData.getName());
    						LogManager.getLogger().trace(MODULE, netServerInstanceData.getName()+" Instance with IP Address "+netServerInstanceData.getAdminHost() + " is down right now.");
    					}
    				}
    			}
    		}

    	} catch (Exception e) {
    	}
    	response.setContentType("application/json");

    	response.getWriter().write(instanceNames.toString());;
    }
}	

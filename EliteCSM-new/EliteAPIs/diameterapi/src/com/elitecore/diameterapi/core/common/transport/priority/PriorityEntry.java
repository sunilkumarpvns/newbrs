package com.elitecore.diameterapi.core.common.transport.priority;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlEnumValue;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
public class PriorityEntry {

	private int[] applications;
	private int[] commandCodes;
	private InetAddress[] ipAddressses;
	private DiameterSessionTypes diameterSessionType;
	private Priority priority;
	private SessionFactoryManager sessionFactoryManager;
	
	public PriorityEntry(@Nonnull SessionFactoryManager sessionFactoryManager, int[] applications, int[] commandCodes,
			InetAddress[] ipAddresss, DiameterSessionTypes diameterSessionType, Priority priority) {
		
		this.sessionFactoryManager = sessionFactoryManager;
		this.applications = applications;
		this.commandCodes = commandCodes;
		this.ipAddressses = ipAddresss;
		this.diameterSessionType = diameterSessionType;
		this.priority = priority;
	}
	
	
	/**
	 * checks for applicationIds, CommandCodes, ipAddresses, newSession,
	 * skips checks for particular field if not configured.
	 * 		<PRE>
	 * Example : 
	 * 		   IF newSession configured THEN
	 *				checks for sessions
	 *		   ELSE
	 *				return true
	 *		</PRE>
	 * 
	 */
	public boolean isApplicable(DiameterPacket diameterPacket, InetAddress ipAddress) {
		if(checkForApplications(diameterPacket) == false) {
			return false;
		}
		if(checkForCommandCodes(diameterPacket) == false) {
			return false;
		}
		if(checkForIPAddress(ipAddress) == false) {
			return false;
		}
		
		if (diameterSessionType == DiameterSessionTypes.NEW) {
			return sessionFactoryManager.hasSession(diameterPacket.getSessionID(), diameterPacket.getApplicationID()) == false;
		} else if (diameterSessionType == DiameterSessionTypes.EXISTING) {
			return sessionFactoryManager.hasSession(diameterPacket.getSessionID(), diameterPacket.getApplicationID());
		}
		
		return true;
	}
	
	private boolean checkForCommandCodes(DiameterPacket diameterPacket){
		if(commandCodes.length == 0) {
			return true;
		}
		
		for(int i = 0 ; i < commandCodes.length; i++){
			if(commandCodes[i] == diameterPacket.getCommandCode()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkForApplications(DiameterPacket diameterPacket){
		if(applications.length == 0){
			return true;
		}
		for(int i = 0 ; i < applications.length; i++){
			if(applications[i] == diameterPacket.getApplicationID()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkForIPAddress (InetAddress ipAddress) {
		
		if(ipAddressses.length == 0) {
			return true;
		}

		for(int i=0 ; i<ipAddressses.length ; i++){
			if(ipAddressses[i].equals(ipAddress)) {
				return true;
			}
		}
		return false;
	}
	
	public Priority getPriority(){
		return priority;
	}
	
	public static class PriorityEntryBuilder {
		private static final String MODULE = "PRIORITY-ENTRY-BUILDER";
		private String applicationIds;
		private String commandCodes;
		private String ipAddressses;
		private DiameterSessionTypes diameterSessionType;
		private Priority priority;
		private SessionFactoryManager sessionFactoryManager;
		
		public PriorityEntryBuilder(SessionFactoryManager sessionFactoryManager) {
			this.sessionFactoryManager = sessionFactoryManager;
			this.priority = Priority.MEDIUM;
			this.diameterSessionType = DiameterSessionTypes.ALL;
		}
		
		public PriorityEntryBuilder withApplicationIds(String applicationIds) {
			this.applicationIds = applicationIds;
			return this;
		}
		
		public PriorityEntryBuilder withCommandCodes(String commandCodes) {
			this.commandCodes = commandCodes;
			return this;
		}
		
		public PriorityEntryBuilder withIpAddresses(String ipAddresses) {
			this.ipAddressses = ipAddresses;
			return this;
		}
		
		public PriorityEntryBuilder withDiameterSessionType(DiameterSessionTypes diameterSessionType) {
			this.diameterSessionType = diameterSessionType;
			return this;
		}
		
		public PriorityEntryBuilder withPriority(Priority priority) {
			this.priority = priority;
			return this;
		}
		
		/**
		 * This method takes String and tokenize it by comma(,) and semicolon(;) then 
		 * generates int Array
		 * 
		 * @author Chetan.Sankhala
		 * @param String
		 * @return int[]
		 * @throws NumberFormatException
		 */
		///FIXME: Use Collectionz.map
		private int[] getIntArrayFromString(String value) throws NumberFormatException{
			char delim1 = ',';
			char delim2 = ';';
			
			List<String> tokens = Splitter.on(delim1).split(value);
			List<String> finalTokens = new ArrayList<String>();
			for(String token : tokens) {
				token = token.trim();
	 			List<String> split = Splitter.on(delim2).split(token);
	 			if(split.isEmpty() == false) {
	 				finalTokens.addAll(split);
	 			}
			}
			
			int[] intArr = new int[finalTokens.size()];
			int i=0;
			for(String token : finalTokens) {
				token = token.trim();
				try {
					intArr[i] = Integer.parseInt(token);
					i++;
				}catch(NumberFormatException ex) {
					throw new NumberFormatException("Invalid value: " + token);
				}
			}
			return intArr;
		}
		
		public PriorityEntry build() throws Exception {
			
			Preconditions.checkNotNull(sessionFactoryManager, "Session factory manager is null");
			
			
			int[] appIds, cc; 
			String[] ips;
			char delimiter = ',';
			
			try {
				appIds = getIntArrayFromString(applicationIds);
			}catch(NumberFormatException numberFormatException) {
				throw new Exception("Invalid applications-IDs: " + applicationIds + ". Reason: " + numberFormatException.getMessage());
			}
			try {
				cc = getIntArrayFromString(commandCodes);
			}catch(NumberFormatException numberFormatException) {
				throw new Exception("Invalid command code: " + commandCodes + ". Reason: " + numberFormatException.getMessage());
			}
			
			Set<InetAddress> availableIPs = new HashSet<InetAddress>();
			try {
				ips = Strings.splitter(delimiter).splitToArray(ipAddressses);
				for(String ip : ips) {
					ip = ip.trim();
					if(DiameterUtility.isIPRange(ip)) {
						Set<InetAddress> tempInetAddresses = getAvailableInetAddresses(ip);
						if(Collectionz.isNullOrEmpty(availableIPs)) {
							continue;
						}
						availableIPs.addAll(tempInetAddresses);
					}else{
						InetAddress tempIp;
						try {
							tempIp = InetAddress.getByName(ip);
						}catch(UnknownHostException ex) {
							throw new UnknownHostException("Invalid IP address: " + ex.getMessage());
						}
						availableIPs.add(tempIp);
					}
				}
			} catch (Exception e) {
				throw e;
			}
			
			InetAddress[] inetAddress = availableIPs.toArray(new InetAddress[availableIPs.size()]);
			LogManager.getLogger().info(MODULE, "Priority Entry: Application(s): " + Arrays.toString(appIds) + 
					", Command-Code(s): " + Arrays.toString(cc) + 
					", IP-Address(es): " + Arrays.toString(inetAddress) + 
					", DiameterSessionType: " + diameterSessionType + 
					", Priority: " + priority );

			return new PriorityEntry(sessionFactoryManager, appIds, cc, inetAddress, diameterSessionType, priority);
		}

		
		/**
		 * This method gives Set of InetAddress from rangeIP.
		 * 
		 * IF rangeIp is null of empty THEN
		 * 		it returns EMPTY_SET <br>
		 * ELSE <br>
		 * 		SET of InetAddress
		 * 
		 * @author Chetan.Sankhala
		 * @param ip
		 * @return emptySet or Set<InetAddress>
		 * @throws NumberFormatException
		 * @throws UnknownHostException
		 */
		private Set<InetAddress> getAvailableInetAddresses(String ip) throws NumberFormatException, UnknownHostException{
			try {
				List<String> availableIPs = DiameterUtility.getAvailableIPs(ip);
				if(Collectionz.isNullOrEmpty(availableIPs)) {
					return Collections.<InetAddress>emptySet();
				}
				
				Set<InetAddress> inetAddresses = new HashSet<InetAddress>();
				for(String address : availableIPs) {
					try {
						InetAddress inetAddr = InetAddress.getByName(address);
						inetAddresses.add(inetAddr);
					} catch (UnknownHostException e) {
						throw new UnknownHostException("Unknown host address " + address + ". Reason: " + e.getMessage());
					}
				}
				return inetAddresses;
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Error while getting available IP address from range " + ip + ". Reason: " + e.getMessage());
			}
		}
	}
	
	
	public static enum DiameterSessionTypes {
        @XmlEnumValue(value = "All")
        ALL(0,"All"),
        @XmlEnumValue(value = "New")
        NEW(1,"New"),
        @XmlEnumValue(value = "Existing")
        EXISTING(2,"Existing");

        public final int val;
        public final String type;
        DiameterSessionTypes(int val,String type) {
            this.val = val;
            this.type = type;
        }

        public static DiameterSessionTypes fromVal(int val) {
            if(ALL.val == val) {
                return ALL;
            }else if(NEW.val == val) {
                return NEW;
            }else if (EXISTING.val == val) {
                return EXISTING;
            }else{
                return null;
            }
        }

        public static DiameterSessionTypes fromType(String type) {
            if(ALL.type.equalsIgnoreCase(type)) {
                return ALL;
            }else if(NEW.type.equalsIgnoreCase(type)) {
                return NEW;
            }else if(EXISTING.type.equalsIgnoreCase(type)) {
                return EXISTING;
            }else{
                return null;
            }
        }
    } 
}

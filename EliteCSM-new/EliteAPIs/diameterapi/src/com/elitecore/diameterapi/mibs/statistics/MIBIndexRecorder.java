package com.elitecore.diameterapi.mibs.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.util.FileUtil;
import com.elitecore.core.commons.fileio.loactionalloactor.FileAllocatorException;
import com.elitecore.diameterapi.diameter.common.data.PeerData;


public class MIBIndexRecorder {

	private static final String MODULE = "MIB-INDX-RECORDER";
	private static final String INDEX_FILE = "_mib_indices.ser";
	private MIBIndexAllocator peerMibSerializeData = new MIBIndexAllocator();
	private String basePath;

	public void recordIndexFor(PeerData peerData) {

		HashMap<String, Long> serializedHashMap = peerMibSerializeData.getKeyToMIBIndexMap();
		String peerIdentifier = getPeerIdentfier(peerData);
		if(serializedHashMap.containsKey(peerIdentifier) == false){
			long index = peerData.getPeerIndex();
			if(index <= 0){
				index = peerMibSerializeData.getNextMIBIndex();
			}
			serializedHashMap.put(peerIdentifier, index);
		}
		peerData.setPeerIndex(serializedHashMap.get(peerIdentifier));
	}

	private String getPeerIdentfier(PeerData peerData) {
		return peerData.getHostIdentity(); 
	}

	public void build(String basePath) throws FileAllocatorException {

		this.basePath = basePath;
		if(Strings.isNullOrBlank(basePath)){
			throw new FileAllocatorException("Base Path for Serialized File: " + INDEX_FILE + " not found");
		}
		basePath = basePath.trim();
		File file = new File(basePath +  File.separator + INDEX_FILE);
		if(file.exists() == false) {
			peerMibSerializeData = new MIBIndexAllocator();
		} else {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(file)); //NOSONAR - Reason: Resources should be closed
				peerMibSerializeData = (MIBIndexAllocator) ois.readObject();
			} catch (Exception e) {
				LogManager.getLogger().warn(MODULE, "Unable to de-serialize Diameter MIB Indices, Reason " + 
						e.getMessage() + ". This may effect SNMP Index Management");
				LogManager.getLogger().trace(e);
				peerMibSerializeData = new MIBIndexAllocator();
			} finally {
				FileUtil.closeQuietly(ois);
			}
		} 
	}

	public void store() throws FileAllocatorException {

		if(Strings.isNullOrBlank(basePath)){
			throw new FileAllocatorException("Base Path for Serialization not found");
		}
		basePath = basePath.trim();
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(new FileOutputStream(basePath +  File.separator + INDEX_FILE )); //NOSONAR - Reason: Resources should be closed
			oos.writeObject(peerMibSerializeData);
		} catch (Exception e) {
			LogManager.getLogger().warn(MODULE, "Unable to serialize Diameter MIB Indices, Reason " + 
					e.getMessage() + ". This may effect SNMP Index Management");
			LogManager.getLogger().trace(e);
		} finally {
			FileUtil.closeQuietly(oos);
		}
	}
}

class MIBIndexAllocator implements Serializable {

	/**
	 * Note: Maintain version control
	 * 
	 * Last version ID: 1L includes, 
	 * AtomicLong MIBIndexAllocator
	 * HashMap<String, Long> keyToMIBIndexMap
	 */
	private static final long serialVersionUID = 1L;

	private static final String MODULE = "MIB-INDX-ALLOCATOR";

	// Max MIB Value --> 4294967294
	private static final long initialValue = Integer.MAX_VALUE * 2L;

	private AtomicLong MIBCurrentIndex;
	private HashMap<String, Long> keyToMIBIndexMap;

	public MIBIndexAllocator() {
		MIBCurrentIndex = new AtomicLong(initialValue);
		keyToMIBIndexMap = new HashMap<String, Long>();
	}

	public Long getNextMIBIndex() {
		Long index = MIBCurrentIndex.decrementAndGet();
		if(index < 1){
			LogManager.getLogger().warn(MODULE, "All Indices for Dynamic Peers, ranging from: " + initialValue 
					+ " to 1 are used, resetting to MIB Max Value: " + initialValue);
			MIBCurrentIndex = new AtomicLong(initialValue);
		}
		return index; 
	}
	public HashMap<String, Long> getKeyToMIBIndexMap() {
		return keyToMIBIndexMap;
	}
}
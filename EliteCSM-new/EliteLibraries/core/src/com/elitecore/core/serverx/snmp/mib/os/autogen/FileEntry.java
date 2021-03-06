package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "FileEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.2021.15.1.
 */
public class FileEntry implements FileEntryMBean, Serializable {

    /**
     * Variable for storing the value of "FileMax".
     * The variable is identified by: "1.3.6.1.4.1.2021.15.1.4".
     */
    protected Integer FileMax = new Integer(1);

    /**
     * Variable for storing the value of "FileSize".
     * The variable is identified by: "1.3.6.1.4.1.2021.15.1.3".
     */
    protected Integer FileSize = new Integer(1);

    /**
     * Variable for storing the value of "FileName".
     * The variable is identified by: "1.3.6.1.4.1.2021.15.1.2".
     */
    protected String FileName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "FileErrorMsg".
     * The variable is identified by: "1.3.6.1.4.1.2021.15.1.101".
     */
    protected String FileErrorMsg = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "FileErrorFlag".
     * The variable is identified by: "1.3.6.1.4.1.2021.15.1.100".
     */
    protected EnumFileErrorFlag FileErrorFlag = new EnumFileErrorFlag();

    /**
     * Variable for storing the value of "FileIndex".
     * The variable is identified by: "1.3.6.1.4.1.2021.15.1.1".
     */
    protected Integer FileIndex = new Integer(1);


    /**
     * Constructor for the "FileEntry" group.
     */
    public FileEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "FileMax" variable.
     */
    public Integer getFileMax() throws SnmpStatusException {
        return FileMax;
    }

    /**
     * Getter for the "FileSize" variable.
     */
    public Integer getFileSize() throws SnmpStatusException {
        return FileSize;
    }

    /**
     * Getter for the "FileName" variable.
     */
    public String getFileName() throws SnmpStatusException {
        return FileName;
    }

    /**
     * Getter for the "FileErrorMsg" variable.
     */
    public String getFileErrorMsg() throws SnmpStatusException {
        return FileErrorMsg;
    }

    /**
     * Getter for the "FileErrorFlag" variable.
     */
    public EnumFileErrorFlag getFileErrorFlag() throws SnmpStatusException {
        return FileErrorFlag;
    }

    /**
     * Getter for the "FileIndex" variable.
     */
    public Integer getFileIndex() throws SnmpStatusException {
        return FileIndex;
    }

}

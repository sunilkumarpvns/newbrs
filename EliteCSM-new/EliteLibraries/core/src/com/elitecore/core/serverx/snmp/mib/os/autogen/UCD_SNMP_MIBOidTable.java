package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.management.snmp.SnmpOidRecord;

// jdmk imports
//
import com.sun.management.snmp.SnmpOidTableSupport;

/**
 * The class contains metadata definitions for "UCD-SNMP-MIB".
 * Call SnmpOid.setSnmpOidTable(new UCD_SNMP_MIBOidTable()) to load the metadata in the SnmpOidTable.
 */
public class UCD_SNMP_MIBOidTable extends SnmpOidTableSupport implements Serializable {

    /**
     * Default constructor. Initialize the Mib tree.
     */
    public UCD_SNMP_MIBOidTable() {
        super("UCD_SNMP_MIB");
        loadMib(varList);
    }

    static SnmpOidRecord varList [] = {
        new SnmpOidRecord("dskTable", "1.3.6.1.4.1.2021.9", "TA"),
        new SnmpOidRecord("dskEntry", "1.3.6.1.4.1.2021.9.1", "EN"),
        new SnmpOidRecord("dskErrorMsg", "1.3.6.1.4.1.2021.9.1.101", "S"),
        new SnmpOidRecord("dskErrorFlag", "1.3.6.1.4.1.2021.9.1.100", "I"),
        new SnmpOidRecord("dskUsedHigh", "1.3.6.1.4.1.2021.9.1.16", "G"),
        new SnmpOidRecord("dskUsedLow", "1.3.6.1.4.1.2021.9.1.15", "G"),
        new SnmpOidRecord("dskAvailHigh", "1.3.6.1.4.1.2021.9.1.14", "G"),
        new SnmpOidRecord("dskAvailLow", "1.3.6.1.4.1.2021.9.1.13", "G"),
        new SnmpOidRecord("dskTotalHigh", "1.3.6.1.4.1.2021.9.1.12", "G"),
        new SnmpOidRecord("dskTotalLow", "1.3.6.1.4.1.2021.9.1.11", "G"),
        new SnmpOidRecord("dskPercentNode", "1.3.6.1.4.1.2021.9.1.10", "I"),
        new SnmpOidRecord("dskPercent", "1.3.6.1.4.1.2021.9.1.9", "I"),
        new SnmpOidRecord("dskUsed", "1.3.6.1.4.1.2021.9.1.8", "I"),
        new SnmpOidRecord("dskAvail", "1.3.6.1.4.1.2021.9.1.7", "I"),
        new SnmpOidRecord("dskTotal", "1.3.6.1.4.1.2021.9.1.6", "I"),
        new SnmpOidRecord("dskMinPercent", "1.3.6.1.4.1.2021.9.1.5", "I"),
        new SnmpOidRecord("dskMinimum", "1.3.6.1.4.1.2021.9.1.4", "I"),
        new SnmpOidRecord("dskDevice", "1.3.6.1.4.1.2021.9.1.3", "S"),
        new SnmpOidRecord("dskPath", "1.3.6.1.4.1.2021.9.1.2", "S"),
        new SnmpOidRecord("dskIndex", "1.3.6.1.4.1.2021.9.1.1", "I"),
        new SnmpOidRecord("extTable", "1.3.6.1.4.1.2021.8", "TA"),
        new SnmpOidRecord("extEntry", "1.3.6.1.4.1.2021.8.1", "EN"),
        new SnmpOidRecord("extErrFixCmd", "1.3.6.1.4.1.2021.8.1.103", "S"),
        new SnmpOidRecord("extCommand", "1.3.6.1.4.1.2021.8.1.3", "S"),
        new SnmpOidRecord("extErrFix", "1.3.6.1.4.1.2021.8.1.102", "I"),
        new SnmpOidRecord("extOutput", "1.3.6.1.4.1.2021.8.1.101", "S"),
        new SnmpOidRecord("extNames", "1.3.6.1.4.1.2021.8.1.2", "S"),
        new SnmpOidRecord("extIndex", "1.3.6.1.4.1.2021.8.1.1", "I"),
        new SnmpOidRecord("extResult", "1.3.6.1.4.1.2021.8.1.100", "I"),
        new SnmpOidRecord("logMatchTable", "1.3.6.1.4.1.2021.16.2", "TA"),
        new SnmpOidRecord("logMatchEntry", "1.3.6.1.4.1.2021.16.2.1", "EN"),
        new SnmpOidRecord("logMatchCounter", "1.3.6.1.4.1.2021.16.2.1.9", "C"),
        new SnmpOidRecord("logMatchCurrentCount", "1.3.6.1.4.1.2021.16.2.1.8", "I"),
        new SnmpOidRecord("logMatchCurrentCounter", "1.3.6.1.4.1.2021.16.2.1.7", "C"),
        new SnmpOidRecord("logMatchGlobalCount", "1.3.6.1.4.1.2021.16.2.1.6", "I"),
        new SnmpOidRecord("logMatchGlobalCounter", "1.3.6.1.4.1.2021.16.2.1.5", "C"),
        new SnmpOidRecord("logMatchRegEx", "1.3.6.1.4.1.2021.16.2.1.4", "S"),
        new SnmpOidRecord("logMatchFilename", "1.3.6.1.4.1.2021.16.2.1.3", "S"),
        new SnmpOidRecord("logMatchRegExCompilation", "1.3.6.1.4.1.2021.16.2.1.101", "S"),
        new SnmpOidRecord("logMatchCycle", "1.3.6.1.4.1.2021.16.2.1.11", "I"),
        new SnmpOidRecord("logMatchName", "1.3.6.1.4.1.2021.16.2.1.2", "S"),
        new SnmpOidRecord("logMatchCount", "1.3.6.1.4.1.2021.16.2.1.10", "I"),
        new SnmpOidRecord("logMatchIndex", "1.3.6.1.4.1.2021.16.2.1.1", "I"),
        new SnmpOidRecord("logMatchErrorFlag", "1.3.6.1.4.1.2021.16.2.1.100", "I"),
        new SnmpOidRecord("logMatchMaxEntries", "1.3.6.1.4.1.2021.16.1", "I"),
        new SnmpOidRecord("fileTable", "1.3.6.1.4.1.2021.15", "TA"),
        new SnmpOidRecord("fileEntry", "1.3.6.1.4.1.2021.15.1", "EN"),
        new SnmpOidRecord("fileMax", "1.3.6.1.4.1.2021.15.1.4", "I"),
        new SnmpOidRecord("fileSize", "1.3.6.1.4.1.2021.15.1.3", "I"),
        new SnmpOidRecord("fileName", "1.3.6.1.4.1.2021.15.1.2", "S"),
        new SnmpOidRecord("fileErrorMsg", "1.3.6.1.4.1.2021.15.1.101", "S"),
        new SnmpOidRecord("fileErrorFlag", "1.3.6.1.4.1.2021.15.1.100", "I"),
        new SnmpOidRecord("fileIndex", "1.3.6.1.4.1.2021.15.1.1", "I"),
        new SnmpOidRecord("memSwapErrorMsg", "1.3.6.1.4.1.2021.4.101", "S"),
        new SnmpOidRecord("memSwapError", "1.3.6.1.4.1.2021.4.100", "I"),
        new SnmpOidRecord("memUsedRealTXT", "1.3.6.1.4.1.2021.4.17", "I"),
        new SnmpOidRecord("memUsedSwapTXT", "1.3.6.1.4.1.2021.4.16", "I"),
        new SnmpOidRecord("memCached", "1.3.6.1.4.1.2021.4.15", "I"),
        new SnmpOidRecord("memBuffer", "1.3.6.1.4.1.2021.4.14", "I"),
        new SnmpOidRecord("memShared", "1.3.6.1.4.1.2021.4.13", "I"),
        new SnmpOidRecord("memMinimumSwap", "1.3.6.1.4.1.2021.4.12", "I"),
        new SnmpOidRecord("memTotalFree", "1.3.6.1.4.1.2021.4.11", "I"),
        new SnmpOidRecord("memAvailRealTXT", "1.3.6.1.4.1.2021.4.10", "I"),
        new SnmpOidRecord("memTotalRealTXT", "1.3.6.1.4.1.2021.4.9", "I"),
        new SnmpOidRecord("memAvailSwapTXT", "1.3.6.1.4.1.2021.4.8", "I"),
        new SnmpOidRecord("memTotalSwapTXT", "1.3.6.1.4.1.2021.4.7", "I"),
        new SnmpOidRecord("memAvailReal", "1.3.6.1.4.1.2021.4.6", "I"),
        new SnmpOidRecord("memTotalReal", "1.3.6.1.4.1.2021.4.5", "I"),
        new SnmpOidRecord("memAvailSwap", "1.3.6.1.4.1.2021.4.4", "I"),
        new SnmpOidRecord("memTotalSwap", "1.3.6.1.4.1.2021.4.3", "I"),
        new SnmpOidRecord("memErrorName", "1.3.6.1.4.1.2021.4.2", "S"),
        new SnmpOidRecord("memIndex", "1.3.6.1.4.1.2021.4.1", "I"),
        new SnmpOidRecord("mrTable", "1.3.6.1.4.1.2021.102", "TA"),
        new SnmpOidRecord("mrEntry", "1.3.6.1.4.1.2021.102.1", "EN"),
        new SnmpOidRecord("mrModuleName", "1.3.6.1.4.1.2021.102.1.2", "S"),
        new SnmpOidRecord("mrIndex", "1.3.6.1.4.1.2021.102.1.1", "OI"),
        new SnmpOidRecord("ssCpuRawUser", "1.3.6.1.4.1.2021.11.50", "C"),
        new SnmpOidRecord("ssCpuRawGuestNice", "1.3.6.1.4.1.2021.11.66", "C"),
        new SnmpOidRecord("ssCpuRawGuest", "1.3.6.1.4.1.2021.11.65", "C"),
        new SnmpOidRecord("ssCpuRawSteal", "1.3.6.1.4.1.2021.11.64", "C"),
        new SnmpOidRecord("ssRawSwapOut", "1.3.6.1.4.1.2021.11.63", "C"),
        new SnmpOidRecord("ssRawSwapIn", "1.3.6.1.4.1.2021.11.62", "C"),
        new SnmpOidRecord("ssCpuRawSoftIRQ", "1.3.6.1.4.1.2021.11.61", "C"),
        new SnmpOidRecord("ssRawContexts", "1.3.6.1.4.1.2021.11.60", "C"),
        new SnmpOidRecord("ssCpuIdle", "1.3.6.1.4.1.2021.11.11", "I"),
        new SnmpOidRecord("ssCpuSystem", "1.3.6.1.4.1.2021.11.10", "I"),
        new SnmpOidRecord("ssCpuUser", "1.3.6.1.4.1.2021.11.9", "I"),
        new SnmpOidRecord("ssSysContext", "1.3.6.1.4.1.2021.11.8", "I"),
        new SnmpOidRecord("ssSysInterrupts", "1.3.6.1.4.1.2021.11.7", "I"),
        new SnmpOidRecord("ssRawInterrupts", "1.3.6.1.4.1.2021.11.59", "C"),
        new SnmpOidRecord("ssIORawReceived", "1.3.6.1.4.1.2021.11.58", "C"),
        new SnmpOidRecord("ssIOReceive", "1.3.6.1.4.1.2021.11.6", "I"),
        new SnmpOidRecord("ssIORawSent", "1.3.6.1.4.1.2021.11.57", "C"),
        new SnmpOidRecord("ssIOSent", "1.3.6.1.4.1.2021.11.5", "I"),
        new SnmpOidRecord("ssSwapOut", "1.3.6.1.4.1.2021.11.4", "I"),
        new SnmpOidRecord("ssCpuRawInterrupt", "1.3.6.1.4.1.2021.11.56", "C"),
        new SnmpOidRecord("ssSwapIn", "1.3.6.1.4.1.2021.11.3", "I"),
        new SnmpOidRecord("ssCpuRawKernel", "1.3.6.1.4.1.2021.11.55", "C"),
        new SnmpOidRecord("ssErrorName", "1.3.6.1.4.1.2021.11.2", "S"),
        new SnmpOidRecord("ssCpuRawWait", "1.3.6.1.4.1.2021.11.54", "C"),
        new SnmpOidRecord("ssIndex", "1.3.6.1.4.1.2021.11.1", "I"),
        new SnmpOidRecord("ssCpuRawIdle", "1.3.6.1.4.1.2021.11.53", "C"),
        new SnmpOidRecord("ssCpuRawSystem", "1.3.6.1.4.1.2021.11.52", "C"),
        new SnmpOidRecord("ssCpuRawNice", "1.3.6.1.4.1.2021.11.51", "C"),
        new SnmpOidRecord("prTable", "1.3.6.1.4.1.2021.2", "TA"),
        new SnmpOidRecord("prEntry", "1.3.6.1.4.1.2021.2.1", "EN"),
        new SnmpOidRecord("prCount", "1.3.6.1.4.1.2021.2.1.5", "I"),
        new SnmpOidRecord("prMax", "1.3.6.1.4.1.2021.2.1.4", "I"),
        new SnmpOidRecord("prErrFixCmd", "1.3.6.1.4.1.2021.2.1.103", "S"),
        new SnmpOidRecord("prErrFix", "1.3.6.1.4.1.2021.2.1.102", "I"),
        new SnmpOidRecord("prMin", "1.3.6.1.4.1.2021.2.1.3", "I"),
        new SnmpOidRecord("prErrMessage", "1.3.6.1.4.1.2021.2.1.101", "S"),
        new SnmpOidRecord("prNames", "1.3.6.1.4.1.2021.2.1.2", "S"),
        new SnmpOidRecord("prErrorFlag", "1.3.6.1.4.1.2021.2.1.100", "I"),
        new SnmpOidRecord("prIndex", "1.3.6.1.4.1.2021.2.1.1", "I"),
        new SnmpOidRecord("snmperrErrMessage", "1.3.6.1.4.1.2021.101.101", "S"),
        new SnmpOidRecord("snmperrNames", "1.3.6.1.4.1.2021.101.2", "S"),
        new SnmpOidRecord("snmperrErrorFlag", "1.3.6.1.4.1.2021.101.100", "I"),
        new SnmpOidRecord("snmperrIndex", "1.3.6.1.4.1.2021.101.1", "I"),
        new SnmpOidRecord("laTable", "1.3.6.1.4.1.2021.10", "TA"),
        new SnmpOidRecord("laEntry", "1.3.6.1.4.1.2021.10.1", "EN"),
        new SnmpOidRecord("laLoadFloat", "1.3.6.1.4.1.2021.10.1.6", "O"),
        new SnmpOidRecord("laLoadInt", "1.3.6.1.4.1.2021.10.1.5", "I"),
        new SnmpOidRecord("laConfig", "1.3.6.1.4.1.2021.10.1.4", "S"),
        new SnmpOidRecord("laLoad", "1.3.6.1.4.1.2021.10.1.3", "S"),
        new SnmpOidRecord("laErrMessage", "1.3.6.1.4.1.2021.10.1.101", "S"),
        new SnmpOidRecord("laNames", "1.3.6.1.4.1.2021.10.1.2", "S"),
        new SnmpOidRecord("laIndex", "1.3.6.1.4.1.2021.10.1.1", "I"),
        new SnmpOidRecord("laErrorFlag", "1.3.6.1.4.1.2021.10.1.100", "I"),
        new SnmpOidRecord("versionDoDebugging", "1.3.6.1.4.1.2021.100.20", "I"),
        new SnmpOidRecord("versionConfigureOptions", "1.3.6.1.4.1.2021.100.6", "S"),
        new SnmpOidRecord("versionIdent", "1.3.6.1.4.1.2021.100.5", "S"),
        new SnmpOidRecord("versionSavePersistentData", "1.3.6.1.4.1.2021.100.13", "I"),
        new SnmpOidRecord("versionCDate", "1.3.6.1.4.1.2021.100.4", "S"),
        new SnmpOidRecord("versionRestartAgent", "1.3.6.1.4.1.2021.100.12", "I"),
        new SnmpOidRecord("versionDate", "1.3.6.1.4.1.2021.100.3", "S"),
        new SnmpOidRecord("versionTag", "1.3.6.1.4.1.2021.100.2", "S"),
        new SnmpOidRecord("versionUpdateConfig", "1.3.6.1.4.1.2021.100.11", "I"),
        new SnmpOidRecord("versionClearCache", "1.3.6.1.4.1.2021.100.10", "I"),
        new SnmpOidRecord("versionIndex", "1.3.6.1.4.1.2021.100.1", "I"),
        new SnmpOidRecord("ucdShutdown", "1.3.6.1.4.1.2021.251.2", "NT"),
        new SnmpOidRecord("ucdStart", "1.3.6.1.4.1.2021.251.1", "NT")    };
}

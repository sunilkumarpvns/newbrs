package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

public class ChangeIMSpackageStoredProcedure extends VoltProcedure {

    public final SQLStmt changeIMSpackage = new SQLStmt(
            "UPDATE TBLM_SUBSCRIBER " +
                    "SET IMSPACKAGE= ? " +
                    "WHERE SUBSCRIBERIDENTITY= ?");

    public VoltTable[] run(String subscriberID, String packageId) throws VoltAbortException {

        voltQueueSQL(changeIMSpackage, packageId, subscriberID);

        return voltExecuteSQL();
    }

}

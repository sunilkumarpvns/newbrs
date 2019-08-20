/**
 * 
 */
package com.elitecore.classicrating.datamanager.rating;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.elitecore.classicrating.datamanager.base.BaseDBHelper;
import com.elitecore.classicrating.datasource.ITransactionContext;
import com.elitecore.commons.logging.LogManager;

/**
 * @author sheetalsoni
 * 
 */
public class CDRChargeDBHelper extends BaseDBHelper {

    public CDRChargeDBHelper(ITransactionContext transactionContext) {
        super(transactionContext);
    }
    private static final String MODULE = "INSERT_CDR_CHARGE";
    private static final String INSERT_CDR_CHARGE_DETAILS = "INSERT INTO CDRCHARGETABLE " + "(CDRID,CHARGE,CUSTOMERNAME,CUSTOMERIDENTIFIER,ACCTINPUTOCTETS," +
            "ACCTOUTPUTOCTETS,VOLUME,CALLEDSTATIONID,CALLSTART,CALLEND,SESSIONTIME,EVENTTYPE,SERVICETYPE,CHARGEDDATE,SESSIONID)" +
            " VALUES (SEQ_CDRCHARGETABLE.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    /**
     * Inserts CDR Charge Details
     *
     * @method insertCDRChargeTable
     * @param CDRChargeData
     * @author sheetalsoni
     * @created 10/6/08
     * @return
     * */
    public void insertCDRChargeDetails(CDRChargeData cdrChargeData) throws SQLException, Exception {

        PreparedStatement pstmtCDRCharge = null;

        try {
            pstmtCDRCharge = super.getTransactionContext().prepareStatement(INSERT_CDR_CHARGE_DETAILS);

            pstmtCDRCharge.setLong(1, cdrChargeData.getCharge());
            pstmtCDRCharge.setString(2, cdrChargeData.getCustomerName());
            pstmtCDRCharge.setString(3, cdrChargeData.getCustomerIdentifier());
            pstmtCDRCharge.setLong(4, cdrChargeData.getAcctInputOctets());
            pstmtCDRCharge.setLong(5, cdrChargeData.getAcctOutputOctets());
            pstmtCDRCharge.setLong(6, cdrChargeData.getVolume());
            pstmtCDRCharge.setString(7, cdrChargeData.getCalledStationId());
            pstmtCDRCharge.setTimestamp(8, cdrChargeData.getCallStart());
            pstmtCDRCharge.setTimestamp(9, cdrChargeData.getCallEnd());
            pstmtCDRCharge.setLong(10, cdrChargeData.getSessionTime());
            pstmtCDRCharge.setString(11, cdrChargeData.getEventType());
            pstmtCDRCharge.setString(12, cdrChargeData.getServiceType());
            pstmtCDRCharge.setTimestamp(13, cdrChargeData.getChargedDate());
            pstmtCDRCharge.setString(14, cdrChargeData.getSessionId());

            pstmtCDRCharge.executeUpdate();

        } catch (SQLException e) {
            //	StringWriter stringWriter = new StringWriter();
			//	exception.printStackTrace(new PrintWriter(stringWriter));
				LogManager.getLogger().trace(MODULE, e.toString());
            super.getTransactionContext().isRollbackOnly();
            LogManager.getLogger().info(MODULE, "Exception caught while inserting CDRCharge Table");
            throw e;
        } finally {
            try {
                if (pstmtCDRCharge != null) {
                    super.getTransactionContext().closePreparedStatement(pstmtCDRCharge);
                }
            } catch (Exception e) {
                //	StringWriter stringWriter = new StringWriter();
				//	exception.printStackTrace(new PrintWriter(stringWriter));
					LogManager.getLogger().trace(MODULE, e.toString());
                LogManager.getLogger().info(MODULE, "Exception caught while closing prepared statement");
                throw e;
            }
        }
    }
}

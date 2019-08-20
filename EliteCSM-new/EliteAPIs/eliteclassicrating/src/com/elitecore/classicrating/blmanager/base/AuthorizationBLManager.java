/**
 *  Copyright (C) Elitecore Technologies LTD.
 *	Elite Classic Rating Project
 *
 *	Created on October 15, 2008
 *	@author Raghu
 *
 */
package com.elitecore.classicrating.blmanager.base;

import java.sql.SQLException;
import java.text.ParseException;

import com.elitecore.classicrating.api.IResponseObject;
import com.elitecore.classicrating.base.IBaseConstant;
import com.elitecore.classicrating.base.IRatingAppContext;
import com.elitecore.classicrating.blmanager.customer.CustomerNotFoundException;
import com.elitecore.classicrating.blmanager.customer.InsufficientBalanceException;
import com.elitecore.classicrating.blmanager.packages.PackageNotFoundException;
import com.elitecore.classicrating.commons.data.CDRData;
import com.elitecore.classicrating.commons.request.RequestParameters;
import com.elitecore.classicrating.commons.response.ResponseObject;
import com.elitecore.classicrating.commons.validators.RequestParameterValidator;
import com.elitecore.classicrating.datamanager.customer.CustomerBalanceData;
import com.elitecore.classicrating.datamanager.customer.CustomerServiceData;
import com.elitecore.classicrating.datamanager.packages.SlabDefinitionData;
import com.elitecore.classicrating.datamanager.rating.PrefixGroupDefinitionData;
import com.elitecore.classicrating.datamanager.rating.RateDefinitionDBHelper;
import com.elitecore.classicrating.datamanager.rating.RateDefinitionData;
import com.elitecore.classicrating.datamanager.rating.RateNotFoundException;
import com.elitecore.classicrating.datasource.ITransactionContext;

/**
 *
 *  AuthorizationBLManager
 *
 * @author raghug
 */
public class AuthorizationBLManager extends RatingBaseBLManager {

    private String MODULE = "Authorization BL Manager";

    public AuthorizationBLManager(IRatingAppContext ratingAppContext) {
        super(ratingAppContext);
    }

    public AuthorizationBLManager(IRatingAppContext ratingAppContext, ITransactionContext transactionContext) {
        super(ratingAppContext, transactionContext);
    }

    /**
     * Performs the Authorization Process and returns the ResponseObject containing the Respose Message.
     * @param request
     * @return
     * @throws RatingBLException
     * @throws Exception
     */
    public IResponseObject doAuthorization(RequestParameters request) throws MandatoryParameterMissingException, ParseException, SQLException, RateNotFoundException, InsufficientBalanceException, CustomerNotFoundException, PackageNotFoundException {
        Logger.trace(MODULE, "Starting Authorization Process...");

        IResponseObject response = new ResponseObject();

        try {
            super.startTransaction();
            request.put(IBaseConstant.REQUEST_TYPE, IBaseConstant.AUTHORISATION);
            RequestParameterValidator.validateParameters(request);

            CDRData cdrData = new CDRData(request);
            CustomerServiceData customerData = doGuiding(cdrData);
            doAuthPreRating(customerData, cdrData, response);
            response.setResponseCode(IBaseConstant.AUTHORIZATION_SUCCESS);
            response.setResponseMessage("Customer " + customerData.getCustomerName() + " is successfully authorized.");

            Logger.trace(MODULE, "Customer " + request.get(IBaseConstant.USERID) + " is successfully authenticated");
        } catch (InsufficientBalanceException ex) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, ex);
            throw ex;
        } catch (CustomerNotFoundException ex) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, ex);
            throw ex;

        } catch (PackageNotFoundException ex) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, ex);
            throw ex;
        } catch (NumberFormatException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } catch (MandatoryParameterMissingException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } catch (ParseException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } catch (SQLException e) {
            super.getTransactionContext().setRollbackOnly();
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } catch (RateNotFoundException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization.");
            Logger.trace(MODULE, e);
            throw e;
        } finally {
            try {
                super.endTransaction();
            } catch (SQLException e) {
                Logger.info(MODULE, "Exception caught while handling Transaction");
                throw e;
            }
        }

        Logger.info(MODULE, "Completed the Authorization Process..");
        return response;
    }

    /**
     * <p>Performs Pre-rating like Balance check, Max-Session-Limit</p>
     *
     * @param customerData
     * @param cdrData
     * @return
     * @throws RatingBLException
     * @throws InsufficientBalanceException
     * @throws RateNotFoundException
     * @throws SQLException
     * @throws RateNotFoundException
     * @throws InsufficientBalanceException
     */
    private void doAuthPreRating(CustomerServiceData customerData, CDRData cdrData, IResponseObject response) throws SQLException, RateNotFoundException, InsufficientBalanceException, PackageNotFoundException {
        Logger.debug(MODULE, "Into doAuthPreRating( " + customerData + " )");
        try {
            if (customerData.getCustomerType().equalsIgnoreCase(IBaseConstant.PREPAID)) {
                CustomerBalanceData customerBalanceData = customerData.getCustomerBalanceData();
                response.put(IBaseConstant.BALANCE, String.valueOf(customerBalanceData.getBalance()));

                // calculate the max limit that is to be allowed based on available balance

                if (cdrData.getEventType() != null && cdrData.getEventType().equalsIgnoreCase(IBaseConstant.EVENT)) {
                    long maxEvents = getMaxEventsAfterRating(customerData, cdrData);
                    response.put(IBaseConstant.MAX_EVENTS, String.valueOf(maxEvents));

                } else if (customerData.getPackageDefinitionData().getUnitType().equalsIgnoreCase(IBaseConstant.SEC)) {
                    long maxSessionTime = getMaxSessionTimeAfterRating(customerData, cdrData);
                    response.put(IBaseConstant.MAX_SESSION_TIME, String.valueOf(maxSessionTime));

                } else if (customerData.getPackageDefinitionData().getUnitType().equalsIgnoreCase(IBaseConstant.MB)) {
                    long maxSessionVolume = getMaxSessionVolumeAfterRating(customerData, cdrData);
                    response.put(IBaseConstant.MAX_SESSION_VOLUME, String.valueOf(maxSessionVolume));
                }
            }
        } catch (SQLException e) {
            Logger.error(MODULE, "Exception caught while doing Authorization Pre-Rating.");
            throw e;
        }

        Logger.debug(MODULE, "Out Of doAuthPreRating( " + customerData + " ) = " + response);

    }

    /**
     * <p>Gets the  Rate Data for Customer</p>
     * @method getRateDefinitionData
     * @param CustomerServiceData
     * @return RateDefinitionData
     *
     */
    private RateDefinitionData getRateDefinitionData(CustomerServiceData customerData, CDRData cdrData) throws PackageNotFoundException {
        Logger.trace(MODULE, "Into getRateDefinitionData( " + customerData + " )");

        RateDefinitionData rateData = new RateDefinitionData();

        rateData.setPackageId(customerData.getPackageId());
        rateData.setServiceType(customerData.getServiceType());

        // Setting ANY as default value for all rate definition conditions
        rateData.setPrefixGroup(IBaseConstant.ANY);
        rateData.setCustomerSubType(IBaseConstant.ANY);
        rateData.setEventType(IBaseConstant.ANY);
        rateData.setVersion(IBaseConstant.ANY);

        if (customerData.getServiceType().equalsIgnoreCase(IBaseConstant.VOIP)) {
            PrefixGroupDefinitionData prefixData = customerData.getPackageDefinitionData().getPrefixGroupDefinitionData();
            if (prefixData != null) {
                rateData.setPrefixGroup(prefixData.getPrefixGroupName());
            }
        }

        String packageType = customerData.getPackageDefinitionData().getPackageType();

        if (cdrData.getEventType() != null && cdrData.getEventType().equalsIgnoreCase(IBaseConstant.EVENT)) {
            rateData.setEventType(IBaseConstant.EVENT);
        } else if (packageType.equalsIgnoreCase(IBaseConstant.FLAT_PACKAGE)) {
        	// Do Nothing
        } else if (packageType.equalsIgnoreCase(IBaseConstant.SEGMENTED)) {
            rateData.setCustomerSubType(customerData.getCustomerSubType());
        } else if (packageType.equalsIgnoreCase(IBaseConstant.INDIVIDUAL)) {
            if (customerData.getVersion() != null && customerData.getVersion().length() > 0) {
                rateData.setVersion(customerData.getVersion());
            }
        } else if (packageType.equalsIgnoreCase(IBaseConstant.USAGE_BASED_PACKAGE)) {

            long totalUsage = 0;
            SlabDefinitionData slabData = customerData.getPackageDefinitionData().getSlabDefinitionData();

            if (slabData != null) {
            	
            	if ( customerData.getPackageDefinitionData().getUnitType().equalsIgnoreCase(IBaseConstant.MB)){
            		totalUsage =(long) customerData.getTotalUsage() / IBaseConstant.MB_CONST;
            	}
            	else if ( customerData.getPackageDefinitionData().getUnitType().equalsIgnoreCase(IBaseConstant.SEC)){
            		totalUsage = (long) customerData.getTotalUsage() / IBaseConstant.SECONDS_OF_MINUTE;
            	} 
            	
                if (slabData.getUnit1() > 0 && totalUsage <= slabData.getUnit1()) {
                    rateData.setEventType(IBaseConstant.SLAB_1);
                } else if (slabData.getUnit2() > 0 && totalUsage <= slabData.getUnit2()) {
                    rateData.setEventType(IBaseConstant.SLAB_2);
                } else if (slabData.getUnit3() > 0 && totalUsage <= slabData.getUnit3()) {
                    rateData.setEventType(IBaseConstant.SLAB_3);
                }
            }
        } else if (packageType.equalsIgnoreCase(IBaseConstant.STEP_WISE_PACKAGE)) {
            SlabDefinitionData slabData = customerData.getPackageDefinitionData().getSlabDefinitionData();

            if (slabData != null) {
                rateData.setEventType(IBaseConstant.SLAB_1);
            }
        } else {
             throw new PackageNotFoundException("No package found with package type: " + packageType);
        }
        Logger.trace(MODULE, "Out Of getRateDefinitionData( " + customerData + " )");
        return rateData;
    }

    /**
     * <p> This method gives Max Session Volume </p>
     * @param customerData
     * @return
     * @throws InsufficientBalanceException
     * @throws RateNotFoundException
     * @throws SQLException
     * @throws RateNotFoundException
     * @throws InsufficientBalanceException
     */
    private long getMaxSessionVolumeAfterRating(CustomerServiceData customerData, CDRData cdrData) throws SQLException, RateNotFoundException, InsufficientBalanceException, PackageNotFoundException {
        Logger.trace(MODULE, "Into getMaxSessionVolume( " + customerData + " )");

        long maxSessionVolume = 0;

        try {
            RateDefinitionData rateData = getRateDefinitionData(customerData, cdrData);

            RateDefinitionDBHelper rateDBHelper = new RateDefinitionDBHelper(super.getTransactionContext());

            double rate = rateDBHelper.getRate(rateData);
           
            double custBalance = customerData.getCustomerBalanceData().getBalance();

            if (custBalance < rate) {
                throw new InsufficientBalanceException("Insufficient balance in account. Customer Balance: " + custBalance);
            }
            
            if (rate == 0.0) {
                maxSessionVolume = (long) (custBalance * IBaseConstant.MB_CONST);
            } else {
                maxSessionVolume = (long) ((custBalance / rate) * (IBaseConstant.MB_CONST));
            }
        } catch (SQLException e) {
            Logger.error(MODULE, "Exception caught while getting Max Session Volume.");
            throw e;
        }
        Logger.trace(MODULE, "OutOf getMaxSessionVolume( " + customerData + " ) = " + maxSessionVolume);

        return maxSessionVolume;
    }

    /**
     * <p> Calculates max session available to customer</p>
     * @method getMaxSessionTimeAfterRating
     * @param customerData
     * @throws RatingBLException, SQLException
     * @return maxSessionTime
     */
    private long getMaxSessionTimeAfterRating(CustomerServiceData customerData, CDRData cdrData) throws SQLException, InsufficientBalanceException, RateNotFoundException, PackageNotFoundException {
        Logger.trace(MODULE, "Into getMaxSessionTime( " + customerData + " )");

        long maxSessionTime = 0;

        try {
            RateDefinitionData rateData = getRateDefinitionData(customerData, cdrData);

            RateDefinitionDBHelper rateDBHelper = new RateDefinitionDBHelper(super.getTransactionContext());
            double rate = rateDBHelper.getRate(rateData);
   
            double custBalance = customerData.getCustomerBalanceData().getBalance();

            if (custBalance < rate) {
                throw new InsufficientBalanceException("Insufficient balance in account. Customer Balance: " + custBalance);
            }

            if (rate == 0.0) {
                maxSessionTime = (long) ((custBalance) * IBaseConstant.SECONDS_OF_MINUTE);
            } else {
                maxSessionTime = (long) ((custBalance / rate) * IBaseConstant.SECONDS_OF_MINUTE);
            }
        } catch (SQLException e) {
            throw e;
        }

        Logger.trace(MODULE, "OutOf get MaxSessionTime( " + customerData + " ) = " + maxSessionTime);

        return maxSessionTime;
    }

    /**
     * <p> Calculates max events value for authorization request </p>
     * @method getMaxEventsAfterRating
     * @param customerData
     * @throws RatingBLException, SQLException
     * @return maxSessionTime
     */
    private long getMaxEventsAfterRating(CustomerServiceData customerData, CDRData cdrData) throws SQLException, InsufficientBalanceException, RateNotFoundException, PackageNotFoundException {
        Logger.trace(MODULE, "Into getMaxEvents( " + customerData + " )");

        long maxEvents = 0;

        try {
            RateDefinitionData rateData = getRateDefinitionData(customerData, cdrData);

            RateDefinitionDBHelper rateDBHelper = new RateDefinitionDBHelper(super.getTransactionContext());
            double rate = rateDBHelper.getRate(rateData);

            double custBalance = customerData.getCustomerBalanceData().getBalance();

            if (custBalance < rate) {
                throw new InsufficientBalanceException("Insufficient balance in account. Customer Balance: " + custBalance);
            }
            if (rate == 0.0) {
                maxEvents = (long) (custBalance);
            } else {
                maxEvents = (long) (custBalance / rate);
            }
        } catch (SQLException e) {
            Logger.error(MODULE, "Exception caught while getting Max Session Volume.");
            throw e;
        }

        Logger.trace(MODULE, "OutOf get getMaxEvents( " + customerData + " ) = " + maxEvents);

        return maxEvents;
    }
}

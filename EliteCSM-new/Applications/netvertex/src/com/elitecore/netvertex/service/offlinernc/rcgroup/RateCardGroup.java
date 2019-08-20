package com.elitecore.netvertex.service.offlinernc.rcgroup;

import static com.elitecore.netvertex.service.offlinernc.util.RnCPreConditions.checkKeyNotNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyValueConstants;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.gateway.file.util.OFCSFileHelper;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.core.RncResponseValueProvider;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCard;

public class RateCardGroup {

	private static final String MODULE = "RCG";

	private String name;
	private LogicalExpression advancedCondition;
	private Optional<RateCard> peakRateCard;
	private Optional<RateCard> specialDaysRateCard;
	private Optional<RateCard> weekendRateCard;
	private Optional<RateCard> offPeakRateCard;
	private Optional<CalendarList> specialDaysCalendarList;
	private List<TimeSlot> specialDaysTimeSlots;
	private List<TimeSlot> weekEndTimeSlots;
	private List<TimeSlot> offPeakDaysTimeSlots;
	private List<TimeSlot> peakDaysTimeSlots;
	private Optional<RateCard> rate2RateCard;
	private Optional<RateCard> rate3RateCard;
	private String ratingKey;
	private String timeFormat;



	public RateCardGroup(String name, LogicalExpression advancedCondition, Optional<RateCard> peakDaysRateCard,
			Optional<RateCard> weekendRateCard, Optional<RateCard> offPeakRateCard,
			Optional<RateCard> specialDaysRateCard, Optional<CalendarList> specialDaysCalendarList, List<TimeSlot> specialDaysTimeSlots,
			List<TimeSlot> weekEndTimeSlots, List<TimeSlot> offPeakDaysTimeSlots, List<TimeSlot> peakDaysTimeSlots, Optional<RateCard> rate2RateCard, 
			Optional<RateCard> rate3RateCard, String ratingKey, String timeFormat) {

		this.name = name;
		this.advancedCondition = advancedCondition;
		this.peakRateCard = peakDaysRateCard;
		this.specialDaysCalendarList = specialDaysCalendarList;
		this.weekendRateCard = weekendRateCard;
		this.offPeakRateCard = offPeakRateCard;
		this.specialDaysRateCard = specialDaysRateCard;
		this.specialDaysTimeSlots = specialDaysTimeSlots;
		this.weekEndTimeSlots = weekEndTimeSlots;
		this.offPeakDaysTimeSlots = offPeakDaysTimeSlots;
		this.peakDaysTimeSlots = peakDaysTimeSlots;
		this.rate2RateCard = rate2RateCard;
		this.rate3RateCard = rate3RateCard;
		this.ratingKey = ratingKey;
		this.timeFormat = timeFormat;
	}

	public boolean apply(RnCRequest request, RnCResponse response) throws OfflineRnCException {

		request.getTraceWriter().println();
		request.getTraceWriter().incrementIndentation();
		request.getTraceWriter().println();
		request.getTraceWriter().print("[ " + MODULE + " ] : " + name);
		request.getTraceWriter().println();

		if (advancedCondition.evaluate(new RncResponseValueProvider(response)) == false) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Advanced condition: " + advancedCondition + " not satisfied");
			}
			request.getTraceWriter().decrementIndentation();	
			return false;
		}

		if (LogManager.getLogger().isDebugLogLevel()) {
			request.getTraceWriter().println();
			request.getTraceWriter().print(" - Advanced condition: " + advancedCondition + " satisfied");
		}

		boolean applied = applyRateCard1(request, response);

		request.getTraceWriter().decrementIndentation();	

		if (applied == false) {
			return applied;
		}

		if (rate2RateCard.isPresent()) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Selected rate2 rate card: " + rate2RateCard.get().getName());
			}
			
			if (rate2RateCard.get().apply(request, response)) {
				response.setAttribute(OfflineRnCKeyConstants.RATE_CARD_ID2, rate2RateCard.get().getName());
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print(" - Rate 2 rate card: " + rate2RateCard.get().getName() + " not applied.");
				}
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Rate 2 rate card not configured");
			}
		}
		
		if (rate3RateCard.isPresent()) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Selected rate3 rate card: " + rate3RateCard.get().getName());
			}
			
			if (rate3RateCard.get().apply(request, response)) {
				response.setAttribute(OfflineRnCKeyConstants.RATE_CARD_ID3, rate3RateCard.get().getName());
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print(" - Rate 3 rate card: " + rate3RateCard.get().getName() + " not applied.");
				}
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Rate 3 rate card not configured");
			}
		}
		
		response.setAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP, name);

		return applied;
	}

	private boolean applyRateCard1(RnCRequest request, RnCResponse response) throws OfflineRnCException {

		String ratingKeyValue = checkKeyNotNull(request, ratingKey);

		Timestamp ratingKeyTimestamp;

		try {
			ratingKeyTimestamp = OFCSFileHelper.timeStampOf(ratingKeyValue, timeFormat);
		} catch (ParseException ex) {
			throw new OfflineRnCException(OfflineRnCErrorCodes.INVALID_EDR, OfflineRnCErrorMessages.INVALID_EDR, 
					OfflineRnCErrorMessages.INVALID_DATE_FORMAT + "-" + ratingKey);
		}
		Calendar sessionStartDate = Calendar.getInstance();
		sessionStartDate.setTime(ratingKeyTimestamp);

		return applySpecialRateCard(request, response, ratingKeyTimestamp, sessionStartDate)
				|| applyWeekendRateCard(request, response, ratingKeyValue, sessionStartDate)
				|| applyOffPeakRateCard(request, response, ratingKeyValue, sessionStartDate)
				|| applyPeakRateCard(request, response, ratingKeyValue, sessionStartDate);
	}

	private boolean applyPeakRateCard(RnCRequest request, RnCResponse response, String sessionStart, Calendar sessionStartDate) throws OfflineRnCException {

		boolean applied = false;
		
		if (peakRateCard.isPresent() == false) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Peak rate card not configured");
			}
			return applied;
		}

		if (peakDaysTimeSlots.isEmpty()) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Selected peak rate card: " + peakRateCard.get().getName());
			}

			applied = peakRateCard.get().apply(request, response);
		} else {
			if (timeslotContains(peakDaysTimeSlots, sessionStartDate)) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print(" - Selected peak rate card: " + peakRateCard.get().getName());
				}

				applied = peakRateCard.get().apply(request, response);	
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print(" - session start date: " + sessionStart
							+ " not within configured peak timeslots.");

				}
			}
		}

		if (applied) {
			response.setAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1, peakRateCard.get().getName());
			response.setAttribute(OfflineRnCKeyConstants.RATE_TYPE, OfflineRnCKeyValueConstants.RATE_TYPE_PEAK_DAY.val);
			
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Peak rate card: " + peakRateCard.get().getName() + " not applied.");
			}
		}

		return applied;
	}

	private boolean applyOffPeakRateCard(RnCRequest request, RnCResponse response, String sessionStart, Calendar sessionStartDate) throws OfflineRnCException {
		boolean applied = false;

		if (offPeakRateCard.isPresent() == false) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Off-Peak rate card not configured");
			}
			return applied;
		}
		
		if (timeslotContains(offPeakDaysTimeSlots, sessionStartDate)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Selected off-peak rate card: " + offPeakRateCard.get().getName());
			}

			applied = offPeakRateCard.get().apply(request, response);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print( " - session start date: " + sessionStart
						+ " not within configured off-peak timeslots.");
			}
		}

		if (applied) {
			response.setAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1, offPeakRateCard.get().getName());
			response.setAttribute(OfflineRnCKeyConstants.RATE_TYPE, OfflineRnCKeyValueConstants.RATE_TYPE_OFF_PEAK_DAY.val);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Off-Peak rate card: " + offPeakRateCard.get().getName() + " not applied.");
			}
		}

		return applied;
	}

	private boolean applyWeekendRateCard(RnCRequest request, RnCResponse response, String sessionStart, Calendar sessionStartDate) throws OfflineRnCException {
		boolean applied = false;
		
		if (weekendRateCard.isPresent() == false) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Weekend rate card not configured");
			}
			
			return applied;
		}
		
		if (timeslotContains(weekEndTimeSlots, sessionStartDate)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Selected weekend rate card: " + weekendRateCard.get().getName());
			}

			applied = weekendRateCard.get().apply(request, response);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - session start date: " + sessionStart
						+ " not within configured weekend timeslots.");
			}
		}

		if (applied) {
			response.setAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1, weekendRateCard.get().getName());
			response.setAttribute(OfflineRnCKeyConstants.RATE_TYPE, OfflineRnCKeyValueConstants.RATE_TYPE_WEEKEND_DAY.val);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Weekend rate card: " + weekendRateCard.get().getName() + " not applied.");
			}
		}

		return applied;
	}

	private boolean applySpecialRateCard(RnCRequest request, RnCResponse response, Timestamp sessionStartTime, Calendar sessionStartDate) throws OfflineRnCException {

		boolean applied = false;

		if (specialDaysRateCard.isPresent() == false) {
			request.getTraceWriter().println();
			request.getTraceWriter().print(" - Special day rate card not conifgured");
			return applied;
		}

		String sessionStart = checkKeyNotNull(request, OfflineRnCKeyConstants.SESSION_CONNECT_TIME);
		if (specialDaysCalendarList.isPresent()) {
			if (specialDaysCalendarList.get().contains(sessionStartTime)) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print(" - session start date: " + sessionStart
							+ " within special days calendar: " + specialDaysCalendarList.get().getCalendarListName());
				}

				if (timeslotContains(specialDaysTimeSlots, sessionStartDate)) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						request.getTraceWriter().println();
						request.getTraceWriter().print(" - Selected special days rate card: " + specialDaysRateCard.get().getName());
					}
					applied = specialDaysRateCard.get().apply(request, response);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						request.getTraceWriter().println();
						request.getTraceWriter().print(" - session start date: " + sessionStart
								+ " not within configured special days timeslots.");
					}
				}
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					request.getTraceWriter().println();
					request.getTraceWriter().print(" - session start date: " + sessionStart
							+ " not within calendar: " + specialDaysCalendarList.get().getCalendarListName());
				}
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Special days calendar not configured, selected special days rate card: " + specialDaysRateCard.get().getName());
			}

			applied = specialDaysRateCard.get().apply(request, response);
		}

		if (applied) {
			response.setAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1, specialDaysRateCard.get().getName());
			response.setAttribute(OfflineRnCKeyConstants.RATE_TYPE, OfflineRnCKeyValueConstants.RATE_TYPE_SPECIAL_DAY.val);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				request.getTraceWriter().println();
				request.getTraceWriter().print(" - Special days rate card: " + specialDaysRateCard.get().getName() + " not applied.");
			}
		}

		return applied;
	}

	private static boolean timeslotContains(List<TimeSlot> timeSlots, Calendar sessionStartDate) {
		for (TimeSlot timeSlot : timeSlots) {
			if (timeSlot.getDuration(sessionStartDate) != AccessTimePolicy.FAILURE) {
				return true;
			}
		}
		return false;
	}
}

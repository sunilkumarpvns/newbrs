package com.elitecore.netvertex.service.offlinernc.rcgroup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.elitecore.acesstime.TimeSlot;
import com.elitecore.acesstime.exception.InvalidTimeSlotException;
import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeRange;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.RateCardGroupTimeSlotConstant;
import com.elitecore.corenetvertex.pd.calender.CalenderData;
import com.elitecore.corenetvertex.pd.calender.CalenderDetails;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.TimeSlotRelationData;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCard;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;

public class RateCardGroupFactory {

	private RateCardFactory rateCardFactory;

	private SystemParameterConfiguration systemParameterConfiguration;
	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal;


	public RateCardGroupFactory(RateCardFactory rateCardFactory,SystemParameterConfiguration systemParameterConfiguration) {

		this.rateCardFactory = rateCardFactory;
		this.systemParameterConfiguration = systemParameterConfiguration;
		this.simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(systemParameterConfiguration.getEdrDateTimeStampFormat());

	}

	public RateCardGroup create(RateCardGroupData rateCardGroupData, String currency, String accountEffect) throws InitializationFailedException {
		LogicalExpression advancedCondition = createAdvancedCondition(rateCardGroupData.getAdvanceCondition());
		Optional<RateCard> peakDaysRateCard = Optional.empty();
		Optional<RateCard> specialDaysRateCard = Optional.empty();
		Optional<RateCard> weekendRateCard = Optional.empty();
		Optional<RateCard> offPeakRateCard = Optional.empty();
		Optional<RateCard> rate2RateCard = Optional.empty();
		Optional<RateCard> rate3RateCard = Optional.empty();
		List<TimeSlot> weekEndTimeSlots = new ArrayList<>();
		List<TimeSlot> specialDaysTimeSlots = new ArrayList<>();
		List<TimeSlot> peakDaysTimeSlots = new ArrayList<>();
		List<TimeSlot> offPeakDaysTimeSlots = new ArrayList<>();
		String ratingKey = systemParameterConfiguration.getRateSelectionWhenDateChange();
		String timeFormat = systemParameterConfiguration.getEdrDateTimeStampFormat();
		
		if (rateCardGroupData.getPeakRateRateCard() != null) {
			peakDaysRateCard = Optional.of(rateCardFactory.createRateCard1(rateCardGroupData.getPeakRateRateCard(), currency, accountEffect));
		}
		
		if (rateCardGroupData.getSpecialDayRateCard() != null) {
			specialDaysRateCard = Optional.of(rateCardFactory.createRateCard1(rateCardGroupData.getSpecialDayRateCard(), currency, accountEffect));
		}
		
		if (rateCardGroupData.getWeekendRateRateCard() != null) {
			weekendRateCard = Optional.of(rateCardFactory.createRateCard1(rateCardGroupData.getWeekendRateRateCard(), currency, accountEffect));
		}
		
		if (rateCardGroupData.getOffpeakRateRateCard() != null) {
			offPeakRateCard = Optional.of(rateCardFactory.createRateCard1(rateCardGroupData.getOffpeakRateRateCard(), currency, accountEffect));
		}
		
		if (rateCardGroupData.getRate2RateCard() != null) {
			rate2RateCard = Optional.of(rateCardFactory.createRateCard2(rateCardGroupData.getRate2RateCard(), currency, accountEffect));
		}
		
		if (rateCardGroupData.getRate3RateCard() != null) {
			rate3RateCard = Optional.of(rateCardFactory.createRateCard3(rateCardGroupData.getRate3RateCard(), currency, accountEffect));
		}
		
		Optional<CalendarList> specialDayCalendarList = Optional.empty();
		if (rateCardGroupData.getSpecialDayCalender() != null) {
			specialDayCalendarList = Optional.of(createCalendarList(rateCardGroupData.getSpecialDayCalender()));
		}
		
		for (TimeSlotRelationData timeSlotRelationData : rateCardGroupData.getTimeSlotRelationData()) {
			try {
				if (timeSlotRelationData.getType().equals(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue())) {
					specialDaysTimeSlots.add(createTimeSlot(timeSlotRelationData));
				} else if(timeSlotRelationData.getType().equals(RateCardGroupTimeSlotConstant.WEEKEND_DAY_RATE.getValue())) {
					weekEndTimeSlots.add(createTimeSlot(timeSlotRelationData));
				} else if(timeSlotRelationData.getType().equals(RateCardGroupTimeSlotConstant.OFF_PEAK_DAY_RATE.getValue())) {
					offPeakDaysTimeSlots.add(createTimeSlot(timeSlotRelationData));
				} else if(timeSlotRelationData.getType().equals(RateCardGroupTimeSlotConstant.PEAK_DAY_RATE.getValue())) {
					peakDaysTimeSlots.add(createTimeSlot(timeSlotRelationData));
				}

			} catch (InvalidTimeSlotException e) {
				throw new InitializationFailedException(e);
			}
			
		}
		return new RateCardGroup(rateCardGroupData.getName(), advancedCondition, peakDaysRateCard, weekendRateCard, 
				offPeakRateCard, specialDaysRateCard, specialDayCalendarList, specialDaysTimeSlots, weekEndTimeSlots, 
				offPeakDaysTimeSlots, peakDaysTimeSlots, rate2RateCard, rate3RateCard, ratingKey, timeFormat);

	}

	private TimeSlot createTimeSlot(TimeSlotRelationData timeSlotRelationData) throws InvalidTimeSlotException {
		return TimeSlot.getTimeSlot(null, null, timeSlotRelationData.getDayOfWeek(), timeSlotRelationData.getTimePeriod());
	}

	private LogicalExpression createAdvancedCondition(String advanceCondition) throws InitializationFailedException {
		if (Strings.isNullOrBlank(advanceCondition) == false) {
			return parseExpression(advanceCondition);
		} else {
			return parseExpression("\"1\" = \"1\"");
		}
	}

	private LogicalExpression parseExpression(String advanceCondition) throws InitializationFailedException {
		try {
			return (LogicalExpression) Compiler.getDefaultCompiler().parseExpression(advanceCondition);
		} catch (InvalidExpressionException ex) {
			throw new InitializationFailedException(ex);
		}
	}
	
	public CalendarList createCalendarList(CalenderData calendarData) throws InitializationFailedException {
		
		List<Calendar> calendars = new ArrayList<>();
		for (CalenderDetails calendarDetail : calendarData.getCalenderDetails()) {
			TimeRange timeRange = null;
			try {
				timeRange = TimeRange.closed(systemParameterConfiguration.getEdrDateTimeStampFormat(), simpleDateFormatThreadLocal.get().format(calendarDetail.getFromDate()), simpleDateFormatThreadLocal.get().format(calendarDetail.getToDate()));
			} catch (ParseException e) {
				throw new InitializationFailedException(e);
			}
			calendars.add(new Calendar(calendarDetail.getCalenderName(), timeRange));
			
		}
		return new CalendarList(calendarData.getCalenderList(), calendars);
	}

}

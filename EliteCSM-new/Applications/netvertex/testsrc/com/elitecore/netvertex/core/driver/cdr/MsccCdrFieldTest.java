package com.elitecore.netvertex.core.driver.cdr;
import com.elitecore.netvertex.rnc.ReportedUsageSummary;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(HierarchicalContextRunner.class)
public class MsccCdrFieldTest {

    public class CALL_START {
        @Test
        public void getValueWhenDateFormatFoundInResponse() {
            PCRFResponse pcrfResponse = spy(new PCRFResponseImpl());
            ReportedUsageSummary reportedUsageSummary= new ReportedUsageSummary(0,null);
            Calendar calendar = createFixedDate();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

            doReturn(calendar.getTime()).when(pcrfResponse).getSessionStartTime();
            pcrfResponse.setChargingCDRDateFormat(simpleDateFormat);

            String formattedDate = MsccCdrFields.CALL_START.getValue(reportedUsageSummary ,pcrfResponse);
            assertEquals("1999-Jun-12 09:12:12",formattedDate);

        }

        @Test
        public void getValueWhenDateFormatNotFoundInResponse() {

            PCRFResponse pcrfResponse = spy(new PCRFResponseImpl());
            ReportedUsageSummary reportedUsageSummary= new ReportedUsageSummary(0,null);
            Calendar calendar = createFixedDate();
            calendar.set(Calendar.MILLISECOND,555);

            doReturn(calendar.getTime()).when(pcrfResponse).getSessionStartTime();

            String formattedDate = MsccCdrFields.CALL_START.getValue(reportedUsageSummary ,pcrfResponse);
            assertEquals("12-Jun-99 09.12.12.555 AM",formattedDate);
        }

        @Test
        public void getValueReturnBlankWhenSessionStartDateInResponseIsNull() {

            PCRFResponse pcrfResponse = spy(new PCRFResponseImpl());
            ReportedUsageSummary reportedUsageSummary= new ReportedUsageSummary(0,null);

            doReturn(null).when(pcrfResponse).getSessionStartTime();

            String formattedDate = MsccCdrFields.CALL_START.getValue(reportedUsageSummary ,pcrfResponse);
            assertEquals("",formattedDate);
        }
    }

    private Calendar createFixedDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1999);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 12);
        calendar.set(Calendar.SECOND, 12);
        return calendar;
    }

    public class CALL_END {
        @Test
        public void getValueWhenDateFormatFoundInReportedUsageSummary() {
            Calendar calendar = createFixedDate();

            PCRFResponse pcrfResponse = new PCRFResponseImpl();
            ReportedUsageSummary reportedUsageSummary= new ReportedUsageSummary(0,null);
            reportedUsageSummary.setSessionStopTime(calendar.getTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
            pcrfResponse.setChargingCDRDateFormat(simpleDateFormat);

            String formattedDate = MsccCdrFields.CALL_END.getValue(reportedUsageSummary,pcrfResponse);
            assertEquals("1999-Jun-12 09:12:12",formattedDate);
        }

        @Test
        public void getValueWhenDateFormatNotFoundInReportedUsageSummary() {
            Calendar calendar = createFixedDate();
            calendar.set(Calendar.MILLISECOND,555);

            PCRFResponse pcrfResponse = new PCRFResponseImpl();
            ReportedUsageSummary reportedUsageSummary= new ReportedUsageSummary(0,null);
            reportedUsageSummary.setSessionStopTime(calendar.getTime());

            String formattedDate = MsccCdrFields.CALL_END.getValue(reportedUsageSummary,pcrfResponse);
            assertEquals("12-Jun-99 09.12.12.555 AM",formattedDate);
        }

        @Test
        public void getValueReturnBlankWhenSessionEndDateIsNullInReportedUsageSummary() {

            PCRFResponse pcrfResponse = new PCRFResponseImpl();
            ReportedUsageSummary reportedUsageSummary= new ReportedUsageSummary(0,null);
            String formattedDate = MsccCdrFields.CALL_END.getValue(reportedUsageSummary ,pcrfResponse);
            assertEquals("",formattedDate);
        }
    }
}

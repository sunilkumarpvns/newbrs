package com.elitecore.core.driverx.cdr.deprecated;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.driverx.cdr.data.CSVStripMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

public abstract class CSVLineBuilderSupport<T> implements BaseCSVDriver.CSVLineBuilder<T> {

    public static final String PREFIX = "PREFIX";
    public static final String SUFFIX = "SUFFIX";
    @Nonnull private final String delimiter;
    @Nonnull private Map<String, CSVStripMapping> stripMappings;
    @Nonnull private final TimeSource timeSource;
    @Nonnull private final SimpleDateFormat timestampFormat;

    public CSVLineBuilderSupport(@Nonnull String delimiter,
                                 @Nonnull Map<String, CSVStripMapping> stripMappings,
                                 @Nonnull SimpleDateFormat timestampFormat,
                                 @Nonnull TimeSource timeSource) {
        this.delimiter = delimiter;
        this.stripMappings = stripMappings;
        this.timeSource = timeSource;
        this.timestampFormat = timestampFormat;
    }


    protected final void appendValue(@Nonnull StringBuilder record, @Nullable String value) {

        String val = value;
        if (val != null) {
            if (val.contains(getDelimiter())) {
                val = val.replaceAll(getDelimiter(), "\\\\" + getDelimiter());
            }
            record.append(val).append(getDelimiter());
        } else {
            record.append(getDelimiter());
        }
    }

    protected final void appendValue(@Nonnull StringBuilder record, int value) {
        record.append(value).append(getDelimiter());
    }

    protected final void appendValue(@Nonnull StringBuilder record, double value) {
        record.append(value).append(getDelimiter());
    }

    protected final void appendValue(@Nonnull StringBuilder record, long value) {
        record.append(value).append(getDelimiter());
    }

    protected final @Nonnull String getStrippedValue(@Nonnull String key, @Nonnull String value) {
        String val = value;
        CSVStripMapping stripMapping = stripMappings.get(key);
        if(stripMapping != null) {
            String pattern = stripMapping.getPattern();
            String seperator = stripMapping.getSeperator();

            if(seperator == null || val == null)
                return val;

            int index = val.indexOf(seperator);
            if(index != -1) {
                if (SUFFIX.equalsIgnoreCase(pattern))
                    val =  val.substring(0, index).trim();
                else if (PREFIX.equalsIgnoreCase(pattern))
                    val =  val.substring(index+1, val.length()).trim();
            }
        }
        return val;
    }

    protected void appendTimestamp(@Nonnull StringBuilder record) {

        String strDateFormat = timestampFormat.format(new Timestamp(timeSource.currentTimeInMillis()));
        if(strDateFormat.contains(getDelimiter())) {
            strDateFormat = strDateFormat.replaceAll(getDelimiter(), "\\\\" + getDelimiter());
        }
        record.append(strDateFormat);
    }

    private String getDelimiter() {
        return delimiter;
    }

}

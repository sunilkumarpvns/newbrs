package com.elitecore.corenetvertex.util;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;

import javax.annotation.Nonnull;

/**
 * Created by harsh on 2/27/17.
 */
public class SystemPropertyReaders {

    private static final String MODULE = "SYS-PROPERTY-RDR";

    public static interface SystemPropertyReader<T> {
        public T read() throws RuntimeException;
    }




    private static class StringPropertyReader<T> implements SystemPropertyReader<T> {

        private String propertyVal;
        private Function<String, T> function;
        private String messageOnException;

        private StringPropertyReader(String propertyVal, Function<String, T> function) {
            this.propertyVal = propertyVal;
            this.function = function;
        }

        @Override
        public T read() throws RuntimeException {
            return function.apply(System.getProperty((propertyVal)));
        }
    }

    private static  class NumberReader implements SystemPropertyReader<Long> {


        private String propertyVal;
        private Long maxVal = Long.MAX_VALUE;
        private Long minVal = Long.MIN_VALUE;
        private Long defaultVal;
        private String messageOnException;

        private NumberReader(String propertyVal) {
            this.propertyVal = propertyVal;
        }


        @Override
        public Long read() throws RuntimeException {
            LogManager.getLogger().info(MODULE, "Reading property:" + propertyVal);

            Long val;
            String stringVal = System.getProperty(propertyVal);;

            if(Strings.isNullOrBlank(stringVal)) {
                if(defaultVal == null) {
                    throw new IllegalArgumentException("No value or Empty string received");
                } else {
                    LogManager.getLogger().warn(MODULE, "Applying default value:" + defaultVal + ". Reason: No value or Empty string received for property:" + propertyVal);
                    val = defaultVal;
                }
            } else {
                try {
                    long parseVale =  Long.parseLong(stringVal);

                    if(minVal != null && parseVale < minVal) {
                        LogManager.getLogger().warn(MODULE, "Applying default value:" + defaultVal + ". Reason: Value:" + parseVale + " is less then min val:" + minVal);
                        val = defaultVal;
                    } else if (maxVal != null && parseVale > maxVal){
                        LogManager.getLogger().warn(MODULE, "Applying default value:" + defaultVal + ". Reason: Value:" + parseVale + " is greater then max val:" + maxVal);
                        val = defaultVal;
                    } else {
                        val = parseVale;
                    }

                } catch(Exception ex) {
                    if(defaultVal == null) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    } else {
                        LogManager.getLogger().error(MODULE, messageOnException + ". Reason:" + ex.getMessage() + ", applying default value:" + defaultVal);
                        val = defaultVal;
                    }
                }
            }

            LogManager.getLogger().info(MODULE, "Value of the property:" + propertyVal + " is " + val);

            return val;
        }


    }


    public static  class NumberReaderBuilder {

        private String propertyVal;
        private Long minVal;
        private Long maxVal;
        private String message;
        private Long defaultValue;

        public NumberReaderBuilder(String propertyVal) {
            this.propertyVal = propertyVal;
        }

        public NumberReaderBuilder between(long to, long from) {
            this.minVal = to;
            this.maxVal = from;
            return this;
        }

        public NumberReaderBuilder onFail(long defaultValue, @Nonnull String message) {
            checkNotNull(message, "message on fail is null");
            this.defaultValue = defaultValue;
            this.message = message;
            return this;
        }

        public SystemPropertyReader<Long> build() throws IllegalArgumentException{

            NumberReader numberReader = new NumberReader(propertyVal);

            if(defaultValue != null) {
                numberReader.defaultVal = defaultValue;
            }

            if(minVal != null) {
                checkArgument(defaultValue == null || defaultValue >= minVal, "Default val:" + defaultValue + " is less than min val:" + minVal);
                numberReader.minVal = minVal;
            }

            if(maxVal != null) {
                checkArgument(defaultValue == null || defaultValue <= maxVal,"Default val:" + defaultValue + " is greater than max val:" + maxVal);
                numberReader.maxVal = maxVal;
            }

            if(message != null) {
                numberReader.messageOnException = message;
            }

            return numberReader;
        }
    }


}

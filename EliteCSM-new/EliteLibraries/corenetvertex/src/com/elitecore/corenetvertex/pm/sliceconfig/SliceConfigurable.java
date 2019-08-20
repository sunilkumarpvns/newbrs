package com.elitecore.corenetvertex.pm.sliceconfig;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pd.sliceconfig.SliceConfigData;
import com.elitecore.corenetvertex.pm.DataReader;
import com.elitecore.corenetvertex.pm.HibernateConfigurationUtil;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SliceConfigurable {
    private static final String MODULE = "SLICE-CONFIGRBLE";
    private final DataReader dataReader;
    private SessionFactory sessionFactory;
    private DataSliceConfiguration dataSliceConfiguration;

    public SliceConfigurable(SessionFactory sessionFactory, DataReader dataReader) {
        this.sessionFactory = sessionFactory;
        this.dataReader = dataReader;
    }

    public void readSliceConfiguration() throws LoadConfigurationException {
        getLogger().info(MODULE, "Read Slice configuration started");

        Session session = sessionFactory.openSession();

        try {
            List<SliceConfigData> sliceConfigurationDatas = dataReader.readAll(SliceConfigData.class, session);
            if (Collectionz.isNullOrEmpty(sliceConfigurationDatas)) {
                throw new LoadConfigurationException("No slice configuration found.");
            }


            if (sliceConfigurationDatas.size() > 1) {
                getLogger().warn(MODULE, "Invalid slice configuation, taking first slice configration with id: "
                        + sliceConfigurationDatas.get(0).getId() + ". Reason: More than one slice configurations found.");
            }

            SliceConfigData sliceConfigData = sliceConfigurationDatas.get(0);
            dataSliceConfiguration = new DataSliceConfiguration();

            dataSliceConfiguration.setMonetaryReservation(sliceConfigData.getMonetaryReservation());
            dataSliceConfiguration.setVolumeSlicePercentage(sliceConfigData.getVolumeSlicePercentage());
            dataSliceConfiguration.setVolumeSliceThreshold(sliceConfigData.getVolumeSliceThreshold());
            dataSliceConfiguration.setVolumeMinimumSlice(sliceConfigData.getVolumeMinimumSlice());
            dataSliceConfiguration.setVolumeMaximumSlice(sliceConfigData.getVolumeMaximumSlice());
            dataSliceConfiguration.setVolumeMinimumSliceUnit(DataUnit.valueOf(sliceConfigData.getVolumeMinimumSliceUnit()));
            dataSliceConfiguration.setVolumeMaximumSliceUnit(DataUnit.valueOf(sliceConfigData.getVolumeMaximumSliceUnit()));
            dataSliceConfiguration.setTimeSlicePercentage(sliceConfigData.getTimeSlicePercentage());
            dataSliceConfiguration.setTimeSliceThreshold(sliceConfigData.getTimeSliceThreshold());
            dataSliceConfiguration.setTimeMinimumSlice(sliceConfigData.getTimeMinimumSlice());
            dataSliceConfiguration.setTimeMaximumSlice(sliceConfigData.getTimeMaximumSlice());
            dataSliceConfiguration.setTimeMinimumSliceUnit(TimeUnit.fromVal(sliceConfigData.getTimeMinimumSliceUnit()));
            dataSliceConfiguration.setTimeMaximumSliceUnit(TimeUnit.fromVal(sliceConfigData.getTimeMaximumSliceUnit()));
            dataSliceConfiguration.setDynamicSlicing(sliceConfigData.getDynamicSlicing() == null ? false : sliceConfigData.getDynamicSlicing());

            getLogger().info(MODULE, "Read slice configuration completed");
        } finally{
            HibernateConfigurationUtil.closeQuietly(session);
        }

    }

    public DataSliceConfiguration getConfiguration() {
        return dataSliceConfiguration;
    }


    @Override
    public String toString() {
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        builder.newline();
        builder.appendHeading(" -- Slice Configuration -- ");

        if (Objects.isNull(dataSliceConfiguration)) {
            builder.appendValue("No slice configuration found");
        } else {
            builder.appendHeading(" -- start of slice configuration -- ");
            builder.appendValue(dataSliceConfiguration.toString());
            builder.appendHeading(" -- end of slice configuration -- ");
        }
        return builder.toString();
    }
}


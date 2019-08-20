package com.elitecore.netvertex.gateway.radius.dictionary;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeValueData;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.dictionary.AttributeModel;
import com.elitecore.coreradius.commons.util.dictionary.AttributeSupportedValueModel;
import com.elitecore.coreradius.commons.util.dictionary.DictionaryModel;
import com.elitecore.coreradius.commons.util.dictionary.VendorInformation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RadiusDictionaryLoader {
    public static final String MODULE = "RADIUS-DICTIONARY" ;
    private static final String DICTIONARY_TYPE = "RADIUS";
    private static final String XML_EXTENSION = ".xml";
    private SessionFactory sessionFactory;
    private ServerContext serverContext;

    public RadiusDictionaryLoader(SessionFactory sessionFactory,
                                  ServerContext serverContext) {
        this.sessionFactory = sessionFactory;
        this.serverContext = serverContext;
    }

    public void load(Dictionary dictionary) throws LoadConfigurationException {

        getLogger().info(MODULE, "Read Radius dictionary configuration started");
        Session session = null;

        try {
            session = sessionFactory.openSession();

            List<AttributeData> attributeDetails = HibernateReader.readAll(AttributeData.class, session);

            HashMap<Long, VendorInformation> idToVendorInformation = new HashMap<>();

            for (AttributeData attributeData : attributeDetails) {

                if(Objects.isNull(attributeData.getType())) {
                    continue;
                }

                if (Objects.equals(attributeData.getDictionaryType(), DICTIONARY_TYPE) == false) {
                    continue;
                }

                if(Objects.nonNull(attributeData.getParentAttributeData())) {
                    continue;
                }

                VendorInformation vendorInformation = idToVendorInformation.computeIfAbsent(Long.parseLong(attributeData.getVendorInformation().getVendorId()), aLong -> new VendorInformation(aLong,
                        attributeData.getVendorInformation().getName(),
                        new HashSet<>()));

                Set<AttributeModel> attributeModels = vendorInformation.getAttributeModels();
                attributeModels.add(buildAttributeDetailsForRadiusAPI(attributeData));
            }

            DictionaryModel dictionaryModel = new DictionaryModel(idToVendorInformation);

            dictionary.load(dictionaryModel, vendorId -> true);

            serialize(dictionaryModel);

            getLogger().info(MODULE,"Read Radius dictionary configuration completed");

        } catch (Exception e) {

            getLogger().info(MODULE, "Error while reading radius configuration from database. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            readFromBackup(dictionary);
        } finally {

            if(Objects.nonNull(session)) {
                session.close();
            }
        }
    }

    private void serialize(DictionaryModel dictionaryModel) {
        final File radiusDictionaryFolder = new File(serverContext.getServerHome() + File.separator + "dictionary" + File.separator + "radius");

        if(getLogger().isInfoLogLevel()) {
            getLogger().debug(MODULE,"Serialization of radius dictionary configuration started");
        }

        radiusDictionaryFolder.mkdirs();
        for (VendorInformation vendorInformation : dictionaryModel.getIdToVendorInformation().values()) {
            try {
                ConfigUtil.serialize(new File(radiusDictionaryFolder, vendorInformation.getVendorName() + ".xml"), VendorInformation.class, vendorInformation);
            } catch (JAXBException | IOException e) {
                getLogger().info(MODULE, "Error while serializing radius dictionary." + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
        if(getLogger().isInfoLogLevel()) {
            getLogger().debug(MODULE,"Serialization of radius dictionary configuration completed");
        }
    }

    private void readFromBackup(Dictionary dictionary) throws LoadConfigurationException {
        getLogger().info(MODULE, "Read radius dictionary from file started");
        final File radiusDictionaryFolder = new File(serverContext.getServerHome() + File.separator + "dictionary" + File.separator + "radius");

        Map<Long, VendorInformation> idToVendorInformation =  deserialize(radiusDictionaryFolder);
        try {
            dictionary.load(new DictionaryModel(idToVendorInformation), vendorId -> true);
            getLogger().info(MODULE,"Read Radius dictionary from file completed");
        } catch (Exception e1) {
            throw new LoadConfigurationException("Error while reading radius dictionary from file. Reason: " + e1.getMessage(), e1);
        }
    }

    private Map<Long,VendorInformation> deserialize(File file) {

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE,"Deserialization of radius dictionary configuration started");
        }

        if(file.exists() == false){
            getLogger().info(MODULE, "File does not exists." + file.getAbsolutePath());
            return Collections.emptyMap();
        }
        Map<Long, VendorInformation> idToVendorInformation = new HashMap<>();

        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            file = fileList[i];
            if (file.isDirectory() == false && file.getName().endsWith(XML_EXTENSION)) {

                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Reading dictionary file [" + file.getName() + "]");
                }

                try {
                    VendorInformation vendorInformation = ConfigUtil.deserialize(file, VendorInformation.class);
                    idToVendorInformation.put(vendorInformation.getVendorId(), vendorInformation);
                } catch (JAXBException | FileNotFoundException e) {
                    getLogger().info(MODULE, "Error while deserializing radius dictionary." + e.getMessage());
                    getLogger().trace(MODULE, e);
                }
            }
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE,"Deserialization of radius dictionary configuration completed");
        }

        return idToVendorInformation;
    }

    private AttributeModel buildAttributeDetailsForRadiusAPI(AttributeData attributeData) {

        return createRadiusAttrData(attributeData);
    }

    private AttributeModel createRadiusAttrData(AttributeData attributeData) {

        Set<AttributeSupportedValueModel> supportedValues = new HashSet<>();
        Set<AttributeValueData> attributeSupportedValues = attributeData.getAttributeSupportedValues();
        if(Collectionz.isNullOrEmpty(attributeSupportedValues) == false) {
            for (AttributeValueData supportedValue : attributeSupportedValues) {
                AttributeSupportedValueModel attributeSupportedValueModel = new AttributeSupportedValueModel(supportedValue.getSupportedValueId(), supportedValue.getName());
                supportedValues.add(attributeSupportedValueModel);
            }
        }
        AttributeModel attributeModel = new AttributeModel(Integer.parseInt(attributeData.getAttributeId().trim()), attributeData.getName(), attributeData.getType(), supportedValues);

        List<AttributeData> childAttributes = attributeData.getChildAttributes();

        Set<AttributeModel> newChildAttributes = new HashSet<>();
        for (AttributeData currentChild : childAttributes) {
            newChildAttributes.add(createRadiusAttrData(currentChild));
        }
        attributeModel.setSubAttributes(newChildAttributes);

        return attributeModel;
    }
}

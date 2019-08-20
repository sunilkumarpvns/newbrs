package com.elitecore.netvertex.gateway.diameter.dictionary;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeValueData;
import com.elitecore.corenetvertex.sm.dictonary.VendorInformation;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.AVPType;
import com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeSupportedValueModel;
import com.elitecore.diameterapi.diameter.common.util.dictionary.DiameterDictionaryModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static java.util.stream.Collectors.toMap;

public class DiameterDictionaryLoader {
    public static final String MODULE = "DIAMETER-DICTIONARY" ;
    private static final String DICTIONARY_TYPE = "DIAMETER";
    private static final String OTHER_TYPE = "OCTETS";
    private static final String XML_EXTENSION = ".xml";
    private SessionFactory sessionFactory;
    private ServerContext serverContext;

    public DiameterDictionaryLoader(SessionFactory sessionFactory, ServerContext serverContext) {
        this.sessionFactory = sessionFactory;
        this.serverContext = serverContext;
    }

    public void load(DiameterDictionary diameterDictionary) throws LoadConfigurationException {

        getLogger().info(MODULE, "Read Diameter dictionary configuration started");
        Session session = null;

        try {
            session = sessionFactory.openSession();

            List<AttributeData> attributeDetails = HibernateReader.readAll(AttributeData.class, session);

            Map<String, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation> idToVendorInformation = new HashMap<>();
            Set<String> vendorIds = new HashSet<>();

            for (AttributeData attributeData : attributeDetails) {
                if(Objects.isNull(attributeData.getType())) {
                    continue;
                }

                if (Objects.equals(attributeData.getDictionaryType(), DICTIONARY_TYPE) == false) {
                    continue;
                }

                com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation vendorInformation = idToVendorInformation.computeIfAbsent(attributeData.getVendorInformation().getId(), s -> buildVendorInformationForDiameterAPI(attributeData.getVendorInformation()));

                vendorIds.add(attributeData.getVendorInformation().getVendorId());

                com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData attribute = buildAttributeDetailsForDiameterAPI(attributeData, vendorInformation);
                vendorInformation.addAttribute(attribute);

            }

            getLogger().info(MODULE,"Read Diameter dictionary configuration completed");

            DiameterDictionaryModel diameterDictionaryModel = new DiameterDictionaryModel(idToVendorInformation, new ArrayList<>(vendorIds));
            diameterDictionary.load(diameterDictionaryModel, vendorId -> true);

            serialize(diameterDictionaryModel);
        } catch (Exception e) {

            getLogger().info(MODULE, "Error while reading diameter dictionary from database. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);

            readFromBackup(diameterDictionary);
        } finally {
            if(Objects.nonNull(session)) {
                session.close();
            }
        }

    }

    public void serialize(DiameterDictionaryModel diameterDictionaryModel) {
        final File diaDictionaryFolder = new File(serverContext.getServerHome() + File.separator + "dictionary" + File.separator + "diameter");

        if(getLogger().isInfoLogLevel()) {
            getLogger().debug(MODULE,"Serialization of diameter dictionary configuration started");
        }

        diaDictionaryFolder.mkdirs();
        for (com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation vendorInformation : diameterDictionaryModel.getIdtoVendorInformation().values()) {
            try {
                ConfigUtil.serialize(new File(diaDictionaryFolder, vendorInformation.getName() + ".xml"), com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation.class, vendorInformation);
            } catch (JAXBException | IOException e) {
                getLogger().info(MODULE, "Error while serializing diameter dictionary." + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }
        if(getLogger().isInfoLogLevel()) {
            getLogger().debug(MODULE,"Serialization of diameter dictionary configuration completed");
        }
    }

    private void readFromBackup(DiameterDictionary diameterDictionary) throws LoadConfigurationException {
        getLogger().info(MODULE, "Read diameter dictionary from file started");

        final File diaDictionaryFolder = new File(serverContext.getServerHome() + File.separator + "dictionary" + File.separator + "diameter");

        Map<String, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation> idToVendorInformation =  deserialize(diaDictionaryFolder);
        Set<String> vendorIds = new HashSet<>();
        vendorIds.addAll(idToVendorInformation.keySet());

        try {
            DiameterDictionaryModel diameterDictionaryModel = new DiameterDictionaryModel(idToVendorInformation, new ArrayList<>(vendorIds));
            diameterDictionary.load(diameterDictionaryModel, vendorId -> true);
            getLogger().info(MODULE,"Read diameter dictionary from file completed");
        } catch (Exception e) {
            throw new LoadConfigurationException("Error while reading diameter dictionary from file. Reason: " + e.getMessage(), e);
        }
    }

    public Map<String, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation> deserialize(File file) {

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE,"Deserialization of diameter dictionary configuration started");
        }

        if(file.exists() == false){
            getLogger().info(MODULE, "File does not exists." + file.getAbsolutePath());
            return Collections.emptyMap();
        }
        Map<String, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation> idToVendorInformation = new HashMap<>();

        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            file = fileList[i];
            if (file.isDirectory() == false && file.getName().endsWith(XML_EXTENSION)) {

                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Reading dictionary file [" + file.getName() + "]");
                }

                try {
                    com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation vendorInformation = ConfigUtil.deserialize(file, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation.class);
                    idToVendorInformation.put(vendorInformation.getVendorId(), vendorInformation);
                } catch (JAXBException | FileNotFoundException e) {
                    getLogger().info(MODULE, "Error while deserializing diameter dictionary." + e.getMessage());
                    getLogger().trace(MODULE, e);
                }
            }
        }

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE,"Deserialization of diameter dictionary configuration completed");
        }

        return idToVendorInformation;
    }

    private com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData buildAttributeDetailsForDiameterAPI(AttributeData attributeData, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation vendorInformation) {

        if (contains(attributeData.getType()) == false) {
            attributeData.setType(OTHER_TYPE);
        }

        Map<Integer, String> idToSupportedValue = new HashMap<>();
        Set<AttributeValueData> attributeSupportedValues = attributeData.getAttributeSupportedValues();
        if(Collectionz.isNullOrEmpty(attributeSupportedValues) == false) {
            idToSupportedValue = attributeSupportedValues.stream().map(supportedValue -> new AttributeSupportedValueModel(supportedValue.getSupportedValueId(), supportedValue.getName()))
                    .collect(toMap(AttributeSupportedValueModel::getId, AttributeSupportedValueModel::getName, (a, b) -> b));
        }

        return new com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData(
                vendorInformation.getVendorId(), attributeData.getAttributeId(), attributeData.getName(), attributeData.getMandatory(), attributeData.getProtectedValue(),
                attributeData.getEncryption(), AVPType.valueOf(attributeData.getType().toUpperCase()), attributeData.getStatus(), attributeData.getDictionaryType(),
                attributeData.getMinimum(), attributeData.getMaximum(), attributeData.getAttributeVendorId(), idToSupportedValue);

    }

    private com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation buildVendorInformationForDiameterAPI(VendorInformation vendorInformation) {
        return new com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation(
                vendorInformation.getVendorId(), vendorInformation.getName(), vendorInformation.getStatus());
    }

    private boolean contains(String type) {

        for (AVPType avpType : AVPType.values()) {
            if (avpType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }

        return false;
    }
}

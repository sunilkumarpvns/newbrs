package com.elitecore.corenetvertex.spr.ddf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.spr.SPRProvider;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.data.DDFConfiguration;
import com.elitecore.corenetvertex.spr.data.DDFEntryConfiguration;
import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RepositorySelector {
    private static final String MODULE = "REPOSITORY-SELECT";
    private List<DDFEntry> ddfEntries;
    private SubscriberRepository defaultSubscriberRepository;

    public RepositorySelector(List<DDFEntry> ddfEntries, SubscriberRepository defaultSubscriberRepository) {

        this.ddfEntries = ddfEntries;
        this.defaultSubscriberRepository = defaultSubscriberRepository;
    }

    public SubscriberRepository select(String identityValue) {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Selecting SPR from DDF");
        }

        for (int i = 0; i < ddfEntries.size(); i++) {

            if (ddfEntries.get(i).isApplicable(identityValue)) {
                if (getLogger().isDebugLogLevel()) {

                    String pattern = ddfEntries.get(i).getIdentityPattern();
                    if (Strings.isNullOrBlank(pattern)) {
                        pattern = "N/A";
                    }
                    getLogger().debug(MODULE, "DDF entry found with identity(" + pattern
                            + "), Selecting SPR(" + ddfEntries.get(i).getSubscriberRepository().getName() + ")");
                }
                return ddfEntries.get(i).getSubscriberRepository();
            }
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "No SPR satisfied for subscriber identity(" + identityValue + "). So selecting default SPR");
        }

        return defaultSubscriberRepository;
    }



    public static RepositorySelector create(DDFConfiguration ddfData, SPRProvider sprProvider){
        if (Objects.isNull(ddfData)) {
            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "DDF configuration not found, so creating SPR using default datasource");
            }
            return new RepositorySelector(Collections.emptyList(), sprProvider.getDefaultRepository());

        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Fetching default SPR");
        }

        SubscriberRepository defaultSubscriberRepository;

        if (Objects.isNull(ddfData.getDefaultSPR())) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Default SPR not found ddf(" + ddfData.getId()
                        + "), so creating SPR using default datasource");
            }
            defaultSubscriberRepository = sprProvider.getDefaultRepository();
        } else {

            defaultSubscriberRepository = sprProvider.getRepository(ddfData.getDefaultSPR());
        }
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Default SPR fetched successfully");
        }


        ArrayList<DDFEntry> ddfEntries = new ArrayList<>(ddfData.getDdfEntries().size());
        for (DDFEntryConfiguration sprConfEntry : ddfData.getDdfEntries()) {

            SubscriberRepositoryConfiguration sprConf = sprConfEntry.getSPRConfiguration();
            SubscriberRepository repository = sprProvider.getRepository(sprConf);

            if ( Objects.isNull(repository)) {
                if (getLogger().isWarnLogLevel()) {
                    getLogger().warn(MODULE, "Skipping DDF entry, SPR not found with ID(" + sprConf.getSprId() + ")");
                }
                continue;
            }

            String identityPatternString = sprConfEntry.getPattern();
            List<String> identityPatterns = CommonConstants.COMMA_SPLITTER.split(identityPatternString);
            List<char[]> identityPatternsInCharAry = null;
            if (Collectionz.isNullOrEmpty(identityPatterns) == false) {
                identityPatternsInCharAry = new ArrayList<>(identityPatterns.size());
                for(int i=0; i < identityPatterns.size(); i++) {
                    identityPatternsInCharAry.add(identityPatterns.get(i).toCharArray());
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Adding DDF entry: " + repository.getName());
            }

            ddfEntries.add(new DDFEntry(identityPatternsInCharAry,identityPatternString, repository));
        }

        ddfEntries.trimToSize();

        return new RepositorySelector(ddfEntries, defaultSubscriberRepository);

    }
}
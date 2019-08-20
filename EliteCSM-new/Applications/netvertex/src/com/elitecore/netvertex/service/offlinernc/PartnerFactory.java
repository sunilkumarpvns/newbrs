package com.elitecore.netvertex.service.offlinernc;

import com.elitecore.commons.collections.Trie;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.pd.account.AccountData;
import com.elitecore.corenetvertex.pd.account.AccountPrefixMasterRelationData;
import com.elitecore.corenetvertex.pd.lob.LobData;
import com.elitecore.corenetvertex.pd.partner.PartnerData;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.sm.location.city.CityData;
import com.elitecore.corenetvertex.sm.location.region.RegionData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.netvertex.service.offlinernc.account.Account;
import com.elitecore.netvertex.service.offlinernc.guiding.Lob;
import com.elitecore.netvertex.service.offlinernc.partner.Partner;
import com.elitecore.netvertex.service.offlinernc.partner.PartnerGroup;
import com.elitecore.netvertex.service.offlinernc.prefix.City;
import com.elitecore.netvertex.service.offlinernc.prefix.Country;
import com.elitecore.netvertex.service.offlinernc.prefix.PrefixTrieFactory;
import com.elitecore.netvertex.service.offlinernc.prefix.Region;
import com.elitecore.netvertex.service.offlinernc.prefix.conf.PrefixConfiguration;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpec;
import com.elitecore.netvertex.service.offlinernc.productspec.ProductSpecFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerFactory {

	private SessionFactory sessionFactory;
	private ProductSpecFactory productSpecFactory;
	private PrefixTrieFactory prefixTrieFactory;
	
	private Map<String, Partner> legalNameToPartner = new HashMap<>();
	
	
	public PartnerFactory(SessionFactory sessionFactory, ProductSpecFactory productSpecFactory) {
		this.sessionFactory = sessionFactory;
		this.productSpecFactory = productSpecFactory;
		this.prefixTrieFactory = new PrefixTrieFactory();
	}

	public void createPartners() throws InitializationFailedException {

		Session session = sessionFactory.openSession();
		List<PartnerData> partnerDatas = HibernateReader.readAll(PartnerData.class, session);
		
		for (PartnerData partnerData : partnerDatas) {
			Partner partner = createPartner(partnerData);
			legalNameToPartner.put(partner.getPartnerLegalName(), partner);
		}
	}

	public Partner createPartner(PartnerData partnerData) throws InitializationFailedException {
		Map<String, Account> accountNameToAccount = new HashMap<>();
		
		for (AccountData accountData : partnerData.getAccountData()) {
			Account account = createAccount(accountData);
			accountNameToAccount.put(accountData.getName(), account);
		}
		
		return new Partner(partnerData.getName(), partnerData.getPartnerLegalName(), partnerData.getRegistraionDate(),
				createCity(partnerData.getCity()), accountNameToAccount);
	}

	private City createCity(CityData city) {
		return new City(city.getCountryId(), city.getName(), createRegion(city.getRegionData()));
	}

	private Region createRegion(RegionData region) {
		return new Region(region.getName(), createCountry(region.getCountryData()));
	}

	private Country createCountry(CountryData countryData) {
		return new Country(countryData.getId(), countryData.getName(), countryData.getCode());
	}

	private Account createAccount(AccountData accountData) throws InitializationFailedException {
		ProductSpec productSpec = productSpecFactory.create(accountData.getProductSpecification());
		AccountPrefixMasterRelationData accountPrefixMasterRelation = accountData.getAccountPrefixMasterRelation();
		
		Trie<PrefixConfiguration> prefixTrie = null;
		if (accountPrefixMasterRelation != null) {
			prefixTrie = prefixTrieFactory.create(accountPrefixMasterRelation.getPrefixListMasterData());
		}
		
		return new Account(accountData.getName(), accountData.getAccountCurrency(),
				accountData.getTimeZone(), createPartnerGroup(accountData.getPartnerData()) ,createLob(accountData.getLob()),
				productSpec, prefixTrie);
	}

	private Lob createLob(LobData lob) {
		return new Lob(lob.getName(), lob.getAlias());
	}

	private PartnerGroup createPartnerGroup(PartnerData partnerData) {
		return new PartnerGroup(partnerData.getName());
	}

	public @Nullable Partner getPartner(String partnerName) {
		return legalNameToPartner.get(partnerName);
	}
}

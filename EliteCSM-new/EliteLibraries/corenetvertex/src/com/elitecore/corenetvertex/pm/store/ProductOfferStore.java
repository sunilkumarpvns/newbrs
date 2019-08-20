package com.elitecore.corenetvertex.pm.store;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class ProductOfferStore<T extends ProductOffer> implements PolicyStore<T>  {

    private static final String MODULE = "PRODUCT-OFFER-STORE";

    @Nonnull
    private DefaultProductOfferStore<T> store;

    @Nonnull
    private BaseProductOfferStore<T> baseProductOfferStore;

    @Nonnull
    private AddOnProductOfferStore<T> addonProductOfferStore;

    public ProductOfferStore(){
        this.store = fromProductOfferStore(productOffer -> true);
        this.baseProductOfferStore = new BaseProductOfferStore<>(productOffer -> PkgType.BASE == productOffer.getType());
        this.addonProductOfferStore = new AddOnProductOfferStore<>(productOffer -> PkgType.ADDON == productOffer.getType());
    }

    @Nullable
    public T byId(String id) {
        return store.byId(id);
    }

    @Nullable
    public T byName(String name) {
        return store.byName(name);
    }

    @Nonnull public List<T> all() {
        return store.all();
    }

    private T getLastKnownGoodIfFailure(T currentOffer) {

        if(currentOffer.getPolicyStatus()== PolicyStatus.SUCCESS || currentOffer.getPolicyStatus() == PolicyStatus.PARTIAL_SUCCESS){
            return currentOffer;
        }

        T previousOffer = byId(currentOffer.getId());

        if (previousOffer != null && previousOffer.getPolicyStatus()!=PolicyStatus.FAILURE) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Used last known good configuration for offer(" + currentOffer.getName() + "). Reason: Offer Status is " + currentOffer.getPolicyStatus());
            }

            previousOffer.setPolicyStatus(PolicyStatus.LAST_KNOWN_GOOD);
            previousOffer.setFailReason(currentOffer.getFailReason());
            previousOffer.setPartialFailReason(currentOffer.getPartialFailReason());

            return previousOffer;
        }
            return currentOffer;

    }

    public void create(@Nonnull List<T> createdOffers,@Nonnull List<String> deletedOffers ){

        List<T> tempList = new ArrayList<>(store.all());

        createdOffers.stream().map(t -> (T) t).forEach(productOffer -> {
            productOffer = getLastKnownGoodIfFailure(productOffer);

            if(tempList.contains(productOffer)==false){
                tempList.add(productOffer);
            } else {
                int index = tempList.indexOf(productOffer);
                tempList.set(index,productOffer);
            }
        });

        Collectionz.filter(tempList, productOffer-> deletedOffers.contains(productOffer.getId())==false);

        store.create(tempList);
        baseProductOfferStore.create(tempList);
        addonProductOfferStore.create(tempList);
    }

    @Nonnull
    public BaseProductOfferStore<T> base() {
        return baseProductOfferStore;
    }

    @Nonnull
    public AddOnProductOfferStore<T> addOn() {
        return addonProductOfferStore;
    }


    DefaultProductOfferStore fromProductOfferStore(Predicate<ProductOffer> filter) {
        return new DefaultProductOfferStore(filter);
    }
}

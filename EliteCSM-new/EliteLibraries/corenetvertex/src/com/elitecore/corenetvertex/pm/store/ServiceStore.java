package com.elitecore.corenetvertex.pm.store;

import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pm.service.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceStore {
    private List<Service> services;
    @Nonnull private Map<String, Service> byName;
    @Nonnull private Map<String, Service> byId;

    public ServiceStore(){
        this.services = new ArrayList<>(2);
        this.byName = new HashMap<>();
        this.byId = new HashMap<>();
    }

    @Nullable
    public Service byId(String id) {
        return byId.get(id);
    }

    @Nullable
    public Service byName(String name) {
        return byName.get(name);
    }

    @Nonnull public List<Service> all() {
        return services;
    }

    public void create(@Nonnull List<Service> services) {
        this.services = services;

        Map<String, Service> tempByName = new HashMap<>();
        Map<String, Service> tempById = new HashMap<>();
        for(Service service :services){
            tempByName.put(service.getName(),service);
            tempById.put(service.getId(),service);
        }
        byName = tempByName;
        byId = tempById;
    }
}

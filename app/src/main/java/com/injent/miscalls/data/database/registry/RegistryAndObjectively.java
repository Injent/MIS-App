package com.injent.miscalls.data.database.registry;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RegistryAndObjectively {
    @Embedded
    private Registry registry;

    @Relation(parentColumn = "id", entityColumn = "reg_id")
    private Objectively objectively;

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Objectively getObjectively() {
        return objectively;
    }

    public void setObjectively(Objectively objectively) {
        this.objectively = objectively;
    }
}

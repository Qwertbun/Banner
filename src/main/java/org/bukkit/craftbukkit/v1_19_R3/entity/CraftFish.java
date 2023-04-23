package org.bukkit.craftbukkit.v1_19_R3.entity;

import net.minecraft.world.entity.animal.AbstractFish;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.entity.Fish;

public class CraftFish extends CraftWaterMob implements Fish {

    public CraftFish(CraftServer server, AbstractFish entity) {
        super(server, entity);
    }

    @Override
    public AbstractFish getHandle() {
        return (AbstractFish) entity;
    }

    @Override
    public String toString() {
        return "CraftFish";
    }
}

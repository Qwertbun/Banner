package org.bukkit.craftbukkit.v1_19_R3.block;

import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import org.bukkit.World;
import org.bukkit.block.Conduit;

public class CraftConduit extends CraftBlockEntityState<ConduitBlockEntity> implements Conduit {

    public CraftConduit(World world, ConduitBlockEntity te) {
        super(world, te);
    }
}

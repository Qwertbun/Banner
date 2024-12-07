package com.mohistmc.banner.eventhandler.dispatcher;

import com.mohistmc.banner.bukkit.BukkitSnapshotCaptures;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockEventDispatcher {

    public static boolean onBlockBreak(ServerPlayerGameMode controller, ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, boolean isSwordNoBreak) {
        // Tell client the block is gone immediately then process events
        // Don't tell the client if its a creative sword break because its not broken!
        if (level.getBlockEntity(pos) == null && !isSwordNoBreak) {
            var packet = new ClientboundBlockUpdatePacket(pos, Blocks.AIR.defaultBlockState());
            player.connection.send(packet);
        }

        var bblock = CraftBlock.at(level, pos);
        var event = new BlockBreakEvent(bblock, player.getBukkitEntity());
        BukkitSnapshotCaptures.captureBlockBreakPlayer(event);

        // Sword + Creative mode pre-cancel
        event.setCancelled(isSwordNoBreak);

        // Calculate default block experience
        var nmsBlock = state.getBlock();

        var itemstack = player.getItemBySlot(EquipmentSlot.MAINHAND);

        if (!event.isCancelled() && !controller.isCreative() && player.hasCorrectToolForDrops(nmsBlock.defaultBlockState())) {
            event.setExpToDrop(nmsBlock.getExpDrop(state, level, pos, itemstack, true));
        }

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            if (isSwordNoBreak) {
                return false;
            }
            // Let the client know the block still exists
            player.connection.send(new ClientboundBlockUpdatePacket(level, pos));

            // Brute force all possible updates
            for (var dir : net.minecraft.core.Direction.values()) {
                player.connection.send(new ClientboundBlockUpdatePacket(level, pos.relative(dir)));
            }

            // Update any tile entity data for this block
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                var packet = blockEntity.getUpdatePacket();
                if (packet != null) {
                    player.connection.send(packet);
                }
            }
            return false;
        }

        // CraftBukkit - update state from plugins
        if (level.getBlockState(pos).isAir()) {
            return false;
        }
        return true;
    }
}

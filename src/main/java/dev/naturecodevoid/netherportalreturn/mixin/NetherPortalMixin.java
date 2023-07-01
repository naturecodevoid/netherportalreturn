package dev.naturecodevoid.netherportalreturn.mixin;

import dev.naturecodevoid.netherportalreturn.Data;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.naturecodevoid.netherportalreturn.NetherPortalReturnMod.LOGGER;

@Mixin(NetherPortal.class)
public class NetherPortalMixin {
    @Inject(at = @At("HEAD"), method = "getNetherTeleportTarget", cancellable = true)
    private static void sendPlayersToSavedPosition(ServerWorld destination, BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d offset, Entity entity, Vec3d velocity, float yaw, float pitch, CallbackInfoReturnable<TeleportTarget> cir) {
        if (destination.getRegistryKey() != World.OVERWORLD) return;
        if (!(entity instanceof PlayerEntity player)) return;
        Vec3d pos = Data.removePlayerPosition(player.getUuid());
        if (pos == null) {
            LOGGER.error(player.getEntityName() + " (" + player.getUuidAsString() + ") has no saved position");
            return;
        }

        if (Data.enabled()) {
            LOGGER.info(player.getEntityName() + " (" + player.getUuidAsString() + ") will be sent to " + pos);
            cir.setReturnValue(new TeleportTarget(pos, velocity, yaw, pitch));
        } else {
            LOGGER.info(player.getEntityName() + " (" + player.getUuidAsString() + ") would be sent to " + pos + ", but the mod is not enabled");
        }
    }

    @Inject(at = @At("TAIL"), method = "getNetherTeleportTarget")
    private static void savePlayerPosition(ServerWorld destination, BlockLocating.Rectangle portalRect, Direction.Axis portalAxis, Vec3d offset, Entity entity, Vec3d velocity, float yaw, float pitch, CallbackInfoReturnable<TeleportTarget> cir) {
        if (destination.getRegistryKey() != World.NETHER) return;
        if (!(entity instanceof PlayerEntity player)) return;
        Vec3d pos = player.getPos();
        Data.putPlayerPosition(player.getUuid(), pos);
        LOGGER.info(player.getEntityName() + " (" + player.getUuidAsString() + ") entered the nether at " + pos);
    }
}

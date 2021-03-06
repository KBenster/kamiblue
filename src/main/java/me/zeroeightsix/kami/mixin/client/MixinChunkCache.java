package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.render.XRay;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created by 20kdc on 15/02/2020.
 */
@Mixin(ChunkCache.class)
public class MixinChunkCache {

    @Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
    public void getState(BlockPos pos, CallbackInfoReturnable<IBlockState> info) {
        if (ModuleManager.isModuleEnabled("XRay"))
            info.setReturnValue(XRay.transform(info.getReturnValue()));
    }

}

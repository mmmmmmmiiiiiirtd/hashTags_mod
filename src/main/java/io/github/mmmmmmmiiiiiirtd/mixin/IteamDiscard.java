package io.github.mmmmmmmiiiiiirtd.mixin;

import io.github.mmmmmmmiiiiiirtd.Hasher;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.item.component.CustomData;

@Mixin(IteamDiscard.class)
public class IteamDiscard {
    @Inject(method = "discard", at = @At("HEAD"))
    private void onDiscard(CallbackInfo ci) {
        ItemEntity entity = (ItemEntity) (Object) this;
        CustomData customData = entity.getItem().get(DataComponents.CUSTOM_DATA);
        long hash = 0L;
        if (customData != null) {
            hash = Hasher.tagToHash(customData.getUnsafe());
        }
        if (hash != 0L) {
            Hasher.releaseTag(hash);
        }
    }

    @Inject(method = "setCount", at = @At("RETURN"))
    private void onSetCount(int count, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        // Если предметов в стаке стало 0, значит этот ItemStack уничтожен
        if (count <= 0) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            long hash = 0L;
            if (customData != null) {
                hash = Hasher.tagToHash(customData.getUnsafe());
            }
            if (hash != 0L) {
                Hasher.releaseTag(hash);
            }
        }
    }
}

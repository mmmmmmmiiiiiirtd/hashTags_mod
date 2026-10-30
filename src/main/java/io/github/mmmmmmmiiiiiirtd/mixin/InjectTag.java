package io.github.mmmmmmmiiiiiirtd.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.github.mmmmmmmiiiiiirtd.Hasher;

import java.util.function.Consumer;

public class InjectTag {
    private static final String PROXIED_HASH_KEY = "HashToTag";
    private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);

    @Inject(method = "update(Ljava/util/function/Consumer;)Lnet/minecraft/world/item/component/CustomData;", at = @At("HEAD"), cancellable = true)
    private void onUpdateAndHash(Consumer<CompoundTag> updater, CallbackInfoReturnable<CustomData> cir){
        if (IS_PROCESSING.get()) return;

        try {
            IS_PROCESSING.set(true);

            CustomData currentInstance = (CustomData) (Object) this;
            CompoundTag currentTag = currentInstance.copyTag();

            if (currentTag.size() == 1 && currentTag.contains(PROXIED_HASH_KEY)) {
                CompoundTag restored = Hasher.hashToTag(currentTag.getLong(PROXIED_HASH_KEY));
                if (restored != null) {
                    currentTag = restored;
                }
            }

            updater.accept(currentTag);

            long hash = Hasher.tagToHash(currentTag);

            CompoundTag newTag = new CompoundTag();
            newTag.putLong(PROXIED_HASH_KEY, hash);

            cir.setReturnValue(CustomData.of(newTag));
        } finally {
            IS_PROCESSING.set(false);
        }
    }

    @Inject(method = "copyTag()Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"), cancellable = true)
    private void onCopyTagAndRestoreOriginal(CallbackInfoReturnable<CompoundTag> cir) {
        if (IS_PROCESSING.get()) return;

        CompoundTag storedTag = cir.getReturnValue();

        if (storedTag != null && storedTag.size() == 1 && storedTag.contains(PROXIED_HASH_KEY)){
            try {
                IS_PROCESSING.set(true);

                long hash = storedTag.getLong(PROXIED_HASH_KEY);

                CompoundTag originalTag = Hasher.hashToTag(hash);

                if (originalTag != null) {
                    cir.setReturnValue(originalTag);
                }
            } finally {
                IS_PROCESSING.set(false);
            }
        }
    }
}

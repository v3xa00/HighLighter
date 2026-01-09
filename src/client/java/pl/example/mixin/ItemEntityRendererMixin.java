package pl.example.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.example.ScaleNbt;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {

    @Inject(
        method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD")
    )
    private void rozszerzitem$pushAndScale(ItemEntity entity, float yaw, float tickDelta,
                                          MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                          int light, CallbackInfo ci) {
        float scale = ScaleNbt.getScale(entity.getStack());
        if (scale != 1.0f) {
            matrices.push();
            matrices.scale(scale, scale, scale);
        }
    }

    @Inject(
        method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("TAIL")
    )
    private void rozszerzitem$pop(ItemEntity entity, float yaw, float tickDelta,
                                 MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                 int light, CallbackInfo ci) {
        float scale = ScaleNbt.getScale(entity.getStack());
        if (scale != 1.0f) {
            matrices.pop();
        }
    }
}

package net.ase.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nyfaria.awcapi.ClientClimberHelper;
import com.nyfaria.awcapi.entity.IAdvancedClimber;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> implements GeoRenderer<T>
{
    @Shadow
    public abstract T getAnimatable();

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"), require = 0, remap = false)
    private void climberPreRender(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci)
    {
        if (entity instanceof IAdvancedClimber)
        {
            ClientClimberHelper.preRenderClimber((IAdvancedClimber) entity, partialTick, poseStack);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "TAIL"), require = 0, remap = false)
    private void climberPostRender(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci)
    {
        if (entity instanceof IAdvancedClimber)
        {
            ClientClimberHelper.postRenderClimber((IAdvancedClimber) entity, partialTick, poseStack, bufferSource);
        }
    }
}

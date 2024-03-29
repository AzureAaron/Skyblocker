package de.hysky.skyblocker.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import de.hysky.skyblocker.debug.Debug;
import de.hysky.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> {

	protected LivingEntityRendererMixin(Context ctx) {
		super(ctx);
	}

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void skyblocker$skipRenderingArmorStandModelIfFullyInvisible(T entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
		if (Utils.isOnSkyblock() && entity instanceof ArmorStandEntity as && as.isInvisible() && !as.isGlowing() && as.isInvisibleTo(MinecraftClient.getInstance().player)) {
			if (!(as.hasStackEquipped(EquipmentSlot.HEAD) || as.hasStackEquipped(EquipmentSlot.CHEST) || as.hasStackEquipped(EquipmentSlot.LEGS) || as.hasStackEquipped(EquipmentSlot.FEET) || as.hasStackEquipped(EquipmentSlot.MAINHAND) || as.hasStackEquipped(EquipmentSlot.OFFHAND))) {
				super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
				ci.cancel();
			}
		}
	}

	@ModifyExpressionValue(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;isVisible(Lnet/minecraft/entity/LivingEntity;)Z"))
	private boolean skyblocker$armorStandVisible(boolean visible, T entity) {
		return entity instanceof ArmorStandEntity && Utils.isOnHypixel() && Debug.seeInvisibleArmorStands() || visible;
	}
}

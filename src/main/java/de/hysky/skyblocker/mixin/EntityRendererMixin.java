package de.hysky.skyblocker.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import de.hysky.skyblocker.skyblock.entity.MobGlow;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;

@Mixin(value = EntityRenderer.class, priority = 800)
public class EntityRendererMixin {

	@ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
	private void skyblocker$enhancedNameTagVisibility(Args args) {
		String name = MobGlow.shouldMobNameTagGlow(args.get(0));

		if (name != null) {
			args.set(0, Text.literal(name).withColor(MobGlow.getMobNameTagGlowColor(name)));
			args.set(3, 0xFFFFFFFF);
		}
	}
}

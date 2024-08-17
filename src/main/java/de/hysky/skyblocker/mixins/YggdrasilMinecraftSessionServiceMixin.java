package de.hysky.skyblocker.mixins;

import java.util.Base64;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

import de.hysky.skyblocker.utils.Utils;

@Mixin(value = YggdrasilMinecraftSessionService.class, remap = false)
public class YggdrasilMinecraftSessionServiceMixin {
	@Unique
    private static final byte BASE64_BYTE_ALIGNMENT = 4;

	@WrapOperation(method = "unpackTextures", remap = false, at = @At(value = "INVOKE", target = "Ljava/util/Base64$Decoder;decode(Ljava/lang/String;)[B", remap = false))
	private byte[] skyblocker$fixIncorrectlyAlignedBase64Textures(Base64.Decoder decoder, String texture, Operation<byte[]> operation) {
		try {
			return operation.call(decoder, texture);
		} catch (Exception e) {
			try {
				String fixed = texture;
				int byteAlignment = fixed.length() % BASE64_BYTE_ALIGNMENT;

				//If the base64 is misaligned then strip it of padding, recalculate the padding needed and then apply it
				if (byteAlignment != 0) {
					String noPadding = fixed.replace("=", "");
					int paddingNeeded = (BASE64_BYTE_ALIGNMENT - (noPadding.length() % BASE64_BYTE_ALIGNMENT)) % BASE64_BYTE_ALIGNMENT;

					fixed = noPadding + "=".repeat(paddingNeeded);
				}

				return operation.call(decoder, fixed);
			} catch (Exception ignored) {}

			throw e;
		}
	}

	@WrapWithCondition(method = "unpackTextures", remap = false, at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V", ordinal = 0, remap = false))
	private boolean skyblocker$dontLogIncorrectEndingByteExceptions(Logger logger, String message, Throwable throwable) {
		return !Utils.isOnHypixel() && throwable instanceof IllegalArgumentException;
	}
}

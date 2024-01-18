package de.hysky.skyblocker.skyblock.entity;

import java.util.List;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.dungeon.LividColor;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.render.culling.OcclusionCulling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

//TODO optimize this to not calculate the string of the name twice per frame
public class MobGlow {
	private static final int ORANGE = 0xf57738;
	private static final int YELLOW = 0xfee15c;
	private static final int PURPLE = 0x5b2cb2;
	private static final int BLUE = 0x57c2f7;
	
	public static boolean shouldMobGlow(Entity entity) {
		Box box = entity.getBoundingBox();

		if (!entity.isInvisible() && OcclusionCulling.getReducedCuller().isVisible(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
			String name = entity.getName().getString();

			// Dungeons
			if (Utils.isInDungeons()) {

				// Minibosses
				if (entity instanceof PlayerEntity) {
					switch (name) {
						case "Lost Adventurer", "Shadow Assassin", "Diamond Guy": return SkyblockerConfigManager.get().locations.dungeons.starredMobGlow;
						case "Arcade Livid", "Crossed Livid", "Doctor Livid", "Frog Livid", "Hockey Livid",
						"Purple Livid", "Scream Livid", "Smile Livid", "Vendetta Livid": return LividColor.shouldGlow(name);
					}
				}

				// Regular Mobs
				if (!(entity instanceof ArmorStandEntity)) {
					List<ArmorStandEntity> armorStands = getArmorStands(entity.getWorld(), box);

					if (!armorStands.isEmpty() && armorStands.get(0).getName().getString().contains("✯")) return SkyblockerConfigManager.get().locations.dungeons.starredMobGlow;
				}

				// Bats
				return SkyblockerConfigManager.get().locations.dungeons.starredMobGlow && entity instanceof BatEntity;
			}

			// Rift
			if (Utils.isInTheRift()) {
				if (entity instanceof PlayerEntity) {
					switch (name) {
						// They have a space in their name for some reason...
						case "Blobbercyst ": return SkyblockerConfigManager.get().locations.rift.blobbercystGlow;
					}
				}
			}
		}

		return false;
	}

	private static List<ArmorStandEntity> getArmorStands(World world, Box box) {
        return world.getEntitiesByClass(ArmorStandEntity.class, box.expand(0, 2, 0), EntityPredicates.NOT_MOUNTED);
	}

	public static int getGlowColor(Entity entity) {
		String name = entity.getName().getString();

		if (entity instanceof PlayerEntity) {
			return switch (name) {
				case "Lost Adventurer" -> YELLOW;
				case "Shadow Assassin" -> PURPLE;
				case "Diamond Guy" -> BLUE;
				case "Arcade Livid", "Crossed Livid", "Doctor Livid", "Frog Livid", "Hockey Livid",
				"Purple Livid", "Scream Livid", "Smile Livid", "Vendetta Livid" -> LividColor.getGlowColor(name);
				case "Blobbercyst " -> Formatting.GREEN.getColorValue();
				default -> ORANGE;
			};
		}

		return ORANGE;
	}

	/**
	 * This returns a string to avoid having to calculate the text's string value multiple times
	 */
	public static String shouldMobNameTagGlow(Text text) {
		if (Utils.isInDungeons()) {
			String name = text.getString();

			if (name.contains("✯") && SkyblockerConfigManager.get().locations.dungeons.starredMobGlow) return name;
		}

		return null;
	}

	public static int getMobNameTagGlowColor(String name) {
		if (name.contains("Lost Adventurer") || name.contains("Frozen Adventurer")) return YELLOW;
		if (name.contains("Shadow Assassin")) return PURPLE;
		if (name.contains("Angry Archaeologist")) return BLUE;

		return ORANGE;
	}
}

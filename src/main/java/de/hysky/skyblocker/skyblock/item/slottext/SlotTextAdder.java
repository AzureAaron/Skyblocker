package de.hysky.skyblocker.skyblock.item.slottext;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.utils.container.AbstractSlotTextAdder;
import de.hysky.skyblocker.utils.container.RegexContainerMatcher;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Simple implementation of a slot text adder.
 * Extend this class and add it to {@link SlotTextManager#adders} to add text to any arbitrary slot.
 */
public abstract class SlotTextAdder extends RegexContainerMatcher implements AbstractSlotTextAdder {
	/**
	 * Utility constructor that will compile the given string into a pattern.
	 *
	 * @see #SlotTextAdder(Pattern)
	 */
	protected SlotTextAdder(@NotNull @Language("RegExp") String titlePattern) {
		super(titlePattern);
	}

	/**
	 * Creates a SlotTextAdder that will be applied to screens with titles that match the given pattern.
	 *
	 * @param titlePattern The pattern to match the screen title against.
	 */
	protected SlotTextAdder(@NotNull Pattern titlePattern) {
		super(titlePattern);
	}

	/**
	 * Creates a SlotTextAdder that will be applied to all screens.
	 */
	protected SlotTextAdder() {
		super();
	}

	@Override
	public boolean isEnabled() {
		return SkyblockerConfigManager.get().general.itemInfoDisplay.slotText;
	}
}

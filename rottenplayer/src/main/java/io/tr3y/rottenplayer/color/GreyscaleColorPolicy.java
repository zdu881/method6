package io.tr3y.rottenplayer.color;

import org.bukkit.DyeColor;

public class GreyscaleColorPolicy implements ColorPolicy {

	@Override
	public DyeColor getColor(char c) {
		switch (c) {
		case '0':
			return DyeColor.BLACK;
		case '1':
			return DyeColor.GRAY;
		case '2':
			return DyeColor.LIGHT_GRAY;
		case '3':
			return DyeColor.WHITE;

		// Anything else we don't know what to do with, just make black.
		default:
			return DyeColor.BLACK;
		}
	}

}

package io.tr3y.rottenplayer.color;

import org.bukkit.DyeColor;

public class DemoColorPolicy implements ColorPolicy {

	// From here, I guess: https://minecraft.gamepedia.com/Talk:Dye/Archive_1#Damage_Values
	@Override
	public DyeColor getColor(char c) {
		switch (c) {
		case '0':
			return DyeColor.BLACK;
		case '1':
			return DyeColor.RED;
		case '2':
			return DyeColor.GREEN;
		case '3':
			return DyeColor.BROWN;
		case '4':
			return DyeColor.BLUE;
		case '5':
			return DyeColor.PURPLE;
		case '6':
			return DyeColor.CYAN;
		case '7':
			return DyeColor.LIGHT_GRAY;
		case '8':
			return DyeColor.GRAY;
		case '9':
			return DyeColor.PINK;
		case 'a':
			return DyeColor.LIME;
		case 'b':
			return DyeColor.YELLOW;
		case 'c':
			return DyeColor.LIGHT_BLUE;
		case 'd':
			return DyeColor.MAGENTA;
		case 'e':
			return DyeColor.ORANGE;
		case 'f':
			return DyeColor.WHITE;
		default:
			return DyeColor.WHITE;
		}
	}

}

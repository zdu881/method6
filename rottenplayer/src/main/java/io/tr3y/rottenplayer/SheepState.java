package io.tr3y.rottenplayer;

import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;

public class SheepState {
	public int x, z;
	public DyeColor color;
	public DyeColor originalColor;

	public SheepState(int x, int z) {
		this.x = x;
		this.z = z;
		this.color = DyeColor.WHITE; // sensible defaults
		this.originalColor = DyeColor.WHITE;
	}

	public SheepState(int x, int z, DyeColor c) {
		this.x = x;
		this.z = z;
		this.color = c;
		this.originalColor = c;
	}

	public static SheepState fromSheep(Sheep sheep) {
		int x = sheep.getLocation().getBlockX();
		int z = sheep.getLocation().getBlockZ();
		return new SheepState(x, z, sheep.getColor());
	}
}

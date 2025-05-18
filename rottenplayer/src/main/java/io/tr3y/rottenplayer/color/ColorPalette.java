package io.tr3y.rottenplayer.color;

import org.bukkit.DyeColor;

public enum ColorPalette implements ColorPolicy {
	
	BW(new BwColorPolicy()),
	GREYSCALE(new GreyscaleColorPolicy()),
	DEMO(new DemoColorPolicy());

	private ColorPolicy delegate;
	
	ColorPalette(ColorPolicy d) {
		this.delegate = d;
	}
	
	@Override
	public DyeColor getColor(char c) {
		return this.delegate.getColor(c);
	}

}

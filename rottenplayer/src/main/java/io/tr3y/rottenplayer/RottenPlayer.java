package io.tr3y.rottenplayer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RottenPlayer extends JavaPlugin {

	@Override
	public void onEnable() {

		Bukkit.getLogger().info("RottenPlayer loaded!");

		this.getCommand("rottenplayer").setExecutor(new CmdPlay(this));

	}

}

package io.tr3y.rottenplayer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import io.tr3y.rottenplayer.color.ColorPolicy;
import io.tr3y.rottenplayer.color.GreyscaleColorPolicy;

public class CmdPlay implements CommandExecutor {

	private RottenPlayer plugin;

	public CmdPlay(RottenPlayer plugin) {
		this.plugin = plugin;
	}

	private File findVideo(String query) {

		Path datadir = plugin.getDataFolder().toPath();

		// Java fukin suuucks.
		// In rust this is just--
		Path sub = Path.of(query);
		Path subExt = Path.of(query + ".txt");
		File f = datadir.resolve(sub).toFile();
		File fExt = datadir.resolve(subExt).toFile();

		if (f.exists()) {
			return f;
		} else if (fExt.exists()) {
			return fExt;
		} else {
			return null;
		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to run this command.");
			return true;
		}

		Player p = (Player) sender;
		World world = p.getWorld();

		if (!sender.hasPermission("rottenplayer.play") && !sender.isOp()) {
			sender.sendMessage("You do not have permission to run this command.");
			return true;
		}

		if (args.length < 4 || args.length > 6) {
			sender.sendMessage("usage: <x> <z> <dir> <filename> [<scale>] [<delay>]");
			return true;
		}

		int x = 0, z = 0;
		byte dir = 0;
		String filename = null;
		int scale = 1;
		int delay = 1;

		try {

			if (args.length >= 4) {
				x = Integer.parseInt(args[0]);
				z = Integer.parseInt(args[1]);
				dir = Byte.parseByte(args[2]);
				filename = args[3];
			}

			if (args.length >= 5) {
				scale = Integer.parseInt(args[4]);
			}

			if (args.length >= 6) {
				delay = Integer.parseInt(args[5]);
			}

		} catch (NumberFormatException e) {
			sender.sendMessage("usage: <x> <z> <dir> <filename> [<scale>] [<delay>]");
			return true;
		}

		File videoFile = this.findVideo(filename);
		if (videoFile == null) {
			sender.sendMessage("Could not find path \"" + filename + "\"");
			return true;
		}

		BukkitScheduler sched = Bukkit.getScheduler();
		Logger log = plugin.getLogger();
		log.info("Loading video: " + videoFile.getPath());

		// Reassign these because Java sucks.
		int wx = x;
		int wz = z;
		byte wdir = dir;
		int vscale = scale;
		int vperiod = delay; // "frame delay" idk

		sched.runTaskAsynchronously(plugin, task -> {

			try {

				FileInputStream stream = new FileInputStream(videoFile);
				RottenVideo vid = RottenVideo.fromInputStream(stream);

				RenderGeom geom = new RenderGeom(vid.width, vid.height, wx, wz, wdir, vscale);
				PlayerWorker worker = new PlayerWorker(world, geom, vid);

				BukkitTask workerTask = sched.runTaskTimer(plugin, worker, 1, vperiod);
				worker.setTask(workerTask);

			} catch (Exception e) {
				sender.sendMessage("Error: " + e.getMessage());
			}

		});

		return true;

	}

}

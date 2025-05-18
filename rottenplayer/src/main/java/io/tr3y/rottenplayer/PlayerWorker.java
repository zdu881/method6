package io.tr3y.rottenplayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import io.tr3y.rottenplayer.color.ColorPolicy;

public class PlayerWorker implements Runnable {

	private World world;
	private RenderGeom geom;
	private RottenVideo video;

	private BukkitTask task;

	private int frame;
	private HashMap<Sheep, SheepState> sheeps;
	private boolean done;

	public PlayerWorker(World w, RenderGeom g, RottenVideo v) {
		this.world = w;
		this.geom = g;
		this.video = v;
		this.frame = 0;
	}

	public void setTask(BukkitTask task) {
		this.task = task;
	}

	private void init() {

		// Find all the sheep.
		BoundingBox aabb = geom.getAABB();
		Collection<Entity> ents = world.getNearbyEntities(aabb, e -> e instanceof Sheep);

		// Put them into the map.
		sheeps = new HashMap<>();
		Iterator<Entity> iter = ents.iterator();
		while (iter.hasNext()) {
			Entity e = iter.next();

			if (!(e instanceof Sheep)) {
				Bukkit.getLogger().warning(
						"Entity " + e.getEntityId() + "(" + e.getLocation() + ") is not a sheep but we were given it");
				continue;
			}

			Sheep sheep = (Sheep) e;
			sheeps.put(sheep, SheepState.fromSheep(sheep));
		}

	}

	private void update() {

		byte[] fb = video.getFrame(frame);
		ColorPolicy policy = video.getColorPolicy();

		for (Map.Entry<Sheep, SheepState> s : sheeps.entrySet()) {

			Sheep sheep = s.getKey();
			SheepState state = s.getValue();

			// Compute the pixel color for where the sheep is.
			int sx = sheep.getLocation().getBlockX();
			int sz = sheep.getLocation().getBlockZ();
			int pixelOff = geom.computeFramePixelIndex(sx, sz);

			if (pixelOff < 0 || pixelOff >= fb.length) {
				// Should we warn on this?
				Bukkit.getLogger().warning("Sheep out of bounds at " + sx + " " + sz + " with fb index " + pixelOff);
				sheep.setColor(DyeColor.RED);
				continue;
			}

			DyeColor pixel = policy.getColor((char) fb[pixelOff]);

			// Only update it if we have to. Just to make sure we don't send
			// unnecessary updates to clients.
			if (!pixel.equals(state.color)) {
				sheep.setColor(pixel);
				state.color = pixel;
			}

			// I don't really know what these were for anymore.
			state.x = sx;
			state.z = sz;

		}

	}

	private void cleanup() {

		for (Map.Entry<Sheep, SheepState> s : sheeps.entrySet()) {
			s.getKey().setColor(s.getValue().originalColor);
		}

	}

	@Override
	public void run() {

		if (done && frame != -1) {
			Bukkit.getLogger().warning(
					"RottenPlayer video ticked after completion.  I won't print this again but we might still get ticked.");
			this.frame = -1;
			return;
		}

		if (frame == 0) {
			init();
		}

		if (frame < video.getFrameCount()) {
			update();
			frame++;
		} else {
			Bukkit.getLogger().info("RottenPlayer finished playing video, cleaning up sheep...");
			cleanup();
			done = true;

			if (task != null) {
				task.cancel(); // this should make us stop ticking
			} else {
				Bukkit.getLogger().warning(
						"RottenPlayer video finished but worker did not have task set, so we cannot cancel the task.  May cause warnings.");
			}
		}

	}

}

package io.tr3y.rottenplayer;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;

/**
 * Utility class for representing a render screen geometry. Converts a global
 * xyz position to a frame index.
 * 
 * Minecraft uses east as +x and south as +z. Up is +y, but we don't actually
 * care about that, we only care about xz coordinates.
 * 
 * The frames are stored row by row with x increasing most quickly. Origin is
 * the upper left, with x increasing to the right and y increasing down. The
 * orientation determines which side is "up" in world space, with 0 being east
 * and 3 being south.
 * 
 * @param vw
 *            Video width
 * @param vh
 *            Video height
 * @param ox
 *            World-space origin X
 * @param oy
 *            World-space origin Y
 * @param oz
 *            World-space origin Z
 * @param dir
 *            Orientation
 * @param scale
 *            Coordinate scale factor.
 */
public class RenderGeom {

	public final int videoW, videoH;

	public final int origX, origZ;
	public final byte orientation;

	public final int scale;

	public RenderGeom(int vw, int vh, int ox, int oz, byte dir, int scale) {
		this.videoW = vw;
		this.videoH = vh;
		this.origX = ox;
		this.origZ = oz;
		this.orientation = dir;
		this.scale = scale;
	}

	private int getPixelCount() {
		return videoW * videoH;
	}

	/**
	 * Computes frame pixel index for a given coordinate. Returns -1 if not part of
	 * canvas.
	 */
	public int computeFramePixelIndex(int x, int z) {

		// Compute offsets relative to world space.
		int rx = x - origX;
		int rz = z - origZ;

		// Declare frame coordinates.
		int fx = -1;
		int fy = -1;

		// Switch to adjust coordinate system
		switch (orientation) {

		case 0:
			fx = rz;
			fy = -rx;
			break;

		case 1:
			fx = rx;
			fy = rz;
			break;

		case 2:
			fx = -rz;
			fy = rx;
			break;

		case 3:
			fx = -rx;
			fy = -rz;
			break;

		default:
			return -1;
		}

		// Apply scale factor. This seems right, right?
		fx /= scale;
		fy /= scale;

		// Now compute the actual offset.
		int off = fy * videoW + fx;
		if (off < this.getPixelCount()) {
			return off;
		} else {
			return -1;
		}

	}

	public BoundingBox getAABB() {
		// n = min, m = max
		double xn, zn, xm, zm;

		// TODO Make sure we're not extending the max corner of out bounding
		// box by 1 block too much. That could be bad.
		switch (orientation) {

		case 0:
			xn = origX - videoH * scale;
			zn = origZ;
			xm = origX;
			zm = origZ + videoW * scale;
			break;

		case 1:
			xn = origX;
			zn = origZ;
			xm = origX + videoW * scale;
			zm = origZ + videoH * scale;
			break;

		case 2:
			xn = origX;
			zn = origZ - videoW * scale;
			xm = origX + videoH * scale;
			zm = origZ;
			break;

		case 3:
			xn = origX - videoW * scale;
			zn = origZ - videoH * scale;
			xm = origX;
			zm = origZ;
			break;

		default:
			return null;
		}

		return new BoundingBox(xn, 0, zn, xm, 255, zm);
	}

}

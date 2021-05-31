/*
 * Created on Apr 23, 2003
 *
 * @author Ragu Vijaykumar
 */
package engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.BoundingBox;
import javax.vecmath.Point3d;

/**
 *	Partition - creates a partition in 3-dimensional space.
 *	Partitions can only be initialized once; however, many instances of partitions
 *  can be made. All partitions are immutable and sets of partitions correspond
 *  to the flyweight design described in any design text.
 *
 *  @author Ragu Vijaykumar
 */
public class Partition {

	// Static Fields
	private static Partition instance = null;
	private static BoundingBox spatialBox = null;
	private static Point3d origin = null;
	private static Point3d upper = null;
	private static Dimension3d dimension = null;
	private static Map partitionMap = null;
	private static int xVal;
	private static int yVal;
	private static int zVal;

	// Non-static Fields
	private final int x;
	private final int y;
	private final int z;

	/**
	 * Creates a dummy partition block
	 * Sets all values to Integer.MIN_VALUE;
	 */
	private Partition() {
		this.x = Integer.MIN_VALUE;
		this.y = Integer.MIN_VALUE;
		this.z = Integer.MIN_VALUE;
	}

	/**
	 * Creates a new Partition in 3 dimensional space
	 * @param x - x value
	 * @param y - y value
	 * @param z - z value
	 */
	private Partition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Initializes partition to create partitions
	 * @param origin - origin of the playing space
	 * @param upper - strictly upper corner of the playing space
	 * @return - instance of the partition to call methods upon. 
	 * Note: this instance does not represent a partition block in the space
	 */
	public static Partition initialize(
		Point3d origin,
		Point3d upper,
		int partitionX,
		int partitionY,
		int partitionZ) {

		if (Partition.instance == null) {

			Partition.origin = new Point3d(origin);
			Partition.upper = new Point3d(upper);

			Partition.spatialBox =
				new BoundingBox(new Point3d(origin), new Point3d(upper));

			Partition.dimension =
				new Dimension3d(partitionX, partitionY, partitionZ);
			Partition.getDimensions(origin, upper);

			Partition.partitionMap =
				new HashMap(
					(int) (partitionX * partitionY * partitionZ * 1.2),
					0.8f);

			Partition.instance = new Partition();
		}

		return Partition.instance;
	}

	/**
	 * Initializes partition to create partitions
	 * @param spatialBox - bounding box of the playing space
	 * @return - instance of the partition to call methods upon. 
	 * Note: this instance does not represent a partition block in the space
	 */
	public static Partition initialize(
		BoundingBox spatialBox,
		int partitionX,
		int partitionY,
		int partitionZ) {

		if (Partition.instance == null) {
			Partition.spatialBox = spatialBox;

			Point3d origin = new Point3d();
			Point3d upper = new Point3d();

			spatialBox.getLower(origin);
			spatialBox.getUpper(upper);

			Partition.origin = origin;
			Partition.upper = upper;

			Partition.dimension =
				new Dimension3d(partitionX, partitionY, partitionZ);
			Partition.getDimensions(origin, upper);

			Partition.partitionMap =
				new HashMap(
					(int) (partitionX * partitionY * partitionZ * 1.2),
					0.8f);

			Partition.instance = new Partition();
		}

		return Partition.instance;
	}

	private static void getDimensions(Point3d origin, Point3d upper) {

		double xDim = (upper.x - origin.x) / Partition.dimension.x;
		double yDim = (upper.y - origin.y) / Partition.dimension.y;
		double zDim = (upper.z - origin.z) / Partition.dimension.z;

		Partition.dimension = new Dimension3d(xDim, yDim, zDim);
	}

	/**
	 * Returns the canonical partition represented by the following absolute 
	 * 3-dimensional coordinates
	 * @param point - point in the space
	 */
	public Partition getBlock(Point3d point) {

		if (point == null)
			throw new NullPointerException("Parameter point is null!" + point);

		double xrel = point.x - Partition.origin.x;
		double yrel = point.y - Partition.origin.y;
		double zrel = point.z - Partition.origin.z;

		int xBlock = (int) (xrel / Partition.dimension.x);
		int yBlock = (int) (yrel / Partition.dimension.y);
		int zBlock = (int) (zrel / Partition.dimension.z);

		Partition.xVal = xBlock;
		Partition.yVal = yBlock;
		Partition.zVal = zBlock;

		Object value = Partition.partitionMap.get(Partition.instance);

		if (value == null) {
			// This is the first encounter of the partition

			value = new Partition(xBlock, yBlock, zBlock);
			Partition.partitionMap.put(value, value);
		}

		return (Partition) value;
	}
	
	/**
	 * Returns a set of blocks that are contained within the container
	 * @requires container != null
	 * @param container - space to query against for partitions
	 * @return set of blocks that are contained within the container and max spatial area
	 */
	public Set getBlocks(BoundingBox container) {
		return this.getBlocks(container, new HashSet());
	}

	/**
	 * Returns a set of blocks that are contained within the container
	 * @requires container != null
	 * @param container - space to query against for partitions
	 * @param blocks - collection to be appended to; if null, then a new set will be returned
	 * @return set of blocks that are contained within the container and max spatial area
	 */
	public Set getBlocks(BoundingBox container, Set blocks) {

		if (container == null)
			throw new NullPointerException("Paramters are null!");

		if (blocks == null)
			blocks = new HashSet();

		// OPTIMIZE - Make these static that can just be overwritten
		Point3d lower = new Point3d();
		Point3d upper = new Point3d();

		// First get the dimensions of the container, which is the intersection of the
		// container and the maximum spatial bounding box
		this.getCorners(container, lower, upper);
		
		// Get extremity partitions
		Partition lowerBox =
			this.getBlock(lower);
		Partition upperBox =
			this.getBlock(upper);

		blocks = new HashSet();

		for (int x = lowerBox.x; x <= upperBox.x; x++) {
			Partition.xVal = x;
			
			for (int y = lowerBox.y; y <= upperBox.y; y++) {
				Partition.yVal = y;
				
				for (int z = lowerBox.z; z <= upperBox.z; z++) {
				
					Partition.zVal = z;
					
					Object value = Partition.partitionMap.get(Partition.instance);
					
					if(value == null) {
						// This is the first encounter of the partition
						value = new Partition(x, y, z);
						Partition.partitionMap.put(value, value);			
					}
					
					blocks.add(value);
				}
			}
		}
		
		return blocks;
	}

	/**
	 * Computes the lower and upper corners of the bounding box with respect to the
	 * relative space that the partitions were created for. Result is the intersection
	 * of the container with the spatial box
	 * @param container - container to check for
	 * @param lower - lower point of the new container
	 * @param upper - upper point of the new container
	 * @effects - mutates lower and upper to contain the partition coordinates of the
	 * intersection between the container and the spatial map
	 */
	private void getCorners(BoundingBox container, Point3d lower, Point3d upper) {
		
		container.getLower(lower);
		container.getUpper(upper);

		double cxl = lower.x;
		double cyl = lower.y;
		double czl = lower.z;
		
		double cxu = upper.x;
		double cyu = upper.y;
		double czu = upper.z;

		double mxl = Partition.origin.x;
		double myl = Partition.origin.y;
		double mzl = Partition.origin.z;
		
		double mxu = Partition.upper.x;
		double myu = Partition.upper.y;
		double mzu = Partition.upper.z;

		if (mxl > cxl)
			lower.x = mxl;
		if (myl > cyl)
			lower.y = myl;
		if (mzl > czl)
			lower.z = mzl;
		if (mxu < cxu)
			upper.x = mxu;
		if (myu < cyu)
			upper.y = myu;
		if (mzu < czu)
			upper.z = mzu;	
	}

	/**
	 * Returns whether this object is equal to this. Can only be called once
	 * @return true iff o is not null, o instanceof Partition, and o represents the same
	 * partition block
	 */
	public boolean equals(Object o) {

		if (o != null && o instanceof Partition) {

			Partition p = (Partition) o;

			if (p.x == Integer.MIN_VALUE) {
				// Class Hash
				return (
					this.x == Partition.xVal
						&& this.y == Partition.yVal
						&& this.z == Partition.zVal);
			}

			// Instance hash
			return (this.x == p.x && this.y == p.y && this.z == p.z);

		}
		return false;
	}

	/**
	 * Computes the hash code of this object
	 * @return a hash code of this object, which is a prime number scaling of
	 * each of the coordinates contained within this class
	 */
	public int hashCode() {

		if (this.x == Integer.MIN_VALUE)
			// Class hash
			return Partition.xVal * 11
				+ Partition.yVal * 13
				+ Partition.zVal * 17;
		else
			// Instance hash
			return this.x * 11 + this.y * 13 + this.z * 17;
	}

	/**
	 * Returns a String object representing the three dimensional coordinates of this region.
	 * @return coorindates of this region
	 */
	public String toString() {
		if (this.x == Integer.MIN_VALUE)
			return new String("Static class instance!");
		else
			return new String("{" + this.x + " " + this.y + " " + this.z + "}");
	}

}

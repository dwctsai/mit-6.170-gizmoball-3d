/* @author David Tsai
 * @created on May 11, 2003
 */
package backend.stream;

import java.io.File;
/**
 * TextureUtils - helps with load/save of textures.  
 * Handles possible texture file extensions.
 */
public class TextureFileUtils {
	public final static String jpeg = "jpeg";
	public final static String jpg = "jpg";
	public final static String gif = "gif";
	public final static String tiff = "tiff";
	public final static String tif = "tif";
	public final static String png = "png";

	/**
	 * @return The extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 &&  i < s.length() - 1) {
			ext = s.substring(i+1).toLowerCase();
		}
		return ext;
	}

}

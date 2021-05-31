/* @author David Tsai
 * @created on Apr 26, 2003
 */
package backend.stream;

import java.io.File;

/**
 * GizmoballFileUtils - helps with load/save.  Handles possible gizmoball game file extensions.
 */
public class GizmoballFileUtils {
	public static final String txt = "txt";		
	public static final String g3d = "g3d";

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

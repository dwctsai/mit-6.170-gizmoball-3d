/* @author David Tsai
 * @created on Apr 26, 2003
 */
package backend.stream;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * GizmoballFileFilter - Filters the save/load of Gizmoball files to 
 * allow only *.gb and *.g3d file formats.
 */
public class GizmoballFileFilter extends FileFilter {

	/**
	 * Accept all directories and all *.txt and *.g3d files.
	 * 
	 * @return true if file is accepted, false if not.
	 */
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = GizmoballFileUtils.getExtension(f);
		if (extension != null) {
			if (extension.equals(GizmoballFileUtils.txt) ||
				extension.equals(GizmoballFileUtils.g3d)) {
					return true;
			} else {
				return false;
			}
		}

		return false;
	}

	//The description of this filter
	/**
	 * @return the description of this filter.
	 */
	public String getDescription() {
		return "*.txt, *.g3d";
	}
}

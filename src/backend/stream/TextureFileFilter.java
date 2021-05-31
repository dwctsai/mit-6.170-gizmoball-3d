/* @author David Tsai
 * @created on May 11, 2003
 */
package backend.stream;


import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * TextureFilter - Filters the save/load of texture files to 
 * allow only *.gb and *.g3d file formats.
 */
public class TextureFileFilter extends FileFilter {
	//Accept all directories and all gif, jpg, tiff, or png files.
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = TextureFileUtils.getExtension(f);
		if (extension != null) {
			if (extension.equals(TextureFileUtils.tiff) ||
				extension.equals(TextureFileUtils.tif) ||
				extension.equals(TextureFileUtils.gif) ||
				extension.equals(TextureFileUtils.jpeg) ||
				extension.equals(TextureFileUtils.jpg) ||
				extension.equals(TextureFileUtils.png)) {
					return true;
			} else {
				return false;
			}
		}

		return false;
	}

	//The description of this filter
	public String getDescription() {
		return "*.tiff, *.tif, *.gif, *.jpeg, *.jpg, *.png";
	}
}

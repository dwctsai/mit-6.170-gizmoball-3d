//
//  Physics3dTest.java
//  
//
//  Created by Eric Tung on Sat Apr 12 2003.
//

// Used for independent modlue testing with junit.

package physics3d;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Physics3dTest extends TestSuite {

    public static Test suite() { return new Physics3dTest(); }
    public Physics3dTest() { this("physics3d Test"); }
    public Physics3dTest(String s)
    {
	super(s);
	addTestSuite(GeometryTest.class);
    }

}

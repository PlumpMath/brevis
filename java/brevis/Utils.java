
package brevis;

import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;

import cleargl.GLVector;

public class Utils {

	public static GLVector DVector3CToGLVector(DVector3C odev) {
		return new GLVector( (float)odev.get0(), (float)odev.get1(), (float)odev.get2() );
	}

	public static DVector3C GLVectorToDVector3(GLVector v) {
		return new DVector3( v.x(), v.y(), v.z() );
	}
}

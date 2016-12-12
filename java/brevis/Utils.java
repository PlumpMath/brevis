
package brevis;

import org.lwjgl.util.vector.Vector3f;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;

import cleargl.GLVector;

public class Utils {
	/*public static Vector3d DVector3CToVector3d( DVector3C odev ) {
		return new Vector3d( odev.get0(), odev.get1(), odev.get2() );
	}*/
	public static Vector3f DVector3CToVector3f( DVector3C odev ) {
		return new Vector3f( (float)odev.get0(), (float)odev.get1(), (float)odev.get2() );
	}
	
	/*public static DVector3 Vector3dToDVector3( Vector3d javav ) {
		return new DVector3( javav.x, javav.y, javav.z );
	}*/
	
	public static DVector3 Vector3fToDVector3( Vector3f javav ) {
		return new DVector3( javav.x, javav.y, javav.z );
	}

	public static GLVector DVector3CToGLVector(DVector3C odev) {
		return new GLVector( (float)odev.get0(), (float)odev.get1(), (float)odev.get2() );
		// TODO Auto-generated method stub
	}

	public static DVector3C GLVectorToDVector3(GLVector v) {
		return new DVector3( v.x(), v.y(), v.z() );
	}
}

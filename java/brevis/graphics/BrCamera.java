package brevis.graphics;

import javax.vecmath.Vector3f;

import static java.lang.Math.*;

import org.lwjgl.opengl.GL11;
//import org.lwjgl.util.vector.Matrix4f;
//import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.opengl.GLContext;

import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.*;

// Currently based on http://www.lloydgoodall.com/tutorials/first-person-camera-control-with-lwjgl/
// now based on https://gist.github.com/DziNeIT/4206709

public class BrCamera {
	// Position x y z
	public float x = 0;
	public float y = 0;
	public float z = 0;
	// Rotation pitch yaw roll
	public float pitch = 0;
	public float yaw = 0;
	public float roll = 0;
	// Field of View
	private float fov = 90;
	// Aspect Ratio
	private float aspectRatio = 1;
	// nearClippingPlane = How close to the camera and behind isn't rendered
	private final float nearClippingPlane;
	// farClippingPlane = Render distance from the camera
	private final float farClippingPlane;

	public BrCamera(float x, float y, float z, float pitch, float yaw, float roll, float fov, float aspectRatio, float zNear, float zFar) {
		super();

		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.nearClippingPlane = zNear;
		this.farClippingPlane = zFar;
	}

	public void processMouse( float dx, float dy, float mouseSpeed) {
		processMouse(dx, dy, mouseSpeed, 90, -90);
	}

	/**
	 * Processes mouse movements
	 *
	 * @param mouseSpeed Speed of movement based on DX
	 * @param maxLookUp Maximum angle that can be looked up
	 * @param maxLookDown Minimum angle that can be looked down
	 */
	public void processMouse( float dx, float dy, float mouseSpeed, float maxLookUp, float maxLookDown) {
		float mouseDX = dx * mouseSpeed * 0.16f;
		float mouseDY = dy * mouseSpeed * 0.16f;
		if (yaw + mouseDX >= 360) {
			yaw = yaw + mouseDX - 360;
		//} else if (yaw + mouseDX < 0) {
		//	yaw = 360 - yaw + mouseDX;
		} else if ( yaw + mouseDX < - 360 ) {
			yaw = 360 - yaw + mouseDX;
		} else {
			yaw += mouseDX;
		}
		if (pitch - mouseDY >= maxLookDown && pitch - mouseDY <= maxLookUp) {
			pitch += -mouseDY;
		} else if (pitch - mouseDY < maxLookDown) {
			pitch = maxLookDown;
		} else if (pitch - mouseDY > maxLookUp) {
			pitch = maxLookUp;
		}
	}

	public void processKeyboard(float delta,  boolean up, boolean down, boolean left, boolean right, boolean rise, boolean sink) {
		processKeyboard(delta, 1,   up,  down,  left,  right,  rise,  sink);
	}

	/**
	 * Processes Keyboard presses using given delta and player movement speed
	 *
	 * @param delta Delta time since last call
	 * @param speed Speed of camera movement
	 */
	public void processKeyboard(float delta, float speed, boolean up, boolean down, boolean left, boolean right, boolean rise, boolean sink) {
		if (delta <= 0) {
			throw new IllegalArgumentException("delta is 0 or is smaller than 0");
		}

		/*boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);*/
		
		boolean keyUp = up;
		boolean keyDown = down;
		boolean keyLeft = left;
		boolean keyRight = right;
		boolean flyUp = rise;
		boolean flyDown = sink;

		if (keyUp && keyRight && !keyLeft && !keyDown) {
			moveFromLook(speed * delta * 0.003f, 0, -speed * delta * 0.003f);
		}
		if (keyUp && keyLeft && !keyRight && !keyDown) {
			moveFromLook(-speed * delta * 0.003f, 0, -speed * delta * 0.003f);
		}
		if (keyUp && !keyLeft && !keyRight && !keyDown) {
			moveFromLook(0, 0, -speed * delta * 0.003f);
		}
		if (keyDown && keyLeft && !keyRight && !keyUp) {
			moveFromLook(-speed * delta * 0.003f, 0, speed * delta * 0.003f);
		}
		if (keyDown && keyRight && !keyLeft && !keyUp) {
			moveFromLook(speed * delta * 0.003f, 0, speed * delta * 0.003f);
		}
		if (keyDown && !keyUp && !keyLeft && !keyRight) {
			moveFromLook(0, 0, speed * delta * 0.003f);
		}
		if (keyLeft && !keyRight && !keyUp && !keyDown) {
			moveFromLook(-speed * delta * 0.003f, 0, 0);
		}
		if (keyRight && !keyLeft && !keyUp && !keyDown) {
			moveFromLook(speed * delta * 0.003f, 0, 0);
		}
		if (flyUp && !flyDown) {
			y += speed * delta * 0.003f;
		}
		if (flyDown && !flyUp) {
			y -= speed * delta * 0.003f;
		}
	}

	/**
	 * Moves camera based on Mouse movements
	 *
	 * @param dx Mouse x movement
	 * @param dy Mouse y movement
	 * @param dz Mouse z movement
	 */
	public void moveFromLook(float dx, float dy, float dz) {
		this.z += dx * (float) cos(toRadians(yaw - 90)) + dz * cos(toRadians(yaw));
		this.x -= dx * (float) sin(toRadians(yaw - 90)) + dz * sin(toRadians(yaw));
		this.y += dy * (float) sin(toRadians(pitch - 90)) + dz * sin(toRadians(pitch));
	}

	/**
	 * Applies optimal states
	 */
	public void optimiseStates() {
		if (GLContext.getCapabilities().GL_ARB_depth_clamp) {
			glEnable(GL_DEPTH_CLAMP);
		}
	}

	/**
	 * Applies the orthographic matrix (GL11.glOrtho)
	 */
	public void orthographicMatrix() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(-aspectRatio, aspectRatio, -1, 1, 0, farClippingPlane);
		glPopAttrib();
	}

	/**
	 * Applies the perspective matrix (GLU.gluPerspective)
	 */
	public void perspectiveMatrix() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(fov, aspectRatio, nearClippingPlane, farClippingPlane);
		glPopAttrib();
	}

	/**
	 * Translates camera position to OpenGL position
	 */
	public void translate() {
		glPushAttrib(GL_TRANSFORM_BIT);
		glMatrixMode(GL_MODELVIEW);
		glRotatef(pitch, 1, 0, 0);
		glRotatef(yaw, 0, 1, 0);
		glRotatef(roll, 0, 0, 1);
		glTranslatef(-x, -y, -z);
		glPopAttrib();
	}
}

package brevis.graphics;

import cleargl.GLVector;
import scenery.*;
import scenery.backends.Renderer;
import scenery.repl.REPL;

public class SceneryApplication extends SceneryDefaultApplication {
	Camera cam;
	
	public SceneryApplication(String applicationName, int windowWidth, int windowHeight) {
        super(applicationName, windowWidth, windowHeight);
    }

    public void init() {

        setRenderer( Renderer.Companion.createRenderer( getApplicationName(), getScene(), 512, 512));
        getHub().add(SceneryElement.RENDERER, getRenderer());

        cam = new DetachedHeadCamera();
        cam.setPosition( new GLVector(0.0f, 0.0f, 5.0f) );
        cam.perspectiveCamera(50.0f, getRenderer().getWindow().getWidth(), getRenderer().getWindow().getHeight(), 0.1f, 1000.0f);
        cam.setActive( true );
        getScene().addChild(cam);

    }


}

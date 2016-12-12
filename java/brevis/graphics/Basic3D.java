

package brevis.graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.ARBShadowAmbient.GL_TEXTURE_COMPARE_FAIL_VALUE_ARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.util.glu.GLU.*;
import brevis.BrObject;
import brevis.BrShape;
import brevis.Engine;

public class Basic3D {
    private static final float LIGHTX = 1.0f;
    private static final float LIGHTY = 0.4f;
    private static final float SHADOW_INTENSITY = 0.65f;
    
    //static float lightPos[] = { 0.0f, 5.0f,-4.0f, 1.0f};           // Light Position                                                                               
    static float lightPos[] = { 0.0f, 1.0f, 0.0f, 1.0f};           // Light Position                                                                               
    static float lightAmb[] = { 0.2f, 0.2f, 0.2f, 1.0f};           // Ambient Light Values                                                                         
    static float lightDif[] = { 0.6f, 0.6f, 0.6f, 1.0f};           // Diffuse Light Values                                                                         
    static float lightSpc[] = {-0.2f, -0.2f, -0.2f, 1.0f};         // Specular Light Values                                                                        
    static ByteBuffer byteBuffer;
    static ByteBuffer floatBuffer;
    static float matAmb[] = {0.4f, 0.4f, 0.4f, 1.0f};              // Material - Ambient Values                                                                    
    static float matDif[] = {0.2f, 0.6f, 0.9f, 1.0f};              // Material - Diffuse Values                                                                    
    static float matSpc[] = {0.0f, 0.0f, 0.0f, 1.0f};              // Material - Specular Values                                                                   
    static float matShn[] = {0.0f, 0.0f, 0.0f, 0.0f};                                // Material - Shininess                                                       
    
    static public int width = 640;
    static public int height = 480;        
    
    static private float[] view_xyz = new float[3];	// position x,y,z
	static private float[] view_hpr = new float[3];	// heading, pitch, roll (degrees)
    
	static ArrayList<BrLight> lights = new ArrayList<BrLight>();
	static { 
		lights.add( new BrLight( 0 ) );
	}
	
	static BrSky sky;
		
	public static void addLight( ) {
		lights.add( new BrLight( lights.size() ) );
	}
	
	public static void lightMove( int lightNum, float[] position ) {
		lights.get(lightNum).setPosition( position );
	}	
	
	public static float[] lightPosition( int lightNum ) {
		return lights.get(lightNum).getPosition();
	}
	
	public static void lightDiffuse( int lightNum, float[] color) {
		lights.get(lightNum).setDiffuse( color );
	}
	
	public static void lightSpecular( int lightNum, float[] color) {
		lights.get(lightNum).setSpecular( color );
	}
	
	public static void lightAmbient( int lightNum, float[] color) {
		lights.get(lightNum).setAmbient( color );
	}
	
	// a good bit from ode4j
    static public void initGL() {
        
    	view_xyz[0] = 2;
		view_xyz[1] = 0;
		view_xyz[2] = 1;
		view_hpr[0] = 180;
		view_hpr[1] = 0;
		view_hpr[2] = 0;
		
		float fov = 45;
		float near = 0.1f;
		float far = 3000;
		//displayCamera = new BrCamera( view_xyz[0], view_xyz[1], view_xyz[2], view_hpr[0], view_hpr[1], view_hpr[2], fov, width, height, near, far );
        
		//light1.setPosition( new float[]{ 1.0f, 0.4f, 1.0f, 0.0f } );
		//light1.setPosition(new float[] { 50.0f, 200.0f, 50.0f, 0.0f }  );
		
		//light1.setPosition(new float[] { light_position.get(0), light_position.get(1), light_position.get(2), light_position.get(3) }  );
		
        GL11.glShadeModel(GL11.GL_SMOOTH);                            // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               // Black Background
        GL11.glClearDepth(1.0f);                                 // Depth Buffer Setup
        GL11.glClearStencil(0);                                  // Stencil Buffer Setup
        GL11.glEnable(GL11.GL_DEPTH_TEST);                            // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);                             // The Type Of Depth Testing To Do
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);  // Really Nice Perspective Calculations
        
        //GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        //GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_FASTEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
   
        
        //light1.enable();
        for( BrLight light : lights ) {
        	//light.enable();
        }
        sky = new BrSky();
        
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);            
        GL11.glViewport(0,0,width,height);                           // Reset The Current Viewport

        GL11.glMatrixMode(GL11.GL_PROJECTION);                            // Select The Projection Matrix
        GL11.glLoadIdentity();                                       // Reset The Projection Matrix
        
        GLU.gluPerspective(45.0f,
                (float) width / (float) height,
                0.05f, 100.0f);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);                             // Select The Modelview Matrix
        GL11.glLoadIdentity();                                       // Reset The Modelview Matrix
        
    }
    
	
    static public void initFrame( BrCamera displayCamera ) {
    	GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
    	
    	//GL11.glEnable( GL11.GL_LIGHTING );
    	//GL11.glEnable( GL11.GL_LIGHT0 );
    	
    	//light1.enable();
    	
    	//glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    	//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
    	GL11.glDisable(GL_TEXTURE_GEN_R);
    	GL11.glDisable(GL_TEXTURE_GEN_Q);    	
    	GL11.glDisable (GL11.GL_TEXTURE_2D);
		GL11.glDisable (GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable (GL11.GL_TEXTURE_GEN_T);
		GL11.glShadeModel (GL11.GL_FLAT);
		GL11.glEnable (GL11.GL_DEPTH_TEST);
		//GL11.glDepthFunc (GL11.GL_LESS);
		GL11.glDepthFunc (GL11.GL_LEQUAL);
		
		GL11.glEnable (GL11.GL_CULL_FACE);
		GL11.glCullFace (GL11.GL_BACK);
		//GL11.glFrontFace (GL11.GL_CCW);

		// setup viewport
		//displayCamera.setupFrame();		
		
		//light1.enable();		
		GL11.glMatrixMode (GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		for( BrLight light : lights ) {
			//light.enable();
			//float[] pos = light.getPosition();
			//System.out.println( "Light " + light + " position " + pos[0] + ", " +  pos[1] + ", " +  pos[2] + "" );
		}
		
		GL11.glColor3f (1.0f, 1.0f, 1.0f);

		// clear the window
		GL11.glClearColor (0.5f ,0.5f ,0.5f ,0);
		GL11.glClear (GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// go to GL_MODELVIEW matrix mode and set the camera
		GL11.glMatrixMode (GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		//displayCamera.setDimensions( Display.getWidth(), Display.getHeight() );
		displayCamera.setupFrame();		
		
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		sky.draw( displayCamera );
		
    }
    
    // Le ew
    static FloatBuffer lightProjection = BufferUtils.createFloatBuffer(16);
	static FloatBuffer lightModelView = BufferUtils.createFloatBuffer(16);
	static Matrix4f lightProjectionTemp = new Matrix4f();
	static Matrix4f lightModelViewTemp = new Matrix4f();
	private static final Matrix4f depthModelViewProjection = new Matrix4f();
    
	static public void drawBox(float w, float h, float d) {
		GL11.glBegin(GL11.GL_QUADS);
			// Front
			GL11.glNormal3f(0f, 0f, 1f);
			GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-w, -h,  d);  // Bottom Left
			GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( w, -h,  d);  // Bottom Right
			GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( w,  h,  d);  // Top Right
			GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-w,  h,  d);  // Top Left
		    // Back 
			GL11.glNormal3f(0f, 0f, -1f);
			GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-w, -h, -d);  // Bottom Right
			GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-w,  h, -d);  // Top Right
			GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( w,  h, -d);  // Top Left
			GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( w, -h, -d);  // Bottom Left 
		    // Top 
			GL11.glNormal3f(0f, -1f, 0f);
			GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-w,  h, -d);  // Top Left
			GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-w,  h,  d);  // Bottom Left 
			GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( w,  h,  d);  // Bottom Right 
			GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( w,  h, -d);  // Top Right 
		    // Bottom 
			GL11.glNormal3f(0f, 1f, 0f);
			GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-w, -h, -d);  // Top Right
			GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( w, -h, -d);  // Top Left 
			GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( w, -h,  d);  // Bottom Left 
			GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-w, -h,  d);  // Bottom Right
		    // Right 
			GL11.glNormal3f(-1f, 0f, 0f);
			GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( w, -h, -d);  // Bottom Right
			GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( w,  h, -d);  // Top Right 
			GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( w,  h,  d);  // Top Left 
			GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( w, -h,  d);  // Bottom Left 
		    // Left 
			GL11.glNormal3f(1f, 0f, 0f);
			GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-w, -h, -d);  // Bottom Left
			GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-w, -h,  d);  // Bottom Right 
			GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-w,  h,  d);  // Top Right
		    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-w,  h, -d);  // Top Left 
		
		    GL11.glEnd();
	
		

		//System.out.println( "I wish I was printing a box of " + w + "," + h + "," + d + " dimensions ");
	}
	
	static public void drawSphere(float r, int stack, int string) {

		Sphere s = new Sphere();
	
		s.draw(r, stack, string);
		

	}

	/*static public void drawCylinder(float baseRadius, float topRadius, float height, int slices, int stacks) {

		Cylinder c = new Cylinder();
	
		c.draw(baseRadius, topRadius, height, slices, stacks);
		
		//System.out.println(baseRadius + " " + topRadius + " " + height + " " + slices + " " + stacks);

	}*/
	
	static public void drawCylinder(float baseRadius, float topRadius, float height, int slices, int stacks, Cylinder data) {

		//Cylinder c = new Cylinder();
	
		data.draw(baseRadius, topRadius, height, slices, stacks);
		
		//System.out.println(baseRadius + " " + topRadius + " " + height + " " + slices + " " + stacks);

	}
	
	// nehe lesson 27
    private static void vMatMult(float[] minv, double[] lp) {
        double res[] = new double[4];                                     // Hold Calculated Results
        res[0]=minv[ 0]*lp[0]+minv[ 4]*lp[1]+minv[ 8]*lp[2]+minv[12]*lp[3];
        res[1]=minv[ 1]*lp[0]+minv[ 5]*lp[1]+minv[ 9]*lp[2]+minv[13]*lp[3];
        res[2]=minv[ 2]*lp[0]+minv[ 6]*lp[1]+minv[10]*lp[2]+minv[14]*lp[3];
        res[3]=minv[ 3]*lp[0]+minv[ 7]*lp[1]+minv[11]*lp[2]+minv[15]*lp[3];
        lp[0]=res[0];                                        // Results Are Stored Back In v[]
        lp[1]=res[1];
        lp[2]=res[2];
        lp[3]=res[3];                                        // Homogenous Coordinate
    }	    

    // ode4j-sdk drawstuff
    private static float[] color = {0,0,0,0};       // current r,g,b,alpha color                                                                                                                                                          
    //private static DS_TEXTURE_NUMBER tnum = DS_TEXTURE_NUMBER.DS_NONE;                      // current texture number                                                                                                                     
    
    public static void setCamera (float x, float y, float z, float h, float p, float r)
    {
            //GL11.glMatrixMode (GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            //GL11.glRotatef (90, 0,0,1);
            //GL11.glRotatef (90, 0,1,0);
            /*GL11.glRotatef (r, 1,0,0);
            GL11.glRotatef (p, 0,1,0);
            GL11.glRotatef (-h, 0,0,1);
            GL11.glTranslatef (-x,-y,-z);*/
            GL11.glRotatef (r, 1,0,0);
            GL11.glRotatef (p, 0,1,0);
            GL11.glRotatef (-h, 0,0,1);
            GL11.glTranslatef (-x,-y,-z);            
    }   
    
	// some from nehe lesson 27    
	//static public void drawShape( BrObject obj, double xrot, double yrot, double zrot, double xoff, double yoff, double zoff, double[] lp, Vector3d dim ) {
    //static public void drawShape( BrObject obj, double[] lp, Vector3f dim ) {
    static public void drawShape( BrObject obj, Vector3f dim ) {     
    	Vector3f vObjPos = new Vector3f();
    	Vector3f.add( obj.getPosition(), obj.getShape().center, vObjPos );
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        
        GL11.glPushMatrix();
        //GL11.glColor4d( obj.color.x, obj.color.y, obj.color.z, obj.color.w );
        
        // This is needed but shouldn't be?
        //setColor( (float)obj.color.x, (float)obj.color.y, (float)obj.color.z, (float)obj.color.w );
                
        GL11.glTranslatef( vObjPos.x, vObjPos.y, vObjPos.z );      // Position The Object
        //GL11.glTranslatef(objPos[0], objPos[1], objPos[2]);      // Position The Object
                        
        Vector4f rot = obj.getRotation();
        GL11.glRotatef( (float)rot.w, (float)rot.x, (float)rot.y, (float)rot.z);
        if( ! ( obj.getShape().type == BrShape.BrShapeType.CONE ||
        		obj.getShape().type == BrShape.BrShapeType.UNIT_CONE ||
        		obj.getShape().type == BrShape.BrShapeType.SPHERE ||
        		obj.getShape().type == BrShape.BrShapeType.UNIT_SPHERE ||
        		obj.getShape().type == BrShape.BrShapeType.CYLINDER ||
        		obj.getShape().type == BrShape.BrShapeType.MESH ||
        		obj.getShape().type == BrShape.BrShapeType.ICOSAHEDRON ) ) {         	 
        	GL11.glScaled( dim.x, dim.y, dim.z );
        	//System.out.println( "drawShape " + dim );
        }
                
        if( obj.getTextureId() != -1 ) {
        	GL11.glEnable(GL11.GL_TEXTURE_2D);
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getTextureId() );
        } else {
        	GL11.glDisable(GL11.GL_TEXTURE_2D);
        	 //setColor( (float)obj.color.x, (float)obj.color.y, (float)obj.color.z, (float)obj.color.w );
        }        
        
        
        // Render primitives directly with vertex commands       
        if( ( obj.getShape().mesh == null ) ||
        		( obj.getShape().getType() == "box" ) || 
        		( obj.getShape().getType() == "cone" ) || 
        		( obj.getShape().getType() == "cylinder" )
        		) {         		//( obj.getShape().getType() == "sphere" ) || 
        //if( obj.getShape().mesh == null ) {
        	//System.out.println( "NO MESH " + obj.type );
        	
        	
        	obj.getShape().opengldraw();
        	
	        /*if( obj.getShape().getType() == "box" )
	        	drawBox( 1, 1, 1 );
	        	
	        else if( obj.getShape().getType() == "cone" )
	        	drawCylinder( (float)dim.y, (float)0.0001, (float)dim.x, numSlices, numStacks, (Cylinder)obj.getShape().data );
	        else if( obj.getShape().getType() == "cylinder" )
	        	drawCylinder( (float)dim.y, (float)dim.z, (float)dim.x, numSlices, numStacks, (Cylinder)obj.getShape().data );
	        else
	        	drawSphere( (float)dim.x, 25, 20);*/
	        	//( (Sphere)obj.getShape().data ).draw( (float)dim.x, 25, 20);
        } else {
        	//GL11.glScaled( dim.x, dim.y, dim.z );
        	if( obj.getShape().mesh.redraw ) {
        		obj.getShape().mesh.opengldrawtolist();
        	}
        	obj.getShape().mesh.opengldraw();
    	}        
        
        GL11.glPopMatrix();
                                 
        /*if( obj.getShape().mesh != null && obj.enabledShadow() ) {
        	//System.out.println( "drawShape " + obj.type + " " + obj.getShape() );
        	castShadow( obj.getShape().mesh, lp);                               // Procedure For Casting The Shadow Based On The Silhouette
        	//System.out.println( "castShadow" );
        } */
                
	}   
    
    static public void drawLine(Vector3f source, Vector3f destination, Vector4f color) {
        
    	GL11.glColor3f( color.x, color.y, color.z );
        GL11.glBegin(GL11.GL_LINE_STRIP);              
        GL11.glColor3f( color.x, color.y, color.z );
        GL11.glVertex3f( source.x, source.y, source.z );
        GL11.glColor3f( color.x, color.y, color.z );
        GL11.glVertex3f( destination.x, destination.y, destination.z );
        GL11.glEnd();
    }
    
    static public void drawFilledPolygon( List<Vector3f> points ) {
    	GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    	GL11.glBegin( GL11.GL_POLYGON );
    	for( Vector3f point : points ) {
    		GL11.glColor3f( 1, 1, 1 );
    		GL11.glVertex3f( point.x, point.y, point.z);
    	}
    	GL11.glEnd();
    }
    
    public static void screenshot( String filename ) throws LWJGLException {
	    GL11.glReadBuffer(GL11.GL_FRONT);	
	    /*if( Display.wasResized() ) {
	    	//GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	    	Display.setDisplayMode(new DisplayMode(Display.getWidth(),Display.getHeight()));
	    }*/
	    //int width = Display.getDisplayMode().getWidth();
	    int width = Display.getWidth();
	    //int height= Display.getDisplayMode().getHeight();
	    int height = Display.getHeight();
	    int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
	    ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
	    GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
	    
	    File file = new File( filename ); // The file to save to.
	    String format = "PNG"; // Example: "PNG" or "JPG"
	    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	      
	    for(int x = 0; x < width; x++) {
	    	for(int y = 0; y < height; y++) {
	    		int i = (x + (width * y)) * bpp;
	    		int r = buffer.get(i) & 0xFF;
	    		int g = buffer.get(i + 1) & 0xFF;
	    		int b = buffer.get(i + 2) & 0xFF;
	    		image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
	    	}
	    }
	      
	    try {
	    	ImageIO.write(image, format, file);
	    } catch (IOException e) { e.printStackTrace(); }
    }
    
    public static BufferedImage screenshotImage( ) throws LWJGLException {
	    GL11.glReadBuffer(GL11.GL_FRONT);	
	    /*if( Display.wasResized() ) {
	    	//GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	    	Display.setDisplayMode(new DisplayMode(Display.getWidth(),Display.getHeight()));
	    }*/
	    //int width = Display.getDisplayMode().getWidth();
	    int width = Display.getWidth();
	    //int height= Display.getDisplayMode().getHeight();
	    int height = Display.getHeight();
	    int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
	    ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
	    GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
	    
	    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	      
	    for(int x = 0; x < width; x++) {
	    	for(int y = 0; y < height; y++) {
	    		int i = (x + (width * y)) * bpp;
	    		int r = buffer.get(i) & 0xFF;
	    		int g = buffer.get(i + 1) & 0xFF;
	    		int b = buffer.get(i + 2) & 0xFF;
	    		image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
	    	}
	    }
	      
	    return image;
    }   
    
    private static void drawGround() {
        glPushAttrib(GL_LIGHTING_BIT);
        {
            glDisable(GL_LIGHTING);
            glBegin(GL_QUADS);
            glColor3f(0.3F, 0.6F, 0.3F);
            glVertex3f(-120, -19, -120);
            glVertex3f(-120, -19, +120);
            glVertex3f(+120, -19, +120);
            glVertex3f(+120, -19, -120);
            glEnd();
        }
        glPopAttrib();
    }
    
    private static void drawScene( Engine e ) {
        glPushMatrix();
        Collection<BrObject> objects = e.getObjects();
        for( BrObject obj : objects ) {
        	drawShape( obj, obj.getShape().getDimension() );        	        	
        }        
        glPopMatrix();
    }
    
    public static void displayEngine( Engine e, BrCamera displayCamera ) {

    	
    	
    	glLoadIdentity();
        // Apply the camera position and orientation to the model-view matrix.
    	initFrame( displayCamera );
        
        // Clear the screen.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        // Store the current attribute state.
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        {
            //generateTextureCoordinates();
            
            drawGround();
            drawScene( e );
            //drawShadowMap( e );
        }
        // Restore the previous attribute state.
        glPopAttrib();
    	
    }
}

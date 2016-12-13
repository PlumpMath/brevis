
package brevis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

// Shouldn't be any opengl stuff in here actually


import java.awt.image.ComponentColorModel;

import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.InternalTextureLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.util.ClasspathLocation;
import org.newdawn.slick.util.ResourceLoader;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DMass;
import org.ode4j.ode.OdeHelper;

//import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import clojure.lang.*;
import brevis.Utils;
import cleargl.GLVector;
import brevis.BrShape.BrShapeType;
import ij.*;

//public class BrObject {
//public class BrObject implements clojure.lang.IRecord {
public class BrObject implements clojure.lang.IPersistentMap, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5143539083266175610L;

	private int dstPixelFormat = SGL.GL_RGBA8;
	
	public Long uid;
	//public String type;
	public clojure.lang.Keyword type;
	public GLVector acceleration;
	public GLVector velocity;
	public GLVector position;
	public double density = 1;
	public BrShape shape;
	public DMass mass;
	public GLVector rotation;
	public GLVector color;
	//public BufferedImage texture;	
	public Texture texture = null;	
	public Object data;
	
	public Long closestNeighbor;
	
	//public Matrix4d transform;
	
	public HashMap<Object,Object> myMap;
	
	public Vector<Long> nbrs;
	protected int texId = -1;
	
	public boolean drawable = true;
	public boolean hasShadow = true;
	
	public BrKDNode myKDnode  = null;
	
	// Physics
	public DBody body;
	public DGeom geom;	
	
	public boolean enabledShadow() {
		return hasShadow;
	}
	
	public void setShadow( boolean newHasShadow ) {
		hasShadow = newHasShadow;
	}
	
	public String toString() {
		/*String s = "#BrObject{ :UID " + uid + ", :type " + type + ", :acceleration " + acceleration +
				", :velocity " + velocity + ", :position " + position + ", :density " + density +
				", :rotation " + rotation + ", : color " + color + ", :shape " + shape +
				"}";*/
		String s = "#BrObject{ :UID " + uid + ", :type " + type + ", :acceleration " + acceleration +
				", :velocity " + velocity + ", :position " + position + ", :density " + density +
				", :rotation " + rotation + ", : color " + color + ", :shape " + shape + ", [";
		/*Iterator itr = this.iterator();
		while( itr.hasNext() ) {
			Object o = itr.next();
			s += o + ", ";
		}*/
		s += "]}";
		return s;
	}
	
	public BrObject() {
		uid = (long)-1;
		//type = "Unassigned";
		type = clojure.lang.Keyword.intern( clojure.lang.Symbol.create( "Unassigned" ) );
		acceleration = new GLVector( 0, 0, 0 );
		velocity = new GLVector( 0, 0, 0 );
		position = new GLVector( 0, 0, 0 );
		shape = null;//BrShape.createSphere( 1 ); too expensive
		color = new GLVector( 1, 1, 1, 1 );
		rotation = new GLVector( 1, 0, 0, 0 );
		data = null;
		myMap = new HashMap<Object,Object>();
		texture = null;
	}
	
	public void setDrawable( boolean newDrawable ) {
		drawable = newDrawable;
	}
	
	public boolean isDrawable() {
		return drawable;
	}
	
	public BrObject assoc(Object key, Object val) {
		myMap.put(key, val);
		return this;
	}

	public BrObject assocEx(Object key, Object val) {
		// no clue if this is supposed to behave differently from assoc
		myMap.put(key, val);
		return this;
	}

	public BrObject without(Object key) {
		myMap.remove(key);
		return this;
	}
	
	public double distanceTo( BrObject other ) {
		GLVector delta = other.getPosition().minus( getPosition() );
		return delta.magnitude();
	}
	
	public void setUID( Long UID ) {
		uid = UID;
	}
	
	/*public String getType() {
		return type;
	}*/
	
	public Object getType() {
		return type;
	}
	
	public Long getUID( ) {
		return uid;	
	}
	
	public void setType( String newType ) {
		type = clojure.lang.Keyword.intern( clojure.lang.Symbol.create( newType ) );
		//type = newType;
	}
	
	public Vector<Long> getNeighbors() {
		return nbrs;
	}
	
	public Long getClosestNeighbor() {
		return closestNeighbor;
	}
	
	public void clearNeighbors() {
		nbrs.clear();
	}
	
	public void addNeighbor( Long UID ) {
		nbrs.add( UID );
	}
	
	public GLVector getPosition() {
		//return position;
		return brevis.Utils.DVector3CToGLVector( body.getPosition() );
	}
	
	public GLVector getVelocity() {
		return brevis.Utils.DVector3CToGLVector( body.getLinearVel() );
		//return velocity;
	}
	
	public GLVector getForce() {
		return brevis.Utils.DVector3CToGLVector( body.getForce() );
		//return velocity;
	}
	
	public GLVector getAcceleration() {
		return acceleration;
	}
	
	public void setAcceleration( GLVector v ) {
		acceleration = v;
	}
	
	public void setVelocity( GLVector v ) {
		//velocity = v;
		body.setLinearVel( brevis.Utils.GLVectorToDVector3( v ) );
	}
	
	public void setPosition( GLVector v ) {
		//position = v;
		if( myKDnode != null ) {
			myKDnode.domain[0] = position.x();
			myKDnode.domain[1] = position.y();
			myKDnode.domain[2] = position.z();
		}
		body.setPosition( brevis.Utils.GLVectorToDVector3( v ) );
	}
	
	public DBody getBody( ) {
		return body;
	}
	
	public void setBody( DBody b ) {
		body = b;
	}
	
	public DGeom getGeom() {
		return geom;
	}
	
	public void setGeom( DGeom g ) {
		geom = g;
	}
		
	public BrShape getShape( ) {
		return shape;
	}
	
	public void setShape( BrShape s ) {
		shape = s;
	}
	
	public void makeReal( Engine e ) throws Exception {
		mass = shape.createMass( density );
		
		//System.out.println( "makeReal " + shape.getDimension() + " " + density + " " + mass );
		
		body = OdeHelper.createBody( e.getWorld() );
		body.setMass( mass );
		HashMap<String,Object> bodymap = new HashMap<String,Object>();
		bodymap.put( "uid", uid );
		bodymap.put( "type", ((Keyword)type).getName() );// or toString
		body.setData( bodymap );
		
		geom = shape.createGeom( e.physics.getSpace() );
		geom.setBody( body );
		geom.setOffsetWorldPosition( position.x(), position.y(), position.z() );
		
		/*if( shape.type != BrShapeType.MESH ) {
			shape.createMesh();
		}*/
		//shape.createVBOFromMesh();
	}
	
	public void recreatePhysicsGeom( Engine e ) throws Exception {
		// need to remove old geom from body??
		geom = shape.createGeom( e.physics.getSpace() );
		geom.setBody( body );
		geom.setOffsetWorldPosition( position.x(), position.y(), position.z() );
	}
	
	/*public void makeAbstract( Engine e ) {
		
	}*/
	
	public void setColor( GLVector c ) {
		color = c;
	}
	
	public GLVector getColor() {
		return color;
	}
	
	public void setDimension( GLVector newDim, boolean withGraphics ) {
		shape.setDimension( newDim, withGraphics );
	}
	
	public GLVector getDimension() {
		return shape.getDimension();
	}
	
	public GLVector getRotation() {
		
		return rotation;
	}
	
	public void setRotation( GLVector v ) {
		rotation = v;
	}
	
	public DMass getMass() {
		return mass;
	}
	
	public double getDoubleMass() {
		return mass.getMass();	
	}
	
	/*public BufferedImage getTexture() {
		return texture;
	}*/
	
	/**
	 * from https://bitbucket.org/kevglass/slick/src/9d7443ec33af80e3cd1d249d99087437d39d5f48/trunk/Slick/src/org/newdawn/slick/opengl/InternalTextureLoader.java?at=default
     * Get the closest greater power of 2 to the fold number
     * 
     * @param fold The target number
     * @return The power of 2
     */
    public static int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    } 
	
    /*
	public void setTextureImage(BufferedImage newTexture) {
		//Generally a good idea to enable texturing first
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		TextureImpl timp;
		int textureID;
		//texture = newTexture;
		if( texture == null ) {
			textureID = GL11.glGenTextures();
			timp = new TextureImpl("NORESOURCE", GL11.GL_TEXTURE_2D, textureID);
		} else {
			timp = (TextureImpl) texture;
			textureID = timp.getTextureID();
		}
		
		ImageIOImageData iiid = new ImageIOImageData();
				
        ByteBuffer buffer = iiid.imageToByteBuffer( newTexture, false, false, null );        

        int width;
        int height;
        int texWidth;
        int texHeight;

        boolean hasAlpha;

        width = newTexture.getWidth();
        height = newTexture.getHeight();
        hasAlpha = newTexture.getColorModel().hasAlpha();
               
        texWidth = (int) Math.pow( 2, Math.ceil( Math.log( width ) / Math.log( 2 ) ) );
        texHeight = (int) Math.pow( 2, Math.ceil( Math.log( height ) / Math.log( 2 ) ) );
              
        int srcPixelFormat = hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
        int componentCount = hasAlpha ? 4 : 3;
        
        int minFilter = 0;//scale?
        int magFilter = 0;
        
        timp.setAlpha( hasAlpha );
        timp.setHeight( height );
        timp.setWidth( width );
        timp.setTextureID( textureID );
        timp.setTextureHeight( texHeight );
        timp.setTextureWidth( texWidth );                       
        
        //System.out.println( "setTextureimage " + width + " " + height + " " + hasAlpha + " " + texWidth + " " + texHeight );
        
        timp.setTextureData(srcPixelFormat, componentCount, minFilter, magFilter, buffer);        
        
        GL13.glActiveTexture( GL13.GL_TEXTURE0 );
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); 
        
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, temp);
        int max = temp.get(0);
        if ((texWidth > max) || (texHeight > max)) {
                try {
					throw new IOException("Attempt to allocate a texture to big for the current hardware");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        
        //}        
        
        // produce a texture from the byte buffer
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 
                      0, 
                      dstPixelFormat, 
                      get2Fold(width), 
                      get2Fold(height), 
                      0, 
                      srcPixelFormat, 
                      GL11.GL_UNSIGNED_BYTE, 
                      buffer);   
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        
        //System.out.println( texture );
        //System.out.println( timp );
        
        texture = timp;
				        
	}*/
	
	// slick2d stuff
	
	/** The colour model including alpha for the GL image */
    private static final ColorModel glAlphaColorModel = 
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
	            new int[] {8,8,8,8},
	            true,
	            false,
	            ComponentColorModel.TRANSLUCENT,
	            DataBuffer.TYPE_BYTE);
    
    /** The colour model for the GL image */
    private static final  ColorModel glColorModel =
    		new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                new int[] {8,8,8,0},
                false,
                false,
                ComponentColorModel.OPAQUE,
                DataBuffer.TYPE_BYTE);
	
	/**
	 * Implement of transform copy area for 1.4
	 * 
	 * @param image The image to copy
 	 * @param x The x position to copy to
	 * @param y The y position to copy to
	 * @param width The width of the image
	 * @param height The height of the image
	 * @param dx The transform on the x axis
	 * @param dy The transform on the y axis
	 */
	private void copyArea(BufferedImage image, int x, int y, int width, int height, int dx, int dy) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		
		g.drawImage(image.getSubimage(x, y, width, height),x+dx,y+dy,null);
	}    
	
	/*
	public void setTextureImp(ImagePlus imp) {
		//Generally a good idea to enable texturing first
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		BufferedImage newTexture = imp.getBufferedImage();

		TextureImpl timp;
		int textureID;
		//texture = newTexture;
		if( texture == null ) {
			textureID = GL11.glGenTextures();
			timp = new TextureImpl("NORESOURCE", GL11.GL_TEXTURE_2D, textureID);
		} else {
			timp = (TextureImpl) texture;
			textureID = timp.getTextureID();
		}
		
		ImageIOImageData iiid = new ImageIOImageData();
				
        ByteBuffer buffer = iiid.imageToByteBuffer( newTexture, false, false, null );
		//ByteBuffer buffer = impToByteBuffer( imp );

        int width;
        int height;
        int texWidth;
        int texHeight;

        boolean hasAlpha;

        width = newTexture.getWidth();
        height = newTexture.getHeight();
        hasAlpha = newTexture.getColorModel().hasAlpha();
               
        texWidth = (int) Math.pow( 2, Math.ceil( Math.log( width ) / Math.log( 2 ) ) );
        texHeight = (int) Math.pow( 2, Math.ceil( Math.log( height ) / Math.log( 2 ) ) );
              
        int srcPixelFormat = hasAlpha ? GL11.GL_RGBA : GL11.GL_RGB;
        int componentCount = hasAlpha ? 4 : 3;
        
        int minFilter = 0;//scale?
        int magFilter = 0;
        
        timp.setAlpha( hasAlpha );
        timp.setHeight( height );
        timp.setWidth( width );
        timp.setTextureID( textureID );
        timp.setTextureHeight( texHeight );
        timp.setTextureWidth( texWidth );                       
        
        //System.out.println( "setTextureimage " + width + " " + height + " " + hasAlpha + " " + texWidth + " " + texHeight );
        
        timp.setTextureData(srcPixelFormat, componentCount, minFilter, magFilter, buffer);        
        
        GL13.glActiveTexture( GL13.GL_TEXTURE0 );
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); 
        
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, temp);
        int max = temp.get(0);
        if ((texWidth > max) || (texHeight > max)) {
                try {
					throw new IOException("Attempt to allocate a texture to big for the current hardware");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        
        //}
        
        // produce a texture from the byte buffer
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 
                      0, 
                      dstPixelFormat, 
                      get2Fold(width), 
                      get2Fold(height), 
                      0, 
                      srcPixelFormat, 
                      GL11.GL_UNSIGNED_BYTE, 
                      buffer);   
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        
        //System.out.println( texture );
        //System.out.println( timp );
        
        texture = timp;
				        
	}*/
	
	public void setTexture( String filename ) {
	//public void setTexture( URL filename ) {
		
		try {
			// load texture from PNG file
			//texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename));
			//ResourceLoader.addResourceLocation( new ClasspathLocation() );// this should probably be a 1x thing
			//texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream( filename.getPath() ) );
			//texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream( filename.getFile() ) );
			//texture = TextureLoader.getTexture("PNG", filename.openStream() );
			
			texture = TextureLoader.getTexture("PNG", Thread.currentThread().getContextClassLoader().getResourceAsStream( filename ) );
			
		
			/*System.out.println("Texture loaded: "+texture);
			System.out.println(">> Image width: "+texture.getImageWidth());
			System.out.println(">> Image height: "+texture.getImageHeight());
			System.out.println(">> Texture width: "+texture.getTextureWidth());
			System.out.println(">> Texture height: "+texture.getTextureHeight());
			System.out.println(">> Texture ID: "+texture.getTextureID());*/
		} catch (IOException e) {
			System.out.println( "Error loading texture: " + filename );
			e.printStackTrace();
		}		
		
	}
	
	/*
	 * Update the orientation of an object
	 */
	public void orient( GLVector objVec, GLVector targetVec ) {
		if( objVec.magnitude() != 0 && targetVec.magnitude() != 0 ) {
			GLVector dir = objVec.cross( targetVec );

			dir.set( 0, ( objVec.y() * targetVec.z() - objVec.z() * targetVec.y() ) );
			dir.set( 1, ( objVec.z() * targetVec.x() - objVec.x() * targetVec.z() ) ); 
			dir.set( 2, ( objVec.x() * targetVec.y() - objVec.y() * targetVec.x() ) );
			if( dir.magnitude() != 0 )
				dir.normalize();
			//dir.scale( 1.0 / dir.length() );
			double vdot = targetVec.x() * objVec.x() + targetVec.y() * objVec.y() + targetVec.z() * objVec.z();  

			vdot = Math.max( Math.min( vdot / ( objVec.magnitude() * targetVec.magnitude() ), 1.0), -1.0 );

			double angle = ( Math.acos( vdot ) * ( 180.0 / Math.PI ) );
			if( dir.magnitude() == 0 ) {
				rotation.set( 0, objVec.x() );
				rotation.set( 1, objVec.y() );
				rotation.set( 2, objVec.z() );
				rotation.set( 3, (float)0.001 );
			} else {
				rotation.set( 0, dir.x() );
				rotation.set( 1, dir.y() );
				rotation.set( 2, dir.z() );
				rotation.set( 3, (float)angle );
			}
			//System.out.println( "orient " + objVec + " " + targetVec + " " + dir + " " + vdot + " " + rotation );
			
		}
	}
	
	public void updateObjectKinematics( double dt ) {	
	//(defn update-object-kinematics
	//		  "Update the kinematics of an object by applying acceleration and velocity for an infinitesimal amount of time."
		//System.out.print( this );
		
		GLVector f = new GLVector( acceleration );
		f.set( 0, f.x() * (float) getDoubleMass() );
		f.set( 1, f.y() * (float) getDoubleMass() );
		f.set( 2, f.z() * (float) getDoubleMass() );
		getBody().addForce( f.x(), f.y(), f.z() );
		orient( new GLVector(0,0,1), getVelocity() );

	}
	
	public int getTextureId() {
		//return texId;
		if( texture != null )
			return texture.getTextureID();
		else
			return -1;
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return myMap.keySet().iterator();
		//return null;
	}

	@Override
	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return myMap.containsKey(arg0);
	}

	@Override
	public IMapEntry entryAt(Object arg0) {
		// TODO Auto-generated method stub
		//return myMap.
		return null;
	}

	@Override
	public IPersistentCollection cons(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return myMap.size();
	}

	@Override
	public IPersistentCollection empty() {
		// TODO Auto-generated method stub
		myMap.clear();
		return this;
	}

	@Override
	public boolean equiv(Object arg0) {
		// TODO Auto-generated method stub
		return myMap.equals(arg0);
	}

	@Override
	public ISeq seq() {
		// TODO Auto-generated method stub		
		return null;
		//List l = new List();
		//l.addAll( myMap.keySet() );
		//ISeq s = (ISeq) PersistentList.create( l );
		//return s;
		//s.addAll( myMap.keySet() );
		//return s;
	}

	@Override
	public Object valAt(Object arg0) {
		// TODO Auto-generated method stub
		return myMap.get(arg0);
	}

	@Override
	public Object valAt(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void destroy( Engine e ) {		
		//shape.destroy();
		//System.out.println( "[A]Number of objects in collision space : " + e.physics.space.getNumGeoms() );
		e.physics.space.remove( geom );
		//System.out.println( "[B]Number of objects in collision space : " + e.physics.space.getNumGeoms() );
		//body.destroy();
		geom.destroy();		
	}

	/* Serialization stuff */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		 out.defaultWriteObject();
	}
		     
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
	
	
}

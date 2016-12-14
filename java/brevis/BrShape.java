
package brevis;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DMass;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DTriMeshData;
import org.ode4j.ode.OdeHelper;

import cleargl.GLVector;
import scenery.Mesh;

public class BrShape implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1469388870902925210L;

	public enum BrShapeType {
		BOX, SPHERE, CONE, CYLINDER, MESH,
		// Unit meshes for optimized rendering
		UNIT_CONE, UNIT_SPHERE, //FLOOR
		ICOSAHEDRON, PRISM
	};
	
	static public String objDir = "obj" + File.separator;
	
	public BrShapeType type;
	public GLVector dim;
	public int vertBID = -1;
	public int colBID = -1;
	public int idxBID = -1;
	public int numIdx = 0;
	public Mesh mesh = null;
	public Object data = null;
	
	public GLVector center;
	
	public void resize( GLVector newDim ) {
		dim = newDim;
		// should reload shoul
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public String toString() {
		String s = "#BrShape{ :type " + type + ", :dim" + dim +
				", :mesh " + mesh + "}";		 				
		return s;
	}
	
	void computeCenter() {
		if( type == BrShapeType.BOX ) {
			center = new GLVector( ( dim.x() / 2f) , ( dim.y() / 2f ), ( dim.z() / 2f ) );
		} else if( type == BrShapeType.SPHERE ) {
			//center = new Vector3f( dim.x, dim.x, dim.x );
			center = new GLVector( 0, 0, 0 );
		} else {
			center = new GLVector( 0, 0, 0 );
		}
	}
	
	BrShape( BrShapeType t, GLVector d, boolean withGraphics ) {
		//type = BrShapeType.SPHERE;
		//dim = new Vector3d(1,1,1);
		type = t;
		dim = d;
		
		if( type == BrShapeType.SPHERE ) {
			createMesh( withGraphics );
		} else if( type == BrShapeType.CYLINDER ) {
			createMesh( withGraphics );
		} else if( type == BrShapeType.CONE ) {
			createMesh( withGraphics );
			/*if( withGraphics )
				opengldrawtolist();*/
		} else if( type == BrShapeType.BOX ) {
			createMesh( withGraphics );
		} else {
			createMesh( withGraphics );
			if( mesh != null ) {
				//mesh.rescaleMesh( (float)dim.x(), (float)dim.y(), (float)dim.z(), withGraphics );
			}
			dim = new GLVector( 1, 1, 1 );
		}
				
		computeCenter();
	}
	
	BrShape( String filename, boolean isResource, boolean withGraphics ) {		
		type = BrShapeType.MESH;
		//createMesh( withGraphics );
		loadMesh( filename, isResource, withGraphics );
		if( mesh != null ) {
			//mesh.rescaleMesh( (float)dim.x(), (float)dim.y(), (float)dim.z(), withGraphics );
		}
		computeCenter();
		//loadMesh( filename, isResource, withGraphics );
	}
	
	BrShape( String filename, boolean isResource, boolean withGraphics, GLVector d ) {		
		type = BrShapeType.MESH;
		dim = d;
		//createMesh( withGraphics );
		loadMesh( filename, isResource, withGraphics );
		if( mesh != null ) {
			//mesh.rescaleMesh( (float)dim.x(), (float)dim.y(), (float)dim.z(), withGraphics );
		}
		computeCenter();
		//loadMesh( filename, isResource, withGraphics );
	}
	
	/*
	BrShape( List<GLVector> verts ) {
		type = BrShapeType.MESH;
		loadMesh( verts );
		dim = new GLVector( 1, 1, 1 );
		computeCenter();
	}
	*/
	
	
	BrShape( Mesh inMesh ) {
		type = BrShapeType.MESH;
		mesh = inMesh;
		dim = new GLVector( 1, 1, 1 );
		computeCenter();
	}

	public String getType() {
		if( type == BrShapeType.BOX ) {
			return "box";
		} else if( type == BrShapeType.SPHERE ) {
			return "sphere";
		} else if( type == BrShapeType.CONE ) {
			return "cone";			
		} else if( type == BrShapeType.CYLINDER ) {
			return "cylinder";
		} else if( type == BrShapeType.ICOSAHEDRON ) {
			return "icosahedron";
		} else if( type == BrShapeType.PRISM ) {
			return "prism";
		} else if( type == BrShapeType.MESH ) {
			return "mesh";
		} else {
			return "unknown";
		}
	}
	
	/*
	 * Return a mass that is appropriate for this object and its dimensions
	 */
	public DMass createMass( double density ) {
		DMass m = OdeHelper.createMass();
		if( type == BrShapeType.BOX ) {
			m.setBox(density, dim.x(), dim.y(), dim.z() );
		} else if( type == BrShapeType.SPHERE || type == BrShapeType.UNIT_SPHERE || type == BrShapeType.ICOSAHEDRON || type == BrShapeType.PRISM ) {
			m.setSphere( density, dim.x() );
		} else if( type == BrShapeType.CONE || type == BrShapeType.UNIT_CONE ) {
			m.setSphere(density, dim.x());
		} else if( type == BrShapeType.CYLINDER ) {
			m.setSphere(density, dim.x());
		} else if( type == BrShapeType.MESH ) {
			m.setSphere(density, dim.x() );
		}
		return m;
	}
	
	/*
	 * VBO code, currently nonfunctional
	 * 
	  public static int createVBOID() {
		  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
		    IntBuffer buffer = BufferUtils.createIntBuffer(1);
		    ARBVertexBufferObject.glGenBuffersARB(buffer);
		    return buffer.get(0);
		  }
		  return 0;
		}*/
	
	/*
	public static int createVBOID() {
	    //IntBuffer buffer = BufferUtils.createIntBuffer(1);
	    //GL15.glGenBuffers(buffer);
	    //return buffer.get(0);
	    //Or alternatively you can simply use the convenience method:
	    return GL15.glGenBuffers(); //Which can only supply you with a single id.
	}


	public static void vertexBufferData(int id, FloatBuffer buffer) { //Not restricted to FloatBuffer
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id); //Bind buffer (also specifies type of buffer)
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); //Send up the data and specify usage hint.
	}
	public static void indexBufferData(int id, IntBuffer buffer) { //Not restricted to IntBuffer
	    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
	    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	public void createVBOFromMesh( ) {
		vertBID = createVBOID();
		colBID = createVBOID();
		idxBID = createVBOID();
		numIdx = mesh.numIdx();
		
		vertexBufferData( vertBID, FloatBuffer.wrap( mesh.verts ) );
		vertexBufferData( colBID, FloatBuffer.wrap( mesh.col ) );
		indexBufferData( idxBID, IntBuffer.wrap( mesh .idx ) );
		
	}
	
	public void createMesh() {
		mesh = new BrMesh();
		
		if( type == BrShapeType.BOX ) {
			mesh.initBox( dim );			
		} else if( type == BrShapeType.SPHERE ) {
			mesh.initSphere( dim );
		} else if( type == BrShapeType.CONE ) {
			mesh.initCone( dim );
		} else if( type == BrShapeType.CYLINDER ) {
			mesh.initCylinder( dim );
		}
	}
	
	public static void bufferData(int id, FloatBuffer buffer) {
		  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
		    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
		    ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		  }
		}
*/
	
	/*public void createMesh() {		
		String filename  = "";
		if( type == BrShapeType.BOX ) {
			//mesh.initBox( dim );
			filename = "box.obj";
		} else if( type == BrShapeType.SPHERE ) {
			//mesh.initSphere( dim );
			filename = "sphere.obj";
		} else if( type == BrShapeType.CONE ) {
			//mesh.initCone( dim );
			filename = "cone.obj";
		} else if( type == BrShapeType.CYLINDER ) {
			//mesh.initCylinder( dim );
			filename = "cylinder.obj";
		}
		filename = objDir + filename;
		
		//System.out.println( "createMesh " + filename + " " + type );
		loadMesh( filename, true );
	}*/
	
	
	public void createMesh( boolean withGraphics ) {		
		String filename  = "";
		if( type == BrShapeType.BOX ) {
			//initBox( dim );
			filename = "box.obj";
		} else if( type == BrShapeType.SPHERE ) {
			//mesh.initSphere( dim );
			filename = "sphere.obj";
		} else if( type == BrShapeType.CONE ) {
			//mesh.initCone( dim );
			filename = "cone.obj";
		} else if( type == BrShapeType.CYLINDER ) {
			//mesh.initCylinder( dim );
			filename = "cylinder.obj";
		} else if( type == BrShapeType.ICOSAHEDRON ) {
			//mesh.initCylinder( dim );
			filename = "icosahedron.obj";
		} else if( type == BrShapeType.PRISM) {
			//mesh.initCylinder( dim );
			filename = "prism.obj";
		}
		//filename = objDir + filename;		
		
		//System.out.println( "createMesh " + filename + " " + type );
		

		loadMesh( filename, true, withGraphics );
	}
	
	public void createMesh( boolean withGraphics, boolean isResource ) {// this version is actually just for meshes		
		String filename  = "";
		if( type == BrShapeType.BOX ) {
			//initBox( dim );
			filename = "box.obj";
		} else if( type == BrShapeType.SPHERE ) {
			//mesh.initSphere( dim );
			filename = "sphere.obj";
		} else if( type == BrShapeType.CONE ) {
			//mesh.initCone( dim );
			filename = "cone.obj";
		} else if( type == BrShapeType.CYLINDER ) {
			//mesh.initCylinder( dim );
			filename = "cylinder.obj";
		} else if( type == BrShapeType.ICOSAHEDRON ) {
			//mesh.initCylinder( dim );
			filename = "icosahedron.obj";
		} else if( type == BrShapeType.PRISM) {
			//mesh.initCylinder( dim );
			filename = "prism.obj";
		}
		if( isResource )
			filename = objDir + filename;
		
		//System.out.println( "createMesh " + filename + " " + type );
		loadMesh( filename, isResource, withGraphics );
	}
	
	/*
	public void loadMesh( List<GLVector> verts ) {
		try {
			mesh = new BrMesh( verts );			
					
			if( mesh.numpolygons() == 0 ) {
				System.out.println("Found 0 faces when loading vert series." );
			}
			
			// this is actually size
			//dim = new Vector3d( mesh.getXWidth(), mesh.getYHeight(), mesh.getZDepth() );
			
			// this is being used for scale
			dim = new GLVector( 1, 1, 1 );
			
			//mesh.opengldrawtolist();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}	
	*/
	
	public void loadMesh( String filename, boolean isResource, boolean withGraphics ) {
		try {
			if( isResource )
				filename = objDir + filename;
			//System.out.println( "Loading object: " + filename );			
			
			if( isResource ) {
				
			}
			mesh = new Mesh();
			
			mesh.readFromOBJ( filename, false );
					
			dim = new GLVector( 1, 1, 1 );
			
			//mesh.opengldrawtolist();
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}		
	
	public DGeom createGeom( DSpace space ) throws Exception {
		DGeom g;
		
		if( mesh != null ) {
			DTriMeshData new_tmdata = OdeHelper.createTriMeshData();
			//System.out.println( "createGeom " + type );
								
			//new_tmdata.build( mesh.trimeshVertices( new float[]{ (float) dim.x(), (float) dim.y(), (float) dim.z() } ), mesh.trimeshIndices() );
			
			if( mesh.getChildren().size() > 1 )
				throw new Exception();
			
			Mesh child = (Mesh)mesh.getChildren().get(0);
			
			// Read all vertices, make a hash map and record vertex indices
			LinkedHashMap<GLVector,Integer> vertHash = new LinkedHashMap<GLVector,Integer>( );// Use hashcodes instead?
			int[] indices = new int[child.getVertices().limit()];
			for( int v = 0; v < child.getVertices().limit(); v += 3 ) {				
				GLVector vector = new GLVector( child.getVertices().get(), child.getVertices().get(), child.getVertices().get() );
				if( vertHash.containsKey( vector ) )
					vertHash.put( vector, vertHash.size() + 1 );
				indices[(int)v/3] = vertHash.get( vector );
			}

			float[] vertices = new float[vertHash.size() * child.getVertexSize()];
			
/*			for( int v = 0; v < vertHash.size(); v++ ) {
				Set<GLVector> ks = vertHash.keySet();
				vertices[v] = 
			}*/

			// Read vertices
			for( int vidx = 0; vidx < vertices.length; vidx++ ) {
				vertices[vidx] = child.getVertices().get();
			}
			
			// Read indices
			for( int iidx = 0; iidx < indices.length; iidx++ ) {
				indices[iidx] = child.getIndices().get();
			}
			
			new_tmdata.build( vertices, null );			
			
			g = OdeHelper.createTriMesh(space, new_tmdata, null, null, null);
			
			
			//g.getBody().
			
			return g;
		} 
		
		// Should be where primitive shapes are
		switch( type ) {
		case BOX:
			return OdeHelper.createBox( space, dim.x(), dim.y(), dim.z() );		
		default:
		case SPHERE:
			return OdeHelper.createSphere( space, dim.x() );			
		}		
	}
	
	public void setDimension( GLVector newDim, boolean withGraphics ) {
		dim = newDim;
		if( mesh != null ) {
			//mesh.rescaleMesh( (float)newDim.x(), (float)newDim.y(), (float)newDim.z(), withGraphics );			
			System.out.println( "not rescaling" );
		}
	}
	
	public GLVector getDimension() {
		return dim;
	}
	
	/*
	public static BrShape createMeshFromBrMesh( BrMesh inMesh ) {
		return ( new BrShape( inMesh ) );
	}
	*/
	
	public static BrShape createMeshFromFile( String filename, boolean isResource, boolean withGraphics, GLVector dim ) {
		return ( new BrShape( filename, isResource, withGraphics, dim ) );
	}
	
	/*
	public static BrShape createMeshFromTriangles( List<GLVector> verts ) {
		return ( new BrShape( verts ) );
	}
	*/
	
	public static BrShape createSphere( double r, boolean withGraphics ) {
		return ( new BrShape( BrShapeType.SPHERE, new GLVector( (float)r, (float)r, (float)r ), withGraphics ) );
	}
	
	public static BrShape createIcosahedron( double r, boolean withGraphics ) {
		return ( new BrShape( BrShapeType.ICOSAHEDRON, new GLVector( (float)r, (float)r, (float)r ), withGraphics ) );
	}
	
	public static BrShape createBox( double x, double y, double z, boolean withGraphics ) {
		return ( new BrShape( BrShapeType.BOX, new GLVector( (float)x, (float)y, (float)z ), withGraphics ) );
	}
	
	public static BrShape createCone( double length, double base, boolean withGraphics ) {
		return ( new BrShape( BrShapeType.CONE, new GLVector( (float)length, (float)base, (float)25 ), withGraphics ));	// last element of vector is # of sides or stacks (depending on renderer)
	}
	
	public static BrShape createCylinder( double length, double radius, boolean withGraphics ) {
		return ( new BrShape( BrShapeType.CYLINDER, new GLVector( (float)length, (float)radius, (float)25 ), withGraphics ));	// last element of vector is # of sides or stacks (depending on renderer)
	}
	
	public static BrShape createCylinder( double length, double radius1, double radius2, boolean withGraphics ) {
		return ( new BrShape( BrShapeType.CYLINDER, new GLVector( (float)length, (float)radius1, (float)radius2 ), withGraphics ));	// last element of vector is # of sides or stacks (depending on renderer)
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		 out.defaultWriteObject();
	}
		     
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
	
	// Drawing
	
	static public void drawBox(float w, float h, float d) {
		System.out.println( "I wish I was printing a box of " + w + "," + h + "," + d + " dimensions ");
	}
	
}

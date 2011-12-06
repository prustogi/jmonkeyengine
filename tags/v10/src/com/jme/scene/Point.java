/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.scene;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.util.LoggingSystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Point</code> defines a collection of vertices that are rendered as
 * single points.
 * 
 * @author Mark Powell
 * @version $Id: Point.java,v 1.17 2006-03-17 20:04:16 nca Exp $
 */
public class Point extends Geometry {

	private static final long serialVersionUID = 1L;

	private float pointSize = 1.0f;
	private boolean antialiased = false;

    protected transient IntBuffer indexBuffer;

	/**
	 * Constructor instantiates a new <code>Point</code> object with a given
	 * set of data. Any data may be null, except the vertex array. If this is
	 * null an exception is thrown.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param vertex
	 *            the vertices or points.
	 * @param normal
	 *            the normals of the points.
	 * @param color
	 *            the color of the points.
	 * @param texture
	 *            the texture coordinates of the points.
	 */
	public Point(String name, Vector3f[] vertex, Vector3f[] normal,
			ColorRGBA[] color, Vector2f[] texture) {

		super(name, 
		        BufferUtils.createFloatBuffer(vertex), 
		        BufferUtils.createFloatBuffer(normal), 
		        BufferUtils.createFloatBuffer(color), 
		        BufferUtils.createFloatBuffer(texture));
        generateIndices();
		LoggingSystem.getLogger().log(Level.INFO, "Point created.");
	}

	/**
	 * Constructor instantiates a new <code>Point</code> object with a given
	 * set of data. Any data may be null, except the vertex array. If this is
	 * null an exception is thrown.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param vertex
	 *            the vertices or points.
	 * @param normal
	 *            the normals of the points.
	 * @param color
	 *            the color of the points.
	 * @param texture
	 *            the texture coordinates of the points.
	 */
	public Point(String name, FloatBuffer vertex, FloatBuffer normal,
			FloatBuffer color, FloatBuffer texture) {

		super(name, vertex, normal, color, texture);
        generateIndices();
		LoggingSystem.getLogger().log(Level.INFO, "Point created.");
	}

	/**
	 * <code>draw</code> calls super to set the render state. After this state
	 * is set, the points are sent to the renderer for display.
	 * 
	 * @param r
	 *            the renderer used for displaying the data.
	 */
	public void draw(Renderer r) {
		if (!r.isProcessingQueue()) {
			if (r.checkAndAdd(this))
				return;
		}
		super.draw(r);
		r.draw(this);
	}

    public void generateIndices() {
        if (indexBuffer == null || indexBuffer.capacity() != batch.getVertQuantity()) {
            indexBuffer = BufferUtils.createIntBuffer(batch.getVertQuantity());
        } else
            indexBuffer.rewind();

        for (int x = 0; x < batch.getVertQuantity(); x++)
            indexBuffer.put(x);
    }
    
    /**
     * 
     * <code>getIndexAsBuffer</code> retrieves the indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    /**
     * 
     * <code>setIndexBuffer</code> sets the index array for this
     * <code>Point</code>.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    public void setIndexBuffer(IntBuffer indices) {
        this.indexBuffer = indices;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
	 *      com.jme.intersection.CollisionResults)
	 */
	public void findCollisions(Spatial scene, CollisionResults results) {
		// TODO Auto-generated method stub

	}
	
	public boolean hasCollision(Spatial scene, boolean checkTriangles) {
		return false;
	}
	
    /**
     * @return true if points are to be drawn antialiased
     */
    public boolean isAntialiased() {
        return antialiased;
    }
    
    /**
     * Sets whether the point should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SB_SRC_ALPHA and a destination of DB_ONE_MINUS_SRC_ALPHA or
     * DB_ONE.
     * 
     * @param antiAliased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
    }

    /**
     * @return the pixel size of each point.
     */
    public float getPointSize() {
        return pointSize;
    }

    /**
     * Sets the pixel width of the point when drawn. Non anti-aliased point
     * sizes are rounded to the nearest whole number by opengl.
     * 
     * @param size
     *            The size to set.
     */
    public void setPointSize(float size) {
        this.pointSize = size;
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (indexBuffer == null)
            s.writeInt(0);
        else {
            s.writeInt(indexBuffer.capacity());
            indexBuffer.rewind();
            for (int x = 0, len = indexBuffer.capacity(); x < len; x++)
                s.writeInt(indexBuffer.get());
        }
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int len = s.readInt();
        if (len == 0) {
            setIndexBuffer(null);
        } else {
            IntBuffer buf = BufferUtils.createIntBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readInt());
            setIndexBuffer(buf);            
        }
    }
}
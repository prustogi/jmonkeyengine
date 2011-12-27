/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BezierMesh;
import com.jme.scene.BezierPatch;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestBezierMesh.java,v 1.11 2004-04-02 15:52:15 mojomonkey Exp $
 */
public class TestBezierMesh extends SimpleGame {
    private TriMesh t;
    private Camera cam;
    private Node root;
    private Node scene;
    private InputHandler input;
    private Thread thread;
    private LightState lightstate;
    private PointLight pl;
    private LightNode lightNode;
    private Vector3f currentPos;
    private Vector3f newPos;
    private Timer timer;
    private WireframeState wf;

    /**
     * Entry point for the test, 
     * @param args
     */
    public static void main(String[] args) {
        TestBezierMesh app = new TestBezierMesh();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();

    }

    /**
     * Not used in this test.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
//      update world data
        timer.update();
        input.update(timer.getTimePerFrame() * 10);
        
        
        //update individual sprites
         if ((int) currentPos.x == (int) newPos.x
             && (int) currentPos.y == (int) newPos.y
             && (int) currentPos.z == (int) newPos.z) {
             newPos.x = (float) Math.random() * 10 - 5;
             newPos.y = (float) Math.random() * 10 - 5;
             newPos.z = (float) Math.random() * 10 - 5;
         }

         currentPos.x -= (currentPos.x - newPos.x)
             / (timer.getFrameRate() / 2);
         currentPos.y -= (currentPos.y - newPos.y)
             / (timer.getFrameRate() / 2);
         currentPos.z -= (currentPos.z - newPos.z)
             / (timer.getFrameRate() / 2);
             
         lightNode.setLocalTranslation(currentPos);

         scene.updateGeometricState(timer.getTimePerFrame(), true);
    }

    /** 
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);
       

    }

    /**
     * creates the displays and sets up the viewport.
     * @see com.jme.app.SimpleGame#initSystem()
     */
    protected void initSystem() {
        
        currentPos = new Vector3f();
        newPos = new Vector3f();
        try {
            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            display.createWindow(
                properties.getWidth(),
                properties.getHeight(),
                properties.getDepth(),
                properties.getFreq(),
                properties.getFullscreen());
            cam =
                display.getRenderer().getCamera(
                    properties.getWidth(),
                    properties.getHeight());

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 5.0f);
        Vector3f left = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonHandler(this, cam, "LWJGL");
        timer = Timer.getTimer("LWJGL");
        display.setTitle("Bezier Mesh Test");
    }

    /** 
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);

        scene = new Node("3D Scene Node");
        root = new Node("Scene Root Node");
        root.attachChild(scene);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        scene.setRenderState(buf);
        cam.update();

        BezierPatch bp = new BezierPatch();
        bp.setAnchor(0, 0, new Vector3f(-0.75f, -0.75f, -0.5f));
        bp.setAnchor(0, 1, new Vector3f(-0.25f, -0.75f, 0.0f));
        bp.setAnchor(0, 2, new Vector3f(0.25f, -0.75f, 0.5f));
        bp.setAnchor(0, 3, new Vector3f(0.75f, -0.75f, 0.5f));
        bp.setAnchor(1, 0, new Vector3f(-0.75f, -0.25f, -0.75f));
        bp.setAnchor(1, 1, new Vector3f(-0.25f, -0.25f, 0.5f));
        bp.setAnchor(1, 2, new Vector3f(0.25f, -0.25f, 0.5f));
        bp.setAnchor(1, 3, new Vector3f(0.75f, -0.25f, 0.75f));
        bp.setAnchor(2, 0, new Vector3f(-0.75f, 0.25f, -0.5f));
        bp.setAnchor(2, 1, new Vector3f(-0.25f, 0.25f, -0.5f));
        bp.setAnchor(2, 2, new Vector3f(0.25f, 0.25f, -0.5f));
        bp.setAnchor(2, 3, new Vector3f(0.75f, 0.25f, 0.0f));
        bp.setAnchor(3, 0, new Vector3f(-0.75f, 0.75f, -0.5f));
        bp.setAnchor(3, 1, new Vector3f(-0.25f, 0.75f, -1.0f));
        bp.setAnchor(3, 2, new Vector3f(0.25f, 0.75f, -1.0f));
        bp.setAnchor(3, 3, new Vector3f(0.75f, 0.75f, -0.5f));
        bp.setDetailLevel(100);

        BezierMesh bez = new BezierMesh("Bezier Mesh");
        bez.setPatch(bp);
        bez.setModelBound(new BoundingSphere());
        bez.updateModelBound();
        scene.attachChild(bez);
        
        MaterialState ms = display.getRenderer().getMaterialState();
        ms.setEmissive(new ColorRGBA(0,0,0.4f,1.0f));
        ms.setAmbient(new ColorRGBA(0.5f,0.5f,0.5f,1.0f));
        ms.setDiffuse(new ColorRGBA(1.0f,0.85f,0.75f,1.0f));
        ms.setSpecular(new ColorRGBA(0.8f,0.8f,0.8f,1.0f));
        ms.setShininess(128.0f);
        ms.setEnabled(true);
        bez.setRenderState(ms);

        pl = new PointLight();
        pl.setAmbient(new ColorRGBA(0, 0, 0, 1));
        pl.setDiffuse(new ColorRGBA(1, 1, 1, 1));
        pl.setSpecular(new ColorRGBA(0, 0, 1, 1));
        pl.setEnabled(true);

        lightstate = display.getRenderer().getLightState();
        lightstate.setTwoSidedLighting(true);
        lightstate.setEnabled(true);
        lightNode = new LightNode("Light Node",lightstate);
        lightNode.setLight(pl);
        lightNode.setTarget(bez);
        
        Vector3f min = new Vector3f(-0.15f, -0.15f, -0.15f);
        Vector3f max = new Vector3f(0.15f,0.15f,0.15f);
        Box lightBox = new Box("box", min,max);
        lightBox.setModelBound(new BoundingSphere());
        lightBox.updateModelBound();
        
        
        lightNode.attachChild(lightBox);
        lightNode.setForceView(true);
        
        scene.attachChild(lightNode);
        
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                TestBezierMesh.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));

        bez.setRenderState(ts);

        scene.updateGeometricState(0.0f, true);

    }
    /**
     * not used.
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {

    }

    /** 
     * Not used.
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {

    }

}
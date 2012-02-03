package com.jme3.scene.plugins;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialList;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

public class MTLLoader implements AssetLoader {

    protected Scanner scan;
    protected MaterialList matList;
    protected Material material;
    protected AssetManager assetManager;
    protected String folderName;

    public void reset(){
        scan = null;
        matList = null;
        material = null;
    }

    protected ColorRGBA readColor(){
        ColorRGBA v = new ColorRGBA();
        v.set(scan.nextFloat(), scan.nextFloat(), scan.nextFloat(), 1.0f);
        return v;
    }

    protected void nextStatement(){
        scan.useDelimiter("\n");
        scan.next();
        scan.useDelimiter("\\p{javaWhitespace}+");
    }

    protected void startMaterial(String name){
        material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("m_UseMaterialColors", true);
        material.setColor("m_Ambient",  ColorRGBA.Black);
        material.setColor("m_Diffuse",  ColorRGBA.White);
        material.setColor("m_Specular", ColorRGBA.Black);
//        material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        matList.put(name, material);
    }

    protected boolean readLine(){
        if (!scan.hasNext()){
            return false;
        }

        String cmd = scan.next();
        if (cmd.startsWith("#")){
            // skip entire comment until next line
        }else if (cmd.equals("newmtl")){
            String name = scan.next();
            startMaterial(name);
        }else if (cmd.equals("Ka") || cmd.equals("Ke") || cmd.equals("Ni") || cmd.equals("illum")){
            // ignore it for now
        }else if (cmd.equals("Kd")){
            ColorRGBA color = readColor();
            MatParam param = material.getParam("m_Diffuse");
            if (param != null){
                color.a = ((ColorRGBA) param.getValue()).getAlpha();
            }
            material.setColor("m_Diffuse", color);
        }else if (cmd.equals("Ks")){
            material.setColor("m_Specular", readColor());
        }else if (cmd.equals("Ns")){
            material.setFloat("m_Shininess", scan.nextFloat() /* (128f / 1000f)*/ );
        }else if (cmd.equals("d")){
            float alpha = scan.nextFloat();
//            if (alpha < 1f){
//                MatParam param = material.getParam("m_Diffuse");
//                ColorRGBA color;
//                if (param != null)
//                    color = (ColorRGBA) param.getValue();
//                else
//                    color = new ColorRGBA(ColorRGBA.White);
//
//                color.a = scan.nextFloat();
//                material.setColor("m_Diffuse", color);
//                material.setBoolean("m_UseAlpha", true);
//                material.setTransparent(true);
//                material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//            }
        }else if (cmd.equals("map_Ka")){
            // ignore it for now
        }else if (cmd.equals("map_Kd")){
            String path = scan.next();
            String name = new File(path).getName();
            TextureKey key = new TextureKey(folderName + name);
            key.setGenerateMips(true);
            Texture texture = assetManager.loadTexture(key);
            if (texture != null){
                texture.setWrap(WrapMode.Repeat);
                material.setTexture("m_DiffuseMap", texture);
            }
        }else if (cmd.equals("map_bump") || cmd.equals("bump")){
            if (material.getParam("m_NormalMap") == null){
                String path = scan.next();
                String name = new File(path).getName();
                TextureKey key = new TextureKey(folderName + name);
                key.setGenerateMips(true);
                Texture texture = assetManager.loadTexture(key);
                if (texture != null){
                    texture.setWrap(WrapMode.Repeat);
                    material.setTexture("m_NormalMap", texture);
                    if (texture.getImage().getFormat() == Format.LATC){
                        material.setBoolean("m_LATC", true);
                    }
                }
            }
        }else if (cmd.equals("map_Ks")){
            String path = scan.next();
            String name = new File(path).getName();
            TextureKey key = new TextureKey(folderName + name);
            key.setGenerateMips(true);
            Texture texture = assetManager.loadTexture(key);
            if (texture != null){
                texture.setWrap(WrapMode.Repeat);
                material.setTexture("m_SpecularMap", texture);

                // NOTE: since specular color is modulated with specmap
                // make sure we have it set
                material.setColor("m_Specular", ColorRGBA.White);
            }
        }else if (cmd.equals("map_d")){
            String path = scan.next();
            String name = new File(path).getName();
            TextureKey key = new TextureKey(folderName + name);
            key.setGenerateMips(true);
            Texture texture = assetManager.loadTexture(key);
            if (texture != null){
                texture.setWrap(WrapMode.Repeat);
                material.setTexture("m_AlphaMap", texture);
                material.setTransparent(true);
                material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                material.getAdditionalRenderState().setAlphaTest(true);
                material.getAdditionalRenderState().setAlphaFallOff(0.01f);
            }
        }else{
            System.out.println("Unknown statement in MTL! "+cmd);
        }
        nextStatement();

        return true;
    }

    @SuppressWarnings("empty-statement")
    public Object load(AssetInfo info){
        this.assetManager = info.getManager();
        folderName = info.getKey().getFolder();

        InputStream in = info.openStream();
        scan = new Scanner(in);
        scan.useLocale(Locale.US);

        matList = new MaterialList();
        while (readLine());
        MaterialList list = matList;

        reset();

        try{
            in.close();
        }catch (IOException ex){
        }
        return list;
    }
}
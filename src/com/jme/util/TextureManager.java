/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
package com.jme.util;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import com.jme.image.BitmapHeader;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
/**
 * 
 * <code>TextureManager</code> provides static methods for building a
 * <code>Texture</code> object. Typically, the information supplied is the
 * filename and the texture properties.
 * 
 * @author Mark Powell
 * @version $Id: TextureManager.java,v 1.18 2004-05-28 16:12:46 mojomonkey Exp $
 */
public class TextureManager {
	// For TGA loading
	private static final int NO_TRANSPARENCY = 255;
	private static final int FULL_TRANSPARENCY = 0;
	private static short idLength;
	private static short colorMapType;
	private static short imageType;
	private static int cMapStart;
	private static int cMapLength;
	private static short cMapDepth;
	private static int xOffset;
	private static int yOffset;
	private static int width;
	private static int height;
	private static short pixelDepth;
	private static short imageDescriptor;
	private static DirectColorModel cm;
	private static int[] pixels;
	/**
	 * <code>loadTexture</code> loads a new texture defined by the parameter
	 * string. Filter parameters are used to define the filtering of the
	 * texture. Whether the texture is to be mipmapped or not is denoted by the
	 * isMipmapped boolean flag. If there is an error loading the file, null is
	 * returned.
	 * 
	 * @param file
	 *            the filename of the texture image.
	 * @param minFilter
	 *            the filter for the near values.
	 * @param magFilter
	 *            the filter for the far values.
	 * @param isMipmapped
	 *            determines if we will load the texture mipmapped or not. True
	 *            load the texture mipmapped, false do not.
	 * @param flipped
	 *            true flips the bits of the image, false does not. True by
	 *            default.
	 * 
	 * @return the loaded texture. If there is a problem loading the texture,
	 *         null is returned.
	 */
	public static com.jme.image.Texture loadTexture(String file, int minFilter,
			int magFilter, boolean isMipMapped) {
		return loadTexture(file, minFilter, magFilter, isMipMapped, true);
	}
	/**
	 * <code>loadTexture</code> loads a new texture defined by the parameter
	 * string. Filter parameters are used to define the filtering of the
	 * texture. Whether the texture is to be mipmapped or not is denoted by the
	 * isMipmapped boolean flag. If there is an error loading the file, null is
	 * returned.
	 * 
	 * @param file
	 *            the filename of the texture image.
	 * @param minFilter
	 *            the filter for the near values.
	 * @param magFilter
	 *            the filter for the far values.
	 * @param isMipmapped
	 *            determines if we will load the texture mipmapped or not. True
	 *            load the texture mipmapped, false do not.
	 * 
	 * @return the loaded texture. If there is a problem loading the texture,
	 *         null is returned.
	 */
	public static com.jme.image.Texture loadTexture(String file, int minFilter,
			int magFilter, boolean isMipmapped, boolean flipped) {
		URL url = null;
		try {
			url = new URL("file:" + file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return loadTexture(url, minFilter, magFilter, isMipmapped, flipped);
	}
	/**
	 * <code>loadTexture</code> loads a new texture defined by the parameter
	 * url. Filter parameters are used to define the filtering of the texture.
	 * Whether the texture is to be mipmapped or not is denoted by the
	 * isMipmapped boolean flag. If there is an error loading the file, null is
	 * returned.
	 * 
	 * @param file
	 *            the url of the texture image.
	 * @param minFilter
	 *            the filter for the near values.
	 * @param magFilter
	 *            the filter for the far values.
	 * @param isMipmapped
	 *            determines if we will load the texture mipmapped or not. True
	 *            load the texture mipmapped, false do not.
	 * @param flipped
	 *            true flips the bits of the image, false does not. True by
	 *            default.
	 * 
	 * @return the loaded texture. If there is a problem loading the texture,
	 *         null is returned.
	 */
	public static com.jme.image.Texture loadTexture(URL file, int minFilter,
			int magFilter, boolean isMipMapped) {
		return loadTexture(file, minFilter, magFilter, isMipMapped, true);
	}
	/**
	 * <code>loadTexture</code> loads a new texture defined by the parameter
	 * url. Filter parameters are used to define the filtering of the texture.
	 * Whether the texture is to be mipmapped or not is denoted by the
	 * isMipmapped boolean flag. If there is an error loading the file, null is
	 * returned.
	 * 
	 * @param file
	 *            the url of the texture image.
	 * @param minFilter
	 *            the filter for the near values.
	 * @param magFilter
	 *            the filter for the far values.
	 * @param isMipmapped
	 *            determines if we will load the texture mipmapped or not. True
	 *            load the texture mipmapped, false do not.
	 * 
	 * @return the loaded texture. If there is a problem loading the texture,
	 *         null is returned.
	 */
	public static com.jme.image.Texture loadTexture(URL file, int minFilter,
			int magFilter, boolean isMipmapped, boolean flipped) {
		if (null == file) {
			return null;
		}
		String fileName = file.getFile();
		if (fileName == null)
			return null;
		/*
		 * // Debugging code; rename every texture request as a TGA file instead
		 * 
		 * try { String URLstring = file.toString(); URLstring =
		 * URLstring.substring(0,URLstring.lastIndexOf('.')) + ".tga"; file =
		 * new URL(URLstring); fileName = file.getFile(); } catch(Exception e) {
		 * e.printStackTrace(); }
		 *  // Debugging code; validate existence of file as a seperate step
		 * 
		 * try { InputStream fileStream = file.openStream(); fileStream.close(); }
		 * catch(IOException e) { LoggingSystem.getLogger().log(Level.WARNING,
		 * "File existence check failed: " + file); return null; }
		 */
		// Changed: It's the responsibility of each case below to ultimately
		// create the com.jme.image.Image imageData object.
		// Changed: Exact the file extension only once.
		// TODO: Some types currently require making a java.awt.Image object as
		// an
		// intermediate step. Rewrite each type to avoid AWT at all costs.
		com.jme.image.Image imageData = null;
		try {
			String fileExt = fileName.substring(fileName.lastIndexOf('.'));
			if (".TGA".equalsIgnoreCase(fileExt)) // TGA, direct to imageData
			{
				imageData = TGAtoJMEImage(file.openStream());
			} else if (".BMP".equalsIgnoreCase(fileExt)) // BMP, awtImage to
														 // imageData
			{
				java.awt.Image image = loadBMPImage(file.openStream());
				imageData = loadImage(image, flipped);
			} else // Anything else
			{
				java.awt.Image image = ImageIO.read(file);
				imageData = loadImage(image, flipped);
			}
		} catch (IOException e) {
			e.printStackTrace();
			LoggingSystem.getLogger().log(Level.WARNING,
					"(IOException) Could not load: " + file);
			return null;
		}
		if (null == imageData) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"(image null) Could not load: " + file);
			return null;
		}
		Texture texture = new Texture();
		texture.setApply(Texture.AM_MODULATE);
		texture.setBlendColor(new ColorRGBA(1, 1, 1, 1));
		texture.setCorrection(Texture.CM_PERSPECTIVE);
		texture.setFilter(magFilter);
		texture.setImage(imageData);
		texture.setMipmapState(minFilter);
		return texture;
	}
	public static com.jme.image.Texture loadTexture(java.awt.Image image,
			int minFilter, int magFilter, boolean isMipmapped, boolean flipped) {
		com.jme.image.Image imageData = loadImage(image, flipped);
		Texture texture = new Texture();
		texture.setApply(Texture.AM_MODULATE);
		texture.setBlendColor(new ColorRGBA(1, 1, 1, 1));
		texture.setCorrection(Texture.CM_PERSPECTIVE);
		texture.setFilter(magFilter);
		texture.setImage(imageData);
		texture.setMipmapState(minFilter);
		return texture;
	}
	/**
	 * 
	 * <code>loadImage</code> sets the image data.
	 * 
	 * @param image
	 *            The image data.
	 * @param flipImage
	 *            if true will flip the image's y values.
	 * @return the loaded image.
	 */
	public static com.jme.image.Image loadImage(java.awt.Image image,
			boolean flipImage) {
		boolean hasAlpha = hasAlpha(image);
		//      Obtain the image data.
		BufferedImage tex = null;
		try {
			tex = new BufferedImage(image.getWidth(null),
					image.getHeight(null), hasAlpha
							? BufferedImage.TYPE_4BYTE_ABGR
							: BufferedImage.TYPE_3BYTE_BGR);
		} catch (IllegalArgumentException e) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Problem creating buffered Image: " + e.getMessage());
			return null;
		}
		Graphics2D g = (Graphics2D) tex.getGraphics();
		g.drawImage(image, null, null);
		g.dispose();
		if (flipImage) {
			//Flip the image
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx,
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			tex = op.filter(tex, null);
		}
		//Get a pointer to the image memory
		ByteBuffer scratch = ByteBuffer.allocateDirect(4 * tex.getWidth()
				* tex.getHeight());
		byte data[] = (byte[]) tex.getRaster().getDataElements(0, 0,
				tex.getWidth(), tex.getHeight(), null);
		scratch.clear();
		scratch.put(data);
		scratch.rewind();
		com.jme.image.Image textureImage = new com.jme.image.Image();
		textureImage.setType(hasAlpha
				? com.jme.image.Image.RGBA8888
				: com.jme.image.Image.RGB888);
		textureImage.setWidth(tex.getWidth());
		textureImage.setHeight(tex.getHeight());
		textureImage.setData(scratch);
		return textureImage;
	}
	/**
	 * <code>loadBMPImage</code> because bitmap is not directly supported by
	 * Java, we must load it manually. The requires opening a stream to the file
	 * and reading in each byte. After the image data is read, it is used to
	 * create a new <code>Image</code> object. This object is returned to be
	 * used for normal use.
	 * 
	 * @param file
	 *            the name of the bitmap file.
	 * 
	 * @return <code>Image</code> object that contains the bitmap information.
	 */
	private static java.awt.Image loadBMPImage(InputStream fs) {
		try {
			DataInputStream dis = new DataInputStream(fs);
			BitmapHeader bh = new BitmapHeader();
			byte[] data = new byte[dis.available()];
			dis.readFully(data);
			dis.close();
			bh.read(data);
			if (bh.bitcount == 24) {
				return (bh.readMap24(data));
			}
			if (bh.bitcount == 32) {
				return (bh.readMap32(data));
			}
			if (bh.bitcount == 8) {
				return (bh.readMap8(data));
			}
		} catch (IOException e) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Error while loading bitmap texture.");
		}
		return null;
	}
	/**
	 * <code>flipEndian</code> is used to flip the endian bit of the header
	 * file.
	 * 
	 * @param signedShort
	 *            the bit to flip.
	 * 
	 * @return the flipped bit.
	 */
	private static short flipEndian(short signedShort) {
		int input = signedShort & 0xFFFF;
		return (short) (input << 8 | (input & 0xFF00) >>> 8);
	}
	/**
	 * <code>hasAlpha</code> returns true if the specified image has
	 * transparent pixels
	 * 
	 * @param image
	 *            Image to check
	 * @return true if the specified image has transparent pixels
	 */
	public static boolean hasAlpha(java.awt.Image image) {
		if (null == image) {
			return false;
		}
		if (image instanceof BufferedImage) {
			BufferedImage bufferedImage = (BufferedImage) image;
			return bufferedImage.getColorModel().hasAlpha();
		}
		PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pixelGrabber.grabPixels();
			ColorModel colorModel = pixelGrabber.getColorModel();
			if (colorModel != null) {
				return colorModel.hasAlpha();
			} else {
				return false;
			}
		} catch (InterruptedException e) {
		}
		return false;
	}
	/**
	 * <code>TGAtoJMEImage</code> is a manual image loader which is entirely
	 * independent of AWT.
	 * 
	 * OUT: RGB8888 or RGBA8888 jme.image.Image object
	 * 
	 * @param fis
	 *            InputStream of an uncompressed 24b RGB or 32b RGBA TGA
	 * 
	 * @return <code>com.jme.image.Image</code> object that contains the
	 *         image, either as a RGB888 or RGBA8888
	 */
	public static com.jme.image.Image TGAtoJMEImage(InputStream fis)
			throws IOException {
		byte red = 0;
		byte green = 0;
		byte blue = 0;
		byte alpha = 0;
		//open a stream to the file
		BufferedInputStream bis = new BufferedInputStream(fis, 8192);
		DataInputStream dis = new DataInputStream(bis);
		//Read the TGA header
		idLength = (short) dis.read();
		colorMapType = (short) dis.read();
		imageType = (short) dis.read();
		cMapStart = flipEndian(dis.readShort());
		cMapLength = flipEndian(dis.readShort());
		cMapDepth = (short) dis.read();
		xOffset = flipEndian(dis.readShort());
		yOffset = flipEndian(dis.readShort());
		width = flipEndian(dis.readShort());
		height = flipEndian(dis.readShort());
		pixelDepth = (short) dis.read();
		imageDescriptor = (short) dis.read();
		//Skip image ID
		if (idLength > 0)
			bis.skip(idLength);
		// Allocate image data array
		byte[] rawData = null;
		if (pixelDepth == 32)
			rawData = new byte[width * height * 4];
		else
			rawData = new byte[width * height * 3];
		int rawDataIndex = 0;
		// Faster than doing a 24-or-32 check on each individual pixel,
		// just make a seperate loop for each.
		if (pixelDepth == 24)
			for (int i = 0; i <= (height - 1); i++) {
				for (int j = 0; j < width; j++) {
					blue = dis.readByte();
					green = dis.readByte();
					red = dis.readByte();
					rawData[rawDataIndex++] = (byte) red;
					rawData[rawDataIndex++] = (byte) green;
					rawData[rawDataIndex++] = (byte) blue;
				}
			}
		else if (pixelDepth == 32)
			for (int i = 0; i <= (height - 1); i++) {
				for (int j = 0; j < width; j++) {
					blue = dis.readByte();
					green = dis.readByte();
					red = dis.readByte();
					alpha = dis.readByte();
					rawData[rawDataIndex++] = (byte) red;
					rawData[rawDataIndex++] = (byte) green;
					rawData[rawDataIndex++] = (byte) blue;
					rawData[rawDataIndex++] = (byte) alpha;
				}
			}
		fis.close();
		//Get a pointer to the image memory
		ByteBuffer scratch = ByteBuffer.allocateDirect(rawData.length);
		scratch.clear();
		scratch.put(rawData);
		scratch.rewind();
		// Create the jme.image.Image object
		com.jme.image.Image textureImage = new com.jme.image.Image();
		if (pixelDepth == 32)
			textureImage.setType(com.jme.image.Image.RGBA8888);
		else
			textureImage.setType(com.jme.image.Image.RGB888);
		textureImage.setWidth(width);
		textureImage.setHeight(height);
		textureImage.setData(scratch);
		return textureImage;
	}
}
// Code Graveyard
/**
 * <code>loadTGAImage</code> because targa is not directly supported by Java,
 * we must load it manually. The requires opening a stream to the file and
 * reading in each byte. After the image data is read, it is used to create a
 * new <code>Image</code> object. This object is returned to be used for
 * normal use.
 * 
 * @param file
 *            the name of the targa file.
 * 
 * @return <code>Image</code> object that contains the targa information.
 */
/*
 * private static java.awt.Image loadTGAImage(InputStream fis) { try {
 * System.out.println("Loading TGA Image...");
 * 
 * int red = 0; int green = 0; int blue = 0; int srcLine = 0; int alpha =
 * FULL_TRANSPARENCY;
 * 
 * //open a stream to the file BufferedInputStream bis = new
 * BufferedInputStream(fis, 8192); DataInputStream dis = new
 * DataInputStream(bis);
 * 
 * //Read the TGA header idLength = (short) dis.read(); colorMapType = (short)
 * dis.read(); imageType = (short) dis.read(); cMapStart =
 * flipEndian(dis.readShort()); cMapLength = flipEndian(dis.readShort());
 * cMapDepth = (short) dis.read(); xOffset = flipEndian(dis.readShort());
 * yOffset = flipEndian(dis.readShort()); width = flipEndian(dis.readShort());
 * height = flipEndian(dis.readShort()); pixelDepth = (short) dis.read();
 * 
 * System.out.println("Loading TGA Image... 1");
 * 
 * if (pixelDepth == 24) { cm = new DirectColorModel(24, 0xFF0000, 0xFF00,
 * 0xFF); } else if (pixelDepth == 32) { cm = new DirectColorModel(32,
 * 0xFF000000, 0xFF0000, 0xFF00, 0xFF); }
 * 
 * System.out.println("Loading TGA Image... 1a");
 * 
 * imageDescriptor = (short) dis.read();
 * 
 * System.out.println("Loading TGA Image... 2");
 * 
 * //Skip image ID if (idLength > 0) { bis.skip(idLength); }
 * 
 * //create the buffer for the image data. pixels = new int[width * height];
 * 
 * System.out.println("Loading TGA Image... 3");
 * 
 * //read the pixel data. for (int i = (height - 1); i >= 0; i--) { srcLine = i *
 * width;
 * 
 * for (int j = 0; j < width; j++) { blue = bis.read() & 0xFF; green =
 * bis.read() & 0xFF; red = bis.read() & 0xFF;
 * 
 * if (pixelDepth == 32) { alpha = bis.read() & 0xFF; pixels[srcLine + j] =
 * alpha < < 24 | red < < 16 | green < < 8 | blue; } else { pixels[srcLine + j] =
 * red < < 16 | green < < 8 | blue; } } }
 * 
 * System.out.println("Loading TGA Image... 4");
 * 
 * //Close the file, we are done. fis.close(); // } catch (IOException e) { //
 * LoggingSystem.getLogger().log(Level.WARNING, // "Unable to load TGA image."); }
 * catch (Exception e) { e.printStackTrace(); }
 * 
 * System.out.println("Done loading TGA");
 * 
 * //create the Image object and return it. return
 * Toolkit.getDefaultToolkit().createImage( new MemoryImageSource(width, height,
 * cm, pixels, 0, width)); }
 */

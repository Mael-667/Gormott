package GormottEngine;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;

public class Utils {

	public static final int floatSize = 4;


    public static String loadFile(String url){
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(url)));
        } catch (IOException excp) {
            throw new RuntimeException("Error reading file [" + url + "]", excp);
        }
        return str;
    }

    public static void setIcon(String iconUrl, long window){
        if(iconUrl != null){
			try {
				//charge l'image et met chaque pixel dans un array
				BufferedImage image = ImageIO.read(new File(iconUrl));
				int[] pixelArray = new int[image.getWidth()*image.getHeight()];
				image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixelArray, 0, image.getWidth());
				//transform le int array en bytebuffer dans un mode qui plait a glwf
				ByteBuffer bb = BufferUtils.createByteBuffer(pixelArray.length * 4);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				for(int i: pixelArray){
					int a = (i >> 24) & 0xFF;
					int r = (i >> 16) & 0xFF;
					int g = (i >> 8) & 0xFF;
					int b = i & 0xFF;

					bb.put((byte) r);
					bb.put((byte) g);
					bb.put((byte) b);
					bb.put((byte) a);
				}
				bb.flip();
				GLFWImage icon = GLFWImage.malloc();
				icon.set(image.getWidth(), image.getHeight(), bb);
				// Création du buffer d’icônes (ici 1 seul)
				GLFWImage.Buffer icons = GLFWImage.malloc(1);
				icons.put(0, icon);
				glfwSetWindowIcon(window, icons);
				icon.free();
				icons.free();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

	
	public static void bindNewShader(int shaderProgramId, int GlShaderType, String url){
		int shaderId = glCreateShader(GlShaderType);
        glShaderSource(shaderId, Utils.loadFile(url));
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
		glAttachShader(shaderProgramId, shaderId);
        glDeleteShader(shaderId);
	}

	public static void time(String funName, Callback c){
		long mtn = System.nanoTime();
		
		c.execCallback();

		long fini = System.nanoTime();
		System.out.println("temps dexecution de "+funName+" "+ ((fini - mtn)/1000)+" micro secondes");
	}
}

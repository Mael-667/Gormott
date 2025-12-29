import org.joml.Matrix4f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.*;

import javax.imageio.ImageIO;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.opengl.GL30.*;

public class GlEngine {
	
	// The window handle
	private long window;
	private int wWidth, wHeight;
	private String wTitle;
	private String iconUrl;

	private final int floatSize = 4;

	public GlEngine(int wWidth, int wHeight, String wTitle, String iconUrl){
		this.wWidth = wWidth;
		this.wHeight = wHeight;
		this.wTitle = wTitle;
		this.iconUrl = iconUrl;
	}

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(wWidth, wHeight, wTitle, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		//set window icon
		Utils.setIcon(iconUrl, window);

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			// We will detect this in the rendering loop
            Input.Handler(window, key, action);
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private Mesh newRect(int l, int h){
		float max = 2f;
		int ratio = l > h ? l : h;
		float trh = h*max/ratio/2;
		float trl = l*max/ratio/2;

		float[] vertices = {
			//Un vertex représente un point qui contient plusieurs parametres tels que sa position ou sa couleur
			//positions              //colors
			trl,  trh, 0.0f,       1.0f, 0.0f, 0.0f,  // top right
			trl, -trh, 0.0f,       0.0f, 1.0f, 0.0f,  // bottom right
			-trl, -trh, 0.0f,      0.0f, 0.0f, 1.0f,  // bottom left
			-trl,  trh, 0.0f,      1.0f, 1.0f, 1.0f  // top left 
		};

		int[] indices = {  // note that we start from 0!
			3,1,0,   // first triangle
			3,2,1    // second triangle
		};  

		return new Mesh(vertices, indices, 6);
	}

	private void debug(){
		int errorId;
		while((errorId = glGetError()) != GL_NO_ERROR){
			System.out.println("Error code : "+errorId);
		}
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);  
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		// glFrontFace(GL_CW);
		Scene scene = new Scene();
		// Mesh rect = newRect(16, 9);
		// scene.addObj(rect);
		Mesh rect = new Mesh("src\\models\\octahedron.obj");
		scene.addObj(rect.scale(.5f).translate(-1.0f, 1.0f, 0.0f));

		Mesh suzanne = new Mesh("src\\models\\suzanne.obj");
		scene.addObj(suzanne.scale(0.5f));

		for (int i = 0; i < 1; i++) {
			Mesh cat = new Mesh("src\\models\\cat.obj");
			scene.addObj(cat.scale(0.02f).translate(30.02f, 0.02f, 3.0f));
		}

		//color
        // glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * floatSize, 3 * floatSize);
        // glEnableVertexAttribArray(1);  


		//crée un callback qui va s'executer a chaque fois qu'on resize la fenetre
		//lors du resize on re-render la scene
		glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback(){
			@Override
			public void invoke(long window, int width, int height){
				glViewport(0, 0, width, height);
				render(scene);
			}
		});
		
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
            // Set the clear color
		    render(scene);
		}
	}

	private void render(Scene scene){
        // Set the clear color
		glClearColor(0.1f, 0.0f, 0.0f, 0.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		scene.render();

		glfwSwapBuffers(window); // swap the color buffers

		// Poll for window events. The key callback above will only be
		// invoked during this call.
		glfwPollEvents();
	}
}

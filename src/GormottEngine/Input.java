package GormottEngine;
import static org.lwjgl.glfw.GLFW.*;

public class Input {
    public static void Handler(long window, int key, int action){
        switch (key) {
            case GLFW_KEY_ESCAPE:
                glfwSetWindowShouldClose(window, true);
                break;
        }
        if(key == GLFW_KEY_J) System.out.println("j");
        if(key == GLFW_KEY_K) System.out.println("k");
    }
}

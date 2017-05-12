package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
	
	private static Quad quad;
	public static Shader testShader;
	
	public static void main(String[] args) {
		Window.makeWindow("Experiment2", 800, 600);
        
		testShader = new Shader("shaders/vertex.shader", "shaders/testMarch2.shader");
		testShader.enable();
		
		quad = new Quad();
		Camera.setupCam();
		
		while(!Window.shouldClose()) {
			Window.clear();
	        
			quad.update();
		    
		    setUniforms(testShader);
			
			Window.update();
		}
	}
	
	public static void setUniforms(Shader shader) {
		Camera.updateCam();
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
	}
}

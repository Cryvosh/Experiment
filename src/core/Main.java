package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
	
	public static Camera3D activeCamera;
	public static Shader activeShader;
	
	private static List<GameObject> objects = new ArrayList<>();
	
	public static void main(String[] args) {
		Window.makeWindow("Experiment2", 800, 600);
        
		activeShader = new Shader("shaders/vertex.shader", "shaders/testMarch3.shader");
		activeShader.enable();
		
		objects.add(new Quad());
		objects.add(activeCamera = new Camera3D());
		
		while(!Window.shouldClose()) {
			Window.clear();
	        
			for(GameObject obj : objects) {
				obj.update();
			}
		    
			setDynamicUniforms(activeShader);
			
			Window.update();
		}
	}
	
	public static void setDynamicUniforms(Shader shader) {
		//Could put these in callbacks, but doesn't seem to affect performance
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
	}
}

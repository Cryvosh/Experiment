package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.File;
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
	
	public static Camera activeCamera;
	public static Shader activeShader;
	
	private static int index = 0;
	private static String[] shaders;
	private static String shaderDirectory = "shaders/frag/active";
	
	private static List<GameObject> objects = new ArrayList<>();
	
	public static void main(String[] args) {
		Window.makeWindow("Experiment", 800, 600);
		
		objects.add(new Quad());
		
		File directory = new File(shaderDirectory);
		shaders = directory.list();
		nextShader();
		
		while(!Window.shouldClose()) {
			Window.clear();
	        
			for(GameObject obj : objects) {
				obj.update();
			}
		    
			setDynamicUniforms(activeShader);
			
			Window.update();
		}
	}
	
	public static void nextShader() {
		System.out.println(shaders[index]);
		objects.remove(activeCamera);
		
		activeShader = new Shader("shaders/vert/vertex.vert", shaderDirectory + "/" + shaders[index]);
		activeShader.enable();
		
		if (activeShader.targetDimension() == 2) {
			objects.add(activeCamera = new Camera2D());
		} else {
			objects.add(activeCamera = new Camera3D());
		}
		
		if (index == shaders.length - 1) {
			index = 0;
		} else {
			index += 1;
		}
	}
	
	public static void setDynamicUniforms(Shader shader) {
		//Could put these in callbacks, but doesn't seem to affect performance
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
	}
}

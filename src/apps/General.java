package apps;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import engine.*;

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

public class General {
	
	public Camera activeCamera;
	public Shader activeShader;
	
	private int index = 0;
	private String[] shaders;
	private String shaderDirectory = "shaders/frag/general";
	
	private List<Entity> objects = new ArrayList<>();
	
	public void run() {
		Window.makeWindow("Experiment", 800, 600);
		
		objects.add(new Quad());
		setup();
		
		while(!Window.shouldClose()) {
			update();
		}
	}
	
	private void setup() {
		File directory = new File(shaderDirectory);
		shaders = directory.list();
		nextShader();
	}
	
	private void update() {
		Window.clear();
        
		for(Entity obj : objects) {
			obj.update();
		}
	    
		setDynamicUniforms(activeShader);
		checkKeys();
		
		Window.update();
	}
	
	public void nextShader() {		
		index = (index + 1) % shaders.length;		
		resetShader();
	}
	
	public void resetShader() {
		System.out.println(shaders[index]);
		objects.remove(activeCamera);
		
		activeShader = new Shader("shaders/vert/vertex.vert", shaderDirectory + "/" + shaders[index]);
		activeShader.enable();
		
		if (activeShader.targetDimension() == 2) {
			objects.add(activeCamera = new Camera2D(activeShader));
		} else {
			objects.add(activeCamera = new Camera3D(activeShader, 120f));
		}
	}
	
	private void checkKeys() {
		if (Keyboard.isKeyDown(GLFW_KEY_TAB)) {
			nextShader();
		}
		if (Keyboard.isKeyDown(GLFW_KEY_R)) {
			resetShader();
		}
	}
	
	public void setDynamicUniforms(Shader shader) {
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
	}
}

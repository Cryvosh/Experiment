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
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class General implements App {
	
	private Camera activeCamera;
	private Quad activeQuad;
	
	private int index = 0;
	private int ssbo = 0;
	private String[] shaders;
	private String shaderDirectory = "shaders/frag/general";
	
	private List<Entity> entites = new ArrayList<>();
	
	public void run() {
		Window.makeWindow("General Experiment", 800, 600);
		
		File directory = new File(shaderDirectory);
		shaders = directory.list();
		nextShader();
		
		while(!Window.shouldClose()) {
			update();
		}
	}
	
	private void update() {
		Window.clear();

		for(Entity ent : entites) {
			ent.update();
		}
		
		activeQuad.render();
		setDynamicUniforms(activeQuad.getShader());
		
		checkKeys();
		
		Window.update();
	}
	
	private void nextShader() {		
		index = (index + 1) % shaders.length;
		resetShader();
	}
	
	private void resetShader() {
		System.out.println(shaders[index]);
		entites.remove(activeCamera);
		setupSSBO();
		
		glfwSetTime(0);
		Shader shader = new Shader("shaders/vert/vertex.vert", shaderDirectory + "/" + shaders[index]);
		
		if (shader.targetDimension() == 2) {
			activeQuad = new Quad(1.0f, 0, shader);
			entites.add(activeCamera = new Camera2D(activeQuad));
		} else {
			activeQuad = new Quad(1.0f, 0, shader);
			entites.add(activeCamera = new Camera3D(activeQuad, 120f));
		}
	}
	
	private void softResetShader() {
		System.out.println(shaders[index]);
		//entites.remove(activeCamera);
		setupSSBO();
		
		glfwSetTime(0);
		Shader shader = new Shader("shaders/vert/vertex.vert", shaderDirectory + "/" + shaders[index]);
		
		if (shader.targetDimension() == 2) {
			activeQuad = new Quad(1.0f, 0, shader);
			activeCamera.swapQuad(activeQuad);
			//entites.add(activeCamera = new Camera2D(activeQuad));
		} else {
			activeQuad = new Quad(1.0f, 0, shader);
			activeCamera.swapQuad(activeQuad);
			//entites.remove(activeCamera);
			//entites.add(activeCamera = new Camera3D(activeQuad, 120f));
		}
	}
	
	private void setupSSBO() {
		ssbo = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
		IntBuffer data = BufferUtils.createIntBuffer(1000000);
		glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
	}
	
	private void checkKeys() {
		if (Keyboard.keyPressed(GLFW_KEY_TAB)) {
			nextShader();
		}
		if (Keyboard.keyPressed(GLFW_KEY_R)) {
			resetShader();
		}
		if (Keyboard.keyPressed(GLFW_KEY_E)) {
			softResetShader();
		}
	}
	
	private void setDynamicUniforms(Shader shader) {
		shader.enable();
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
		shader.disable();
	}
}

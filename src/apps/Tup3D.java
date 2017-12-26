package apps;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import engine.*;

import org.joml.*;

import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.math.BigInteger;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Tup3D {
	
	public Camera3D activeCamera;
	public Shader activeShader;
	
	private float verticalFOV = 120f;
	
	private int ssbo = 0;
	private int width = 1000;
	private boolean nFromFile = true;
	private BigInteger n;
	
	private String shaderDirectory = "shaders/frag/Tup3D";
	
	private List<Entity> objects = new ArrayList<>();
	
	public void run() {
		Window.makeWindow("Experiment", 800, 600);
		
		objects.add(new Quad());
		
		resetShader();
		setup();
		
		while(!Window.shouldClose()) {
			update();
		}
	}
	
	private void resetShader() {
		objects.remove(activeCamera);
		
		activeShader = new Shader("shaders/vert/vertex.vert", shaderDirectory + "/Tup3D.frag");
		activeShader.enable();
		
		objects.add(activeCamera = new Camera3D(activeShader, verticalFOV));
		setup();
	}
	
	private void setup() {
		if(nFromFile) {
			try {
				n = new BigInteger(Files.readAllBytes(Paths.get("n_val/n.val")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			n = BigInteger.ZERO;
		}
		
		activeShader.setUniform1i("WIDTH", width);
		setupSSBO();
		bufferN(n, 0);
	}
	
	private void update() {
		Window.clear();
        
		for(Entity obj : objects) {
			obj.update();
		}
	    
		Window.update();
		setDynamicUniforms(activeShader);
		BigInteger oldN = n;
		
		modifyN();
		checkKeys();
		
		if(n.compareTo(oldN) != 0) {
			bufferN(n, 0);
		}
	}
	
	private void setupSSBO() {
		ssbo = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
	}
	
	private void setDynamicUniforms(Shader shader) {
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
	}
	
	private void checkKeys() {
		if (Keyboard.isKeyDown(GLFW_KEY_8)) {
			n = n.subtract(BigInteger.ONE);
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_7)) {
			n = n.subtract(BigInteger.ONE);
		}
		if (Keyboard.isKeyDown(GLFW_KEY_9)) {
			n = n.add(BigInteger.ONE);
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_0)) {
			n = n.add(BigInteger.ONE);
		}
		
		if (Keyboard.isKeyDown(GLFW_KEY_R)) {
			resetShader();
		}
	}
	
	private void modifyN() {
		Vector3f pos = activeCamera.pos;
		if(pos.z > width) {
			pos.z = 0;
			n = n.add(BigInteger.ONE);
		} else if (pos.z < 0) {
			pos.z = width;
			n = n.subtract(BigInteger.ONE);
		}
	}
	
	private void bufferN(BigInteger b, int binding) {
		byte[] bytes = b.toByteArray();
		int[] ints = new int[(int) Math.ceil(bytes.length/4f)];
		
		for(int i=bytes.length-1; i>=0; i-=1) {
			int j = bytes.length - 1 - i;
			ints[j/4] |= ((bytes[i]&0xFF)<<(8*(j%4)));
		}
		
		IntBuffer data = BufferUtils.createIntBuffer(ints.length);
		data.put(ints);
		data.flip();
		
		glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_STATIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, ssbo);
		
		activeShader.setUniform1i("LENGTH", ints.length);
	}
}

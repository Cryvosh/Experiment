package apps;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.stb.STBEasyFont;

import engine.*;
import org.joml.*;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class Tup3D implements App {
	
	public Shader tup3D;
	public Camera3D activeCamera;
	
	private int width = 1000;
	private float verticalFOV = 120f;
	
	private int startingN = 13;
	
	private int ssbo = 0;
	private boolean modified = false;
	private BigInteger n = BigInteger.valueOf(startingN);
	
	private String shaderDirectory = "shaders/frag/Tup3D";
	private String defaultNPath = "appData/Tup3D/n.val";
	private String defaultObjPath = "appData/Tup3D/n.obj";
	
	private List<Entity> entities = new ArrayList<>();
	private List<Quad> quad2D = new ArrayList<>();
	private List<Quad> quad3D = new ArrayList<>();
	
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	private InputReader reader = new InputReader(br);
	
	public void run() {
		Window.makeWindow("Tup3D Experiment", 800, 600);
		Thread inputThread = new Thread(reader);
		inputThread.start();
		
		//nFromFile("shaders/frag/Tup3D/Tup3D.frag");
		nFromObj("appData/Tup3D/n.obj");
		//nFromFile(defaultNPath);
		resetMainQuad();
		
		while(!Window.shouldClose()) {
			update();
		}
		
		try {
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void resetMainQuad() {
		glfwSetTime(0);		
		tup3D = new Shader("shaders/vert/vertex.vert", shaderDirectory + "/Tup3D.frag");
		tup3D.enable();
		tup3D.setUniform1i("WIDTH", width);
		tup3D.disable();
		quad3D.clear();
		quad3D.add(new Quad(1.0f, 0, tup3D));
		
		entities.remove(activeCamera);		
		entities.add(activeCamera = new Camera3D(quad3D, verticalFOV));
		
		setupSSBO();
		bufferN(n, 0);
	}
	
	private void resetQuads() {
		glfwSetTime(0);
		
		tup3D = new Shader("shaders/vert/vertex.vert", shaderDirectory + "/Tup3D.frag");
		tup3D.enable();
		tup3D.setUniform1i("WIDTH", width);
		tup3D.disable();
		quad3D.clear();
		quad3D.add(new Quad(1.0f, 0, tup3D));
		
		Vector3f pos = activeCamera.getPos();
		
		entities.remove(activeCamera);
		entities.add(activeCamera = new Camera3D(quad3D, verticalFOV));
		
		activeCamera.setPos(pos);
		
		setupSSBO();
		bufferN(n, 0);
	}
	
	private void update() {
		Window.clear();
		
		for(Entity ent : entities) {
			ent.update();
		}
		
		for(Quad q : quad3D) {
			q.render();
			setDynamicUniforms(q.getShader());
		}
		
		modifyN();
		checkKeys();
		parseInput(reader.getInput());
		
		if(modified) {
			bufferN(n, 0);
			modified = false;
		}
	    
		Window.update();
	}
	
	private void setupSSBO() {
		ssbo = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
	}
	
	private void setDynamicUniforms(Shader shader) {
		shader.enable();
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
		shader.disable();
	}
	
	private void checkKeys() {
		if (Keyboard.keyPressed(GLFW_KEY_8)) {
			n = n.subtract(BigInteger.ONE);
			modified = true;
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_7)) {
			n = n.subtract(BigInteger.ONE);
			modified = true;
		}
		if (Keyboard.keyPressed(GLFW_KEY_9)) {
			n = n.add(BigInteger.ONE);
			modified = true;
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_0)) {
			n = n.add(BigInteger.ONE);
			modified = true;
		}
		
		if (Keyboard.keyPressed(GLFW_KEY_R)) {
			resetMainQuad();
		}
	}
	
	private void modifyN() {
		Vector3f pos = activeCamera.getPos();
		if(pos.z > width) {
			pos.z = 0;
			n = n.add(BigInteger.ONE);
			modified = true;
		} else if (pos.z < 0) {
			pos.z = width;
			n = n.subtract(BigInteger.ONE);
			modified = true;
		}
	}
	
	private int index(Vector3f p) {
		int x = (int)Math.floor(p.x);
		int y = (int)Math.floor(p.y);
		int z = (int)Math.floor(p.z);
		
		if(x < 0 || y < 0 || z < 0) {
			return -1;
		}
		if(x >= width || z >= width) {
			return -1;
		}
		
		return (width*z + x) + (width*width*y);
	}
	
	private void nFromObj(String path) {
		List<Vector3f> vertices = FileIO.objToVertList(path);
		int byteLength = (int)Math.ceil((width*width*width)/8f);
		byte[] bytes = new byte[byteLength];
		Vector3f offset = new Vector3f(width/2, 0, width/2);
		for (Vector3f vert : vertices) {
			vert = vert.add(offset);
			int index = index(vert);
			if(index >= 0 && index < (byteLength)*8) {
				int byteIndx = index/8;
				int bit = index - 8*byteIndx;
				bytes[byteLength - byteIndx] |= (1<<bit);
			}
		}
		n = new BigInteger(bytes);
	}
	
	private void nFromFile(String path) {
		try {
			n = new BigInteger(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void bufferN(BigInteger b, int binding) {
		byte[] bytes = b.toByteArray();
		int[] ints = new int[(int) Math.ceil(bytes.length/4f) + 1];
		
		for(int i=bytes.length-1; i>=0; i-=1) {
			int j = bytes.length - 1 - i;
			ints[j/4] |= ((bytes[i]&0xFF)<<(8*(j%4)));
		}
		
		IntBuffer data = BufferUtils.createIntBuffer(ints.length);
		data.put(ints);
		data.flip();
		
		glBufferData(GL_SHADER_STORAGE_BUFFER, data, GL_DYNAMIC_DRAW);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, binding, ssbo);
		
		tup3D.enable();
		int maxY = (int)(((long)1<<32)/((long)width*(long)width));
		tup3D.setUniform1i("maxY", maxY);
		tup3D.setUniform1i("LENGTH", ints.length);
		tup3D.disable();
	}
	
	private void parseInput(String text) {
		if(text == "") {
			return;
		}
		
		String[] split = text.split(" ");
		
		if(split[0].equalsIgnoreCase("/nFromFile")) {
			if(split.length > 1) {
				nFromFile(split[1]);
			} else {
				nFromFile(defaultNPath);
			}
			modified = true;
			System.out.println("Command recognized");
		} else if (split[0].equalsIgnoreCase("/nFromObj")) {
			if(split.length > 1) {
				nFromObj(split[1]);
			} else {
				nFromObj(defaultObjPath);
			}
			modified = true;
			System.out.println("Command recognized");
		} else {
			System.out.println("Command not recognized");
		}
	}
}

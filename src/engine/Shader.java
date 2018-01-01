package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.joml.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.awt.List;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.lang.Math;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Shader {
	
	private int targetDimension;
	private final int programID;
	private Map<String, Integer> locationCache = new HashMap<>();
	private Set<String> invalidUniforms = new HashSet<>();
	
	public Shader(String vertPath, String fragPath) {
		programID = load(vertPath, fragPath);
		
		if(fragPath.matches(".*2[d|D].*")) {
			targetDimension = 2;
		} else if (fragPath.matches(".*3[d|D].*")){
			targetDimension = 3;
		}
	}
	
	public void enable() {
		glUseProgram(programID);
	}
	
	public void disable() {
		glUseProgram(0);
	}
	
	public int targetDimension() {
		return targetDimension;
	}
	
	private int getUniform(String name) {
		if (locationCache.containsKey(name)) {
			return locationCache.get(name);
		} else if (invalidUniforms.contains(name)) {
			return -1;
		}
		
		int location = glGetUniformLocation(programID, name);
		
		if (location == -1) {
			invalidUniforms.add(name);
			System.out.println("Invalid/unused uniform: " + name);
		} else {
			locationCache.put(name, location);
		}
		
		return location;
	}
	
	public void setUniform1i(String name, int value) {
		glUniform1i(getUniform(name), value);
	}
	
	public void setUniform1f(String name, float value) {
		glUniform1f(getUniform(name), value);
	}
	
	public void setUniform2f(String name, float x, float y) {
		glUniform2f(getUniform(name), x, y);
	}
	
	public void setUniform3f(String name, float x, float y, float z) {
		glUniform3f(getUniform(name), x, y, z);
	}
	
	public void setUniformMatrix4f(String name, Matrix4f matrix) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		glUniformMatrix4fv(getUniform(name), true, matrix.get(buffer));
	}
	
	private int load(String vertPath, String fragPath) {		
		
		int vertShader = glCreateShader(GL_VERTEX_SHADER);
		int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
		
		String vertSource = FileIO.shaderToString(vertPath);
		String fragSource = FileIO.shaderToString(fragPath);
		
		glShaderSource(vertShader, vertSource);
		glShaderSource(fragShader, fragSource);
		
		glCompileShader(vertShader);
		glCompileShader(fragShader);
		
		if (glGetShaderi(vertShader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println("Failed to compile vertex shader " + vertPath);
			System.out.println(glGetShaderInfoLog(vertShader));
			return -1;
		}
		
		if (glGetShaderi(fragShader, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println("Failed to compile fragment shader " + fragPath);
			System.out.println(glGetShaderInfoLog(fragShader));
			return -1;
		}
		
		int program = glCreateProgram(); 
		
		glAttachShader(program, vertShader);
		glAttachShader(program, fragShader);
		glLinkProgram(program);
		glValidateProgram(program);
		
		glDeleteShader(vertShader);
		glDeleteShader(fragShader);
		
		return program;
	}
}

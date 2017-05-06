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
	
	public static void main(String[] args) {
		
		Window window = new Window("Experiment", 800, 600);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		float vertices[] = {
				 -1.0f, -1.0f, 0.0f,
				 -1.0f,  1.0f, 0.0f,
				  1.0f,  1.0f, 0.0f,
				  1.0f,  1.0f, 0.0f,
				  1.0f, -1.0f, 0.0f,
				 -1.0f, -1.0f, 0.0f,
		};
		
		int vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesBuffer.put(vertices).flip();
		
		int vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glBindVertexArray(0);
        
		Shader testShader = new Shader("shaders/vertex.shader", "shaders/testMarch.shader");
		testShader.enable();
		
		while(!window.shouldClose()) {
			window.clear();
	        
			glBindVertexArray(vaoID);
		    glEnableVertexAttribArray(0);
			
		    glDrawArrays(GL_TRIANGLES, 0, 6);
		    testShader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		    testShader.setUniform2f("iResolution", window.getWidth(), window.getHeight());
			
		    glDisableVertexAttribArray(0);
		    glBindVertexArray(0);
		    
			if(Mouse.isButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				System.out.println("FPS: " + 1/window.getDT());
			}
			
			window.update();
		}
	}
}

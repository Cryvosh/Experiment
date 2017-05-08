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
	
	private static int vaoID;
	public static Shader testShader;
	
	public static void main(String[] args) {
		Window.makeWindow("Experiment", 800, 600);
		drawQuad();
        
		testShader = new Shader("shaders/vertex.shader", "shaders/testMarch2.shader");
		testShader.enable();
		
		Camera.setupCam();
		
		while(!Window.shouldClose()) {
			Window.clear();
	        
			glBindVertexArray(vaoID);
		    glEnableVertexAttribArray(0);
		    glDrawArrays(GL_TRIANGLES, 0, 6);
		    
		    setUniforms(testShader);
		    
			if(Mouse.isButtonUp(GLFW_MOUSE_BUTTON_LEFT)) {
				System.out.println("FPS: " + 1/Window.getDT());
			}
			
			Window.update();
		}
	}
	
	public static void setUniforms(Shader shader) {
		Camera.updateCam();
		shader.setUniform2f("iMouse", (float)Cursor.x(), (float)Cursor.y());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
	}
	
	public static void drawQuad() {
		float vertices[] = {
				 -1.0f, -1.0f, 0.0f,
				 -1.0f,  1.0f, 0.0f,
				  1.0f,  1.0f, 0.0f,
				  1.0f,  1.0f, 0.0f,
				  1.0f, -1.0f, 0.0f,
				 -1.0f, -1.0f, 0.0f,
		};
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesBuffer.put(vertices).flip();
		
		int vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glBindVertexArray(0);
	}
}

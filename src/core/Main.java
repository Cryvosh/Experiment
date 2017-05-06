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
	
	private static Camera camera = new Camera(0.0f, 0.0f, 0.0f);
	
	public static void main(String[] args) {
		Window.makeWindow("Experiment", 800, 600);
		drawQuad();
        
		Shader testShader = new Shader("shaders/vertex.shader", "shaders/testMarch.shader");
		testShader.enable();
		
		while(!Window.shouldClose()) {
			Window.clear();
	        
			glBindVertexArray(vaoID);
		    glEnableVertexAttribArray(0);
		    glDrawArrays(GL_TRIANGLES, 0, 6);
		    
		    camera.setYaw((float)Cursor.getX());
		    camera.setPitch((float)Cursor.getY());
		    
		    if (Keyboard.isKeyPressed(GLFW_KEY_W))
	        {
	            camera.moveForwards((float)Window.getDT());
	        }
	        if (Keyboard.isKeyPressed(GLFW_KEY_S))
	        {
	            camera.moveBackwards((float)Window.getDT());
	        }
	        if (Keyboard.isKeyPressed(GLFW_KEY_A))
	        {
	            camera.moveLeft((float)Window.getDT());
	        }
	        if (Keyboard.isKeyPressed(GLFW_KEY_D))
	        {
	            camera.moveRight((float)Window.getDT());
	        }
		    
		    setUniforms(testShader);
		    
			if(Mouse.isButtonUp(GLFW_MOUSE_BUTTON_LEFT)) {
				System.out.println("FPS: " + 1/Window.getDT());
			}
			
			Window.update();
		}
	}
	
	public static void setUniforms(Shader shader) {
		shader.setUniform3f("iPosition", camera.getPos().x, camera.getPos().y, camera.getPos().z);
		shader.setUniform2f("iMouse", (float)Cursor.getX(), (float)Cursor.getY());
		shader.setUniform1f("iGlobalTime", (float)glfwGetTime());
		shader.setUniform2f("iResolution", Window.getWidth(), Window.getHeight());
		shader.setUniform3f("iFront", Cursor.front.x, Cursor.front.y, Cursor.front.z);
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

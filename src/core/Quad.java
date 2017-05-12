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

//Generalize to/inherit from Mesh later
public class Quad implements GameObject {
	
	private int vaoID;
	
	public Quad() {
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
	
	public void update() {
		glBindVertexArray(vaoID);
	    glEnableVertexAttribArray(0);
	    glDrawArrays(GL_TRIANGLES, 0, 6);
	}
}

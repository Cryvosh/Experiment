package engine;

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
public class Quad {
	
	private int vaoID;
	private Shader shader;
	
	public Quad(float x, float depth, Shader shader) {
		this.shader = shader;
		
		float vertices[] = {				
				 -x, -x, depth,
				 -x,  x, depth,
				  x,  x, depth,
				  x,  x, depth,
				  x, -x, depth,
				 -x, -x, depth,
		};
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesBuffer.put(vertices).flip();
		
		int vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);		
	}
	
	public int getVAO() {
		return this.vaoID;
	}
	
	public Shader getShader() {
		return this.shader;
	}
	
	public void render() {
		shader.enable();
		glBindVertexArray(vaoID);
	    glEnableVertexAttribArray(0);
	    glDrawArrays(GL_TRIANGLES, 0, 6);
	    glDisableVertexAttribArray(0);
	    glBindVertexArray(0);
	    shader.disable();
	}
}

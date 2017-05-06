package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
	
	private String title;
	
	private long window;
	private int width, height;
	
	private double dt, now, then;

	public Window(String title, int width, int height) {
		this.title = title;
		this.width = width;
		this.height = height;
		
		if (!init()) {
			glfwTerminate();
		}
	}
	
	private boolean init() {
		
		if (!glfwInit()) {
			System.out.println("GLFW failed to initialize");
			return false;
		}
		
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		
		if (window == NULL) {
			System.out.println("Failed to create window");
			return false;
		}
		
		setCallbacks();
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		then = glfwGetTime();
		
		return true;
	}
	
	public void update() {
		glfwPollEvents();
		glfwSwapBuffers(window);
		glfwSwapInterval(0);
		
		now = glfwGetTime();
		dt = now - then;
		then = now;	
	}
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	private void setCallbacks() {
		GLFWWindowSizeCallback resizeCallback = new GLFWWindowSizeCallback() {
			public void invoke(long window, int argWidth, int argHeight) {
				width = argWidth;
				height = argHeight;
				glViewport(0, 0, argWidth, argHeight);
			}
		};
		glfwSetWindowSizeCallback(window, resizeCallback);
		
		glfwSetKeyCallback(window, new Keyboard());
		glfwSetCursorPosCallback(window, new Cursor());
		glfwSetMouseButtonCallback(window, new Mouse());
	}
	
	public double getDT() {
		return this.dt;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
}

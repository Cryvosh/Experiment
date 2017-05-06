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
	
	private static String title;
	
	private static long window;
	private static int width, height;
	
	private static double dt, now, then;

	public static void makeWindow(String title, int width, int height) {
		Window.title = title;
		Window.width = width;
		Window.height = height;
		
		if (!init()) {
			glfwTerminate();
		}
	}
	
	private static boolean init() {
		
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
		
		Cursor.setVisibility(false);
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		then = glfwGetTime();
		return true;
	}
	
	public static void update() {
		glfwPollEvents();
		glfwSwapBuffers(window);
		glfwSwapInterval(0);
		
		now = glfwGetTime();
		dt = now - then;
		then = now;	
	}
	
	public static void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	private static void setCallbacks() {
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
	
	public static long getID() {
		return window;
	}
	
	public static double getDT() {
		return Window.dt;
	}
	
	public static int getWidth() {
		return Window.width;
	}
	
	public static int getHeight() {
		return Window.height;
	}
	
}

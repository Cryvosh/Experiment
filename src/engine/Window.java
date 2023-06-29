package engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

public class Window {
	
	private static String title;
	
	private static long windowID = -1;
	private static int width, height;
	
	private static double dt, now, then;

	public static void makeWindow(String title, int width, int height) {
		Window.title = title;
		Window.width = width;
		Window.height = height;
		
		if (!setup()) {
			glfwTerminate();
		}
	}
	
	private static boolean setup() {
		if (!glfwInit()) {
			System.out.println("GLFW failed to initialize");
			return false;
		}
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		
		windowID = glfwCreateWindow(width, height, title, NULL, NULL);
		
		if (windowID == NULL) {
			System.out.println("Failed to create window");
			return false;
		}
		
		setCallbacks();
		glfwMakeContextCurrent(windowID);
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Cursor.setVisibility(false);
		
		then = glfwGetTime();
		return true;
	}
	
	public static void update() {
		glfwPollEvents();
		glfwSwapBuffers(windowID);
		glfwSwapInterval(1); //vsync?
		
		now = glfwGetTime();
		dt = now - then;
		then = now;
		
		checkKeys();
	}
	
	private static void checkKeys() {
		if (Keyboard.keyHeldDown(GLFW_KEY_ESCAPE)) {
			Cursor.setVisibility(true);
		}
		
		if(Keyboard.keyHeldDown(GLFW_KEY_T)) {
			System.out.println(glfwGetTime());
		}
		
		if(Keyboard.keyHeldDown(GLFW_KEY_SPACE)) {
			System.out.println("FPS: " + 1 / Window.getDT());
		}
	}
	
	public static void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public static void close() {
		glfwSetWindowShouldClose(windowID, true);
	}
	
	public static boolean shouldClose() {
		return glfwWindowShouldClose(windowID);
	}
	
	private static void setCallbacks() {
		GLFWWindowSizeCallback resizeCallback = new GLFWWindowSizeCallback() {
			public void invoke(long window, int argWidth, int argHeight) {
				width = argWidth;
				height = argHeight;
				glViewport(0, 0, argWidth, argHeight);
			}
		};
		glfwSetWindowSizeCallback(windowID, resizeCallback);
		
		glfwSetScrollCallback(windowID, new Scroll());
		glfwSetKeyCallback(windowID, new Keyboard());
		glfwSetCursorPosCallback(windowID, new Cursor());
		glfwSetMouseButtonCallback(windowID, new Mouse());
	}
	
	public static long getID() {
		return windowID;
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

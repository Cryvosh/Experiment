package engine;

import org.joml.Vector3f;
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

public class Cursor implements GLFWCursorPosCallbackI {
	
	private static double x, y;
	private static double dx, dy;
	private static double tempDelta;
	private static double lastX = 400, lastY = 300;
	
	private static double sens = 0.2;
	
	private static boolean moved = false;
	private static boolean visible;
	
	@Override
	public void invoke(long window, double x, double y) {
		
		if(!moved) {
			lastX = x;
			lastY = y;
			moved = true;
		}
		
		Cursor.x = x;
		Cursor.y = y;
		
		dx = x - lastX;
		dy = lastY - y;
		
		lastX = x;
		lastY = y;
		
		dx *= sens;
		dy *= sens;
	}
	
	public static double x() {
		return x;
	}
	
	public static double y() {
		return y;
	}
	
	public static double dx() {
		tempDelta = dx;
		dx = 0;
		return tempDelta;
	}
	
	public static double dy() {
		tempDelta = dy;
		dy = 0;
		return tempDelta;
	}
	
	public static boolean getVisibility() {
		return visible;
	}
	
	public static void setVisibility(boolean vis) {
		visible = vis;
		
		if (vis == true) {
			glfwSetInputMode(Window.getID(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		} else {
			glfwSetInputMode(Window.getID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		}
	}
}

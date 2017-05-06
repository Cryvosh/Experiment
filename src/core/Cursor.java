package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import math.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Cursor implements GLFWCursorPosCallbackI {
	
	private static double posX, posY;
	private static double deltaX, deltaY;
	private static boolean visible;
	
	public static Vector3f front = new Vector3f(0, 0, 0);
	
	private boolean firstInput = true;
	
	public static float pitch, yaw;
	
	double lastX = Window.getWidth();
	double lastY = 300;
	
	float sens = 0.5f;
	
	@Override
	public void invoke(long window, double x, double y) {
		
	    if(firstInput)
	    {
	        lastX = x;
	        lastY = y;
	        firstInput = false;
	    }
	    
	    posX = x;
	    posY = y;
		
		deltaX = x - lastX;
		deltaY = lastY - y;
		
		lastX = x;
		lastY = y;
		
		deltaX *= sens;
		deltaY *= sens;
		
		yaw += deltaX;
		pitch += deltaY;
		
		if (pitch > 89.0f) {
			pitch = 89.0f;
		}
			
		if (pitch < -89.0f) {
			pitch = -89.0f;
		}
		
		front.x = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.y = (float) (Math.sin(Math.toRadians(pitch)));
		front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));		
	}
	
	public static double getX() {
		return Cursor.posX;
	}
	
	public static double getY() {
		return Cursor.posY;
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

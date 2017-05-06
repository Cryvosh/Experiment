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

public class Cursor implements GLFWCursorPosCallbackI {
	
	private static double x, y;
	
	@Override
	public void invoke(long window, double x, double y) {
		Cursor.x = x;
		Cursor.y = y;
	}
	
	public static double getX() {
		return Cursor.x;
	}
	
	public static double getY() {
		return Cursor.y;
	}
}

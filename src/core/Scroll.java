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

public class Scroll implements GLFWScrollCallbackI {
	
	private static double x, y;
	private static double dx, dy;
	
	@Override
	public void invoke(long window, double dx, double dy) {
		Scroll.dx = dx;
		Scroll.dy = dy;
		
		x += dx;
		y += dy;
		
		Main.activeCamera.handleScroll(dy);
	}
	
	public static double x() {
		return Scroll.x;
	}
	
	public static double y() {
		return Scroll.y;
	}
	
	public static double dx() {
		return Scroll.dx;
	}
	
	public static double dy() {
		return Scroll.dy;
	}
}

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

public class Mouse implements GLFWMouseButtonCallbackI {

	private static boolean[] buttons = new boolean[GLFW_MOUSE_BUTTON_LAST];
	private static boolean[] buttonsDown = new boolean[GLFW_MOUSE_BUTTON_LAST];
	private static boolean[] buttonsUp = new boolean[GLFW_MOUSE_BUTTON_LAST];

	@Override
	public void invoke(long window, int button, int action, int mods) {
		if (action != GLFW_RELEASE && buttons[button] == false) {
			buttonsDown[button] = true;
			buttonsUp[button] = false;
		}
		else if (action == GLFW_RELEASE && buttons[button] == true) {
			buttonsDown[button] = false;
			buttonsUp[button] = true;
		}

		buttons[button] = (action != GLFW_RELEASE);	
		Cursor.setVisibility(false);
	}
	
	public static boolean isButtonDown(int button) {
		boolean temp = buttonsDown[button];
		buttonsDown[button] = false;
		return temp;
	}
	
	public static boolean isButtonUp(int button) {
		boolean temp = buttonsUp[button];
		buttonsUp[button] = false;
		return temp;
	}
	
	public static boolean isButtonPressed(int button) {
		return buttons[button];
	}	
}

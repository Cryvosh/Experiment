package engine;

import static org.lwjgl.glfw.GLFW.*;

public interface Camera extends Entity {	
	public default float multiplySpeed(float lower, float upper) {
		if (Keyboard.keyHeldDown(GLFW_KEY_LEFT_SHIFT)) {
			return upper;
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_LEFT_CONTROL)) {
			return lower;
		}
		return 1.0f;
	}
}

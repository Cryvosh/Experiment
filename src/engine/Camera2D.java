package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.joml.*;
import java.lang.Math;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Camera2D implements Camera {
	
	private float moveSpeed = 2.0f;
	private float zoomSpeed = 2.0f;
	
	private float currMoveSpeed;
	private float currZoomSpeed;
	
	private float ctrMoveMultiplier = 0.3f;
	private float shiftMoveMultiplier = 20.0f;
	
	private float ctrZoomlMultiplier = 0.5f;
	private float shiftZoomMultiplier = 5.0f;
	
	private float zoom = 1.0f;
	private float zoomExponent = -1.0f;
	
	private Vector2f pos = new Vector2f(0.0f, 0.0f);
	
	private Shader activeShader;
	
	public Camera2D(Quad quad) {
		activeShader = quad.getShader();
	}
	
	public void swapQuad(Quad quad) {
		this.activeShader = quad.getShader();
	}

	public void update() {
		
		currMoveSpeed = (float) (moveSpeed * Window.getDT() * zoom);
		currMoveSpeed *= multiplySpeed(ctrMoveMultiplier, shiftMoveMultiplier);
		currZoomSpeed = (float) (zoomSpeed * Window.getDT());
		currZoomSpeed *= multiplySpeed(ctrZoomlMultiplier, shiftZoomMultiplier);
		
		if (Keyboard.keyHeldDown(GLFW_KEY_UP)) {
			zoomExponent += currZoomSpeed;
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_DOWN)) {
			zoomExponent -= currZoomSpeed;
		}
		
		if (Keyboard.keyHeldDown(GLFW_KEY_W)) {
			Vector2f offset = new Vector2f(0.0f, currMoveSpeed);
			pos.add(offset);
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_S)) {
			Vector2f offset = new Vector2f(0.0f, currMoveSpeed);
			pos.sub(offset);
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_A)) {
			Vector2f offset = new Vector2f(currMoveSpeed, 0.0f);
			pos.sub(offset);
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_D)) {
			Vector2f offset = new Vector2f(currMoveSpeed, 0.0f);
			pos.add(offset);
		}
		
		if (Keyboard.keyHeldDown(GLFW_KEY_LEFT_ALT)) {
			System.out.println("POS: " + (int)Math.round(pos.x) + " " + (int)Math.round(pos.y));
		}
		
		if (Keyboard.keyHeldDown(GLFW_KEY_P)) {
			System.out.println(zoom);
			//System.out.println("Spiral index: " + spiralindex((int)Math.floor(pos.x), (int)Math.floor(pos.y)));
		}
		
		if (Keyboard.keyHeldDown(GLFW_KEY_O)) {
			System.out.println("Grid index: " + gridindex((int)Math.floor(pos.x), (int)Math.floor(pos.y)));
		}
		
		zoom = 1/(float)Math.pow(2.0, zoomExponent);
		
		activeShader.enable();
		activeShader.setUniform1f("iZoom", zoom);
		activeShader.setUniform2f("iPosition", pos.x, pos.y);
		activeShader.disable();
	}
	
	private int spiralindex(int x, int y) {
		int index = 0;
		if(x*x >= y*y) {
			index = 4*x*x - x - y;
			if (x < y) {
				index -= 2 * (x-y);
			}
		} else {
			index = 4*y*y - x - y;
			if (x < y) {
				index += 2 * (x-y);
			}	
		}
		return index;
	}
	
	int gridindex(int x, int y) {
		int width = 10000;

		if(x >= width || x < 0 || y < 0) {
			return -1;
		}

		int index = width*y + x;
		return index;
	}
}

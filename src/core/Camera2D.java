package core;

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

public class Camera2D implements GameObject {
	
	private float panSpeed = 1.0f;
	private float zoomSpeed = 1.5f;
	
	private float currPanSpeed;
	private float currZoomSpeed;
	
	private Vector2f pos = new Vector2f(0.0f, 0.0f);
	private float zoomScale = 1.0f;
	private float scale = 1.0f;

	public void update() {
		
		currPanSpeed = (float) (panSpeed * Window.getDT() / zoomScale);
		currZoomSpeed = (float) (zoomSpeed * Window.getDT());
		
		if (Keyboard.isKeyPressed(GLFW_KEY_UP)) {
			scale += currZoomSpeed;
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_DOWN)) {
			scale -= currZoomSpeed;
		}
		
		if (Keyboard.isKeyPressed(GLFW_KEY_W)) {
			Vector2f offset = new Vector2f(0.0f, currPanSpeed);
			pos.add(offset);
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_S)) {
			Vector2f offset = new Vector2f(0.0f, currPanSpeed);
			pos.sub(offset);
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_A)) {
			Vector2f offset = new Vector2f(currPanSpeed, 0.0f);
			pos.sub(offset);
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_D)) {
			Vector2f offset = new Vector2f(currPanSpeed, 0.0f);
			pos.add(offset);
		}
		
		zoomScale = (float)Math.pow(zoomSpeed, scale);
		Main.activeShader.setUniform1f("iZoomScale", zoomScale);
		Main.activeShader.setUniform2f("iPosition", pos.x, pos.y);
	}
}

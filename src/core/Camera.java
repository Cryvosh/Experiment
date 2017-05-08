package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.joml.*;
import org.joml.Math;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Camera {
	
	private static float pitch, yaw;
	
	private static Vector3f pos = new Vector3f(0.0f, 0.0f, 3.0f);
	private static Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	private static Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	private static Vector3f frontClone, upClone;
	
	private static float speed = 1.5f;
	private static float shiftSpeed = 8.0f;
	private static float currSpeed;
	
	private static float verticalFOV = 120.0f;
	
	public static void setupCam() {
		Main.testShader.setUniform1f("iVerticalFOV", verticalFOV);
	}
	
	public static void updateCam () {
		setFront();
		
		currSpeed = (float) (speed * Window.getDT());
		
		if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
			currSpeed = (float) (shiftSpeed * Window.getDT());
		} else {
			currSpeed = (float) (speed * Window.getDT());
		}
		
		if (Keyboard.isKeyPressed(GLFW_KEY_W)) {
			frontClone = new Vector3f(front);
			pos.add(frontClone.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_S)) {
			frontClone = new Vector3f(front);
			pos.sub(frontClone.mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_A)) {
			frontClone = new Vector3f(front);
			upClone = new Vector3f(up);
			pos.sub(((frontClone.cross(upClone)).normalize()).mul(currSpeed));
		}
		if (Keyboard.isKeyPressed(GLFW_KEY_D)) {
			frontClone = new Vector3f(front);
			upClone = new Vector3f(up);
			pos.add(((frontClone.cross(upClone)).normalize()).mul(currSpeed));
		}
		
		Matrix4f view = new Matrix4f().lookAt(pos, front.add(pos), up);
		
		Main.testShader.setUniformMatrix4f("iViewMatrix", view);
		Main.testShader.setUniform3f("iPosition", pos.x, pos.y, pos.z);
	}
	
	public static void addPitch(double offset) {
		pitch += offset;
		
		if (pitch > 89.0f) {
	        pitch = 89.0f;
		} else if (pitch < -89.0f) {
	        pitch = -89.0f;
	    }
	}
	
	public static void addYaw(double offset) {
		yaw += offset;
	}
	
	private static void setFront() {
		front.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
		front.y = (float) (Math.sin(Math.toRadians(pitch)));
		front.z = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
		front.normalize();
	}
}

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

public class Camera3D implements Camera {
	
	private static float pitch, yaw;
	
	private Vector3f pos = new Vector3f(-2.0f, -0.3f, 0.5f);
	private Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	private Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
	private Vector3f frontClone, upClone;
	
	private float speed = 1.5f;
	private float ctrlMultiplier = 0.1f;
	private float shiftMultiplier = 20.0f;
	private float currSpeed;
	
	private float verticalFOV = 120.0f;
	
	public Camera3D () {
		Main.activeShader.setUniform1f("iVerticalFOV", verticalFOV);
	}
	
	public void update () {
		rotate(Cursor.dy(), Cursor.dx());
		modifySpeed(Scroll.dy());
		setFront();
		
		currSpeed = (float) (speed * Window.getDT());
		
		if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
			currSpeed = (float) (speed * shiftMultiplier * Window.getDT());
		} else if (Keyboard.isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
			currSpeed = (float) (speed * ctrlMultiplier * Window.getDT());
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
		
		Main.activeShader.setUniformMatrix4f("iViewMatrix", view);
		Main.activeShader.setUniform3f("iPosition", pos.x, pos.y, pos.z);
	}
	
	public void rotate(double dPitch, double dYaw) {
		pitch += dPitch;
		yaw += dYaw;
		
		if (pitch > 89.0f) {
	        pitch = 89.0f;
		} else if (pitch < -89.0f) {
	        pitch = -89.0f;
	    }
	}
	
	private void setFront() {
		front.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
		front.y = (float) (Math.sin(Math.toRadians(pitch)));
		front.z = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
		front.normalize();
	}
	
	public void modifySpeed(double dy) {
		if(dy > 0) {
			this.speed *= 1.2;
		} else if (dy < 0) {
			this.speed *= 0.8;
		}
	}
}

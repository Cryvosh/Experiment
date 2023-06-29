package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.*;

import org.joml.*;

import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.math.BigInteger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Camera3D implements Camera {
	
	private float pitch, yaw;
	private float nearClip = 0;
	
	private Vector3f pos = new Vector3f(0.0f, 0.0f, 0.0f);
	private Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
	
	private Vector3f unitX = new Vector3f(1.0f, 0.0f, 0.0f);
	private Vector3f unitY = new Vector3f(0.0f, 1.0f, 0.0f);
	private Vector3f unitZ = new Vector3f(0.0f, 0.0f, 1.0f);
	
	private float baseSpeed = 1.5f;
	private float ctrlMultiplier = 0.1f;
	private float shiftMultiplier = 20.0f;
	private float currSpeed;
	
	private List<Shader> shaders = new ArrayList<>();
	
	public Camera3D (List<Quad> quad, float fov) {		
		for(Quad q : quad) {
			shaders.add(q.getShader());
		}
		
		for(Shader shader: shaders) {
			shader.enable();
			shader.setUniform1f("iVerticalFOV", fov);
			shader.disable();
		}
	}
	
	public Camera3D (Quad quad, float fov) {
		shaders.add(quad.getShader());
		
		for(Shader shader: shaders) {
			shader.enable();
			shader.setUniform1f("iVerticalFOV", fov);
			shader.disable();
		}
	}
	
	public void swapQuad(Quad quad) {
		this.shaders = new ArrayList<>();
		shaders.add(quad.getShader());
		
		for(Shader shader: shaders) {
			shader.enable();
			shader.setUniform1f("iVerticalFOV", 120f);
			shader.disable();
		}
	}
	
	public void update () {
		rotate(Cursor.dy(), Cursor.dx());
		modifySpeed(Scroll.dy());
		modifyNearClip();
		setFront();
		
		currSpeed = (float) (baseSpeed * Window.getDT());
		currSpeed *= multiplySpeed(ctrlMultiplier, shiftMultiplier);
		
		// Local space movement
		if (Keyboard.keyHeldDown(GLFW_KEY_W)) {
			Vector3f frontClone = new Vector3f(front);
			pos.add(frontClone.mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_S)) {
			Vector3f frontClone = new Vector3f(front);
			pos.sub(frontClone.mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_A)) {
			Vector3f frontClone = new Vector3f(front);
			Vector3f global_y = new Vector3f(unitY);
			pos.sub(((frontClone.cross(global_y)).normalize()).mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_D)) {
			Vector3f frontClone = new Vector3f(front);
			Vector3f global_y = new Vector3f(unitY);
			pos.add(((frontClone.cross(global_y)).normalize()).mul(currSpeed));
		}
		
		// Global space movement
		if (Keyboard.keyHeldDown(GLFW_KEY_H)) {
			Vector3f global_x = new Vector3f(unitX);
			pos.add(global_x.mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_K)) {
			Vector3f global_x = new Vector3f(unitX);
			pos.sub(global_x.mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_Y)) {
			Vector3f global_y = new Vector3f(unitY);
			pos.add(global_y.mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_I)) {
			Vector3f global_y = new Vector3f(unitY);
			pos.sub(global_y.mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_U)) {
			Vector3f global_z = new Vector3f(unitZ);
			pos.add(global_z.mul(currSpeed));
		}
		if (Keyboard.keyHeldDown(GLFW_KEY_J)) {
			Vector3f gloabl_z = new Vector3f(unitZ);
			pos.sub(gloabl_z.mul(currSpeed));
		}
		
		if (Keyboard.keyHeldDown(GLFW_KEY_LEFT_ALT)) {
			System.out.println("POS: " + (int)pos.x + " " + (int)pos.y + " " + (int)pos.z);
		}
		
		Matrix4f view = new Matrix4f().lookAt(pos, front.add(pos), unitY);
		
		for(Shader shader: shaders) {
			shader.enable();
			shader.setUniformMatrix4f("iViewMatrix", view);
			shader.setUniform3f("iPosition", pos.x, pos.y, pos.z);
			shader.disable();
		}
	}
	
	public Vector3f getPos() {
		return this.pos;
	}
	
	public void setPos(Vector3f pos) {
		this.pos = pos;
	}
	
	private void rotate(double dPitch, double dYaw) {
		pitch += dPitch;
		yaw += dYaw;
		
		pitch = Math.max(-89.9f, Math.min(89.9f, pitch));
	}
	
	private void setFront() {
		front.x = (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
		front.y = (float) (Math.sin(Math.toRadians(pitch)));
		front.z = (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
		front.normalize();
	}
	
	private void modifySpeed(double dy) {
		if(dy > 0) {
			baseSpeed *= 1.2;
		} else if (dy < 0) {
			baseSpeed *= 0.8;
		}
	}
	
	private void modifyNearClip() {
		if (Keyboard.keyHeldDown(GLFW_KEY_RIGHT_BRACKET)) {
			nearClip += 10 * Window.getDT();
		} else if (Keyboard.keyHeldDown(GLFW_KEY_LEFT_BRACKET)) {
			nearClip = Math.max(0, nearClip - 10 * (float)Window.getDT());
		}
		for(Shader shader: shaders) {
			shader.setUniform1f("iNearClip", nearClip);
		}
	}
}

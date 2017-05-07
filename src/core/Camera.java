package core;

import org.joml.Vector3f;
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

public class Camera {
	
	private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
	private float yaw = 0.0f;
	private float pitch = 0.0f;
	
	public Camera(float x, float y, float z) {
		position = new Vector3f(x, y, z);
	}
	
	public void moveForwards(float distance)
	{
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw));
	}
	
	public void moveBackwards(float distance)
	{
	    position.x += distance * (float)Math.sin(Math.toRadians(yaw));
	    position.z -= distance * (float)Math.cos(Math.toRadians(yaw));
	}
	
	public void moveLeft(float distance)
	{
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw+90));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw+90));
	}
	
	public void moveRight(float distance)
	{
	    position.x -= distance * (float)Math.sin(Math.toRadians(yaw-90));
	    position.z += distance * (float)Math.cos(Math.toRadians(yaw-90));
	}	
	
	public Vector3f getPos() {
		return position;
	}
	
	public void setYaw(float amount) {
		yaw = amount;
	}
	
	public void setPitch(float amount) {
		pitch = amount;
	}
	
}

package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import apps.*;

import java.io.File;
import java.nio.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
	
	private static int index = 1;
	
	private static App activeApp;
	private static String pkgDir = "src/apps";
	private static List<Class> apps = new ArrayList<>();
	
	public static void main(String[] args) {
		setupApps();
		runNextApp();
	}
	
	public static void runNextApp() {
		index = (index + 1) % apps.size();
		try {
			activeApp = (App)apps.get(index).newInstance();
			activeApp.run();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private static void setupApps() {
		File appDir = new File(pkgDir);
		File[] appFiles = appDir.listFiles((d, name) -> name.endsWith(".java"));
		
		for(File app : appFiles) {
			try {
				String pkgName = pkgDir.substring(pkgDir.lastIndexOf('/')+1);
				String fileName = app.getName();
				String className = pkgName + "." + fileName.substring(0, fileName.lastIndexOf('.'));
				apps.add(Class.forName(className));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
}

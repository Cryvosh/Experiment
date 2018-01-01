package engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

public class FileIO {
	public static String shaderToString(String filePath) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				result.append(buffer + '\n');
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	public static List<Vector3f> objToVertList(String filePath) {
		List<Vector3f> vertices = new ArrayList<>();
		String buffer = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			while((buffer = reader.readLine()) != null) {
				if (buffer.startsWith("v ")) {
					String[] split = buffer.split(" ");
					
					float x = Float.valueOf(split[1]);
					float y = Float.valueOf(split[2]);
					float z = Float.valueOf(split[3]);
					
					vertices.add(new Vector3f(x, y, z));
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vertices;
	}
}

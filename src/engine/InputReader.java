package engine;

import java.io.BufferedReader;
import java.io.IOException;

public class InputReader implements Runnable {
	private String input = "";
	private BufferedReader br;
	
	public InputReader(BufferedReader br) {
		this.br = br;
	}
	
	public void run() {		
		while(!Thread.interrupted()) {
			try {
				if(br.ready()) {
					input = br.readLine();
				}
			} catch (IOException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public String getInput() {
		String temp = input;
		input = "";
		return temp;
	}
}

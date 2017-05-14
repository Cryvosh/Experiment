package core;

public interface Camera extends GameObject {
	public void update();
	public void handleCursor(double dx, double dy);
}

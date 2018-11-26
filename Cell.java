import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cell {
	
	public String status;
	public int age;
	
	public Canvas canvas;
	public GraphicsContext gc;
	
	public int col;
	public int row;
	
	
	public Cell(int col, int row) {
		this.status = "neutral";
		this.age = 0;
		
		this.canvas = new Canvas(20,20);
		this.gc = canvas.getGraphicsContext2D();
		
		this.gc.setFill(Color.ALICEBLUE);
		this.gc.fillRect(0, 0, 20, 20);
		
		this.col = col;
		this.row = row;
	}
}


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cell {
	
	public String status;
	public Canvas canvas;
	public GraphicsContext gc;
	public int col;
	public int row;
	
	
	public Cell(int col, int row) {
		this.status = "dead";
		this.canvas = new Canvas(60,60);
		this.gc = canvas.getGraphicsContext2D();
		this.gc.setFill(Color.BLACK);
		this.gc.fillRect(0, 0, 60, 60);
		this.col = col;
		this.row = row;
	}
	
	public Cell[][] InitializeCells(int gridWidth, int gridLength) {
		Cell[][] cells = new Cell[gridWidth][gridLength];
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridLength; j++) {
				cells[i][j] = new Cell(i,j);
			}
		}
		return cells;
	}
	
	public void killCell() {
		
	}
}


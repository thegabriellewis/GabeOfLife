import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


/*	
	Game of Life 
	Author: Gabriel Lewis
	Start Date: 11/03/2018
*/

public class Grid extends Application {
	
	Stage window;
	GridPane grid;
	
	Cell cells;
	Cell[][] gridCells;
	
	static String dead = "dead";
	static String alive = "alive";
	
	static Paint colorDead = Color.BLACK;
	static Paint colorAlive = Color.RED;
	
	static int gridWidth = 10;
	static int gridLength = 10;
	
	static String currentGrid[][] = new String[gridWidth][gridLength];
	static String previousGrid[][] = new String[gridWidth][gridLength];
	static String tempGrid[][] = new String[gridWidth][gridLength];
	
	public static void main(String[] args) { // main function
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("Game of Life");
		Stage secondary = new Stage();
		secondary.setTitle("Game of Life");
		
		TextField column = new TextField();
		TextField row = new TextField();
		Button button = new Button("enter");

		final int c1;
		int r1;
		button.setOnAction(e -> giveLife(Integer.valueOf(column.getText()),Integer.valueOf(row.getText())));//column.getText()
		
		//layout
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20,20,20,20));
		layout.getChildren().addAll(column,row,button);
		
		Grid();
		initializeGrid();
		Scene planet = new Scene(grid, 710, 710);
		Scene planet2 = new Scene(layout,300,200);
		
		window.setScene(planet);
		secondary.setScene(planet2);
		
		window.show();
		secondary.show();
	}
	
	public void Grid() {
		grid = new GridPane();
		grid.setPadding(new Insets(10,10,10,10)); 
		grid.setVgap(10);
		grid.setHgap(10);
	
	}
	
	public void initializeGrid() {
		cells = new Cell(gridWidth,gridLength);
		gridCells = cells.InitializeCells(gridWidth, gridLength);
		
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				currentGrid[x][y] = dead;
				previousGrid[x][y] = dead;
				
				GridPane.setConstraints(gridCells[x][y].canvas,x,y);
				grid.getChildren().addAll(gridCells[x][y].canvas);
			}
		}
	}
	
	public void initializeTempGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				tempGrid[x][y] = dead;
			}
		}
	}
	
	public void giveLife(int col, int row) {
		if ((col <= gridWidth) && (row <= gridLength)) {
			Cell newLife = new Cell(col,row);
			newLife.status = alive;
			newLife.gc.setFill(colorAlive);
			newLife.gc.fillRect(0, 0, 60, 60);
			gridCells[col][row] = newLife;
			updateCells();
		}
	}
	
	public void updateCells() {
		grid.getChildren().clear();
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				currentGrid[x][y] = gridCells[x][y].status;
				
				GridPane.setConstraints(gridCells[x][y].canvas,x,y);
				grid.getChildren().addAll(gridCells[x][y].canvas);
			}
		}
	}
	
	
	public void live() {
		initializeGrid();
		while (!(extinction()) && !(changeless())) {
			generation();
		}
	}
	
	public void generation() {
		initializeTempGrid();
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				birth(x,y);
				deathByIsolation(x,y);
				deathByOverPopulation(x,y);
				survival(x,y);
			}
		}
		updatePreviousGrid();
		updateCurrentGrid();
		
	}
	
	public void updatePreviousGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				previousGrid[x][y] = currentGrid[x][y];
			}
		}
	}
	public void updateCurrentGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				currentGrid[x][y] = tempGrid[x][y];
			}
		}
	}
	
	public void birth(int col, int row) {
		if (currentGrid[col][row] == dead) {
			if (numNeighbors(col,row) == 3) {
				tempGrid[col][row] = alive;
			}
		}
	}
	
	public void deathByIsolation(int col, int row) {
		if (currentGrid[col][row] == alive) {
			if (numNeighbors(col,row) <= 1) {
				tempGrid[col][row] = dead;
			}
		}
	}
	
	public void deathByOverPopulation(int col, int row) {
		if (currentGrid[col][row] == alive) {
			if (numNeighbors(col,row) >= 4) {
				tempGrid[col][row] = dead;
			}
		}
	}
	
	public void survival(int col, int row) {
		if (currentGrid[col][row] == alive) {
			if (numNeighbors(col,row) == 2 || numNeighbors(col,row) == 3) {
				tempGrid[col][row] = alive;
			}
		}
	}
	
	public boolean extinction() { // If all cells are dead
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				if (currentGrid[x][y] == alive) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean changeless() { // If there is no change between generations
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				if (currentGrid[x][y] != previousGrid[x][y]) {
					return false;
				}
			}
		}
		return true;
	}
	
	/*public static boolean flipflop() { // If generations are "flip-flop"-ing
		
	}*/
	
	public int numNeighbors(int col, int row) {
		
		int count = 0;
		/*if (col == 0) { // Cell is located along left edge of the grid
			if (row == 0) { // Cell is located in the top left corner
				count = currentGrid[col+1][row] + currentGrid[col+1][row+1] + currentGrid[col][row+1];
			} else if (row == (gridLength-1)) { // Cell is located in the bottom left corner
				count = currentGrid[col][row-1] + currentGrid[col+1][row-1] + currentGrid[col+1][row];
			} else { // Else, left-edge cell
				count = currentGrid[col][row-1] + currentGrid[col+1][row-1] 
						+ currentGrid[col+1][row] + currentGrid[col+1][row+1] + currentGrid[col][row+1];
			}
		} else if (col == (gridWidth-1)) { // Cell is located along right edge of the grid
			if (row == 0) { // Cell is located in the top right corner
				count = currentGrid[col-1][row] + currentGrid[col-1][row+1] + currentGrid[col][row+1];
			} else if (row == (gridLength-1)) { // Cell is located in the bottom right corner
				count = currentGrid[col][row-1] + currentGrid[col-1][row-1] + currentGrid[col-1][row];
			} else { // Else, right-edge cell
				count = currentGrid[col][row-1] + currentGrid[col-1][row-1] 
						+ currentGrid[col-1][row] + currentGrid[col-1][row+1] + currentGrid[col][row+1];
			}
		} else if (row == 0) { // Cell is located along top edge of the grid
			count = currentGrid[col-1][row] + currentGrid[col-1][row+1] 
					+ currentGrid[col][row+1] + currentGrid[col+1][row+1] + currentGrid[col+1][row];
		} else if (row == (gridLength-1)) { // Cell is located along bottom edge of the grid
			count = currentGrid[col-1][row] + currentGrid[col-1][row-1] 
					+ currentGrid[col][row-1] + currentGrid[col+1][row-1] + currentGrid[col+1][row];
		} else { // Else, middle cell
			count = currentGrid[col][row-1] + currentGrid[col+1][row-1] 
					+ currentGrid[col+1][row] + currentGrid[col+1][row+1] + currentGrid[col][row+1]
					+ currentGrid[col-1][row+1] + currentGrid[col-1][row] + currentGrid[col-1][row-1];
		}*/
		return count;
	}

}
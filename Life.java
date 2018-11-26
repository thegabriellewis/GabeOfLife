import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

/*	Author: Gabriel Lewis
 *  Date: 11/03/2018
 *  Game of Life */

public class Life extends Application {

	Stage window; // Create a Stage (window) for application
	
	static int dead = 0;
	static int alive = 1;
	
	static int gridWidth, gridLength = 20;
	static int currentGrid[][] = new int[gridWidth][gridLength];
	static int previousGrid[][] = new int[gridWidth][gridLength];
	static int tempGrid[][] = new int[gridWidth][gridLength];
	
	public static void main(String[] args) { // main function
		launch(args);
		//initializeGrid();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		window = primaryStage; // Assign window the value of primaryStage
		window.setTitle("Game of Life"); // set window title
		
		GridPane grid = new GridPane(); // Create a GridPane
		grid.setPadding(new Insets(10,10,10,10)); // Set grid padding
		grid.setVgap(10);
		grid.setHgap(10);
	
		
		/* Creating A Cell: 
			1. Create the canvas
			2. Create GraphicsContext to draw on
			3. Set gc fill color - setFill(Paint p)
			4. Fill rectangle using current fill color - fillRect(x pos top left, y pos top left, width, height)
			5. Add canvas to GridPane - setConstraints(child, column, row)
			6. Add canvas to grid - getChildren().addAll()
		*/
		
		
		

		GridPane.setConstraints(canvasDead0, 0, 0); 		
		GridPane.setConstraints(canvasDead1, 0, 1);
		GridPane.setConstraints(canvasDead2, 0, 2);
		GridPane.setConstraints(canvasDead3, 0, 3);
		GridPane.setConstraints(canvasDead4, 0, 4);
	
		grid.getChildren().addAll(canvasDead0);
		grid.getChildren().addAll(canvasDead1);
		grid.getChildren().addAll(canvasDead2);
		grid.getChildren().addAll(canvasDead3);
		grid.getChildren().addAll(canvasDead4);
 
		Scene planet = new Scene(grid, 800, 800);
		window.setScene(planet);
		window.show();
	}
	
	public static void initializeGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				currentGrid[x][y] = dead;
				previousGrid[x][y] = dead;
			}
		}
	}
	
	public static void initializeTempGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				tempGrid[x][y] = dead;
			}
		}
	}
	
	public static void live() {
		initializeGrid();
		while (!(extinction()) && !(changeless())) {
			generation();
		}
	}
	
	public static void generation() {
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
	
	public static void updatePreviousGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				previousGrid[x][y] = currentGrid[x][y];
			}
		}
	}
	public static void updateCurrentGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				currentGrid[x][y] = tempGrid[x][y];
			}
		}
	}
	
	public static void birth(int col, int row) {
		if (currentGrid[col][row] == dead) {
			if (numNeighbors(col,row) == 3) {
				tempGrid[col][row] = alive;
			}
		}
	}
	
	public static void deathByIsolation(int col, int row) {
		if (currentGrid[col][row] == alive) {
			if (numNeighbors(col,row) <= 1) {
				tempGrid[col][row] = dead;
			}
		}
	}
	
	public static void deathByOverPopulation(int col, int row) {
		if (currentGrid[col][row] == alive) {
			if (numNeighbors(col,row) >= 4) {
				tempGrid[col][row] = dead;
			}
		}
	}
	
	public static void survival(int col, int row) {
		if (currentGrid[col][row] == alive) {
			if (numNeighbors(col,row) == 2 || numNeighbors(col,row) == 3) {
				tempGrid[col][row] = alive;
			}
		}
	}
	
	public static boolean extinction() { // If all cells are dead
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				if (currentGrid[x][y] == alive) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean changeless() { // If there is no change between generations
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
	
	public static int numNeighbors(int col, int row) {
		
		int count = 0;
		if (col == 0) { // Cell is located along left edge of the grid
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
		}
		return count;
	}

}





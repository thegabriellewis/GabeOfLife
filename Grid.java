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
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



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
	Cell[][] tempGridCells;
	Cell[][] prevGridCells;
	
	static String dead = "dead";
	static String alive = "alive";
	
	static Paint colorDead = Color.DARKBLUE;
	static Paint colorAlive = Color.RED;
	
	static int gridWidth = 20;
	static int gridLength = 20;
	
	
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
		Button button4 = new Button("random");
		Button button2 = new Button("+1 generation");
		Button button3 = new Button("life");

		button.setOnAction(e -> giveLife(Integer.valueOf(column.getText()),Integer.valueOf(row.getText())));//column.getText()
		button2.setOnAction(e -> generation());
		button3.setOnAction(e -> live());
		button4.setOnAction(e -> randomGrid());
		
		//layout
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20,20,20,20));
		layout.getChildren().addAll(column,row,button,button4,button2, button3);
		
		Grid();
		initializeGrid();
		Scene planet = new Scene(grid, 910, 910);
		Scene planet2 = new Scene(layout,300,250);
		
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
	
	public void initializeGrid() { // +
		cells = new Cell(gridWidth,gridLength);
		gridCells = cells.InitializeCells(gridWidth, gridLength);
		tempGridCells = cells.InitializeCells(gridWidth, gridLength);
		prevGridCells = cells.InitializeCells(gridWidth, gridLength);
		
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				GridPane.setConstraints(gridCells[x][y].canvas,x,y);
				grid.getChildren().addAll(gridCells[x][y].canvas);
			}
		}
	}
	
	public void randomGrid() {
		initializeGrid();
		Random rand = new Random();
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				int  n = rand.nextInt(3) + 1;
				if (n == 3) {
					giveLife(x,y);
				}
			}
		}
	}
	
	public void giveLife(int col, int row) { // +
		if ((col < gridWidth) && (row < gridLength)) {
			Cell newLife = new Cell(col,row);
			newLife.status = alive;
			newLife.gc.setFill(colorAlive);
			newLife.gc.fillRect(0, 0, 60, 60);
			tempGridCells[col][row] = newLife;
			updateCells();
		}
	}
	
	public void giveDeath(int col, int row) {
		if ((col < gridWidth) && (row < gridLength)) {
			Cell newLife = new Cell(col,row);
			newLife.status = dead;
			newLife.gc.setFill(colorDead);
			newLife.gc.fillRect(0, 0, 60, 60);
			tempGridCells[col][row] = newLife;
			updateCells();
		}
	}
	
	public void updateCells() { // +
		grid.getChildren().clear();
		updateCurrentGrid();
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				GridPane.setConstraints(gridCells[x][y].canvas,x,y);
				grid.getChildren().addAll(gridCells[x][y].canvas);
			}
		}
	}
	
	
	public void live(){
		int count = 0;
		while (true) {
			generation(); 
		}
	    
	}
	
	public void generation() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				birth(x,y);
				deathByIsolation(x,y);
				deathByOverPopulation(x,y);
				survival(x,y);
			}
		}
		updatePreviousGrid();
		updateCells();
	}
	
	public void updatePreviousGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				prevGridCells[x][y] = gridCells[x][y];
			}
		}
	}
	public void updateCurrentGrid() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				gridCells[x][y] = tempGridCells[x][y];
			}
		}
	}
	
	public void birth(int col, int row) {
		if (gridCells[col][row].status == dead) {
			if (numNeighbors(col,row) == 3) {
				giveLife(col,row);
			}
		}
	}
	
	public void deathByIsolation(int col, int row) {
		if (gridCells[col][row].status == alive) {
			if (numNeighbors(col,row) <= 1) {
				giveDeath(col,row);
			}
		}
	}
	
	public void deathByOverPopulation(int col, int row) {
		if (gridCells[col][row].status == alive) {
			if (numNeighbors(col,row) >= 4) {
				giveDeath(col,row);
			}
		}
	}
	
	public void survival(int col, int row) {
		if (gridCells[col][row].status == alive) {
			if (numNeighbors(col,row) == 2 || numNeighbors(col,row) == 3) {
				giveLife(col,row);
			}
		}
	}
	
	public boolean extinction() { // If all cells are dead
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				if (gridCells[x][y].status == alive) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean changeless() { // If there is no change between generations
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				if (gridCells[x][y].status != prevGridCells[x][y].status) {
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
		if (col == 0) { // Cell is located along left edge of the grid
			if (row == 0) { // Cell is located in the top left corner
				if (gridCells[col+1][row].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row+1].status == alive) {
					count += 1;
				}
				if (gridCells[col][row+1].status == alive) {
					count += 1;
				}
			} else if (row == (gridLength-1)) { // Cell is located in the bottom left corner
				if (gridCells[col][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row].status == alive) {
					count += 1;
				}
			} else { // Else, left-edge cell
				if (gridCells[col][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row+1].status == alive) {
					count += 1;
				}
				if (gridCells[col][row+1].status == alive) {
					count += 1;
				}
			}
		} else if (col == (gridWidth-1)) { // Cell is located along right edge of the grid
			if (row == 0) { // Cell is located in the top right corner
				if (gridCells[col-1][row].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row+1].status == alive) {
					count += 1;
				}
				if (gridCells[col][row+1].status == alive) {
					count += 1;
				}
				
			} else if (row == (gridLength-1)) { // Cell is located in the bottom right corner
				if (gridCells[col][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row].status == alive) {
					count += 1;
				}
				
			} else { // Else, right-edge cell
				if (gridCells[col][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row+1].status == alive) {
					count += 1;
				}
				if (gridCells[col][row+1].status == alive) {
					count += 1;
				}
			}
		} else if (row == 0) { // Cell is located along top edge of the grid
			if (gridCells[col-1][row].status == alive) {
				count += 1;
			}
			if (gridCells[col-1][row+1].status == alive) {
				count += 1;
			}
			if (gridCells[col][row+1].status == alive) {
				count += 1;
			}
			if (gridCells[col+1][row+1].status == alive) {
				count += 1;
			}
			if (gridCells[col+1][row].status == alive) {
				count += 1;
			}
		} else if (row == (gridLength-1)) { // Cell is located along bottom edge of the grid
			if (gridCells[col-1][row].status == alive) {
				count += 1;
			}
			if (gridCells[col-1][row-1].status == alive) {
				count += 1;
			}
			if (gridCells[col][row-1].status == alive) {
				count += 1;
			}
			if (gridCells[col+1][row-1].status == alive) {
				count += 1;
			}
			if (gridCells[col+1][row].status == alive) {
				count += 1;
			}
		} else { // Else, middle cell
			if (gridCells[col][row-1].status == alive) {
				count += 1;
			}
			if (gridCells[col+1][row-1].status == alive) {
				count += 1;
			}
			if (gridCells[col+1][row].status == alive) {
				count += 1;
			}
			if (gridCells[col+1][row+1].status == alive) {
				count += 1;
			}
			if (gridCells[col][row+1].status == alive) {
				count += 1;
			}
			if (gridCells[col-1][row+1].status == alive) {
				count += 1;
			}
			if (gridCells[col-1][row].status == alive) {
				count += 1;
			}
			if (gridCells[col-1][row-1].status == alive) {
				count += 1;
			}
		}
		return count;
	}

}
import javafx.application.Application;
import javafx.beans.property.StringProperty;
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
import com.sun.javafx.tk.Toolkit.Task;
import javafx.concurrent.WorkerStateEvent;

/*	
	Game of Life 
	Author: Gabriel Lewis
	Start Date: 11/03/2018
*/

public class Grid extends Application {
	
	Stage window;
	Stage secondary;
	GridPane grid;
	
	Cell cells;
	Cell[][] gridCells;
	Cell[][] tempGridCells;
	Cell[][] prevGridCells;
	
	static String dead = "dead";
	static String alive = "alive";
	static String neutral = "neutral";
	
	static Paint colorNeutral = Color.ALICEBLUE;
	static Paint colorDead = Color.BLACK;
	static Paint colorAlive = Color.LIMEGREEN;
	static Paint colorTeen = Color.DARKSLATEBLUE;
	static Paint colorAdult = Color.RED;
	
	
	static int gridWidth = 36;
	static int gridLength = 36;
	
	public Label label1a;
	public Label label2a;
	public Label label3a;
	
	Integer numGenerations = 0;
	Integer numBirths = 0;
	Integer numDeaths = 0;
	
	
	public static void main(String[] args) { // main function
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		secondary = new Stage();
		
		window.setTitle("Game of Life");
		secondary.setTitle("Game of Life");
		
		Label label1 = new Label("Generation: ");
		Label label2 = new Label("Births: ");
		Label label3 = new Label("Deaths: ");
		
		label1a = new Label(numGenerations.toString());
		label2a = new Label(numBirths.toString());
		label3a = new Label(numDeaths.toString());
		
		Button button2 = new Button("RANDOM");
		Button button3 = new Button("+1 GEN.");

		button3.setOnAction(e -> generation());
		button2.setOnAction(e -> randomGrid());
		
		GridPane.setConstraints(label1, 0,0);
		GridPane.setConstraints(label2, 0,1);
		GridPane.setConstraints(label3, 0,2);
		
		GridPane.setConstraints(label1a, 2,0);
		GridPane.setConstraints(label2a, 2,1);
		GridPane.setConstraints(label3a, 2,2);
		
		GridPane.setColumnSpan(label1a, 2);
		GridPane.setColumnSpan(label2a, 2);
		GridPane.setColumnSpan(label3a, 2);

		GridPane.setConstraints(button2, 0,3);
		GridPane.setConstraints(button3, 2, 3);
		GridPane.setColumnSpan(button2, 2);
		GridPane.setColumnSpan(button3, 2);
		
		//layout
		GridPane layout = new GridPane();
		layout.setPadding(new Insets(20,20,20,20));
		layout.setVgap(10);
		layout.setHgap(10);
		layout.getChildren().addAll(label1, label2, label3, label1a, label2a, label3a, button2, button3);
		
		Grid();
		initializeGrid();
		Scene planet = new Scene(grid, 910, 910);
		Scene planet2 = new Scene(layout,200,150);
		
		window.setScene(planet);
		secondary.setScene(planet2);
		
		window.show();
		secondary.show();
	}
	
	public void Grid() {
		grid = new GridPane();
		grid.setPadding(new Insets(10,10,10,10)); 
		grid.setVgap(5);
		grid.setHgap(5);
	
	}
	
	public void initializeGrid() { // +
		cells = new Cell(gridWidth,gridLength);
		gridCells = InitializeCells(gridWidth, gridLength);
		tempGridCells = InitializeCells(gridWidth, gridLength);
		prevGridCells = InitializeCells(gridWidth, gridLength);
		
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				GridPane.setConstraints(gridCells[x][y].canvas,x,y);
				grid.getChildren().addAll(gridCells[x][y].canvas);
			}
		}
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
	
	public void randomGrid() {
		initializeGrid();
		numBirths = 0;
		Random rand = new Random();
		for (int x = 3; x < gridWidth-3; x++) {
			for (int y = 3; y < gridLength-3; y++) {
				int  n = rand.nextInt(10) + 1;
				if (n == 3) {
					giveLife(x,y);
				}
			}
		}
	}
	
	public void giveLife(int col, int row) { // +
		if ((col < gridWidth) && (row < gridLength)) {
			tempGridCells[col][row].status = alive;
			tempGridCells[col][row].age = 0;
			tempGridCells[col][row].gc.setFill(colorAlive);
			tempGridCells[col][row].gc.fillRect(0, 0, 20, 20);
			numBirths += 1;
			updateCells();
		}
	}
	
	public void giveDeath(int col, int row) {
		if ((col < gridWidth) && (row < gridLength)) {
			tempGridCells[col][row].status = dead;
			tempGridCells[col][row].age = -1;
			tempGridCells[col][row].gc.setFill(colorDead);
			tempGridCells[col][row].gc.fillRect(0, 0, 20, 20);
			numDeaths += 1;
			updateCells();
		}
	}
	
	public void updateCells() { // +
		grid.getChildren().clear();
		updatePreviousGrid();
		updateCurrentGrid();
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				GridPane.setConstraints(gridCells[x][y].canvas,x,y);
				grid.getChildren().addAll(gridCells[x][y].canvas);
			}
		}
		label1a.setText(numGenerations.toString());
		label2a.setText(numBirths.toString());
		label3a.setText(numDeaths.toString());
	}
	
	
	public void live() throws InterruptedException {
		int count = 0;
		while (true) {
			generation();
		}  
	}
	
	public void generation() { 
		numBirths = 0;
		numDeaths = 0;
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				birth(x,y);
				deathByIsolation(x,y);
				deathByOverPopulation(x,y);
				survival(x,y);
				buryTheDead(x,y);
			}
		}
		numGenerations += 1;
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
		if (gridCells[col][row].status == neutral) {
			if (numNeighbors(col,row) == 3) {
				giveLife(col,row);
			}
		}
	}
	
	public void birthday(int col, int row) {
		if (tempGridCells[col][row].status == alive) {
			tempGridCells[col][row].age += 1;
			checkIfMature(col,row);
		}
	}
	
	public void checkIfMature(int col, int row) {
		if (tempGridCells[col][row].status == alive) {
			if (tempGridCells[col][row].age == 5) {
				tempGridCells[col][row].gc.setFill(colorTeen);
				tempGridCells[col][row].gc.fillRect(0, 0, 20, 20);
			}
			if (tempGridCells[col][row].age == 10) {
				tempGridCells[col][row].gc.setFill(colorAdult);
				tempGridCells[col][row].gc.fillRect(0, 0, 20, 20);
			}
		}
	}
	
	public void buryTheDead(int col, int row) {
		if ((tempGridCells[col][row].status == dead) && tempGridCells[col][row].age == -2) {
			tempGridCells[col][row].status = neutral;
			tempGridCells[col][row].age = 0;
			tempGridCells[col][row].gc.setFill(colorNeutral);
			tempGridCells[col][row].gc.fillRect(0, 0, 20, 20);
		} else {
			tempGridCells[col][row].age = -2;
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
				birthday(col,row);
			}
		}
	}
	
	public boolean extinction() {
		for (int x = 0; x < gridWidth; x++) {
			for (int y = 0; y < gridLength; y++) {
				if (gridCells[x][y].status == alive) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean changeless() {
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
		if (col == 0) {
			if (row == 0) {
				if (gridCells[col+1][row].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row+1].status == alive) {
					count += 1;
				}
				if (gridCells[col][row+1].status == alive) {
					count += 1;
				}
			} else if (row == (gridLength-1)) {
				if (gridCells[col][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col+1][row].status == alive) {
					count += 1;
				}
			} else {
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
		} else if (col == (gridWidth-1)) {
			if (row == 0) {
				if (gridCells[col-1][row].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row+1].status == alive) {
					count += 1;
				}
				if (gridCells[col][row+1].status == alive) {
					count += 1;
				}
				
			} else if (row == (gridLength-1)) {
				if (gridCells[col][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row-1].status == alive) {
					count += 1;
				}
				if (gridCells[col-1][row].status == alive) {
					count += 1;
				}
				
			} else {
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
		} else if (row == 0) {
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
		} else if (row == (gridLength-1)) {
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
		} else {
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
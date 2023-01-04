package application;
/*TIC TAC TOE CONTROLLER
 * Author: Frank Chen
 * Date: 04/10/2022
 * Purpose: The methods and click handlers for our tic tac toe program reside here
 */

import java.io.File;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
//imports what we need
public class TicTacToeController {
	@FXML
	Button b1;
	@FXML
	Button b2;
	@FXML
	Button b3;
	@FXML
	Button b4;
	@FXML
	Button b5;
	@FXML
	Button b6;
	@FXML
	Button b7;
	@FXML
	Button b8;
	@FXML
	Button b9;
	@FXML
	GridPane gameBoard;
	//imports our buttons and gameboard
	Button[][] board;
	//creates an empty 2d array for our board

	// Deals with button clicks
	private boolean isFirstPlayer = true;
	private String gameState = "playerVersusPlayer";
	private boolean won = false;
	Stage secondaryStage;
	//creates variables that we will use in the future of our code
	
	//handles button clicks
	public void buttonClickHandler(ActionEvent evt) {
		playClickSound(); //plays the click sound when we click on the board
		//if our board is empty, fills it up into a 3x3 board with our buttons. This is important for checking for a winner and AI.
		if (board == null)
			board = new Button[][] { { b1, b2, b3 }, { b4, b5, b6 }, { b7, b8, b9 } }; // we must put values into our 2d
																						// array here because it's ONLY HERE where these values are not null
			
		Button clickedButton = (Button) evt.getTarget();
		String buttonLabel = clickedButton.getText();

		//checks if the button is empty, if the game is still ongoing, and if it's X's turn. If both are true, puts down an X at that spot.
		if ("".equals(buttonLabel) && isFirstPlayer && find3InARow(true).equals("n")) {
			clickedButton.setText("X");
			isFirstPlayer = false;

			// if hard Ai is selected, bestmove is involked which places an O, then makes it back to X's turn.
			if (gameState.equals("playerVersusHardAI") && !find3InARow(true).equals("t")) {
				bestMove();
			// if easy Ai is selected, randommove is involked which places an O, then makes it back to X's turn.
			} else if (gameState.equals("playerVersusEasyAI") && !find3InARow(true).equals("t")) {
				randomMove();
			}
		//if the conditionals above are not satisfied, that means the gamemode is Player vs Player, which lets a player place the O
		} else if ("".equals(buttonLabel) && !isFirstPlayer && find3InARow(true).equals("n")) {
			clickedButton.setText("O");
			isFirstPlayer = true;

		}
		if (won == false) { //if the game hasn't been won yet, checks if the game is won.
			//this is important because this makes the highlight winning combo run ONCE. If more are ran, the remove class code
			//will only remove the first 'layer', so the colors may not always be erased.
			String result = find3InARow(false);
			if (result.equals("X")) { //if X won, it will play the triumphant sound
				playWinSound();
			} else if (result.equals("O")) { //if O won, it will play the loss sound
				playLossSound();
			}
			
			
		}

	}
	
	private void playClickSound(){ //method to play a clicking sound
		AudioClip note = new AudioClip(this.getClass().getResource("click.mp3").toString());
		note.play();
	}
	
	private void playWinSound(){ //method to play a winning jingle
		AudioClip note = new AudioClip(this.getClass().getResource("win.mp3").toString());
		note.play();
	}
	
	private void playLossSound(){ //method to play a sad trombone from the price is right
		AudioClip note = new AudioClip(this.getClass().getResource("lplusratio.mp3").toString());
		note.play();
	}
	
	//method to highlight the three winning buttons
	private void highlightWinningCombo(Button first, Button second, Button third) {
		first.getStyleClass().add("winning-button");
		second.getStyleClass().add("winning-button");
		third.getStyleClass().add("winning-button");
		won = true; //makes won==true so this method won't run after the first time
	}

	//method to handle the menu getting clicked
	public void menuClickHandler(ActionEvent evt) {
		MenuItem clickedMenu = (MenuItem) evt.getTarget();
		String menuLabel = clickedMenu.getText();
		
		
		//if play is clicked, clears everything, makes the gamemode PvP, then makes the boolean won=false
		if ("Play".equals(menuLabel)) {
			ObservableList<Node> buttons = gameBoard.getChildren();

			buttons.forEach(btn -> {
				((Button) btn).setText("");
				btn.getStyleClass().remove("winning-button");
			});

			gameState = "playerVersusPlayer";
			isFirstPlayer = true;
			won = false;
		}
		
		//if play hard ai is clicked, clears everything, makes the gamemode PvEasyAI, then makes the boolean won=false
		if ("Play(Hard AI)".equals(menuLabel)) {
			ObservableList<Node> buttons = gameBoard.getChildren();

			buttons.forEach(btn -> {
				((Button) btn).setText("");

				btn.getStyleClass().remove("winning-button");
			});

			gameState = "playerVersusHardAI";
			isFirstPlayer = true;
			won = false;
		}
		
		//if play easy AI is clicked, clears everything, makes the gamemode PvHardAI, then makes the boolean won=false
		if ("Play(Easy AI)".equals(menuLabel)) {
			ObservableList<Node> buttons = gameBoard.getChildren();

			buttons.forEach(btn -> {
				((Button) btn).setText("");

				btn.getStyleClass().remove("winning-button");
			});

			gameState = "playerVersusEasyAI";
			isFirstPlayer = true;
			won = false;
		}
		
		//if quit is clicked, exits the program
		if ("Quit".equals(menuLabel)) {
			Platform.exit();
			System.exit(0);
		}
		
		//if how to play is clicked, involkes the openhowtowindow method
		if ("How To Play".equals(menuLabel)) {
			openHowToWindow();
		}
		
		//if about is clicked, involkes the openabout method
		if ("About".equals(menuLabel)) {
			openAbout();
		}

	}
	
	//method to launch a new window with the howtoplay fxml file
	private void openHowToWindow() {
		try {
			// load the pop up you created
			Pane howTo = (Pane) FXMLLoader.load(getClass().getResource("HowToPlay.fxml"));

			// create a new scene
			Scene howToScene = new Scene(howTo, 250, 250);

			// add css to the new scene
			// howToScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// create new stage to put scene in
			secondaryStage = new Stage();
			secondaryStage.setScene(howToScene);
			secondaryStage.setResizable(false);
			secondaryStage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//method to launch a new window with the about fxml file
	private void openAbout() {
		try {
			// load the pop up you created
			Pane howTo = (Pane) FXMLLoader.load(getClass().getResource("About.fxml"));

			// create a new scene
			Scene howToScene = new Scene(howTo, 250, 250);

			// add css to the new scene
			// howToScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// create new stage to put scene in
			secondaryStage = new Stage();
			secondaryStage.setScene(howToScene);
			secondaryStage.setResizable(false);
			secondaryStage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// close the window that is currently open
	// it figures out where the event came from and closes that stage
	public void closeCurrentWindow(final ActionEvent evt) {
		final Node source = (Node) evt.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		stage.close();
	}

	//method to make the O get placed on a random open button
	public void randomMove() {
		while (true) { //makes this loop run until an O is placed
			int i = (int) (Math.random() * 3); 
			int j = (int) (Math.random() * 3); //generates a random coordinate in the 3x3 grid

			int openSpots = 0; //checks how many open spots there are
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					if (board[x][y].getText().equals("")) {
						openSpots++;
					}
				}
			}
			//if there are no open spots left, returns
			if (openSpots == 0) {
				return;
			} else
			//if there are open spots, checks if the randomly generated spot is empty
			//if it is, sets it as O and returns
			if (board[i][j].getText().equals("")) {
				board[i][j].setText("O");
				isFirstPlayer = true;
				return;
			}
		}
	}
	
	//method to calculate the best possible move
	public void bestMove() {
		int bestScore = Integer.MIN_VALUE; //min value so that score is guranteed to be bigger than it on the first runthrough
		int[] move = { 0, 0 }; //keeps track of our best move coordinates

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) { //runs through each button on our grid
				// Checks if the displayed text is zero, if it is, changes it to 'O'
				if (board[i][j].getText().equals("")) {
					board[i][j].setText("O");
					int score = minimax(0, false); //then runs minimax to get a score
					board[i][j].setText(""); //turns the O back to empty
					if (score > bestScore) { //if the score we just got is EVER better than the current score, we make that our new current score
						bestScore = score;
						move[0] = i; //thus, these coordinates will correspond to our best score, hence it will be our best move
						move[1] = j;
					}
				}
			}
		}

		board[move[0]][move[1]].setText("O"); //after every possibility is considered, takes our best score's coordinates and fills it with O
		isFirstPlayer = true; //makes it out player's turn
	}
	
	//recursive method to help us calculate the best possible score
	private int minimax(int depth, boolean isMaximizing) {
		//essentially, this method uses recursion. It cycles through each possibility where an X or O can go, then takes that, calls it's own 
		//method recursively, which does the same thing and places the alternate symbol, then calls it's own method recursively again.
		//this essentially goes on until the board is filled, upon which, the scores are returned, choosing the best/worst scores and sending them back
		//to each level... essentially we are left with an optimal path to the end.
		
		String result = find3InARow(true); //first checks whether or not the game is either won or the board is full 
		if (!result.equals("n")) {
			if (result.equals("X")) { //if X won, returns -10
				return -10;
			} else if (result.equals("O")) { //if O won, returns 10
				return 10;
			} else {
				return 0; //if it is a tie, returns 0
			}
		}

		if (isMaximizing) { //if we are trying to MAXIMIZE/true (it is O's turn)
			int bestScore = Integer.MIN_VALUE;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					// Is the spot available?
					if (board[i][j].getText().equals("")) { 
						board[i][j].setText("O"); //finds empty spots, sets it to O and runs the recursive method
						int score = minimax(depth + 1, false);
						board[i][j].setText("");
						if (score > bestScore) { //takes the best score(maximize) out of every possiblity the method has ran through
							bestScore = score;
						}
					}
				}
			}
			return bestScore;

			//flip-flops between the player's optimal move and the computer's optimal move
		} else { //if we are trying to MINIMIZE/true (it is X's turn) 
			int bestScore = Integer.MAX_VALUE;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					// Is the spot available?
					if (board[i][j].getText().equals("")) {
						board[i][j].setText("X"); //finds empty spots, sets it to X and runs the recursive method
						int score = minimax(depth + 1, true);
						board[i][j].setText("");
						if (score < bestScore) { //takes the lowest score(minimum) out of every possiblity the method has ran through
							bestScore = score;
						}
					}
				}
			}
			return bestScore;
		}
	}

	//method to find if three of a kind exists in any row, column, or diagonal in the 3x3 grid we created earlier
	private String find3InARow(boolean isRecursion) {
		//loops through the three rows and checks if any of them are 3 Xs or Os
		for (int x = 0; x < 3; x++) {
			if ("" != board[x][0].getText() && board[x][0].getText() == board[x][1].getText()
					&& board[x][0].getText() == board[x][2].getText()) {

				if (isRecursion == false) { //if this is not involked by our recursive method, it will highlight the board
					highlightWinningCombo(board[x][0], board[x][1], board[x][2]);
				}
				
				return board[x][0].getText(); //returns the winning character
			}
		}

		//loops through the three columns and checks if any of them are 3 Xs or Os
		for (int x = 0; x < 3; x++) {
			if ("" != board[0][x].getText() && board[0][x].getText() == board[1][x].getText()
					&& board[0][x].getText() == board[2][x].getText()) {

				if (isRecursion == false) { //if this is not involked by our recursive method, it will highlight the board
					highlightWinningCombo(board[0][x], board[1][x], board[2][x]);
				}

				return board[0][x].getText(); //returns the winning character
			}
		}
		
		//Checks the diagonal if there are 3 Xs or Os
		if ("" != b1.getText() && b1.getText() == b5.getText() && b5.getText() == b9.getText()) {

			if (isRecursion == false) { //if this is not involked by our recursive method, it will highlight the board
				highlightWinningCombo(b1, b5, b9);
			}

			return b1.getText(); //returns the winning character
		}
		
		//Checks the other diagonal if there are 3 Xs or Os
		if ("" != b3.getText() && b3.getText() == b5.getText() && b5.getText() == b7.getText()) {

			if (isRecursion == false) {//if this is not involked by our recursive method, it will highlight the board
				highlightWinningCombo(b3, b5, b7);
			}

			return b3.getText(); //returns the winning character
		}
		
		//checks if there are any open spots left on the board
		int openSpots = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j].getText().equals("")) {
					openSpots++;
				}
			}
		}
		
		//if there are no more open spots, returns t for tie
		if (openSpots == 0) {
			return "t";
		} else { //if there are still open spots, returns n for no win (yet)?
			return "n";
		}

	}

}




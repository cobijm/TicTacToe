package tictactoe;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TicTacToe extends Application {

	private boolean playable = true;
	private boolean turnX = true;
	private int count = 0;
	private Box[][] board = new Box[3][3];
	private List<Pattern> patterns = new ArrayList<>();
	private Text currPlayer = new Text("X");
	private Text playerLabel = new Text("Current Player: ");

	private Pane root = new Pane();

	//initialize board
	private Parent createContent() {
		root.setPrefSize(600, 700);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Box tile = new Box();
				tile.setTranslateX(j * 200);
				tile.setTranslateY(i * 200);

				root.getChildren().add(tile);

				board[j][i] = tile;
			}
		}

		for (int y = 0; y < 3; y++) {
			patterns.add(new Pattern(board[0][y], board[1][y], board[2][y]));
		}

		for (int x = 0; x < 3; x++) {
			patterns.add(new Pattern(board[x][0], board[x][1], board[x][2]));
		}
		
		patterns.add(new Pattern(board[0][0], board[1][1], board[2][2]));
		patterns.add(new Pattern(board[2][0], board[1][1], board[0][2]));
		
		//Current player setting
		currPlayer.setX(525);
		currPlayer.setY(670);
		currPlayer.setFont(Font.font(72));
		playerLabel.setX(0);
		playerLabel.setY(670);
		playerLabel.setFont(Font.font(72));
		root.getChildren().addAll(playerLabel, currPlayer);
		
		return root;
	}

	//start 
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(createContent()));
		primaryStage.show();
	}

	//check if winner or draw
	private void checkState() {
		for (Pattern pattern : patterns) {
			//winner
			if (pattern.isComplete()) {
				playable = false;
				winner(pattern);
				break;
			}
		}
		//draw
		if (count == 9) {
			Alert a = new Alert(AlertType.INFORMATION, "DRAW");
			a.setHeaderText(null);
			a.showAndWait();
			playAgain();
		}
	}

	//if there is a winner, show winner
	private void winner(Pattern pattern) {
		Alert a = new Alert(AlertType.INFORMATION, "Congratulations, " + pattern.boxes[2].getValue() + " is the winner");
		a.setHeaderText(null);
		a.showAndWait();
		playAgain();
	}

	//prompt asking to play again
	private void playAgain() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("Would you like to play again? Click OK for yes, cancel for no");
		Optional<ButtonType> result = alert.showAndWait();
		if (!result.isPresent()) {
			// alert is exited, no button has been pressed.
		} else if (result.get() == ButtonType.OK) {
			resetBoard();
		} else if (result.get() == ButtonType.CANCEL) {
			Platform.exit();
		}

	}

	//condition to win
	private class Pattern {
		private Box[] boxes;

		public Pattern(Box... boxes) {
			this.boxes = boxes;
		}

		public boolean isComplete() {
			if (boxes[0].getValue().isEmpty()) {
				return false;
			}

			return boxes[0].getValue().equals(boxes[1].getValue()) && boxes[0].getValue().equals(boxes[2].getValue());
		}
	}

	//fill in the boxes
	private class Box extends StackPane {
		private Text text = new Text();
		final Image oImage = new Image("o.jpg");
		final Image xImage = new Image("x.jpg");
		private ImageView imageView = new ImageView();
		public boolean occupied = false;

		public Box() {
			//individual boxes
			Rectangle border = new Rectangle(200, 200);
			border.setFill(null);
			border.setStroke(Color.BLACK);

			//value inside(hidden)
			text.setFont(Font.font(72));

			imageView.setFitHeight(200);
			imageView.setFitWidth(200);
			setAlignment(Pos.CENTER);
			getChildren().addAll(border, imageView);

			//event to check for mouse clicks
			setOnMouseClicked(event -> {
				//check if game is playable
				if (!playable) {
					resetBoard();
					return;
				}

				//if button is pressed, determine whose turn and set conditions accordingly
				//if it is player X turn
				if (event.getButton() == MouseButton.PRIMARY && turnX && !this.occupied) {
					this.occupied = true;
					drawX();
					turnX = false;
					count++;
					currPlayer.setText("O");
					checkState();
					return;
				//if it is player O turn
				} else if (event.getButton() == MouseButton.PRIMARY && !turnX && !this.occupied) {
					this.occupied = true;
					drawO();
					turnX = true;
					count++;
					currPlayer.setText("X");
					checkState();
					return;
				}
			});
		}

		//get the text value of the square
		public String getValue() {
			return text.getText();
		}

		//draw X square with hidden value
		private void drawX() {
			text.setText("X");
			imageView.setImage(xImage);
		}

		//draw O square with hidden value
		private void drawO() {
			text.setText("O");
			imageView.setImage(oImage);
		}
	}

	//reset conditions of board to replay
	public void resetBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j].text.setText(null);
				board[i][j].imageView.setImage(null);
				board[i][j].occupied = false;
			}
		}
		count = 0;
		playable = true;
		turnX = true;
		currPlayer.setText("X");
	}

	//launch
	public static void main(String[] args) {
		launch(args);
	}
}
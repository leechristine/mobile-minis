package com.example.game.TilesGame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.game.Activities.main.ScoreManager;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * An abstract board manager class that implements the Board interface and should not be
 * instantiated. *
 */
abstract class BoardManager extends ClassLoader implements Board {

  /** The width of a tile. (Default is 4X4) */
  int tileWidth = TileManager.getWidth4By4();

  /** The height of a tile. (Default is 4X4) */
  int tileHeight = TileManager.getHeight4By4();

  /** The width of this board. (Default is 4X4 board) */
  int boardWidth = 4 * tileWidth;

  /** The height of this board. (Default is 4X4 board) */
  int boardHeight = 4 * tileHeight;

  /** A list of all tiles on this board. */
  ArrayList<ArrayList<Tile>> tileBoard = new ArrayList<>(); // Index 0 is top of board

  /** A boolean representing whether the game has started. */
  boolean gameStart = false;

  /** A boolean representing whether the game has ended. */
  boolean gameEnd = false;

  TileFactory tileFactory;

  ScoreManager scoreManager;

  private TileDrawer tileDrawer;

  /** Construct a board manager. */
  BoardManager(Context context) {
    scoreManager = new ScoreManager(context.getSharedPreferences("highScores", MODE_PRIVATE));
    tileFactory = new TileFactory();
    tileDrawer = new TileDrawer(tileWidth, tileHeight);
  }

  public int getScore() {
    return scoreManager.getScore();
  }

  public boolean isGameStart() {
    return gameStart;
  }

  public void setGameStart(boolean gameStart) {
    this.gameStart = gameStart;
  }

  public boolean isGameEnd() {
    return gameEnd;
  }

  /** Create the starting items in a board. */
  public void createBoardItems() {
    // Add a hidden row of tiles above board to appear when game starts.
    tileBoard.add(new ArrayList<Tile>());

    // Add five arrays to tileBoard to represent the five onscreen rows of tiles.
    tileBoard.add(new ArrayList<Tile>());
    tileBoard.add(new ArrayList<Tile>());
    tileBoard.add(new ArrayList<Tile>());
    tileBoard.add(new ArrayList<Tile>());
    tileBoard.add(new ArrayList<Tile>());

    Random ran = new Random(); // Use a random variable to randomize the key tile in each row.

    // Fill first five rows with both danger tiles and key tiles.
    for (int i = 0; i < 5; i++) {
      ArrayList<Tile> tileRow = tileBoard.get(i);
      Integer keyTileIndex = ran.nextInt(4);
      for (int j = 0; j < 4; j++) {
        if (keyTileIndex.equals(j)) {
          tileRow.add(tileFactory.getKeyTile(j * tileWidth, (i - 2) * tileHeight));
        } else {
          tileRow.add(tileFactory.getDangerTile(j * tileWidth, (i - 2) * tileHeight));
        }
      }
    }
    // Fill last row with danger tiles.
    ArrayList<Tile> tileRow = tileBoard.get(5);
    for (int j = 0; j < 4; j++) {
      tileRow.add(tileFactory.getDangerTile(j * tileWidth, 3 * tileHeight));
    }
  }

  /** Update the items in a board. */
  public void update() {
    if (gameStart) {
      // Check if the game will end this turn.
      if (doesGameEnd()) {
        gameEnd = true;
        return;
      }
      // Move all the tiles on this board, incrementing the speed by 50 every 15 points.
      int increment = Math.floorDiv(scoreManager.getScore(), 15);
      for (ArrayList<Tile> tileRow : tileBoard) {
        for (Tile tile : tileRow) {
          tile.move(100 + (increment * 50));
        }
      }
      // Populate top of board with new tiles and remove tiles that have passed bottom of board.
      populate();
    }
  }

  /** Draw the items in a board. */
  public void draw(Canvas canvas) {
    // Draw tiles.
    for (ArrayList<Tile> tileRow : tileBoard) {
      for (Tile tile : tileRow) {
        tileDrawer.draw(canvas, tile);
      }
    }
    // Draw score.
    drawScore(canvas);
  }

  /** Draw the score. */
  private void drawScore(Canvas canvas) {
    Paint paint = new Paint();
    paint.setTypeface(Typeface.DEFAULT_BOLD);
    paint.setTextSize(80);
    paint.setColor(Color.MAGENTA);
    canvas.drawText(scoreManager.getScore() + "", (2 * tileWidth - 35), 150, paint);
  }

  /**
   * Mark the tile at location (x, y) as touched.
   *
   * @param x: the x-coordinate of the touched tile.
   * @param y: the y-coordinate of the touched tile.
   */
  public void touchTile(float x, float y) {
    for (ArrayList<Tile> tileRow : tileBoard) {
      for (Tile tile : tileRow) {
        if ((tile.getX() <= x && x <= (tile.getX() + tileWidth))
            && (tile.getY() <= y && y <= (tile.getY() + tileHeight))) { // If this tile was touched
          if (!tile.isTouch()) { // If tile has not already been touched.
            tile.setTouch(true);
          }
          if (tile instanceof KeyTile) {
            scoreManager.addScore("tiles"); // Increment score by one (only if tile is a KeyTile).
          }
        }
      }
    }
  }

  /** Check if the game is going to end. * */
  boolean doesGameEnd() {
    for (ArrayList<Tile> tileRow : tileBoard) {
      for (Tile tile : tileRow) {
        if (tile instanceof DangerTile
            && tile.isTouch()) { // Check if a danger tile has been touched.
          return true;
        }
        if (tile instanceof KeyTile && !tile.isTouch() && tile.getY() >= boardHeight - 80) {
          ((KeyTile) tile).setMissed(true);
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Populate top of board with a new row of tiles once the bottom row of tiles has passed the
   * bottom of the board. *
   */
  void populate() {
    if (tileBoard.get(5).get(0).getY()
        >= boardHeight) { // If the last row of tiles has passed the bottom.
      // Move the first five rows of tiles one spot to the right in tileBoard array.
      for (int i = 5; i > 0; i--) {
        tileBoard.set(i, tileBoard.get(i - 1));
      }

      // Add a new row of tiles to the top of the board.
      ArrayList<Tile> newTileRow = new ArrayList<>();
      tileBoard.set(0, newTileRow);
      int newTileY = tileBoard.get(1).get(0).getY() - tileHeight;

      // Use a random variable to randomize the key tile in new row.
      Random ran = new Random();
      Integer keyTileIndex = ran.nextInt(4);

      // Fill new row with both danger tiles and key tiles.
      for (int j = 0; j < 4; j++) {
        if (keyTileIndex.equals(j)) {
          newTileRow.add(tileFactory.getKeyTile(j * tileWidth, newTileY));
        } else {
          newTileRow.add(tileFactory.getDangerTile(j * tileWidth, newTileY));
        }
      }
    }
  }
}

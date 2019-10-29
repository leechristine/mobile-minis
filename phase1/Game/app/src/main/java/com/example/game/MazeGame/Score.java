package com.example.game.MazeGame;

/**
 * Class representing the player's current score. Score is currently calculated based on the number
 * of steps the player has taken.
 */
public class Score {

  // Current score.
  int score = 0;

  public Score(int score) {
    this.score = score;
  }

  /** Increment current score. */
  void incrementScore() {
    score += 1;
  }

  /**
   * Multiply score by the factor of the collectable.
   *
   * @param collectable a score affecting item
   */
  void multiplyScore(Collectable collectable) {
    score *= collectable.getMultiplier();
  }

  /** Getter for score. */
  int getScore() {
    return score;
  }
}

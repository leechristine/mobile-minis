package com.example.game.Activities.main.GameLauncher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.Activities.main.MainActivity;
import com.example.game.Activities.main.ThemeManager;
import com.example.game.R;
import com.example.game.Save.User;

public class TilesGameLauncher extends AppCompatActivity {
  User usr;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Set the theme.
    usr = (User) getIntent().getSerializableExtra("UserObject");
    String username = usr.getUsername();
    SharedPreferences mSettings = this.getSharedPreferences("Settings", MODE_PRIVATE);
    ThemeManager.setTheme(TilesGameLauncher.this, mSettings.getInt(username + "theme", 0));

    super.onCreate(savedInstanceState);
    setContentView(R.layout.tiles_game_launch);
  }

  /** Called when the user taps the '4 X 4' button */
  public void startTiles4By4(View view) {
    Intent intent = new Intent(this, com.example.game.TilesGame.TileGameActivity.class);
    intent.putExtra("UserObject", usr);
    intent.putExtra("BoardType", "4By4");
    startActivity(intent);
  }

  /** Called when the user taps the '5 X 5' button */
  public void startTiles5By5(View view) {
    Intent intent = new Intent(this, com.example.game.TilesGame.TileGameActivity.class);
    intent.putExtra("UserObject", usr);
    intent.putExtra("BoardType", "5By5");
    startActivity(intent);
  }

  /** Called when the user taps the 'EXIT' button */
  public void exitTilesGame(View view) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra("UserObject", usr);
    startActivity(intent);
  }
}
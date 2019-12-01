package com.example.game.DataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {UserAccount.class, UserScores.class, UserCollectibles.class},
    version = 2, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

  // Create singleton; so can't create multiple instances
  private static UserDatabase instance;

  // To access our Dao (Data access objects)
  public abstract UserAccountDao userAccountDao();

  public abstract UserScoresDao userScoresDao();

  public abstract UserCollectiblesDao userCollectiblesDao();

  // synchrnoized means only one thread can access this method at a time
  public static synchronized UserDatabase getInstance(Context context) {
    if (instance == null) {
      instance =
          Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, "user_database")
              .fallbackToDestructiveMigration()
              .build();
    }
    return instance;
  }
}

package com.hienqp.roomdatabasetincodeyoutube.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hienqp.roomdatabasetincodeyoutube.User;

// thay đổi version thành 2
@Database(entities = {User.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "user.db";
    private static UserDatabase instanceUserDatabase;

    // khai báo 1 Migration với tham số cho Constructor là oldVersion và newVersion
    private static Migration migration_from_1_to_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // câu lệnh SQL truy vấn database
            // ALTER TABLE tên_bảng ADD COLUMN tên_cột định_dạng
            database.execSQL("ALTER TABLE user ADD COLUMN year TEXT");
        }
    };

    public static synchronized UserDatabase getInstance(Context context) {
        if (instanceUserDatabase == null) {
            instanceUserDatabase = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries() // cho phép setting Query trên MainThread
                    .addMigrations(migration_from_1_to_2) // thêm method addMigrations(Migration) để update database
                    .build();
        }

        return instanceUserDatabase;
    }

    public abstract UserDAO userDAO();
}

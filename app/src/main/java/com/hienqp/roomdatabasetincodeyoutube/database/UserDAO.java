package com.hienqp.roomdatabasetincodeyoutube.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hienqp.roomdatabasetincodeyoutube.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getListUser();

    @Query("SELECT * FROM user WHERE username= :username")
    List<User> checkUser(String username);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM user")
    void deleteAllUser();

    @Query("SELECT * FROM user WHERE username LIKE '%' || :name || '%'")
    List<User> searchUser(String name);
}

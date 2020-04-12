package com.example.labGPS.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao

public interface MemoryDao {
//описываем методы для работы с базой данных.
// Нам нужны будут методы для получения/добавления/изменения/удаления заметок

    @Query("SELECT * FROM memory")
    public List<Memory> getAll();

    @Query("SELECT * FROM memory WHERE title LIKE :title AND " +
            "time LIKE :time AND "+"description LIKE :description")
    Memory findByName(String title, String time, String description);

    @Insert
    void insert(Memory...memory);

    @Delete
    void delete(Memory...memory);

    //Удалить один элемент по id
    @Query("DELETE FROM Memory WHERE id = :itemId")
    void deleteByItemId(long itemId);

    @Update
    void update(Memory...memory);

    @Query("DELETE FROM Memory")
    public void deleteall();
}

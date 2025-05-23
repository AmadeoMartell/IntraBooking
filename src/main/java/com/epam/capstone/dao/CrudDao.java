package com.epam.capstone.dao;

import java.util.List;

public interface CrudDao <T, V>{
    T findById(V id);
    List<T> findAll();
    void save(T t);
    void update(T t);
    void delete(T t);
}

package service;

import domain.EntityInterface;

import java.util.UUID;

public interface ServiceInterface<T extends EntityInterface> {
    void create(T entity);
    void edit(T entity);
    void delete(T entity);
    void listAll();
    T getById(UUID id);
}

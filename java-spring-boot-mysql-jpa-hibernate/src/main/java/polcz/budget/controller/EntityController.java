package polcz.budget.controller;

import org.springframework.beans.factory.annotation.Autowired;

import polcz.budget.model.AbstractEntity;
import polcz.budget.service.EntityService;

public class EntityController<T extends AbstractEntity> {

    @Autowired
    private EntityService service;

    public String create(T entity) {
        try {
            service.update(entity);
        } catch (Exception ex) {
            return "Error creating the entity: " + ex.toString();
        }
        return "Entity succesfully created!";
    }

    public String delete(T entity, int uid) {
        try {
            entity.setUid(uid);
            service.remove(entity);
        } catch (Exception ex) {
            return "Error deleting the entity: " + ex.toString();
        }
        return "Entity succesfully deleted!";
    }
}
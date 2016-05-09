package polcz.budget.controller;

import org.springframework.beans.factory.annotation.Autowired;

import polcz.budget.model.AbstractNameDescEntity;
import polcz.budget.service.EntityService;

public class NameDescController<T extends AbstractNameDescEntity> {

    @Autowired
    private EntityService service;

    // private Class<T> entityClass;
    //
    // public NameDescController(Class<T> entityClass) {
    // this.entityClass = entityClass;
    // }

    public String create(T entity) {
        try {
            service.create(entity);
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

    public String update(int id, String name, String desc, Class<T> entityClass) {
        try {
            T entity = service.find(id, entityClass);
            entity.setName(name);
            entity.setDesc(desc);
            service.update(entity);
        } catch (Exception ex) {
            return "Error updating the entity: " + ex.toString();
        }
        return "Entity succesfully updated!";
    }
}
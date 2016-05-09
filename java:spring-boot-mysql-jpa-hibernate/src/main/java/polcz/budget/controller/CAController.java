package polcz.budget.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.model.TChargeAccount;
import polcz.budget.service.CAService;

/**
 * Class UserController
 */
@Controller
@RequestMapping(value = "/ca")
public class CAController {

    @Autowired
    private CAService service;

    @RequestMapping("/")
    @ResponseBody
    public String test() {
        return CAController.class.getSimpleName() + " works fine!";
    }
    
    @RequestMapping(value = "/create")
    @ResponseBody
    public String create(String name, String desc) {
        try {
            TChargeAccount entity = new TChargeAccount(name, desc);
            service.create(entity);
        } catch (Exception ex) {
            return "Error creating the entity: " + ex.toString();
        }
        return "Entity succesfully created!";
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public String delete(int uid) {
        try {
            TChargeAccount entity = new TChargeAccount();
            entity.setUid(uid);
            service.remove(entity);
        } catch (Exception ex) {
            return "Error deleting the entity: " + ex.toString();
        }
        return "Entity succesfully deleted!";
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String updateName(int id, String name, String desc) {
        try {
            TChargeAccount entity = service.find(id);
            entity.setName(name);
            entity.setDesc(desc);
            service.update(entity);
        } catch (Exception ex) {
            return "Error updating the entity: " + ex.toString();
        }
        return "Entity succesfully updated!";
    }
}
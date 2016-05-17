package polcz.budget.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.model.Cluster;
import polcz.budget.model.Ugylet;
import polcz.budget.service.EntityService;

@Controller
@RequestMapping(value = "/tr")
public class TransactionController extends EntityController<Ugylet> {

    @Autowired
    EntityService service;

    @RequestMapping("/")
    @ResponseBody
    public String test() {
        return TransactionController.class.getSimpleName() + " works fine!";
    }

    @RequestMapping(value = "/get/{uid}")
    public @ResponseBody Ugylet getByUid(@PathVariable int uid) {
        return service.find(uid, Ugylet.class);
    }

    @RequestMapping(value = "/get/{from}/{to}")
    public @ResponseBody List<Ugylet> getRange(@PathVariable int from, @PathVariable int to) {
        return service.findRange(new int[] { from, to }, Ugylet.class);
    }
    
    @RequestMapping(value = "/get")
    public @ResponseBody List<Ugylet> getAll() {
        return service.findAll(Ugylet.class);
    }
}
package polcz.budget.controller;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.model.TCluster;
import polcz.budget.service.EntityService;

@Controller
@RequestMapping(value = "/cl")
public class ClusterController extends NameDescController<TCluster> {

    @Autowired
    EntityService service;

    @RequestMapping("/")
    @ResponseBody
    public String test() {
        return ClusterController.class.getSimpleName() + " works fine!";
    }

    public String create(String name, int sgn, TCluster parent) {
        return super.create(new TCluster(name, sgn, parent));
    }

    @RequestMapping(value = "/create")
    @ResponseBody
    public String createAlt1(String name, int sgn, String parent) {
        System.out.println("parent = " + parent);
        TCluster parentCl = service.findByName(parent, TCluster.class);
        System.out.println("parentCl = " + parentCl);
        return create(name, sgn, parentCl);
    }

    @RequestMapping(value = "/save/{name}/{sgn}/{parent}")
    @ResponseBody
    public String createAlt2(
            @PathVariable("name") String name,
            @PathVariable("sgn") int sgn,
            @PathVariable("parent") String parent) {
        return createAlt1(name, sgn, parent);
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public String delete(int uid) {
        return super.delete(new TCluster(), uid);
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String update(int id, String name, String desc) {
        return super.update(id, name, desc, TCluster.class);
    }
}
package polcz.budget.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.model.Cluster;
import polcz.budget.model.ClusterTree;
import polcz.budget.service.EntityService;

@Controller
@RequestMapping(value = "/cl")
public class ClusterController extends NameDescController<Cluster> {

    @Autowired
    EntityService service;

    @RequestMapping(value = "/")
    public String test() {
        return "list_clusters.html";
    }

    public String create(String name, int sgn, Cluster parent) {
        return super.create(new Cluster(name, sgn, parent));
    }

    @RequestMapping(value = "/create")
    @ResponseBody
    public String createAlt1(String name, int sgn, String parent) {
        System.out.println("parent = " + parent);
        Cluster parentCl = service.findByName(parent, Cluster.class);
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
        return super.delete(new Cluster(), uid);
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String update(int id, String name, String desc) {
        return super.update(id, name, desc, Cluster.class);
    }

    @RequestMapping(value = "/get/{uid}")
    public @ResponseBody Cluster getByUid(@PathVariable int uid) {
        return service.find(uid, Cluster.class);
    }

    @RequestMapping(value = "/get")
    public @ResponseBody List<Cluster> getAll() {
        return service.findAll(Cluster.class);
    }
    
    @RequestMapping(value = "/get-tree")
    public @ResponseBody ClusterTree getAllTreeView() {
        return new ClusterTree(service.findByName("Nem_Adott", Cluster.class));
    }
}
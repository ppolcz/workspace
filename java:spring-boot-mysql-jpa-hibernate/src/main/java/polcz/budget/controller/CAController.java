package polcz.budget.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.model.TChargeAccount;
import polcz.budget.service.EntityService;

@Controller
@RequestMapping(value = "/ca")
public class CAController extends NameDescController<TChargeAccount> {

    @Autowired
    EntityService service;

    @RequestMapping("/")
    @ResponseBody
    public String test() {
        return CAController.class.getSimpleName() + " works fine!";
    }

    @RequestMapping(value = "/create")
    @ResponseBody
    public String createAlt1(String name, String desc) {
        return super.create(new TChargeAccount(name, desc));
    }

    /**
     * localhost:8080/ca/save/szepszab/OTP SZEP kartya Szabadido
     */
    @RequestMapping(value = "/save/{name}/{desc}")
    @ResponseBody
    public String createAlt2(@PathVariable String name, @PathVariable String desc) {
        return createAlt1(name, desc);
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public String delete(int uid) {
        return super.delete(new TChargeAccount(), uid);
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String update(int id, String name, String desc) {
        return super.update(id, name, desc, TChargeAccount.class);
    }

    @RequestMapping(value = "/get/{uid}")
    public @ResponseBody TChargeAccount getByUid(@PathVariable int uid) {
        return service.find(uid, TChargeAccount.class);
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody TChargeAccount sayHello(@RequestParam(value = "name", required = false, defaultValue = "Stranger") String name) {
        return new TChargeAccount("kutya", "csoka");
    }
}
package polcz.budget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.model.TChargeAccount;

@Controller
@RequestMapping(value = "/ca")
public class CAController extends NameDescController<TChargeAccount> {

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
    public String createAlt2(
            @PathVariable("name") String name,
            @PathVariable("desc") String desc) {
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
}
package polcz.budget.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.model.Market;

/**
 * Class UserController
 */
@Controller
@RequestMapping(value = "/market")
public class MarketController extends NameDescController<Market> {

    @RequestMapping("/")
    @ResponseBody
    public String test() {
        return MarketController.class.getSimpleName() + " works fine!";
    }

    @RequestMapping(value = "/create")
    @ResponseBody
    public String createAlt1(String name, String desc) {
        return super.create(new Market(name, desc));
    }

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
        return super.delete(new Market(), uid);
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public String update(int id, String name, String desc) {
        return super.update(id, name, desc, Market.class);
    }
}
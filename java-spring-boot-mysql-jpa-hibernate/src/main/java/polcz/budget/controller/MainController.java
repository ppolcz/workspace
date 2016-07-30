package polcz.budget.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import polcz.budget.service.OdfLoaderService;

@Controller
public class MainController {

    @Autowired
    OdfLoaderService odfService;
    
    @RequestMapping("/tr-list")
    public String index() {
      return "transaction_list.html";
    }
    
    @RequestMapping("/cl-list")
    public String index2() {
        return "cluster_list.html";
    }
    
    @RequestMapping("/cl-tree")
    public String index3() {
      return "cluster_tree.html";
    }
    
    @RequestMapping("/odf-load")
    @ResponseBody
    public String loadOdf() {
        odfService.process("koltsegvetes.ods");
        return "Odf spreadsheet loaded!";
    }
    @RequestMapping("/odf-test")
    @ResponseBody
    public String testOdf() {
        odfService.process("koltsegvetes-test.ods");
        return "Odf spreadsheet loaded!";
    }
}

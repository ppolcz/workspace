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
    
	@RequestMapping("/")
	@ResponseBody
	public String index() {
		return "Proudly handcrafted by <a href='http://netgloo.com/en'>netgloo</a> :)";
	}

	@RequestMapping("/load-odf")
	@ResponseBody
	public String loadOdf() {
	    odfService.process();
	    return "Odf spreadsheet loaded!";
	}
}

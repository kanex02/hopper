package nz.ac.canterbury.seng302.tab.controller.blog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/coffee")
public class CoffeeController {

    @GetMapping
    public String brewCoffee() {
        return "error/418";
    }



}

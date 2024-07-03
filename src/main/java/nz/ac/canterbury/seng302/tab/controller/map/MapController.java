package nz.ac.canterbury.seng302.tab.controller.map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/map")
    public String getMapPage() {
        return "map/map.html";
    }

}

package launchcode.liftoff_project.Controllers;

import ch.qos.logback.core.net.SyslogOutputStream;
import launchcode.liftoff_project.Model.Trail;
import launchcode.liftoff_project.Model.data.TrailRepository;
import org.hibernate.annotations.SQLInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("alltrails")
public class TrailController {

    @Autowired
    private TrailRepository trailRepository;

    @GetMapping
    public String index(Model model){

        List<Integer> difficulty = new ArrayList<>();
        difficulty.add(1);
        difficulty.add(2);
        difficulty.add(3);
        difficulty.add(4);
        difficulty.add(5);


        model.addAttribute("trails", trailRepository.findAll());
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("requireDogs", false);
        model.addAttribute("requireKids", false);
        model.addAttribute("requireBikes", false);

        return "alltrails";
    }

    @PostMapping()
    public String displayFilterResults(Model model, @RequestParam Double minLength, @RequestParam Double maxLength,
                                       @RequestParam List<Integer> difficulty, @RequestParam String searchLocation,
                                       @RequestParam(name="requireDogs", required=false) Boolean requireDogs,
                                       @RequestParam(name="requireKids", required=false) Boolean requireKids,
                                       @RequestParam(name="requireBikes", required=false) Boolean requireBikes, @RequestParam String sort){

        Iterable<Trail> allTrailsSorted = trailRepository.findAll(Sort.by(Sort.Direction.ASC, sort));

        if (requireDogs == null){requireDogs = false;}
        if (requireKids == null){requireKids = false;}
        if (requireBikes == null){requireBikes = false;}

        Collection<Trail> results = filterTrails(minLength, maxLength, difficulty, allTrailsSorted, requireDogs, requireKids, requireBikes);

        model.addAttribute("minLength", minLength);
        model.addAttribute("maxLength", maxLength);
        model.addAttribute("difficulty", difficulty);
        model.addAttribute("searchLocation", searchLocation);
        model.addAttribute("requireDogs", requireDogs);
        model.addAttribute("requireKids", requireKids);
        model.addAttribute("requireBikes", requireBikes);
        model.addAttribute("sort", sort);
        model.addAttribute("trails", results);

        return "alltrails";

    }

    public static ArrayList<Trail> filterTrails(Double minLength, Double maxLength, List<Integer> difficulty, Iterable<Trail> allTrails,
                                                Boolean requireDogs, Boolean requireKids, Boolean requireBikes){

        ArrayList<Trail> results = new ArrayList<>();

        if (minLength == null){ minLength = 0.0; }
        if (maxLength == null){ maxLength = 1000.0;}
        if (difficulty == null){
            difficulty = new ArrayList<>();
            difficulty.add(1);
            difficulty.add(2);
            difficulty.add(3);
            difficulty.add(4);
            difficulty.add(5);
        }

        for (Trail trail : allTrails){
            if (
                (requireDogs && !trail.getDogs()) ||
                (requireKids && !trail.getFamily()) ||
                (requireBikes && !trail.getBikes())
            ) {continue;}

            if (
                trail.getLength() > minLength
                && trail.getLength() < maxLength
                && difficulty.contains(trail.getDifficulty())) {
                results.add(trail);
            }
        }

        return results;
    }

}

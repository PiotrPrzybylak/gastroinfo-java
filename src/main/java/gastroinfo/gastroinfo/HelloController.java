package gastroinfo.gastroinfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class HelloController {

    private final ZoneId DEFAULT_TIMEZONE = ZoneId.of("Europe/Warsaw");

    private final String[] WEEKDAYS = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};

    private final JdbcTemplate jdbc;

    @GetMapping("/")
    public String home() {
        return "redirect:/lunches/lodz";
    }


    @GetMapping({"/lunches/{town}", "/lunches", "/lunches/"})
    public String lunches(Model model, LocalDate date, @PathVariable(required = false) String town) {
        displayLunches(model, date);
        return "hello";
    }

    @GetMapping({"/lunches-new/{town}"})
    public String lunchesNew(Model model, LocalDate date, @PathVariable(required = false) String town) {
        displayLunches(model, date);
        return "hello-new";
    }

    private void displayLunches(Model model, LocalDate date) {
        if (date == null) {
            date = LocalDate.now(DEFAULT_TIMEZONE);
        }
        model.addAttribute("date", date);
        List<Map<String, Object>> offers = jdbc.queryForList("""
                select
                offers.id,
                offer as description,
                price,
                name,
                address,
                phone,
                zone,
                lunch_served_from,
                lunch_served_until,
                lunch_delivery,
                offers.place_id
                from offers join places on offers.place_id = places.id where date = ?""", date);
        Map<String, List<Map<String, Object>>> zones = new HashMap<>();
        for (Map<String, Object> offer : offers) {
            zones.computeIfAbsent((String) offer.get("zone"), (k) -> new ArrayList<>()).add(offer);
            var pictures = jdbc.queryForList("select * from lunch_pictures where place_id = ? and date = ?", offer.get("place_id"), date);
            offer.put("pictures", pictures);
        }
        model.addAttribute("zones", zones.entrySet().stream().map((e) -> Map.of("name", e.getKey(), "offers", e.getValue())));
    }

    @GetMapping("/rankings")
    public String rankings(Model model) {

        var restaurants = jdbc.queryForList("select * from places");
        var rankings = jdbc.queryForList("select * from rankings");

        for (var ranking : rankings) {
            var ranking_restaurants = new ArrayList<>();
            var ids = ((String) ranking.get("restaurants_ids")).split(",");
            for (int i = 0; i < ids.length; i++) {
                for (Map<String, Object> restaurant : restaurants) {
                    if (ids[i].equals(restaurant.get("id") + "")) {

                        List<Double> ratings = (List<Double>) restaurant.computeIfAbsent("ratings", (k) -> new ArrayList<Double>());
                        var restaurant_ranking = new HashMap<>(restaurant);
                        var rating = 1.0 - (i) / (ids.length - 1.0);
                        restaurant_ranking.put("place", String.format("%.3f", rating));
                        ranking_restaurants.add(restaurant_ranking);
                        ratings.add(rating);
                        break;
                    }
                }
            }
            ranking.put("restaurants", ranking_restaurants);
        }
        for (var restaurant : restaurants) {
            if (restaurant.containsKey("ratings")) {
                List<Double> ratings = (List) restaurant.get("ratings");
                double average = ratings.stream().mapToDouble(a -> a).average().getAsDouble();
                restaurant.put("average", average);
                restaurant.put("votes", ratings.size());
            } else {
                restaurant.put("average", 0d);
                restaurant.put("votes", 0);
            }
        }

        restaurants.sort((a, b) -> (int) ((((double) b.get("average")) - ((double) a.get("average"))) * 10000));

        model.addAttribute("restaurants", restaurants);
        model.addAttribute("rankings", rankings);

        return "rankings";
    }

    @GetMapping("/place/{id}")
    public String rankings(Model model, @PathVariable int id) {
        var place = jdbc.queryForMap("select * from places where id = ?", id);

        var openingHours = jdbc.queryForList("select * from place_opening_hours where place_id = ? order by weekday", id);
        for (Map<String, Object> day : openingHours) {
            day.put("weekdayName", WEEKDAYS[(Integer) day.get("weekday")]);
        }
        place.put("openingHours", openingHours);

        model.addAttribute("place", place);
        return "place";
    }

    @GetMapping("/test")
    public String test(Model model, HttpServletRequest req) {
        model.addAttribute("test__date", LocalDate.now());
        model.addAttribute("test__datetime", LocalDateTime.now());
        model.addAttribute("test__zoneddatetime", ZonedDateTime.now());
        model.addAttribute("test__ZoneId.systemDefault()", ZoneId.systemDefault());
        model.addAttribute("test__warsawdatetime", LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
        model.addAttribute("test__warsawdate", LocalDate.now(ZoneId.of("Europe/Warsaw")));
        model.addAttribute("test__instant", Instant.now());
        model.addAttribute("test__localaddress", req.getLocalAddr());
        model.addAttribute("test__localport", req.getLocalPort());
        return "test";
    }
}

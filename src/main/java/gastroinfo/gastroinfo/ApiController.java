package gastroinfo.gastroinfo;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController()
@RequestMapping("/api")
public class ApiController {

    @PutMapping("/places/{id}/lunches/{date}")
    public void saveOffer(@PathVariable("id") int placeId, @PathVariable LocalDate date, @RequestBody Offer offer, @AuthenticationPrincipal UserDetails user) {
        System.out.println(user);
        System.out.println(placeId);
        System.out.println(date);
        System.out.println(offer.description);
    }

    @GetMapping("/places/{id}/lunches/")
    public List<Offer> showOffers(@PathVariable("id") int placeId) {
        System.out.println(placeId);
        Offer offer1 = new Offer();
        offer1.description = "Żyrafy wchodzą do szafy";
        offer1.price = BigDecimal.TEN;
        Offer offer2 = new Offer();
        return List.of(offer1, offer2);
    }


}

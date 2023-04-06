package gastroinfo.gastroinfo;

import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController()
@RequestMapping("/api")
public class ApiController {

    @PutMapping("/places/{id}/lunches/{date}")
    public void saveOffer(@PathVariable("id") int placeId, @PathVariable LocalDate date,  @RequestBody Offer offer) {
        System.out.println(placeId);
        System.out.println(date);
        System.out.println(offer.description);
    }

}

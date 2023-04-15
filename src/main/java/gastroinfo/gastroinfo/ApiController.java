package gastroinfo.gastroinfo;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/api")
@AllArgsConstructor
public class ApiController {

    private final JdbcTemplate jdbc;

    @GetMapping("/me")
    public PlaceUser me(@AuthenticationPrincipal PlaceUser user) {
        return user;
    }

    @PutMapping("/places/{id}/lunches/{date}")
    public void saveOffer(@PathVariable("id") int placeId, @PathVariable LocalDate date, @RequestBody Offer offer, @AuthenticationPrincipal PlaceUser user) {

        if (placeId != user.getId()) {
            throw new AccessDeniedException("Can't change other places' data");
        }

        jdbc.update("delete from offers where place_id = ? and date = ?", placeId, date);
        jdbc.update("insert into offers (place_id, date, offer, price) values (?, ?, ?, ?)", placeId, date, offer.description, offer.price);
    }

    @GetMapping("/places/{id}/lunches/")
    public List<Offer> showOffers(@PathVariable("id") int placeId, @RequestParam("date_from") LocalDate dateFrom, @RequestParam("date_to") LocalDate dateTo) {


        List<Map<String, Object>> offers = jdbc.queryForList("""
select
    offers.id,
    offer,
    price,
    offers.date,
    count(lunch_pictures.id) as pictures_count
from offers left join lunch_pictures on offers.place_id = lunch_pictures.place_id and offers.date = lunch_pictures.date where offers.place_id = ? and offers.date between ? and ? group by offers.id order by offers.date""", placeId, dateFrom, dateTo);

        List<Offer> result = new ArrayList<>();
        for (Map<String, Object> offer : offers) {
            Offer offerDto = new Offer();
            offerDto.id = ((Long) offer.get("id"));
            offerDto.description = ((String) offer.get("offer"));
            offerDto.price = (BigDecimal) offer.get("price");
            offerDto.date = ((Date) offer.get("date")).toLocalDate();
            offerDto.pictures_count = ((Long) offer.get("pictures_count"));
            result.add(offerDto);

        }
        return result;
    }

    @GetMapping("/lunches/{id}/pictures")
    public List<OfferPicture> showPictures(@PathVariable("id") int offerId) {

        var pictures = jdbc.queryForList("select lunch_pictures.id, url from lunch_pictures join offers on lunch_pictures.place_id = offers.place_id  and lunch_pictures.date = offers.date where offer_id = ?", offerId);

        List<OfferPicture> result = new ArrayList<>();
        for (Map<String, Object> picture : pictures) {
            OfferPicture pictureDto = new OfferPicture();
            pictureDto.id = ((Integer) picture.get("id"));
            pictureDto.url = ((String) picture.get("url"));
            result.add(pictureDto);
        }
        return result;
    }

}

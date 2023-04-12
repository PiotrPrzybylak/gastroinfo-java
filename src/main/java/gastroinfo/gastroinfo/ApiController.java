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
import org.springframework.web.util.HtmlUtils;

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

        offer.description = HtmlUtils.htmlEscape(offer.description).replaceAll("\n", "<br/>");

        jdbc.update("delete from offers where place_id = ? and date = ?", placeId, date);
        jdbc.update("insert into offers (place_id, date, offer, price) values (?, ?, ?, ?)", placeId, date, offer.description, offer.price);
    }

    @GetMapping("/places/{id}/lunches/")
    public List<Offer> showOffers(@PathVariable("id") int placeId, @RequestParam("date_from") LocalDate dateFrom, @RequestParam("date_to") LocalDate dateTo) {


        List<Map<String, Object>> offers = jdbc.queryForList("""
                select
                offer,
                price,
                date
                from offers where place_id = ? and date between ? and ? order by date""", placeId, dateFrom, dateTo);

        List<Offer> result = new ArrayList<>();
        for (Map<String, Object> offer : offers) {
            Offer offerDto = new Offer();
            offerDto.description = ((String) offer.get("offer")).replaceAll("<br/>", "\n");
            offerDto.price = (BigDecimal) offer.get("price");
            offerDto.date = ((Date) offer.get("date")).toLocalDate();
            result.add(offerDto);

        }
        return result;
    }

}

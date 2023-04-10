package gastroinfo.gastroinfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class PlaceUser extends User {

    private final Long id;

    public PlaceUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(username, password, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

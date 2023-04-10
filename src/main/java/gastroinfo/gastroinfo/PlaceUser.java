package gastroinfo.gastroinfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class PlaceUser extends User {

    private final Long id;

    private final String name;

    public PlaceUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id, String name) {
        super(username, password, authorities);
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

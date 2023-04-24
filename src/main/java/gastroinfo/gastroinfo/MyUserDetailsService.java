package gastroinfo.gastroinfo;


import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbc;

    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            var user = jdbc.queryForMap("select * from places where username = ?", username);
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_PLACE");
            return new PlaceUser((String) user.get("username"), (String) user.get("password"), authorities, ((Long) user.get("id")), (String) user.get("name"));
        } catch (EmptyResultDataAccessException e) {
            try {
                var user = jdbc.queryForMap("select * from users where email = ?", username);
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
                return new PlaceUser((String) user.get("email"), (String) user.get("password"), authorities, ((Integer) user.get("id")).longValue(), (String) user.get("name"));
            } catch (EmptyResultDataAccessException e2) {
                throw new UsernameNotFoundException(username);
            }
        }
    }
}
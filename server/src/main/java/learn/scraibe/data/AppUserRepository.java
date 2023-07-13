package learn.scraibe.data;

import learn.scraibe.models.AppUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AppUserRepository {
    @Transactional
    AppUser findByUsername(String username);

    @Transactional
    public AppUser findByEmail(String username);

    @Transactional
    public List<AppUser> getAllUsers();

    @Transactional
    public AppUser create(AppUser user);

    @Transactional
    public boolean update(AppUser user);
    @Transactional
    public boolean deleteUser(int userId);
}

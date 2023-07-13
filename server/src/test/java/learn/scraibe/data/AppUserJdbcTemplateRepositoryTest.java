package learn.scraibe.data;

import learn.scraibe.models.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AppUserJdbcTemplateRepositoryTest {

    static boolean hasRun = false;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AppUserJdbcTemplateRepository repository;

    @BeforeEach
    void setup() {
        if (!hasRun) {
            jdbcTemplate.update("call set_known_good_state();");
            hasRun = true;
        }
    }

    @Test
    void shouldGetAll(){
        List<AppUser> result = repository.getAllUsers();
        assertTrue(result.size() >= 1);
        assertTrue(result.size() <= 4);
    }

    @Test
    void shouldFindByUsername() {
        AppUser actual = repository.findByUsername("sai_shinobi");
        assertEquals("sai_shinobi@gmail.com", actual.getEmail());
        assertTrue(actual.isEnabled());
        assertEquals(1, actual.getAuthorities().size());
        assertTrue(actual.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("user")));
        assertEquals(1, actual.getCourses().size());
        assertEquals(1, actual.getCourses().get(0).getCourseId());
    }

    @Test
    void shouldNotFindByMissingUsername() {
        AppUser actual = repository.findByUsername("test");
        assertNull(actual);
    }

    @Test
    void shouldFindByEmail() {
        AppUser actual = repository.findByEmail("sai_shinobi@gmail.com");
        assertEquals("sai_shinobi", actual.getUsername());
        assertTrue(actual.isEnabled());
        assertEquals(1, actual.getAuthorities().size());
        assertTrue(actual.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("user")));
        assertEquals(1, actual.getCourses().size());
        assertEquals(1, actual.getCourses().get(0).getCourseId());
    }

    @Test
    void shouldNotFindByMissingEmail() {
        AppUser actual = repository.findByEmail("test@test.com");
        assertNull(actual);
    }

    @Test
    void shouldCreateUser() {
        AppUser appUser = new AppUser();
        appUser.setAppUserId(5);
        appUser.setUsername("Test");
        appUser.setEmail("Testy@test.com");
        appUser.setPassword("password");
        appUser.setEnabled(true);
        appUser.setCourses(null);
        Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("user"));
        appUser.setAuthorities(roles);

        AppUser actual = repository.create(appUser);
        assertEquals("Test", actual.getUsername());
        assertEquals("Testy@test.com", actual.getEmail());
        assertTrue(actual.isEnabled());
        assertEquals(1, actual.getAuthorities().size());
        assertTrue(actual.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("user")));
    }

    @Test
    void shouldUpdate() {
        AppUser appUser = new AppUser();
        appUser.setAppUserId(2);
        appUser.setUsername("just_bob");
        appUser.setEmail("Testy2@test.com"); //changing
        appUser.setPassword("$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        appUser.setEnabled(true);
        appUser.setCourses(null);
        Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("user"));
        appUser.setAuthorities(roles);

        boolean actual = repository.update(appUser);
        assertTrue(actual);
    }

    @Test
    void shouldNotUpdateMissing() {
        AppUser appUser = new AppUser();
        appUser.setAppUserId(290);
        appUser.setUsername("fake");
        appUser.setEmail("fake@fake.com"); //changing
        appUser.setPassword("$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa");
        appUser.setEnabled(true);
        appUser.setCourses(null);
        Collection<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority("user"));
        appUser.setAuthorities(roles);

        boolean actual = repository.update(appUser);
        assertFalse(actual);
    }

    @Test
    void shouldDelete() {
        boolean result = repository.deleteUser(3);
        assertTrue(result);
    }

    @Test
    void shouldDeleteMissing() {
        boolean result = repository.deleteUser(90);
        assertFalse(result);
    }

}
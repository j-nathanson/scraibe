package learn.scraibe.security;

import learn.scraibe.data.AppUserJdbcTemplateRepository;
import learn.scraibe.data.AppUserRepository;
import learn.scraibe.domain.Result;
import learn.scraibe.domain.ResultType;
import learn.scraibe.models.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AppUserServiceTest {

    @Autowired
    AppUserService service;

    @MockBean
    AppUserJdbcTemplateRepository appUserRepository;

    @Test
    void shouldFindAll(){
        AppUser appUser1 = new AppUser(1, "sai_shinobi", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);
        AppUser appUser2 = new AppUser(2, "test_man", "test@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        List<AppUser> mockList = new ArrayList(List.of(appUser1,appUser2));
        when(appUserRepository.getAllUsers()).thenReturn(mockList);
        List<AppUser> actual = service.getAll();
        assertEquals(2, actual.size());
        assertEquals(1, actual.get(0).getAppUserId());
        assertEquals(2, actual.get(1).getAppUserId());

    }

    @Test
    void shouldFindByUsername() {
        AppUser appUser = new AppUser(1, "sai_shinobi", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);
        AppUser mockOut = new AppUser(1, "sai_shinobi", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        when(appUserRepository.findByUsername(appUser.getUsername())).thenReturn(mockOut);

        AppUser actual = service.loadUserByUsername(appUser.getUsername());

        assertEquals(actual.getAppUserId(), mockOut.getAppUserId());
    }

    @Test
    void shouldNotFindByMissingUsername() {
        AppUser appUser = new AppUser(1, "test", "test@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        when(appUserRepository.findByUsername(appUser.getUsername())).thenThrow(
                new UsernameNotFoundException(appUser.getUsername() + " not found.")
        );

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(appUser.getUsername());
        });
    }

    @Test
    void shouldFindByEmail() {
        AppUser appUser = new AppUser(1, "sai_shinobi", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);
        AppUser mockOut = new AppUser(1, "sai_shinobi", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        when(appUserRepository.findByEmail(appUser.getEmail())).thenReturn(mockOut);

        AppUser actual = service.loadUserByEmail(appUser.getEmail());

        assertEquals(actual.getAppUserId(), mockOut.getAppUserId());
    }

    @Test
    void shouldNotFindByMissingEmail() {
        AppUser appUser = new AppUser(1, "test", "test@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        when(appUserRepository.findByEmail(appUser.getEmail())).thenThrow(
                new UsernameNotFoundException(appUser.getEmail() + " not found.")
        );

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByEmail(appUser.getEmail());
        });
    }

    @Test
    void shouldCreateUser() {
        AppUser mockUser = new AppUser(0, "test", "test@gmail.com", "$2a$10$MCEiJGzws9wNIyPA1Nu17.Gm9oNsJ8ulEji725NjwGZPewHRTjsF2", true, List.of("user"), null);
        when(appUserRepository.create(any(AppUser.class))).thenReturn(mockUser);

        Result<AppUser> mockResult = new Result<>();
        mockResult.setPayload(mockUser);

        Credentials credentials = new Credentials("test", "P@ssw0rd!", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertTrue(actual.isSuccess());
        assertEquals(mockResult.getPayload().getUsername(), actual.getPayload().getUsername());
    }

    @Test
    void shouldNotCreateWithNullUsername() {
        Credentials credentials = new Credentials(null, "P@ssw0rd!", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithBlankUsername() {
        Credentials credentials = new Credentials("", "P@ssw0rd!", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateTooLongUsername() {
        Credentials credentials = new Credentials("WhisperingThunderstormSerenadeWithEnigmaticEchoesCarHouse", "P@ssw0rd!", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }


    @Test
    void shouldNotCreateWithNullEmail() {
        Credentials credentials = new Credentials("test", "P@ssw0rd!", null);

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithBlankEmail() {
        Credentials credentials = new Credentials("test", "P@ssw0rd!", "");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithInvalidEmail() {
        Credentials credentials = new Credentials("test", "P@ssw0rd!", "doesNotEndWithDotCom");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithNullPassword() {
        Credentials credentials = new Credentials("test", null, "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithShortPassword() {
        Credentials credentials = new Credentials("test", "short", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithPasswordHavingNoDigits() {
        Credentials credentials = new Credentials("test", "P@ssword!", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithPasswordHavingNoLetters() {
        Credentials credentials = new Credentials("test", "9@000!", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithPasswordHavingNoSpecialCharacters() {
        Credentials credentials = new Credentials("test", "Passw0rd", "test@gmail.com");

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithDuplicateEmail() {
        Credentials credentials = new Credentials("test", "Passw0rd!", "justbob@aol.com");

        when(appUserRepository.create(any(AppUser.class))).thenThrow(
                new DuplicateKeyException("Error Message")
        );

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldNotCreateWithDuplicateUsername() {
        Credentials credentials = new Credentials("justbob", "Passw0rd!", "test@gmail.com");

        when(appUserRepository.create(any(AppUser.class))).thenThrow(
                new DuplicateKeyException("Error Message")
        );

        Result<AppUser> actual = service.create(credentials);
        assertFalse(actual.isSuccess());
        assertEquals(ResultType.INVALID, actual.getResultType());
    }

    @Test
    void shouldUpdateUser(){
        AppUser appUser = new AppUser(1, "newUserName", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        when(appUserRepository.update(appUser)).thenReturn(true);
        Result<AppUser> actual = service.update(appUser);

        assertTrue(actual.isSuccess());
        assertEquals(actual.getPayload().getAppUserId(),1);
    }

    @Test
    void shouldUpdateUserWithIdNotSet(){
        AppUser appUser = new AppUser(0, "newUserName", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        when(appUserRepository.update(appUser)).thenReturn(false);
        Result<AppUser> actual = service.update(appUser);

        assertFalse(actual.isSuccess());
        assertEquals(actual.getResultType(),ResultType.INVALID);
    }

    @Test
    void shouldUpdateUserWithMissingId(){
        AppUser appUser = new AppUser(90, "newUserName", "sai_shinobi@gmail.com", "$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa", true, Arrays.asList("user"), null);

        when(appUserRepository.update(appUser)).thenReturn(false);
        Result<AppUser> actual = service.update(appUser);

        assertFalse(actual.isSuccess());
        assertEquals(actual.getResultType(),ResultType.NOT_FOUND);
    }

    @Test
    void shouldDelete(){
        when(appUserRepository.deleteUser(1)).thenReturn(true);
        assertTrue(service.delete(1));
    }

    @Test
    void shouldNotDeleteMissing(){
        when(appUserRepository.deleteUser(10)).thenReturn(false);
        assertFalse(service.delete(10));
    }

}
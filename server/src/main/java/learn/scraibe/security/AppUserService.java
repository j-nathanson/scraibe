package learn.scraibe.security;

import learn.scraibe.data.AppUserRepository;
import learn.scraibe.domain.Result;
import learn.scraibe.domain.ResultType;
import learn.scraibe.models.AppUser;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository repository;
    private final PasswordEncoder encoder;

    public AppUserService(AppUserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = repository.findByUsername(username);

        if (appUser == null) {
            throw new UsernameNotFoundException(username + " not found.");
        }

        return appUser;
    }

    public AppUser loadUserByEmail(String email) throws UsernameNotFoundException {
        AppUser appUser = repository.findByEmail(email);

        if (appUser == null) {
            throw new UsernameNotFoundException(email + " not found.");
        }

        return appUser;
    }

    public List<AppUser> getAll(){
        return repository.getAllUsers();
    }

    public Result<AppUser> create(Credentials credentials) {
        Result<AppUser> result = validate(credentials);

        if (credentials.getPassword() == null) {
            result.addMessage("password is required", ResultType.INVALID);
            return result;
        }

        if (!isValidPassword(credentials.getPassword())) {
            result.addMessage(
                    "password must be at least 8 characters and contain a digit," +
                            " a letter, and a non-digit/non-letter", ResultType.INVALID);
        }


        if (!result.isSuccess()) {
            return result;
        }

        String hashedPassword = encoder.encode(credentials.getPassword());

        //TODO might need to change last parameter of courses
        AppUser appUser = new AppUser(0, credentials.getUsername(), credentials.getEmail(),
                hashedPassword, true, List.of("user"), null);

        try {
            appUser = repository.create(appUser);
            result.setPayload(appUser);
        } catch (DuplicateKeyException e) {
            result.addMessage("The provided username or email already exists", ResultType.INVALID);
        }

        return result;
    }

    public Result<AppUser> update(AppUser appUser) {
        Result<AppUser> result = new Result<>();

        if (appUser.getAppUserId() <= 0) {
            result.addMessage("User Id must be set", ResultType.INVALID);
            return result;
        }

        Credentials credentials = new Credentials(appUser.getUsername(), appUser.getPassword(), appUser.getEmail());
        result = validate(credentials);

        if (!result.isSuccess()) {
            return result;
        }

        if (!repository.update(appUser)) {
            result.addMessage(String.format("could not find user at id %s", appUser.getAppUserId()), ResultType.NOT_FOUND);
        }

        result.setPayload(appUser);

        return result;
    }

    public boolean delete(int userId){
        return repository.deleteUser(userId);
    }


    private Result<AppUser> validate(Credentials credentials) {
        Result<AppUser> result = new Result<>();
        if (credentials.getUsername() == null || credentials.getUsername().isBlank()) {
            result.addMessage("username is required", ResultType.INVALID);
            return result;
        }

        if (credentials.getEmail() == null || credentials.getEmail().isBlank()) {
            result.addMessage("email is required", ResultType.INVALID);
            return result;
        }

        if (credentials.getUsername().length() > 50) {
            result.addMessage("username must be less than 50 characters", ResultType.INVALID);
        }

        if (!isValidEmail(credentials.getEmail())) {
            result.addMessage("please enter a valid email.", ResultType.INVALID);
        }

        return result;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return pattern.matcher(email).matches();

    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        int digits = 0;
        int letters = 0;
        int others = 0;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                digits++;
            } else if (Character.isLetter(c)) {
                letters++;
            } else {
                others++;
            }
        }

        return digits > 0 && letters > 0 && others > 0;
    }
}

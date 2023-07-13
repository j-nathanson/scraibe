package learn.scraibe.controllers;

import learn.scraibe.domain.Result;
import learn.scraibe.models.AppUser;
import learn.scraibe.security.AppUserService;
import learn.scraibe.security.Credentials;
import learn.scraibe.security.JwtConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/security")
@ConditionalOnWebApplication
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtConverter jwtConverter;
    private final AppUserService appUserService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtConverter jwtConverter, AppUserService appUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtConverter = jwtConverter;
        this.appUserService = appUserService;
    }

    @PostMapping("/authenticate-username")
    public ResponseEntity<Object> authenticate(@RequestBody Credentials credentials) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {
                AppUser appUser = (AppUser) authentication.getPrincipal();
                return new ResponseEntity<>(makeAppUserTokenMap(appUser), HttpStatus.OK);
            }

        } catch (AuthenticationException e) {
            System.out.println(e);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/authenticate-email")
    public ResponseEntity<Object> authenticateByEmail(@RequestBody Credentials credentials) {

        String email = credentials.getEmail();
        AppUser appUser = new AppUser();


        try {
            appUser = appUserService.loadUserByEmail(email);
        } catch (UsernameNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (appUser != null) {
            credentials.setUsername(appUser.getUsername());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    credentials.getUsername(), credentials.getPassword());
            try {
                Authentication authentication = authenticationManager.authenticate(authToken);

                if (authentication.isAuthenticated()) {
                    AppUser newUser = (AppUser) authentication.getPrincipal();
                    return new ResponseEntity<>(makeAppUserTokenMap(newUser), HttpStatus.OK);
                }

            } catch (AuthenticationException e) {
                System.out.println(e);
            }

        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@AuthenticationPrincipal AppUser appUser) {
        return new ResponseEntity<>(makeAppUserTokenMap(appUser), HttpStatus.OK);
    }

    @PostMapping("/create-account")
    public ResponseEntity<Object> create(@RequestBody Credentials credentials) {
        Result<AppUser> result = appUserService.create(credentials);

        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword());

        try {
            Authentication authentication = authenticationManager.authenticate(authToken);

            if (authentication.isAuthenticated()) {
                AppUser appUser = (AppUser) authentication.getPrincipal();
                return new ResponseEntity<>(makeAppUserTokenMap(appUser), HttpStatus.CREATED);
            }

        } catch (AuthenticationException e) {
            System.out.println(e);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//
//
//
//
//        HashMap<String, Integer> map = new HashMap<>();
//        map.put("appUserId", result.getPayload().getAppUserId());
//
//        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    private HashMap<String, String> makeAppUserTokenMap(AppUser appUser) {
        HashMap<String, String> map = new HashMap<>();
        String jwtToken = jwtConverter.getTokenFromUser(appUser);
        map.put("jwt_token", jwtToken);
        return map;
    }
}

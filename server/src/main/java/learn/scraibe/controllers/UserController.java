package learn.scraibe.controllers;

import learn.scraibe.domain.Result;
import learn.scraibe.domain.ResultType;
import learn.scraibe.models.AppUser;
import learn.scraibe.security.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final AppUserService appUserService;

    public UserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping
    ResponseEntity<List<AppUser>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getAll());
    }

    @GetMapping("/get-email/{email}")
    ResponseEntity<Object> getByEmail(@PathVariable String email){
        try {
            AppUser user = appUserService.loadUserByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-username/{username}")
    ResponseEntity<Object> getByUsername(@PathVariable String username){
        try {
            AppUser user = appUserService.loadUserByUsername(username);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/edit/{id}")
    ResponseEntity<Object> editUser(@PathVariable int id, @RequestBody AppUser appUser){
        if (id != appUser.getAppUserId()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Result<AppUser> result = appUserService.update(appUser);
        if (!result.isSuccess()) {
            if (result.getResultType() == ResultType.NOT_FOUND) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Object> deleteUser(@PathVariable int id){
        if (appUserService.delete(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

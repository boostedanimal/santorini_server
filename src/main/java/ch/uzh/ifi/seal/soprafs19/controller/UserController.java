package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {

        this.service = service;
    }
    //in front end GET method will be used to retrieve user data
    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@RequestBody User newUser){
        User tempUser = this.service.createUser(newUser);
        return tempUser;
    }

    @PostMapping("/login")
    User attemptLogin(@RequestBody User newUser){
        return this.service.attemptLogin(newUser);
    }

    //@CrossOrigin annotation enables cross-origin requests only for this specific method. By default, its allows all origins, all headers, the HTTP methods specified in the @RequestMapping annotation and a maxAge of 30 minutes is used.

    @CrossOrigin
    @PutMapping("/users")
    User logoutUser(@RequestBody User newUser){
        return this.service.logoutUser(newUser);
    }

    @GetMapping("/users/{id}")
    User getsingleUser(@PathVariable String id){
        //change string id to long id, otherwise type error
        return this.service.getsingleUser(Long.parseLong(id));
    }

    @CrossOrigin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/users/{id}")
    void updateUser(@RequestBody User newUser){
        if (!this.service.updateUser(newUser)) {
            throw new AlreadyExistsException("This username is already taken, Change failed!");
        }

    }
}

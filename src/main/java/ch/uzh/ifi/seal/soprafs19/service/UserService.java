package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.controller.AlreadyExistsException;
import ch.uzh.ifi.seal.soprafs19.controller.NonRegisteredUserException;
import ch.uzh.ifi.seal.soprafs19.entity.User;

import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    // createUser for registration process, checks if username is already taken, if so exception is raised
    //TODO: check should also be done for the password
    public User createUser(User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()) != null) {
            throw new AlreadyExistsException("Username already exists: " + newUser.getUsername());
        }

        newUser.setToken(UUID.randomUUID().toString());
        //setStatus to offline since we want to change status with login
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setCreationdate((LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
    //attemptLogin will check if the user is registered, sets status to online
    //TODO: where is the best place to create token for user
    public User attemptLogin(User newUser) {
        User loginUser = userRepository.findByUsername(newUser.getUsername());
        if (loginUser != null && loginUser.getUsername().equals(newUser.getUsername()) && loginUser.getPassword().equals(newUser.getPassword())) {
            User loggedinUser = userRepository.findByUsername(newUser.getUsername());
            loggedinUser.setStatus(UserStatus.ONLINE);
            loggedinUser.setToken(UUID.randomUUID().toString());
            userRepository.save(loggedinUser);
            return loggedinUser;
        }
        throw new NonRegisteredUserException("Username not registered: " + newUser.getUsername());

    }

    // when logging out the user with his token is found and status needs to change to Offline
    public User logoutUser(User newUser){
        User loggingoutUser  = userRepository.findByToken(newUser.getToken());
        loggingoutUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(loggingoutUser);
        return loggingoutUser;

    }

    //getting one user means to look for his specific user id if registration was successful
    public User getsingleUser(long id){
        User singleUser = userRepository.findById(id);
        if(singleUser != null){
            return singleUser;
        }
        else {
            throw new NonRegisteredUserException("User is not registered!");
        }
    }

    //updating a user profile means changing username and his birthday
    public boolean updateUser(User newUser){
        User changingUser = userRepository.findByToken(newUser.getToken());
        //throw exception if new username is already taken
        if(userRepository.findByUsername(newUser.getUsername()) != null && userRepository.findByUsername(newUser.getUsername()) != changingUser){
            throw new AlreadyExistsException("Your desired username is already in use --> Your change wasn't effective!");
        } else {
            //if username passed check the desired changes will be applied
            changingUser.setUsername(newUser.getUsername());
            changingUser.setBirthday(newUser.getBirthday());
            userRepository.save(changingUser);
            return true;
        }
    }

}

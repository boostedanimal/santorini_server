package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.controller.AlreadyExistsException;
import ch.uzh.ifi.seal.soprafs19.controller.NonRegisteredUserException;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.ONLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }
    @Test
    public void createUserToken(){
       //first delete all users from the repository
       userRepository.deleteAll();
       //assert that there is no user left after clearing the repository
       Assert.assertNull(userRepository.findByUsername("testUser"));
       // creating a dumy user
       User testUser = new User();
       testUser.setUsername("dumy");
       testUser.setPassword("1234");

       User createdUser = userService.createUser(testUser);
       // assert that the dumy user has got a token
       Assert.assertNotNull(createdUser.getToken());
   }
    // we expect to get an exception that occurs when 2 users with same username are created
   @Test(expected = AlreadyExistsException.class)
    public void createError(){
       userRepository.deleteAll();
       User testUser = new User();
       testUser.setUsername("dumy");
       testUser.setPassword("1234");
       User createdUser = userService.createUser(testUser);

       User testUser2 = new User();
       testUser2.setUsername("dumy");
       testUser2.setPassword("1234");
       User createdUsercopy = userService.createUser(testUser);


   }

   @Test
    public void getsingleUser(){
       userRepository.deleteAll();
       User testUser = new User();
       testUser.setUsername("dumy");
       testUser.setPassword("1234");
       User createdUser = userService.createUser(testUser);
       User createdUserCopy = userService.getsingleUser(createdUser.getId());
       Assert.assertEquals(createdUser,createdUserCopy);
   }

   @Test(expected = NonRegisteredUserException.class)
    public void checkUserError(){
       userRepository.deleteAll();
       User testUser = new User();
       testUser.setUsername("dumy");
       testUser.setPassword("1234");
       User createdUser = userService.createUser(testUser);

    }

    @Test
    public void updateUser(){
       userRepository.deleteAll();
       User testUser = new User();
       testUser.setUsername("dumy");
       testUser.setPassword("1234");

       User testUsercopy = new User();
       testUsercopy.setUsername("dumy2");
       testUsercopy.setPassword("1234");

       User testUsercopy2 = new User();
       testUsercopy2.setUsername("dumy3");
       testUsercopy2.setPassword("12234");

       //
       User createdUser = userService.createUser(testUser);
       User createdUsercopy = userService.createUser(testUsercopy2);
       testUsercopy.setToken(createdUser.getToken());
       testUsercopy.setBirthday("TBD");

       boolean result = userService.updateUser(testUsercopy);
       Assert.assertEquals(result, false);

    }


}

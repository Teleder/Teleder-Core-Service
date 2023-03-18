package teleder.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teleder.core.model.User.User;
import teleder.core.repository.IUserRepository;

@RestController
public class UserController {
    @Autowired
    IUserRepository user;
    @RequestMapping(value="/")
    public void redirect()  {
        user.insert(new User());
    }
}

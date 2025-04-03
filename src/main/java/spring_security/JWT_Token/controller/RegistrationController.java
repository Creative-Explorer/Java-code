package spring_security.JWT_Token.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring_security.JWT_Token.entity.MyUser;
import spring_security.JWT_Token.repository.MyUserRepository;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private MyUserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

  @PostMapping ("/register /save")
    public MyUser creatuser(@RequestBody MyUser user) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      return repository.save(user);
  }

}
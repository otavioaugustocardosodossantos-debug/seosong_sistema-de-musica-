package itj.seosong.services;

import org.springframework.stereotype.Service;

import itj.seosong.entities.User;
import itj.seosong.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void registry(User new_User) {
        repository.save(new_User);
    }
}
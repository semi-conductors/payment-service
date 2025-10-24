package com.rentmate.service.payment.service;

import com.rentmate.service.payment.entity.UserEntity;
import com.rentmate.service.payment.repo.UserRepository;
import com.rentmate.service.payment.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Optional<UserEntity> getUserById(Long id){
        if (id == null) return Optional.empty();
        return userRepository.findById(id);
    }
    public UserEntity saveCustomer(UserEntity user){
        return userRepository.save(user);
    }
    public UserEntity userToUserEntity(User user){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserID(user.getUserId());
        userEntity.setUserName(user.getName());
        userEntity.setAccountId(user.getStripeAccountId());
        userEntity.setCustomerId(user.getStripeCustomerId());
        userEntity.setEmail(user.getEmail());
        return userEntity;
    }
}

package com.nextu.storage.services;

import com.nextu.storage.dto.UserGetDTO;
import com.nextu.storage.entities.FileData;
import com.nextu.storage.entities.User;
import com.nextu.storage.repository.FileRepository;
import com.nextu.storage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserGetDTO create(User user){
        User userAfterSave = userRepository.save(user);
        UserGetDTO userGetDTO = getUserGetDTO(userAfterSave);
        return userGetDTO;
    }
    public UserGetDTO findById(String id){
        User user = userRepository.findById(id).orElse(null);
        if(user==null){
            return null;
        }
        return getUserGetDTO(user);
    }

    public void update(User user){
        userRepository.save(user);
    }

    public User findUserById(String id){
        User user = userRepository.findById(id).orElse(null);
        return user;
    }
    public void deleteById(String id){
        User user = userRepository.findById(id).orElse(null);
        if(user!=null){
            userRepository.delete(user);
        }
    }
    private static UserGetDTO getUserGetDTO(User userAfterSave) {
        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setId(userAfterSave.getId());
        userGetDTO.setFirstName(userAfterSave.getFirstName());
        userGetDTO.setLastName(userAfterSave.getLastName());
        userGetDTO.setLogin(userAfterSave.getLogin());
        return userGetDTO;
    }

    public boolean checkIfUserAlreadyExist(String login){
        return userRepository.findByLogin(login).isPresent();
    }

}

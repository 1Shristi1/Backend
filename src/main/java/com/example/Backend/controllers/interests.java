package com.example.Backend.controllers;

import com.example.Backend.models.UserInfo;
import com.example.Backend.repo.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/user/{userId}/interest")
public class interests {

    @Autowired
    userRepository userRepository;

   @PutMapping
   public ResponseEntity<String> addInterest(@PathVariable String userId, @RequestBody String interest)
   {
       UserInfo user = userRepository.findByUserId(userId);
       if(user == null)
       {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Id Doesn't exists");
       }

       user.getInterests().add(interest);

       userRepository.save(user);
       return new ResponseEntity<>("Interest added Successfully",HttpStatus.OK);
   }

   @GetMapping
   public ResponseEntity<?> getAllInterest(@PathVariable String userId)
   {
       UserInfo user = userRepository.findByUserId(userId);
       if(user == null)
       {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Id Doesn't exists");
       }

       return new ResponseEntity<>(user.getInterests(),HttpStatus.OK);
   }

   @DeleteMapping
    public ResponseEntity<String> deleteInterest(@PathVariable String userId, @RequestBody String interest)
   {
       UserInfo user = userRepository.findByUserId(userId);
       if(user == null)
       {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Id Doesn't exists");
       }

       List<String> interests = user.getInterests();
       if(!interests.contains(interest))
       {
           return new ResponseEntity<>("Interest not found",HttpStatus.NOT_FOUND);
       }

       interests.remove(interest);
       userRepository.save(user);
       return new ResponseEntity<>("interest removed successfully",HttpStatus.OK);

   }


}

package com.example.Backend.controllers;

import com.example.Backend.models.Login;
import com.example.Backend.models.UserInfo;
import com.example.Backend.repo.EventsRepository;
import com.example.Backend.repo.userRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class user
{

  @Autowired
  userRepository userRepository;

  @Autowired
  EventsRepository eventsRepository;


  @PostMapping()
  public ResponseEntity<Map<String, String>> addUser(@RequestBody UserInfo user) {
    if(user.getEmail() == null || user.getFirstName() == null || user.getLastName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body error");
    }
    UserInfo existsUser = userRepository.findByEmail(user.getEmail());
    if(existsUser != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email Already exists");
    }
    UserInfo saveUser = userRepository.save(user);
    Map<String,String> response = new HashMap<>();
    response.put("userid", saveUser.getUserId());
    return new ResponseEntity<>(response,HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Login login)  {
    if(login.getEmail() == null || login.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body error");
    }

    UserInfo user = userRepository.findByEmail(login.getEmail());
    if(user == null || !user.getPassword().equals(login.getPassword()))
    {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid email or password");
    }

    Map<String,Object> response = new HashMap<>();
    response.put("userid",user.getUserId());
    response.put("email",user.getEmail());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/{userId}")
  public Map<String, Object> getUser(@PathVariable String userId)
  {
     UserInfo user = userRepository.findByUserId(userId);

     if(user == null)
     {
       throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Id Doesn't exists");
     }

    Map<String, Object> response = new HashMap<>();


        response.put("userId",user.getUserId());
    response.put("firstName",user.getFirstName());
    response.put("lastName",user.getLastName());
    response.put("email",user.getEmail());
    response.put("city",user.getCity());
    response.put("interests",user.getInterests());
    response.put("events",user.getEvents());

      return response;
  }



  @DeleteMapping("/{userId}")
  public ResponseEntity<Map<String,String>> deleteUser(@PathVariable String userId)
  {

    UserInfo user = userRepository.findByUserId(userId);

    if(user == null)
    {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Id Doesn't exists");
    }

     System.out.println(user);
    userRepository.delete(user);
    return new ResponseEntity<>(
            new HashMap<String,String>() {{
              put("message", "User Deleted Successfully");
            }},HttpStatus.OK);
  }

  @PutMapping("/{userId}/event/{eventId}")
  public ResponseEntity<Map<String, String>> resisterEvent(@PathVariable String userId, @PathVariable String eventId) {
    UserInfo user = userRepository.findByUserId(userId);

    if(user == null)
    {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Id Doesn't exists");
    }


    List list = eventsRepository.findEventsByEventId(Long.parseLong(eventId));
    if(list.size() == 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id doesn't exist");
    }

    user.getEvents().add(list);
    userRepository.save(user);

    return new ResponseEntity<Map<String,String>>(
            new HashMap<String, String>(){{
              put("message", "Successfully Registered for the event");
            }}, HttpStatus.OK);
  }


}

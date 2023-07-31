package com.example.Backend.service;

import com.example.Backend.models.EventDetail;
import com.example.Backend.models.Events;
import com.example.Backend.models.UserInfo;
import com.example.Backend.repo.EventsRepository;
import com.example.Backend.repo.userRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventBriteService {

    @Autowired
    EventsRepository eventsRepository;
    @Autowired
    userRepository userRepository;

    private final String apiUrl = "https://www.eventbriteapi.com/v3/organizations/1673863675553/events/";
    private final String authToken = "OOQQ34SEOQ3B5BWZLNBH";

    //all events
    public Object fetchEvent() throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        headers.set("Content-Type", "application/json");

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);


        ResponseEntity<String> responseEntity = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        Object object = new Object();
        // Process the response
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
           Map<String,Object> mp = new ObjectMapper().readValue(responseEntity.getBody(), HashMap.class);
           object = mp.get("events");
           return object;
        } else {
            throw new RuntimeException("Failed to fetch data from the external API.");
        }
    }


    public  List<Map<String, Object>> fetchEventbasedonVenue(Integer venueId) throws JsonProcessingException {

        Object object = fetchEvent();

        ObjectMapper objectMapper = new ObjectMapper();
        List list = objectMapper.convertValue(object, List.class);
        List<Map<String, Object>> eventsBasedOnVenue = new ArrayList<>();

        for (Object element : list) {
            Map<String, Object> map = objectMapper.convertValue(element, Map.class);
           String venue_Id = (String) map.get("venue_id");

           if(venue_Id != null)
           {
               Integer id = Integer.parseInt(venue_Id);
               if (id == venueId) {
                   eventsBasedOnVenue.add(map);
               }
           }


        }

        return eventsBasedOnVenue;

    }

     //create events
    public ResponseEntity<Map<String, String>> createEvent(@RequestBody Object object) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        headers.set("Content-Type", "application/json");
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            HttpEntity<String> entity = new HttpEntity<String>(objectMapper.writeValueAsString(object), headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            System.out.println(response.getBody());

            Map<String, Object> mp = new ObjectMapper().readValue(response.getBody(), HashMap.class);

            Events events = new Events(Long.parseLong(mp.get("id").toString()));
            eventsRepository.save(events);
        }
        catch (Exception eek) {
            System.out.println("** Exception: "+ eek.getMessage());
        }

        return new ResponseEntity<Map<String,String>>(
                new HashMap<String, String>(){{
                    put("message", "Event Added Successfully");
                }}, HttpStatus.OK);
    }


    // Get Single event


    public Object getEvent(@PathVariable("eventid") long eventid) {
        List list = eventsRepository.findEventsByEventId(eventid);
        if(list.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event id doesn't exist");
        }

        String theUrl = "https://www.eventbriteapi.com/v3/events/"+Long.toString(eventid)+"/";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> mp = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        headers.set("Content-Type", "application/json");

        try {

            HttpEntity<String> entity = new HttpEntity<String>(headers);
            ResponseEntity<String> response = restTemplate.exchange(theUrl, HttpMethod.GET, entity, String.class);

            mp = new ObjectMapper().readValue(response.getBody(), HashMap.class);
        }
        catch (Exception eek) {
            System.out.println("** Exception: "+ eek.getMessage());
        }
        return mp;
    }


    //events based on interests of user

//    public ResponseEntity<List<Events>> getEventsbyInterests(@PathVariable String userId) throws JsonProcessingException {
//        UserInfo user = userRepository.findByUserId(userId);
//
//        if(user == null)
//        {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Id Doesn't exists");
//        }
//
//        Object object = fetchEvent();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        List list = objectMapper.convertValue(object, List.class);
//
//        List<Events> eventsbasedoninterests = new ArrayList<>();
//
//        List<String> interests = user.getInterests();
//
//
//        return eventsbasedoninterests;
//
//    }


}

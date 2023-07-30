package com.example.Backend.controllers;

import com.example.Backend.models.UserInfo;
import com.example.Backend.service.EventBriteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/event")
public class event {

    private final EventBriteService eventBriteService;

    @Autowired
    public event(EventBriteService eventBriteService)
    {
        this.eventBriteService = eventBriteService;
    }

    @GetMapping
    public Object getEvent() throws JsonProcessingException {
        return eventBriteService.fetchEvent();
    }

    @PostMapping
    public void createEvent(@RequestBody Object object) throws JsonProcessingException
    {
        eventBriteService.createEvent(object);
    }

    @GetMapping("/{eventid}")
    public Object getEventDetail(@PathVariable long eventid )
    {
        return eventBriteService.getEvent(eventid);
    }

    // Register for the event


}

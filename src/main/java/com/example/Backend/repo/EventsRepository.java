package com.example.Backend.repo;


import com.example.Backend.models.Events;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EventsRepository extends MongoRepository<Events, String> {

          List<Events> findEventsByEventId(long eventId);
    }


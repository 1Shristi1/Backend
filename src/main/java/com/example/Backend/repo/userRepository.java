package com.example.Backend.repo;



import com.example.Backend.models.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userRepository extends MongoRepository<UserInfo, String> {


    UserInfo findByUserId(String userId);


    UserInfo findByEmail(String email);




}

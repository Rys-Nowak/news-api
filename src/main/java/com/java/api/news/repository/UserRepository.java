package com.java.api.news.repository;

import com.java.api.news.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}

package com.example.demo.run;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface RunRepository extends ListCrudRepository<Run, Integer> {

    // @Query("select * from run where location = :location") We can also create custom queries
    List<Run> findAllByLocation(String location);
}
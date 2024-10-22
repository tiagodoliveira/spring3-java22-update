package com.example.demo.run;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcClientRunRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcClientRunRepository.class);
    private final JdbcClient jdbcClient;

    public JdbcClientRunRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private List<Run> runs = new ArrayList<>();

    public List<Run> findAll() {
        return jdbcClient.sql("SELECT * FROM Run")
                .query(Run.class)
                .list();
    }

    public Optional<Run> findById(Integer id) {
        return jdbcClient.sql("SELECT id, title, started_on, completed_on, miles, location FROM Run Where id = :id")
                .param("id", id)
                .query(Run.class)
                .optional();
    }

    public void create(Run run) {
        var nrOfAffectedRows = jdbcClient.sql("INSERT INTO Run(id, title, started_on, completed_on, miles, location) values(?,?,?,?,?,?)")
                .params(List.of(run.id(), run.title(), run.startedOn(), run.completedOn(), run.miles(), run.location().toString()))
                .update();

        Assert.state(nrOfAffectedRows == 1, "Failed to create run " + run.title());
    }

    public void update(Run run, Integer id) {
        var nrOfAffectedRows = jdbcClient.sql("UPDATE Run SET title = ?, started_on = ?, completed_on = ?, location = ? WHERE id = ?")
                .params(List.of(run.title(), run.startedOn(), run.completedOn(), run.miles(), run.location().toString(), id))
                .update();

        Assert.state(nrOfAffectedRows == 1, "Failed to update run " + run.title());
    }

    public void delete(Integer id) {
        var nrOfAffectedRows = jdbcClient.sql("DELETE FROM Run WHERE id = :id")
                .param("id", id)
                .update();

        Assert.state(nrOfAffectedRows == 1, "Failed to update run " + id);
    }

    public int count(){
        return jdbcClient.sql("SELECT * from Run").query().listOfRows().size();
    }

    public void saveAll(List<Run> runs) {
        runs.forEach(this::create);
    }

    public List<Run> findByLocation(String location) {
        return jdbcClient.sql("SELECT * FROM Run WHERE location = :location")
                .param("location", location)
                .query(Run.class)
                .list();
    }

    @PostConstruct
    public void init() {
        runs.add(new Run(
                1 ,
                "First Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(1, ChronoUnit.HOURS),
                5,
                Location.OUTDOOR,1));

        runs.add(new Run(
                2 ,
                "Morning Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(2, ChronoUnit.HOURS),
                7,
                Location.INDOOR,1));

        runs.add(new Run(
                3 ,
                "Afternoon Run",
                LocalDateTime.now(),
                LocalDateTime.now().plus(30, ChronoUnit.MINUTES),
                2,
                Location.OUTDOOR,1));
    }
}

package ru.artschool.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.artschool.server.repository.StudentRepository;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StudentRepository studentRepo;

    public StatsController(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    // Статистика: сколько учеников на каждом курсе
    @GetMapping("/students-by-course")
    public Map<String, Long> studentsByCourse() {
        return studentRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        s -> (s.getCourse() == null ? "Без курса" : s.getCourse().getTitle()),
                        Collectors.counting()
                ));
    }
}
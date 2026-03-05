package ru.artschool.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.artschool.server.model.Course;
import ru.artschool.server.model.Student;
import ru.artschool.server.repository.CourseRepository;
import ru.artschool.server.repository.StudentRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(CourseRepository courseRepo, StudentRepository studentRepo) {
        return args -> {
            if (courseRepo.count() == 0) {
                Course painting = courseRepo.save(new Course("Живопись"));
                Course drawing  = courseRepo.save(new Course("Рисунок"));

                studentRepo.save(new Student("Аня", 12, painting));
                studentRepo.save(new Student("Илья", 14, drawing));
            }
        };
    }
}
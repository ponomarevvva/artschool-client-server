package ru.artschool.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.artschool.server.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
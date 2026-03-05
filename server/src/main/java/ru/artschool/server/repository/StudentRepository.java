package ru.artschool.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.artschool.server.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
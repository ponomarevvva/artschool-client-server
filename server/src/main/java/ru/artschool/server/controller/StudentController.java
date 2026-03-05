package ru.artschool.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.artschool.server.model.Course;
import ru.artschool.server.model.Student;
import ru.artschool.server.repository.CourseRepository;
import ru.artschool.server.repository.StudentRepository;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;

    public StudentController(StudentRepository studentRepo, CourseRepository courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    // 1) Получить всех студентов
    @GetMapping
    public List<Student> getAll() {
        return studentRepo.findAll();
    }

    // 2) Фильтрация по имени: /students?name=ма
    @GetMapping(params = "name")
    public List<Student> searchByName(@RequestParam String name) {
        String q = name.toLowerCase();
        return studentRepo.findAll().stream()
                .filter(s -> s.getName() != null && s.getName().toLowerCase().contains(q))
                .toList();
    }

    // DTO для создания/обновления
    public static class StudentCreateRequest {
        public String name;
        public int age;
        public Long courseId;
    }

    // 3) Добавить студента
    @PostMapping
    public Student add(@RequestBody StudentCreateRequest req) {
        Course course = getCourseOrThrow(req.courseId);
        return studentRepo.save(new Student(req.name, req.age, course));
    }

    // 4) Обновить студента
    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody StudentCreateRequest req) {
        Student s = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));

        Course course = getCourseOrThrow(req.courseId);

        s.setName(req.name);
        s.setAge(req.age);
        s.setCourse(course);

        return studentRepo.save(s);
    }

    // 5) Удалить студента
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentRepo.deleteById(id);
    }

    // Вспомогательная функция: получить курс по id или кинуть ошибку
    private Course getCourseOrThrow(Long courseId) {
        if (courseId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courseId is required");
        }
        return courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found: " + courseId));
    }
}
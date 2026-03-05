package ru.artschool.server.model;

import jakarta.persistence.*;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;

    @ManyToOne
    private Course course;

    public Student() {}

    public Student(String name, int age, Course course) {
        this.name = name;
        this.age = age;
        this.course = course;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public Course getCourse() { return course; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(Course course) { this.course = course; }
}
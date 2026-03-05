package ru.artschool.client;

public class CourseDto {
    public Long id;
    public String title;

    @Override
    public String toString() {
        return title + " (id=" + id + ")";
    }
}
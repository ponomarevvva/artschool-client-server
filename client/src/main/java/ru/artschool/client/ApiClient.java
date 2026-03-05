package ru.artschool.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

public class ApiClient {
    private static final String BASE = "http://localhost:8080";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper om = new ObjectMapper();

    private String authHeader; // "Basic ..."

    public void setCredentials(String username, String password) {
        String token = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        this.authHeader = "Basic " + token;
    }

    private HttpRequest.Builder authed(URI uri) {
        HttpRequest.Builder b = HttpRequest.newBuilder(uri);
        if (authHeader != null) b.header("Authorization", authHeader);
        return b;
    }

    // ---------- AUTH ----------
    public void register(String username, String password) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);

        String json = om.writeValueAsString(payload);

        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("Register failed: " + resp.statusCode() + " " + resp.body());
        }
    }

    public Map<String, String> me() throws Exception {
        HttpRequest req = authed(URI.create(BASE + "/auth/me")).GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("Auth failed: " + resp.statusCode() + " " + resp.body());
        }
        return om.readValue(resp.body(), new TypeReference<Map<String, String>>() {});
    }

    // ---------- COURSES ----------
    public List<CourseDto> getCourses() throws Exception {
        HttpRequest req = authed(URI.create(BASE + "/courses")).GET().build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, new TypeReference<List<CourseDto>>() {});
    }

    public CourseDto addCourse(String title) throws Exception {
        String json = om.writeValueAsString(Map.of("title", title));
        HttpRequest req = authed(URI.create(BASE + "/courses"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, CourseDto.class);
    }

    public CourseDto updateCourse(long id, String title) throws Exception {
        String json = om.writeValueAsString(Map.of("title", title));
        HttpRequest req = authed(URI.create(BASE + "/courses/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, CourseDto.class);
    }

    public void deleteCourse(long id) throws Exception {
        HttpRequest req = authed(URI.create(BASE + "/courses/" + id)).DELETE().build();
        http.send(req, HttpResponse.BodyHandlers.discarding());
    }

    // ---------- STUDENTS ----------
    public List<StudentDto> getStudents() throws Exception {
        HttpRequest req = authed(URI.create(BASE + "/students")).GET().build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, new TypeReference<List<StudentDto>>() {});
    }

    public List<StudentDto> searchStudents(String name) throws Exception {
        String q = URLEncoder.encode(name, StandardCharsets.UTF_8);
        HttpRequest req = authed(URI.create(BASE + "/students?name=" + q)).GET().build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, new TypeReference<List<StudentDto>>() {});
    }

    public StudentDto addStudent(String name, int age, long courseId) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("age", age);
        payload.put("courseId", courseId);

        String json = om.writeValueAsString(payload);
        HttpRequest req = authed(URI.create(BASE + "/students"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, StudentDto.class);
    }

    public StudentDto updateStudent(long id, String name, int age, long courseId) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("age", age);
        payload.put("courseId", courseId);

        String json = om.writeValueAsString(payload);
        HttpRequest req = authed(URI.create(BASE + "/students/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, StudentDto.class);
    }

    public void deleteStudent(long id) throws Exception {
        HttpRequest req = authed(URI.create(BASE + "/students/" + id)).DELETE().build();
        http.send(req, HttpResponse.BodyHandlers.discarding());
    }

    // ---------- STATS ----------
    public Map<String, Long> studentsByCourse() throws Exception {
        HttpRequest req = authed(URI.create(BASE + "/stats/students-by-course")).GET().build();
        String body = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
        return om.readValue(body, new TypeReference<Map<String, Long>>() {});
    }
}
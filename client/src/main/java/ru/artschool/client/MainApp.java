package ru.artschool.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainApp extends Application {

    private final ApiClient api = new ApiClient();
    private String role = "USER"; // ADMIN или USER

    @Override
    public void start(Stage stage) {
        showLogin(stage);
    }

    private boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    // ---------- LOGIN / REGISTER ----------
    private void showLogin(Stage stage) {
        TextField userField = new TextField();
        userField.setPromptText("Логин");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Пароль");

        Label hint = new Label("Админ: admin / admin");

        Button loginBtn = new Button("Войти");
        Button regBtn = new Button("Регистрация (USER)");

        loginBtn.setOnAction(e -> {
            try {
                String u = userField.getText().trim();
                String p = passField.getText();

                api.setCredentials(u, p);
                Map<String, String> me = api.me();
                role = me.getOrDefault("role", "USER");

                showMenu(stage);
            } catch (Exception ex) {
                showError(ex);
            }
        });

        regBtn.setOnAction(e -> {
            try {
                String u = userField.getText().trim();
                String p = passField.getText();

                api.register(u, p);
                new Alert(Alert.AlertType.INFORMATION, "Зарегистрировано. Теперь нажмите «Войти».").showAndWait();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        VBox root = new VBox(10,
                new Label("Вход"),
                userField,
                passField,
                loginBtn,
                regBtn,
                hint
        );
        root.setPadding(new Insets(16));

        stage.setTitle("ArtSchool — Login");
        stage.setScene(new Scene(root, 340, 260));
        stage.show();
    }

    // ---------- MAIN MENU ----------
    private void showMenu(Stage stage) {
        Button studentsBtn = new Button("Ученики");
        Button coursesBtn  = new Button("Курсы");
        Button statsBtn    = new Button("Статистика");
        Button aboutBtn    = new Button("Об авторе");

        Label who = new Label("Роль: " + role + (isAdmin() ? " (полный доступ)" : " (только просмотр)"));

        studentsBtn.setOnAction(e -> openStudentsWindow());
        coursesBtn.setOnAction(e -> openCoursesWindow());
        statsBtn.setOnAction(e -> openStatsWindow());
        aboutBtn.setOnAction(e -> openAboutWindow());

        VBox root = new VBox(10,
                new Label("ИС художественной школы"),
                who,
                studentsBtn,
                coursesBtn,
                statsBtn,
                aboutBtn
        );
        root.setPadding(new Insets(16));

        stage.setTitle("ArtSchool Client");
        stage.setScene(new Scene(root, 380, 260));
        stage.show();
    }

    // ---------- COURSES WINDOW ----------
    private void openCoursesWindow() {
        Stage st = new Stage();
        st.setTitle("Курсы");

        TableView<CourseDto> table = new TableView<>();
        TableColumn<CourseDto, String> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(String.valueOf(v.getValue().id)));
        TableColumn<CourseDto, String> cTitle = new TableColumn<>("Название");
        cTitle.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().title));
        table.getColumns().add(cId);
        table.getColumns().add(cTitle);

        TextField titleField = new TextField();
        titleField.setPromptText("Название курса");

        Button refresh = new Button("Обновить");
        Button add = new Button("Добавить");
        Button upd = new Button("Изменить выбранный");
        Button del = new Button("Удалить выбранный");

        add.setDisable(!isAdmin());
        upd.setDisable(!isAdmin());
        del.setDisable(!isAdmin());

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) titleField.setText(sel.title);
        });

        Runnable reload = () -> {
            try {
                table.setItems(FXCollections.observableArrayList(api.getCourses()));
            } catch (Exception ex) { showError(ex); }
        };

        refresh.setOnAction(e -> reload.run());

        add.setOnAction(e -> {
            try {
                if (!titleField.getText().isBlank()) {
                    api.addCourse(titleField.getText().trim());
                    titleField.clear();
                    reload.run();
                }
            } catch (Exception ex) { showError(ex); }
        });

        upd.setOnAction(e -> {
            try {
                CourseDto sel = table.getSelectionModel().getSelectedItem();
                if (sel == null) return;
                api.updateCourse(sel.id, titleField.getText().trim());
                reload.run();
            } catch (Exception ex) { showError(ex); }
        });

        del.setOnAction(e -> {
            try {
                CourseDto sel = table.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    api.deleteCourse(sel.id);
                    reload.run();
                }
            } catch (Exception ex) { showError(ex); }
        });

        HBox controls = new HBox(10, titleField, add, upd, del, refresh);
        controls.setPadding(new Insets(10));

        VBox root = new VBox(table, controls);
        reload.run();

        st.setScene(new Scene(root, 840, 420));
        st.show();
    }

    // ---------- STUDENTS WINDOW ----------
    private void openStudentsWindow() {
        Stage st = new Stage();
        st.setTitle("Ученики");

        TableView<StudentDto> table = new TableView<>();
        TableColumn<StudentDto, String> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(String.valueOf(v.getValue().id)));
        TableColumn<StudentDto, String> cName = new TableColumn<>("Имя");
        cName.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(v.getValue().name));
        TableColumn<StudentDto, String> cAge = new TableColumn<>("Возраст");
        cAge.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(String.valueOf(v.getValue().age)));
        TableColumn<StudentDto, String> cCourse = new TableColumn<>("Курс");
        cCourse.setCellValueFactory(v -> new javafx.beans.property.SimpleStringProperty(
                v.getValue().course == null ? "" : v.getValue().course.title
        ));
        table.getColumns().add(cId);
        table.getColumns().add(cName);
        table.getColumns().add(cAge);
        table.getColumns().add(cCourse);

        TextField nameField = new TextField(); nameField.setPromptText("Имя");
        TextField ageField = new TextField();  ageField.setPromptText("Возраст");
        ComboBox<CourseDto> courseBox = new ComboBox<>(); courseBox.setPromptText("Курс");

        TextField searchField = new TextField(); searchField.setPromptText("Фильтр по имени");
        Button searchBtn = new Button("Искать");

        Button refresh = new Button("Обновить");
        Button add = new Button("Добавить");
        Button upd = new Button("Изменить выбранного");
        Button del = new Button("Удалить выбранного");

        add.setDisable(!isAdmin());
        upd.setDisable(!isAdmin());
        del.setDisable(!isAdmin());

        Runnable reload = () -> {
            try {
                courseBox.setItems(FXCollections.observableArrayList(api.getCourses()));
                table.setItems(FXCollections.observableArrayList(api.getStudents()));
            } catch (Exception ex) { showError(ex); }
        };

        refresh.setOnAction(e -> reload.run());

        searchBtn.setOnAction(e -> {
            try {
                String q = searchField.getText().trim();
                if (q.isBlank()) reload.run();
                else table.setItems(FXCollections.observableArrayList(api.searchStudents(q)));
            } catch (Exception ex) { showError(ex); }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel == null) return;

            nameField.setText(sel.name);
            ageField.setText(String.valueOf(sel.age));

            if (sel.course != null && courseBox.getItems() != null) {
                Optional<CourseDto> match = courseBox.getItems().stream()
                        .filter(c -> c.id != null && c.id.equals(sel.course.id))
                        .findFirst();
                match.ifPresent(courseBox::setValue);
            }
        });

        add.setOnAction(e -> {
            try {
                String n = nameField.getText().trim();
                int a = Integer.parseInt(ageField.getText().trim());
                CourseDto c = courseBox.getValue();
                if (n.isBlank() || c == null) return;

                api.addStudent(n, a, c.id);
                reload.run();
            } catch (Exception ex) { showError(ex); }
        });

        upd.setOnAction(e -> {
            try {
                StudentDto sel = table.getSelectionModel().getSelectedItem();
                if (sel == null) return;

                String n = nameField.getText().trim();
                int a = Integer.parseInt(ageField.getText().trim());
                CourseDto c = courseBox.getValue();
                if (n.isBlank() || c == null) return;

                api.updateStudent(sel.id, n, a, c.id);
                reload.run();
            } catch (Exception ex) { showError(ex); }
        });

        del.setOnAction(e -> {
            try {
                StudentDto sel = table.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    api.deleteStudent(sel.id);
                    reload.run();
                }
            } catch (Exception ex) { showError(ex); }
        });

        HBox row1 = new HBox(10, nameField, ageField, courseBox, add, upd, del, refresh);
        row1.setPadding(new Insets(10));
        HBox row2 = new HBox(10, searchField, searchBtn);
        row2.setPadding(new Insets(10));

        VBox root = new VBox(table, row1, row2);
        reload.run();

        st.setScene(new Scene(root, 1000, 520));
        st.show();
    }

    // ---------- STATS ----------
    private void openStatsWindow() {
        Stage st = new Stage();
        st.setTitle("Статистика");

        TextArea area = new TextArea();
        area.setEditable(false);

        Button refresh = new Button("Обновить");
        refresh.setOnAction(e -> {
            try {
                Map<String, Long> map = api.studentsByCourse();
                area.setText(map.entrySet().stream()
                        .map(e2 -> e2.getKey() + ": " + e2.getValue())
                        .collect(Collectors.joining("\n")));
            } catch (Exception ex) { showError(ex); }
        });

        VBox root = new VBox(10, refresh, area);
        root.setPadding(new Insets(10));
        st.setScene(new Scene(root, 450, 350));
        st.show();
        refresh.fire();
    }

    // ---------- ABOUT ----------
    private void openAboutWindow() {
        Stage st = new Stage();
        st.setTitle("Об авторе");

        String text = """
ОБ АВТОРЕ

ФИО: Куликова Ксения Владимировна
Группа / учебное заведение: ДПИ23-1С Финансовый Университет при Правительстве Российской Федерации
Контакты: 233290@edu.fa.ru

Технологии, использованные в проекте:
- Java 17
- Spring Boot (REST API)
- Spring Data JPA (Hibernate)
- H2 Database
- Spring Security (Basic Auth, роли ADMIN/USER)
- JavaFX (GUI-клиент)
- HTTP (JSON) взаимодействие с сервером

Опыт работы с технологиями (кратко):
В ходе выполнения проекта освоены основы построения клиент–серверной архитектуры,
разработки REST API на Spring Boot, работы с ORM (JPA/Hibernate) и базой данных H2,
а также создание графического интерфейса на JavaFX и реализация авторизации/ролей
с использованием Spring Security.

Даты выполнения проекта:
Начало: 02.10.2025
Завершение: 25.12.2025
""";

        TextArea area = new TextArea(text);
        area.setEditable(false);
        area.setWrapText(true);

        VBox root = new VBox(area);
        root.setPadding(new Insets(12));
        VBox.setVgrow(area, Priority.ALWAYS);
        area.setPrefHeight(1000);

        st.setScene(new Scene(root, 780, 520));
        st.show();
    }

    private void showError(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(ex.getClass().getSimpleName());
        a.setContentText(ex.getMessage());
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
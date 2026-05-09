package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    private Group root;
    private Box cube;
    private PerspectiveCamera camera;
    private double rotation = 0;
    private double rotationX = 0;
    private double rotationY = 0;
    
    // Для контроля мыши
    private double prevMouseX = 0;
    private double prevMouseY = 0;
    private boolean isMousePressed = false;
    
    // Для броска
    private double velocityX = 0;
    private double velocityY = 0;
    private double velocityZ = 0;
    private double posZ = 0;
    private double posY = 0;
    
    // Освещение
    private PointLight pointLight;
    private AmbientLight ambientLight;
    private double lightAttenuation = 0.01;
    private double ambientIntensity = 0.5;
    
    private AnimationTimer animationTimer;

    @Override
    public void start(Stage primaryStage) {
        // Создание корневой группы
        root = new Group();
        
        // Камера
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-400);
        
        // Добавление подсветки
        setupLighting();
        
        // Создание кубика
        createDice();
        
        // Создание поверхности
        createSurface();
        
        // UI панель
        VBox controlPanel = createControlPanel();
        
        BorderPane mainRoot = new BorderPane();
        mainRoot.setCenter(root);
        mainRoot.setRight(controlPanel);
        
        Scene mainScene = new Scene(mainRoot, 1200, 700);
        mainScene.setCamera(camera);
        mainScene.setFill(Color.DARKGRAY);
        
        // Обработка входов
        setupMouseControl(mainScene);
        
        primaryStage.setTitle("3D Кубик для игры");
        primaryStage.setScene(mainScene);
        primaryStage.setX(100);
        primaryStage.setY(100);
        primaryStage.show();
        
        // Анимация
        startAnimation();
    }

    private void setupLighting() {
        // Точечный источник света
        pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(300);
        pointLight.setTranslateY(-300);
        pointLight.setTranslateZ(-200);
        root.getChildren().add(pointLight);
        
        // Фоновый свет
        ambientLight = new AmbientLight(Color.color(0.5, 0.5, 0.5, ambientIntensity));
        root.getChildren().add(ambientLight);
    }

    private void createDice() {
        cube = new Box(100, 100, 100);
        cube.setTranslateY(0);
        cube.setTranslateZ(0);
        posY = 0;
        posZ = 0;
        
        // Создание материалов с текстурами
        createCubeMaterials();
        
        root.getChildren().add(cube);
    }

    private void createCubeMaterials() {
        // Загрузка текстур
        try {
            Image texture1 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/textures/copper.png")));
            Image texture2 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/textures/gold.png")));
            Image texture3 = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/textures/tree.png")));
            
            PhongMaterial material1 = new PhongMaterial();
            material1.setDiffuseMap(texture1);
            material1.setSpecularPower(32);
            
            PhongMaterial material2 = new PhongMaterial();
            material2.setDiffuseMap(texture2);
            material2.setSpecularPower(32);
            
            PhongMaterial material3 = new PhongMaterial();
            material3.setDiffuseMap(texture3);
            material3.setSpecularPower(32);
            
            cube.setMaterial(material1);
            
        } catch (Exception e) {
            // Если текстуры не найдены, используем однотонные материалы
            PhongMaterial whiteMaterial = new PhongMaterial(Color.WHITE);
            whiteMaterial.setSpecularPower(32);
            cube.setMaterial(whiteMaterial);
        }
    }

    private void createSurface() {
        Box surface = new Box(800, 10, 600);
        surface.setTranslateY(200);
        
        PhongMaterial surfaceMaterial = new PhongMaterial(Color.LIGHTGRAY);
        surfaceMaterial.setSpecularPower(16);
        surface.setMaterial(surfaceMaterial);
        
        root.getChildren().add(surface);
    }

    private void setupMouseControl(Scene scene) {
        scene.setOnMousePressed(event -> {
            isMousePressed = true;
            prevMouseX = event.getSceneX();
            prevMouseY = event.getSceneY();
        });
        
        scene.setOnMouseReleased(event -> {
            isMousePressed = false;
        });
        
        scene.setOnMouseDragged(event -> {
            if (isMousePressed) {
                double deltaX = event.getSceneX() - prevMouseX;
                double deltaY = event.getSceneY() - prevMouseY;
                
                rotationY += deltaX * 0.5;
                rotationX += deltaY * 0.5;
                
                prevMouseX = event.getSceneX();
                prevMouseY = event.getSceneY();
                
                updateCubeRotation();
            }
        });
        
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                throwDice();
            }
        });
    }

    private void updateCubeRotation() {
        cube.getTransforms().clear();
        Rotate rotateX = new Rotate(rotationX, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(rotationY, Rotate.Y_AXIS);
        cube.getTransforms().addAll(rotateX, rotateY);
    }

    private void throwDice() {
        velocityY = -15;
        velocityX = (Math.random() - 0.5) * 10;
        velocityZ = (Math.random() - 0.5) * 10;
        rotationX += (Math.random() - 0.5) * 180;
        rotationY += (Math.random() - 0.5) * 180;
    }

    private VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #34495e;");
        panel.setPrefWidth(200);
        
        Label titleLabel = new Label("Управление освещением");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        
        Label attenuationLabel = new Label("Затухание света:");
        attenuationLabel.setStyle("-fx-text-fill: white;");
        
        Slider attenuationSlider = new Slider(0, 1, lightAttenuation);
        attenuationSlider.setOnMouseReleased(e -> {
            lightAttenuation = attenuationSlider.getValue();
            updateLighting();
        });
        
        Label ambientLabel = new Label("Фоновое освещение:");
        ambientLabel.setStyle("-fx-text-fill: white;");
        
        Slider ambientSlider = new Slider(0, 1, ambientIntensity);
        ambientSlider.setOnMouseReleased(e -> {
            ambientIntensity = ambientSlider.getValue();
            updateLighting();
        });
        
        Button throwButton = new Button("Бросить кубик (Space)");
        throwButton.setPrefWidth(170);
        throwButton.setStyle("-fx-font-size: 12;");
        throwButton.setOnAction(e -> throwDice());
        
        Label infoLabel = new Label("Вращение: перетащите мышью\nБросок: нажмите Space");
        infoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10;");
        
        panel.getChildren().addAll(
                titleLabel,
                new Separator(),
                attenuationLabel,
                attenuationSlider,
                ambientLabel,
                ambientSlider,
                new Separator(),
                throwButton,
                infoLabel
        );
        
        return panel;
    }

    private void updateLighting() {
        if (ambientLight != null) {
            ambientLight.setColor(Color.color(0.5, 0.5, 0.5, ambientIntensity));
        }
    }

    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Применение гравитации и физики
                velocityY += 0.5; // Гравитация
                
                posY += velocityY;
                posZ += velocityZ;
                posZ += velocityX * 0.1;
                
                // Столкновение с поверхностью
                if (posY >= 100) {
                    posY = 100;
                    velocityY *= -0.7; // Отскок
                    if (Math.abs(velocityY) < 1) {
                        velocityY = 0;
                    }
                }
                
                cube.setTranslateY(posY);
                cube.setTranslateZ(posZ);
                
                // Вращение при падении
                if (velocityY != 0) {
                    rotationX += velocityX;
                    rotationY += velocityZ;
                    updateCubeRotation();
                }
            }
        };
        animationTimer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

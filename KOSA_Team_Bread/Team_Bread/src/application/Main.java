// Main.java

package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX 애플리케이션의 메인 진입점(Entry Point) 클래스입니다.
 * FXML 파일을 로드하여 초기 화면을 설정합니다.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // FXML 파일을 로드합니다. getResource()의 경로는 FXML 파일의 위치에 따라 달라집니다.
            // 보통 'resources' 폴더는 클래스패스에 포함되므로 상대 경로로 접근할 수 있습니다.
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main/MainPage.fxml"));
            Parent root = loader.load();
            
            // Scene 생성 및 Stage에 설정
            Scene scene = new Scene(root, 800, 600);
            
            primaryStage.setTitle("성심당 쿠폰 관리 시스템");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
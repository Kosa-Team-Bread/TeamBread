// Main.java

package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX 애플리케이션의 메인 진입점(Entry Point) 클래스. FXML 파일을 로드하여 초기 화면을 설정
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			// FXML 파일 로드. getResource()의 경로는 FXML 파일의 위치에 따라 달라짐
			// 보통 'resources' 폴더는 클래스패스에 포함되므로 상대 경로로 접근 가능
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
			Parent root = loader.load();

			// Scene 생성 및 Stage에 설정
			// 프로그램 창 크기 (1440 x 900)
			Scene scene = new Scene(root, 1440, 900);

			// 지정된 경로의 CSS 파일을 Scene에 적용
			scene.getStylesheets().add(getClass().getResource("/view/Style.css").toExternalForm());

			primaryStage.setTitle("성심당 쿠폰 관리 시스템");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
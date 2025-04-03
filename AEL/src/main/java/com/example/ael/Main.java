package com.example.ael;

import com.ebay.api.client.auth.oauth2.model.OAuthResponse;
import com.ebay.api.client.auth.oauth2.model.RefreshToken;
import com.example.ael.Utility.ListManager;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.HostServices;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends Application {

    private static Stage primaryStage = null;
    private static Object persistentData = null;
    private static HostServices host = null;
    private static List<OAuthResponse> tokens = new ArrayList<>();

    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);

    @Override
    public void start(Stage stage) throws IOException {
        host = getHostServices();
        accessRefreshTimers();
        requestTimers();
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoginScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        primaryStage.setTitle("AEL");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void openDoc(String url) {
        host.showDocument(url);
    }

    public static void changeScene(String fxmlName) {
        Main.changeScene(fxmlName, null);
    }

    public static void changeScene(String fxmlName, Object passedData) {
        Parent pane = null;
        try {
            pane = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(fxmlName)));
            primaryStage.getScene().setRoot(pane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void setPersistentData(Object data) {
        persistentData = data;
    }

    public static Object getPersistentData() {
        return persistentData;
    }

    public static void popUpModalScreen(String fxmlName) {
        Stage popUpStage = new Stage();
        Scene newScene = null;

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlName));
            newScene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        popUpStage.setScene(newScene);
        popUpStage.initOwner(primaryStage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.showAndWait();

    }

    public static HostServices getHost() {
        return host;
    }

    Runnable ebayTimer = Main::sendEbayRequest;

    Runnable amazonTimer = Main::sendAmazonRequest;

    public static void sendEbayRequest(){
        Date lastDate = Date.from(Instant.now().minusSeconds(60*10));
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(lastDate);
        for (int i = 0; i < tokens.size(); i++) {
            try {
                new EbayRequestHandler()
                        .sendRequest(tokens
                                        .get(i)
                                        .getAccessToken()
                                        .get().getToken(),
                                date);
                System.out.println("Here");
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendAmazonRequest(){
        Date lastDate = Date.from(Instant.now().minusSeconds(60*10));
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(lastDate);
        for (int i = 0; i < tokens.size(); i++) {
            try {
                new AmazonRequest()
                        .sendRequest(date);
                System.out.println("Here");
            } catch (URISyntaxException | IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void requestTimers(){
        ScheduledFuture<?> scheduledFuture =
                ses.scheduleAtFixedRate(sendEbayRequest,
                        10,
                        600,
                        TimeUnit.SECONDS);

        ScheduledFuture<?> scheduledFuture1 =
                ses.scheduleAtFixedRate(amazonTimer,
                        10,
                        600,
                        TimeUnit.SECONDS);

    }

    public void accessRefreshTimers(){
        ScheduledFuture<?> scheduledFuture =
                //ses.scheduleAtFixedRate(accessTokenRefresh,
                ses.scheduleAtFixedRate( this::foo,
                        5,
                        60*60*2,
                        TimeUnit.SECONDS);
    }

    Runnable accessTokenRefresh = () -> {
        try {
            getAccessTokens();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    public void foo(){}

    public void getAccessTokens() throws IOException {
        EbayRequestHandler handler = new EbayRequestHandler();
        Properties properties = new Properties();
        RefreshToken token = new RefreshToken();
        ArrayList<String> files = new ArrayList<>();
        try(Stream<Path> stream = Files.list(Paths.get("src/refreshTokens/Ebay/"))){
            files = (ArrayList<String>) stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }catch (IOException e){
            e.printStackTrace();
        }
        for (int i = 0; i < files.size(); i++) {
            properties.load(new FileReader("src/refreshTokens/Ebay/"
                    + files.get(i)));
            token.setToken(properties.get("RefreshToken").toString());
            tokens.add(handler.getAccessTokenFromRefresh(token));
            System.out.println(token.getToken());
        }

    }

    public static void main(String[] args) throws IOException {
        launch();
    }
}
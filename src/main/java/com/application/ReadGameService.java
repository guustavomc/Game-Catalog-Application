package com.application;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReadGameService {

    private static final String GAMES_FILE_PATH = "data/Games.json";

    private List<Game> listGame = new ArrayList<>();

    @PostConstruct
    public void getGameList(){
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            File file = new File(GAMES_FILE_PATH);

            JsonNode rootNode = objectMapper.readTree(file);

            for(JsonNode gameNode : rootNode){
                String name = gameNode.get("name").asText();
                String publisher = gameNode.get("publisher").asText();
                String releaseDate = gameNode.get("releaseDate").asText();

                List<String> availablePlatforms = new ArrayList<>();
                JsonNode availablePlatformsNode = gameNode.get("availablePlatforms");

                for (JsonNode platformNode : availablePlatformsNode){
                    availablePlatforms.add(platformNode.asText().trim());
                }

                listGame.add(new Game(name, publisher,releaseDate,availablePlatforms));

            }

        }
        catch (IOException e){
            throw new RuntimeException("Error reading courses JSON", e);
        }
    }

    public List<Game> findGames(){
        return this.listGame;
    }

    public Game findGameWithName(String name){
        return (Game) listGame.stream().filter(game -> game.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Game> findGameWithPublisher(String name){
        return listGame.stream().filter(game -> game.getPublisher().equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    public List<Game> findGamesInPlatform(String name){
        return listGame.stream().filter(game -> game.getAvailablePlatforms().stream().anyMatch(t -> t.trim().equalsIgnoreCase(name))).collect(Collectors.toList());
    }

    public void addGame(Game game){
        listGame.add(game);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(GAMES_FILE_PATH), listGame);

            ClassPathResource resource = new ClassPathResource("Games.json");

            try(OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(resource.getFile()), StandardCharsets.UTF_8)){

            }
        } catch (IOException  e) {
            throw new RuntimeException("Failed to update Games.json", e);
        }


    }

}

package com.application;
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

    private List<Game> listGame = new ArrayList<>();

    @PostConstruct
    public void getGameList(){
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            ClassPathResource resource = new ClassPathResource("Games.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            for(JsonNode gameNode : rootNode){
                String name = gameNode.get("name").asText();
                String publisher = gameNode.get("publisher").asText();
                String releaseDate = gameNode.get("releaseDate").asText();

                List<String> availablePlatforms = new ArrayList<>();
                JsonNode availablePlaformsNode = gameNode.get("availablePlaforms");

                for (JsonNode platformNode : availablePlaformsNode){
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
        return listGame.stream().filter(game -> game.getAvailablePlaforms().stream().anyMatch(t -> t.trim().equalsIgnoreCase(name))).collect(Collectors.toList());
    }

    public void addGame(Game game){
        listGame.add(game);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(listGame);

            ClassPathResource resource = new ClassPathResource("Games.json");

            try(OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(resource.getFile()), StandardCharsets.UTF_8)){

            }
        } catch (IOException  e) {
            throw new RuntimeException("Failed to update Games.json", e);
        }


    }

}

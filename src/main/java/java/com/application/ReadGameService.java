package java.com.application;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

}

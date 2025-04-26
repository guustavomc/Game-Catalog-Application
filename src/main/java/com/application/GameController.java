package com.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private ReadGameService service;

    public GameController(ReadGameService service){
        this.service=service;
    }

    @GetMapping
    public List<Game> getAllGames(){
        return service.findGames();
    }

    @GetMapping("/name/{name}")
    public Game getGameWithName(@PathVariable("name") String name){
        return service.findGameWithName(name);
    }

    @GetMapping("/publisher/{publisher}")
    public List<Game> getGamesWithPublisher(@PathVariable("publisher") String publisher){
        return  service.findGameWithPublisher(publisher);
    }

    @GetMapping("/platform/{platform}")
    public List<Game> getGamesOnPlatform(@PathVariable("platform") String platform){
        return service.findGamesInPlatform(platform);
    }

    @PostMapping
    public ResponseEntity<String> createGame(@RequestBody Game newGame){
        service.addGame(newGame);
        return new ResponseEntity<>("Game Created", HttpStatus.CREATED);
    }


}

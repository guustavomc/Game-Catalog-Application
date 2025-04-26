package com.application;
import java.util.List;


public class Game {

    private String name;
    private String publisher;
    private String releaseDate;
    private List<String> availablePlatforms;

    public Game(){}


    public Game(String name, String publisher, String releaseDate, List<String> availablePlatforms){
        this.name=name;
        this.publisher=publisher;
        this.releaseDate=releaseDate;
        this.availablePlatforms=availablePlatforms;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getAvailablePlatforms() {
        return availablePlatforms;
    }

    public void setAvailablePlatforms(List<String> availablePlaforms) {
        this.availablePlatforms = availablePlaforms;
    }
}

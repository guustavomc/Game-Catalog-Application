package java.com.application;
import java.util.List;

public class Game {

    private String name;
    private String publisher;
    private String releaseDate;
    private List<String> availablePlaforms;


    public Game(String name, String publisher, String releaseDate, List<String> availablePlaforms){
        this.name=name;
        this.publisher=publisher;
        this.releaseDate=releaseDate;
        this.availablePlaforms=availablePlaforms;
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

    public List<String> getAvailablePlaforms() {
        return availablePlaforms;
    }

    public void setAvailablePlaforms(List<String> availablePlaforms) {
        this.availablePlaforms = availablePlaforms;
    }
}

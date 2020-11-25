package mars.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rover {
    @JsonProperty("name")
    private String name;

    @SuppressWarnings("unused")
    public Rover() {

    }

    public Rover(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

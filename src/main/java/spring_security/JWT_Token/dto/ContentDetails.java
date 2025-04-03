package spring_security.JWT_Token.dto;

public class ContentDetails {
    private String duration; // e.g., "PT1H2M3S" for 1 hour, 2 minutes, and 3 seconds
    private String dimension; // e.g., "2d" or "3d"
    private String definition; // e.g., "hd" or "sd"
    private String caption; // e.g., "true" or "false"

    // Getters and Setters
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

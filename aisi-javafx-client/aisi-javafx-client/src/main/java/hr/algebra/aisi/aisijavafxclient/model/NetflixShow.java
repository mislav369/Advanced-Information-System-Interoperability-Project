package hr.algebra.aisi.aisijavafxclient.model;

public class NetflixShow {
    private Long id;
    private String showType;
    private String title;
    private String director;
    private String castMembers;
    private String country;
    private String dateAdded;
    private Integer releaseYear;
    private String rating;
    private String duration;
    private String listedIn;
    private String description;

    public NetflixShow() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShowType() { return showType; }
    public void setShowType(String showType) { this.showType = showType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getCastMembers() { return castMembers; }
    public void setCastMembers(String castMembers) { this.castMembers = castMembers; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getListedIn() { return listedIn; }
    public void setListedIn(String listedIn) { this.listedIn = listedIn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return title + " (" + releaseYear + ")";
    }

}

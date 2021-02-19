package se.andreasson.core.model;

import javax.persistence.*;

@Entity
@Table(name ="artists")
public class Artist {
    @Id
    private int artistId;
    private String name;

    public Artist() {
    }

    public Artist(int artistId) {
        this.artistId = artistId;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "artistId=" + artistId +
                ", name='" + name + '\'' +
                '}';
    }
}

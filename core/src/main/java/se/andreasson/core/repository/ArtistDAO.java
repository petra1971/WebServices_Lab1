package se.andreasson.core.repository;

import se.andreasson.core.model.Artist;

import java.util.List;

public interface ArtistDAO {

    void create(Artist a);

    List<Artist> getAllArtists();

    List<Artist> getByName(String name);

}

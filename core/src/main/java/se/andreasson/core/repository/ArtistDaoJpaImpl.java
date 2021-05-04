package se.andreasson.core.repository;

import se.andreasson.core.model.Artist;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class ArtistDaoJpaImpl implements ArtistDAO {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("Webservice_Lab1");

    @Override
    public void create(Artist a) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(a);
        em.getTransaction().commit();
    }

    @Override
    public List<Artist> getAllArtists() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Artist> list = em.createQuery("from Artist", Artist.class).getResultList();
        em.getTransaction().commit();
        return list;
    }

    @Override
    public List<Artist> getByName(String name) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Artist> list = em.createQuery("from Artist a where a.name = :name", Artist.class)
                .setParameter("name", name).getResultList();
        em.getTransaction().commit();
        return list;
    }

    @Override
    public Artist getById(int id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Artist artist = em.createQuery("from Artist  a where a.artistId = :artistId", Artist.class)
                .setParameter("artistId", id).getSingleResult();
        return artist;
    }
}


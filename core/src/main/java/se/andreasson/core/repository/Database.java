package se.andreasson.core.repository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Database {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("Webservice_Lab1");

}

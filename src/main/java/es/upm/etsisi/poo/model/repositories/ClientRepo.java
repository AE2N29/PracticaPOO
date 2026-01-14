package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.users.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<Client,Integer>{
}

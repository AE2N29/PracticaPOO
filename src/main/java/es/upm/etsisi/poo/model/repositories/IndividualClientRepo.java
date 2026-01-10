package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.users.IndividualClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualClientRepo extends JpaRepository<IndividualClient, Integer>{
}

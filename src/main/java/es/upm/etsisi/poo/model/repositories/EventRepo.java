package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.products.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepo extends JpaRepository<Event, Integer> {

}

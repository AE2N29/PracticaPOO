package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.persistence.OldTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepo extends JpaRepository<OldTicket, Integer> {
}

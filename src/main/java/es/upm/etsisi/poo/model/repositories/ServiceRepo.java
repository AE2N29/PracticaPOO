package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.products.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Integer> {
}

package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.users.Cashier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashierRepo extends JpaRepository<Cashier, Integer> {

}

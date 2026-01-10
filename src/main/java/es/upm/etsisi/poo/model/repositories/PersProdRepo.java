package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.products.PersonalizedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersProdRepo extends JpaRepository<PersonalizedProduct, Integer>{

}

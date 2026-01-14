package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.users.CompanyClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyClientRepo extends JpaRepository<CompanyClient, Integer>{
}

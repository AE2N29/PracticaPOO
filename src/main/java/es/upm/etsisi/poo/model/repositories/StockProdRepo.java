package es.upm.etsisi.poo.model.repositories;

import es.upm.etsisi.poo.model.products.StockProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockProdRepo extends JpaRepository<StockProduct, Integer>{
}

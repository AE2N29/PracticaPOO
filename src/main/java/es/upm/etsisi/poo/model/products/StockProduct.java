package es.upm.etsisi.poo.model.products;

import es.upm.etsisi.poo.exceptions.StoreException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "StockProducts")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class StockProduct extends AbstractProduct {
    protected Category category;
    protected double price;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int stockProdId;


    public StockProduct(String id, String name, Category category, double price) throws StoreException {
        super(id, name);
        this.category = category;
        this.price = price;
    }

    public StockProduct(String name, Category category, double price) throws StoreException {
        super(name);
        this.category = category;
        this.price = price;
    }
    // Constructor de copia, para controlar que el cambio de un producto, no cambie el ticket
    public StockProduct(StockProduct source) throws StoreException {
        super(source.getId(), source.getName());
        this.category = source.getCategory();
        this.price = source.getPrice();
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public boolean hasCategory() {
        return true; // Este SÍ tiene categoría
    }

    @Override
    public boolean setCategory(Category category) {
        this.category = category;
        return true;
    }

    @Override
    public boolean setPrice(double price) {
        if (price > 0) {
            this.price = price;
            return true;
        }
        return false;
    }

    @Override
    public double getPrice() { return price; }

    @Override
    public boolean isAvailable() { return true; }

    @Override
    public String toString() {
        return "{class:Product, id:" + getId() + ", name:'" + getName() + "', category:" + this.category.name() +
                ", price:" + String.format("%.1f", this.price) + "}";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockProduct)) return false;

        StockProduct product = (StockProduct) o;

        return Double.compare(product.price, price) == 0 &&
                getId().equals(product.getId()) &&
                getName().equals(product.getName()) &&
                category == product.category;
    }
}

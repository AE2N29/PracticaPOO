package es.upm.etsisi.poo.products;

import es.upm.etsisi.poo.Category;

public class StockProducts extends AbstractProduct {
    protected Category category;
    protected double price;


    public StockProducts(String id, String name, Category category, double price) {
        super(id, name);
        this.category = category;
        this.price = price;
    }

    public StockProducts(String name, Category category, double price) {
        super(name);
        this.category = category;
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }
    public double getPrice() {
        return calculatePrice();
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void  setName(String name) {
        this.name = name;
    }

    @Override
    public double calculatePrice() { // el precio es el mismo siempre, a diferencia con los personalizables
        return price;
    }

    @Override
    public boolean availability() { //siempre true, no ha que validar nada como en los eventos
        return true;
    }

    @Override
    public String toString() {
        return "{class:Product, id:" + this.id + ", name: '" + this.name + "', category:" + this.category.name() +
                ", price:" + String.format("%.1f", this.price) + "}";  // se usa string.format para devolver solo un decimal
    }
    public boolean equals(StockProducts product)
    {
        return this.id.equals(product.getId()) && this.name.equals(product.getName()) && this.category.equals(product.getCategory())
                && this.price == product.getPrice();
    }
}

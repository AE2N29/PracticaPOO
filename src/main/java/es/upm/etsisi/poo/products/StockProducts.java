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

    @Override
    public boolean setPrice(double price) {
        if (price > 0) {
            this.price = price;
            return true;
        }
        return false;
    }

    @Override
    public boolean setCategory(Category category) {
        this.category = category;
        return true;
    }

    @Override
    public boolean hasCategory()
    {
        return true;
    }
    public void  setName(String name) {
        this.name = name;
    }

    @Override
    protected double calculatePrice() { // el precio es el mismo siempre, a diferencia con los personalizables
        return price;
    }

    @Override
    protected boolean availability() { //siempre true, no ha que validar nada como en los eventos
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

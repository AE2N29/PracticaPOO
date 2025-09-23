package es.upm.etsisi.poo;

public class Product {
    private int identifier;
    private String name;
    private Category category;
    private double price;

    public Product(int identifier, String name, Category category, double price) {
        this.identifier = identifier;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "{class:Product, id:" + this.identifier + ", name:" + this.name +
                ", category:" + this.category.name() + ", price:" + this.price + "}";
    }
}

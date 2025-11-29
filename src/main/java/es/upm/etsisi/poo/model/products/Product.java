package es.upm.etsisi.poo.model.products;

public class Product {
    private int id;
    private String name;
    private Category category;
    private double price;

    public Product(int id, String name, Category category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public int getId() {
        return id;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{class:Product, id:" + this.id + ", name: '" + this.name + "', category:" + this.category.name() +
                ", price:" + String.format("%.1f", this.price) + "}";  // se usa string.format para devolver solo un decimal
    }

    public boolean equals(Product prod) {
        return this.id == prod.getId() && this.name.equals(prod.getName()) && this.category == prod.getCategory() && this.price == prod.getPrice();
    }
}

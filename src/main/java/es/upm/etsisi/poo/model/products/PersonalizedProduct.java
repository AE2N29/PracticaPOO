package es.upm.etsisi.poo.model.products;

import es.upm.etsisi.poo.exceptions.StoreException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PersonalizedProds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizedProduct extends StockProduct {

    private final List<String> customTexts;
    private final int maxCustomTexts;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int persProdId;

    public PersonalizedProduct(String id, String name, Category category, double price, int maxCustomTexts) throws StoreException {
        super(id, name, category, price);
        this.maxCustomTexts = maxCustomTexts;
        this.customTexts = new ArrayList<>();
    }

    public PersonalizedProduct(String name, Category category, double price, int maxCustomTexts) throws StoreException {
        super(name, category, price);
        this.maxCustomTexts = maxCustomTexts;
        this.customTexts = new ArrayList<>();
    }

    public boolean addCustomText(String text) {
        if(this.customTexts.size() < this.maxCustomTexts) {
            this.customTexts.add(text);
            return true;
        }
        return false;
    }

    public List<String> getCustomTexts() {
        return new ArrayList<>(this.customTexts);
    }

    public int getMaxCustomTexts() {
        return this.maxCustomTexts;
    }

    @Override
    public double getPrice() {
        int textsNumber = customTexts.size();
        double base = super.getPrice();
        return base * (1 + (0.10 * textsNumber));
    }

    @Override
    public String toString() {
        String base = "{class:ProductPersonalized, id:" + getId() +
                ", name:'" + getName() +
                "', category:" + getCategory().name() +
                ", price:" + String.format("%.1f", getPrice()) +
                ", maxPersonal:" + maxCustomTexts;

        if (!customTexts.isEmpty()) {
            base += ", personalizationList:" + customTexts;
        }
        return base + "}";
    }
}

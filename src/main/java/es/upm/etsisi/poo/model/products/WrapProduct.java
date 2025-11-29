package es.upm.etsisi.poo.model.products;

import java.util.ArrayList;
import java.util.List;

public class WrapProduct extends StockProducts {

    private final List<String> customTexts;
    private final int maxCutomTexts;

    public WrapProduct(String id, String name, Category category, double price, int maxCutomTexts) {
        super(id, name, category, price);
        this.maxCutomTexts = maxCutomTexts;
        this.customTexts = new ArrayList<>();
    }
    public WrapProduct(String name, Category category, double price, int maxCutomTexts) {
        super(name, category, price);
        this.maxCutomTexts = 0;
        this.customTexts = new ArrayList<>();
    }

    public boolean addCustomText(String text) {
        if(this.customTexts.size() < this.maxCutomTexts) {
            this.customTexts.add(text);
            return true;
        }
        return false;
    }

    public List<String> getCustomTexts() {
        return new ArrayList<>(this.customTexts);
    }
    public int getMaxCutomTexts() {
        return this.maxCutomTexts;
    }

    @Override
    public double calculatePrice(){
        int textsNumber = customTexts.size();
        double base = super.calculatePrice();
        double finalprice =  base * (1+(0.10 * textsNumber));
        return finalprice;
    }

}

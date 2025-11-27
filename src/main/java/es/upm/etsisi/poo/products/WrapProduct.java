package es.upm.etsisi.poo.products;
import es.upm.etsisi.poo.Category;

import java.util.ArrayList;
import java.util.List;

public class WrapProduct extends StockProducts {

    private final List<String> customTexts;
    private final int maxCutomTexts;

    public WrapProduct(String id, String name, Category category, double proce, int maxCutomTexts) {
        super(id, name, category, proce);
        this.maxCutomTexts = maxCutomTexts;
        this.customTexts = new ArrayList<>();
    }

    public boolean addCustomText(String text) {
        if(this.maxCutomTexts < this.maxCutomTexts) {
            this.customTexts.add(text);
            return true;
        }
        return false;
    }

    @Override
    public double calculatePrice(){
        int textsNumber = customTexts.size();
        double base = super.calculatePrice();
         double finalprice =  base + (0.10 * textsNumber);
         return finalprice;
    }

}

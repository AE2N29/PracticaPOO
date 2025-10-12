package es.upm.etsisi.poo;

import java.util.HashMap;
import java.util.Map;

public class TicketHM {
    private final int MAX_PRODS_TICKET = 100; //  ticket no puede tener mas de 100 productos
    private final HashMap<Integer, Integer> productIdToUnits;

    public TicketHM() {
        this.productIdToUnits = new HashMap<>();
    }

    public void resetTicket() {  //  uso de HashMap.clear para resetear el ticket
        this.productIdToUnits.clear();
        System.out.println("ticket new: ok");
    }

    public void add(int prodID, int amount) {
        if (amount <= 0) {
            System.out.println("ticket add: ok");
            return;
        }
        Product prod = ProductHM.getProduct(prodID);
        if (prod == null) {
            System.out.println("ERROR: There isn't any product with " + prodID + " as ID");
            return;
        }
        if (amount + productTotalUnits() <= MAX_PRODS_TICKET){ // verificar que se respete la max cantidad
            productIdToUnits.put(prodID, amount);
        } else {
            System.out.println("ERROR: Couldn't add, max products allowed per ticket exceeded!");
            return;
        }
        // Imprimir el contenido del ticket
        printTicketLines(false);
        System.out.println("ticket add: ok");
    }

    public void remove(int prodID) {
        Product prod = ProductHM.getProduct(prodID);
        if (prod == null) {  // si no encuentra el producto con este id en la base de datos
            System.out.println("ERROR: There isn't any product with " + prodID + " as ID");
            return;
        }
        if (!productIdToUnits.containsKey(prodID)) {  // si no existe producto con ID proporcionada en el ticket, mensaje de error
            System.out.print("ERROR: the product with " + prodID + " as ID wasn't in the current ticket");
            return;
        }
        productIdToUnits.remove(prodID);
        System.out.print("ticket remove: ok");  // si existe el producto, se devuelve confirmacion de borrado
    }

    public void print() {
        printTicketLines(true);
    }

    private int productTotalUnits(){ // metodo que devuelve la cantidad de productos que haya en el mapa
        int suma = 0;
        for(Integer units : productIdToUnits.values()) {  // recorre todas las cantidades del mapa, llamando a cada una units
            suma += units;
        }
        return suma;
    }

    private void printTicketLines(boolean printOkAtEnd) {
        double totalPrice = 0, totalDiscount = 0;
        int booksCount = 0, clothingCount = 0, stationeryCount = 0, electronicsCount = 0, merchCount = 0;

        for (Map.Entry<Integer, Integer> couple : productIdToUnits.entrySet()) {  // bucle que pilla cada pareja de datos (id, cantidad) y los llama couple
            Product product = ProductHM.getProduct(couple.getKey());  // pilla el producto de la base de datos con el (id) de couple
            int amount = couple.getValue();
            if (product != null || amount > 0) {
                switch (product.getCategory()) {   // switch que cambia la cantidad de categoría segun la categoria que tenga el producto
                    case ELECTRONICS -> electronicsCount += amount;  // esto pq hay que considerarlo para aplicar descuentos
                    case CLOTHES -> clothingCount += amount;
                    case STATIONERY -> stationeryCount += amount;
                    case BOOK -> booksCount += amount;
                    case MERCH -> merchCount += amount;
                }
            }
        }

        for (Map.Entry<Integer, Integer> couple : productIdToUnits.entrySet()) {
            Product product = ProductHM.getProduct(couple.getKey());
            int amount = couple.getValue();

            if (product == null || amount <= 0) continue;   // si el producto no existe en la base de datos o tiene cantidad menor a 0,
                                                            // pasamos a la siguiente pareja en el bucle for

            int count = switch (product.getCategory()) {  // pillamos la cuenta de productos con esa categoria para aplicar descuento
                case ELECTRONICS -> electronicsCount;
                case CLOTHES -> clothingCount;
                case STATIONERY -> stationeryCount;
                case BOOK -> booksCount;
                case MERCH -> merchCount;
            };
            double discountRate = switch (product.getCategory()) {  // consideramos el descuento de cada categoria
                case ELECTRONICS -> 0.03;
                case STATIONERY -> 0.05;
                case CLOTHES -> 0.07;
                case BOOK -> 0.1;
                case MERCH -> 0.0;
            };

            for (int i = 0; i < amount; i++) {
                double prodDiscount;

                if(count >= 2) {  // el descuento se aplica cuando hay mas de dos prod de la misma categoria
                    prodDiscount = product.getPrice() * discountRate;
                    System.out.printf("%s **discount -%.1f\n", product, prodDiscount);
                }
                else{
                    prodDiscount = 0;
                    System.out.println(product);
                }
                totalPrice += product.getPrice();
                totalDiscount += prodDiscount;
            }
        }

        System.out.printf("Total price: %.1f\n", totalPrice);
        System.out.printf("Total discount: %.1f\n", totalDiscount);
        System.out.printf("Final price: %.1f\n", (totalPrice - totalDiscount));

        if (printOkAtEnd) {
            System.out.println("ticket print: ok");
        }
    }
}
package es.upm.etsisi.poo;

public class Ticket {
    Ticket ticket;
    Product [] products;
    public Ticket(Product [] products ) {
        this.products = products;
    }
  public Ticket(){new Ticket();}

    public Ticket ticketnew(){
        return  new Ticket();
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void add (Product product, int amount){
        int i = 0;
         while(i < amount){
             int products_amount = products.length + 1;
          products = new Product[products.length];
          i++;
         }
    }
    public void TicketRemove(Product product){
        if(Catalog.IDInProducts(product.getId())) {
            for (int i = 0; i < products.length; i++) {
                if (products[i] == product) {
                    products[i] = null;
                }
            }
        }
    }


}

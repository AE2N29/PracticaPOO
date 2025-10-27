package es.upm.etsisi.poo;

public class Client extends User {
    private String cashierIdAssociated;

    public Client(String name, String dni, String email, String cashierId) {
        super(name, dni, email);
        this.cashierIdAssociated = cashierId;
    }

    public String getCashierIdAssociated() {
        return cashierIdAssociated;
    }

    public String toString() {
        return "{class:Client, dni:" + this.dni + ", name:" + this.name + ", email:" + this.email + ", registeredByCashierDni:" + cashierIdAssociated + "}";
    }
}

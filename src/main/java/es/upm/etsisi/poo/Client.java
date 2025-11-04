package es.upm.etsisi.poo;

public class Client extends User {
    private String dni;
    private Cashier associatedCashier;

    public Client(String name, String dni, String email, Cashier associatedCashier) {       // Tengo mis dudas sobre si asociar el Cashier asi
        super(name, email);
        this.dni = dni;
        this.associatedCashier = associatedCashier; // Lo suyo sería que al principio sea null
    }

    public String getDni() {
        return dni;
    }

    public String toString() {
        if (associatedCashier == null) {
            return "{class:Client, dni:" + this.dni + ", name:" + this.name + ", email:" + this.email + ", associatedCashierWorkerID: None}";
        } else {
            return "{class:Client, dni:" + this.dni + ", name:" + this.name + ", email:" + this.email + ", associatedCashierWorkerID:"
                    + this.associatedCashier.getUPMWorkerID() + "}";
        }
    }

    public Cashier getAssociatedCashier() {
        return associatedCashier;
    }

    public void setAssociatedCashier(Cashier attendant) {
        this.associatedCashier = attendant;
    }
}

package es.upm.etsisi.poo;

public class Client extends User {
    private String dni;
    private Cashier associatedCashier;

    public Client(String name, String dni, String email, Cashier associatedCashier) {
        super(name, email);
        this.dni = dni;
        this.associatedCashier = associatedCashier;
    }

    public String getDni() {
        return dni;
    }

    public Cashier getAssociatedCashier() {
        return associatedCashier;
    }

    public void setAssociatedCashier(Cashier attendant) {
        this.associatedCashier = attendant;
    }

    @Override
    public String toString() {
        return "Client{identifier='" + this.dni + "', name='" + this.name +
                "', email='" + this.email + "', cash=" + this.associatedCashier.getUPMWorkerID() + "}";
        }
    }
}
package es.upm.etsisi.poo.model.users;

public class CorporateClient extends Client {
    public CorporateClient(String name, String id, String email, Cashier cashier) {
        super(name, id, email, cashier);
    }

    @Override
    protected String getHeader() {
        return "COMPANY"; //
    }
}

package es.upm.etsisi.poo.model.users;

public class CompanyClient extends Client {
    public CompanyClient(String name, String id, String email, Cashier cashier) {
        super(name, id, email, cashier);
    }

    @Override
    protected String getHeader() {
        return "COMPANY"; //
    }
}

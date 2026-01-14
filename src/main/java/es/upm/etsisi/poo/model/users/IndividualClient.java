package es.upm.etsisi.poo.model.users;

public class IndividualClient extends Client {
    public IndividualClient(String name, String id, String email, Cashier cashier) {
        super(name, id, email, cashier);
    }

    @Override
    protected String getHeader() {
        return "USER"; //
    }
}

package es.upm.etsisi.poo.model.products;
import java.time.LocalDateTime;
public class Service extends AbstractProduct {

    public static int counter = 1;
    private ServiceTypes serviceType;
    private LocalDateTime expirationDate;

    // Usamos el tipo como nombre, así podemos encasillar service como AbstractProduct tambien
    public Service(ServiceTypes serviceType, LocalDateTime expirationDate){
        super(generateServiceID(), serviceType.toString());
        this.serviceType = serviceType;
        this.expirationDate = expirationDate;
    }

    //Syncronized evita errores si se llama de varios lugares al mismo tiempo
    private static synchronized String generateServiceID() {
        return (counter++) + "S";
    }

    public ServiceTypes getServiceType() {
        return serviceType;
    }
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    @Override
    public double getPrice() {
        return 0.0;
    }
    @Override
    public boolean setPrice(double price) { // No deja poner precio ditinto a 0.0
        return false;
    }

    @Override
    public boolean isAvailable() {
        return LocalDateTime.now().isBefore(expirationDate);
    }
    @Override
    public String toString() {
        return "{class:Service, type:" + serviceType +
                ", id:" + getId() +
                ", expiration:" + expirationDate.toLocalDate() + "}";
    }
}

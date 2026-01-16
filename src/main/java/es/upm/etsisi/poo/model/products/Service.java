package es.upm.etsisi.poo.model.products;

import es.upm.etsisi.poo.exceptions.StoreException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Service extends AbstractProduct {
    public static int counter = 1;
    private ServiceTypes serviceType;
    private LocalDateTime expirationDate;

    // Usamos el tipo como nombre, así podemos encasillar service como AbstractProduct tambien
    public Service(ServiceTypes serviceType, LocalDateTime expirationDate) throws StoreException {
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
        // Formatter hace que 2026-06-06T00:00 pase a -> Sun Jun 06 00:00:00 z 2026
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        ZoneId cet = ZoneId.of("Europe/Madrid");
        String formattedDate = expirationDate.atZone(cet).format(formatter); // Añade CET a donde se encuentra la 'z'
        return "{class:ProductService, id:" + getId() +
                ", category:" + serviceType +
                ", expiration:" + formattedDate + "}";
    }

}

package es.upm.etsisi.poo. model.sales;

import es. upm.etsisi.poo.model.users.Client;
import es.upm. etsisi.poo.model.users.CorporateClient;
import es. upm.etsisi.poo.model.users.IndividualClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory que crea e inyecta la estrategia de impresión correcta
 * Usa un Map para despacho dinámico de comportamientos (Tabla de despacho)
 * Patrón:  Factory + Strategy + Dependency Injection
 */
public class TicketPrinterFactory {

    // ← TABLA DE DESPACHO (Map) para mapear tipos de cliente a impresoras
    private static final Map<Class<?>, TicketPrinter> printerStrategies = new HashMap<>();

    static {
        // Registrar estrategias según tipo de cliente
        printerStrategies.put(IndividualClient.class, new IndividualTicketPrinter());
        printerStrategies.put(CorporateClient.class, new CorporateTicketPrinter());
    }

    /**
     * Obtiene la estrategia de impresión apropiada según el tipo de cliente
     * @param client Cliente del ticket
     * @return TicketPrinter correspondiente al tipo de cliente
     */
    public static TicketPrinter getPrinterForClient(Client client) {
        TicketPrinter printer = printerStrategies.get(client.getClass());

        // Si no se encuentra, usar Individual por defecto
        if (printer == null) {
            printer = new IndividualTicketPrinter();
        }

        return printer;
    }

    /**
     * Registra una nueva estrategia de impresión
     * Permite extensibilidad sin modificar el código existente (Open/Closed Principle)
     */
    public static void registerPrinter(Class<?> clientType, TicketPrinter printer) {
        printerStrategies.put(clientType, printer);
    }

    /**
     * Retorna todas las estrategias registradas (para debugging/info)
     */
    public static Map<Class<?>, TicketPrinter> getAllStrategies() {
        return new HashMap<>(printerStrategies);
    }

    /**
     * Retorna el nombre de la estrategia para un tipo de cliente
     */
    public static String getPrinterStrategyName(Client client) {
        TicketPrinter printer = getPrinterForClient(client);
        return printer.getStrategyName();
    }
}
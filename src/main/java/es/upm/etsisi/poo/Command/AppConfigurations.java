package es.upm.etsisi.poo.Command;

 //Centraliza todos los parámetros configurables del proyecto
 //Cambiar valores aquí en lugar de buscarlos por todo el código

public class AppConfigurations {

    // ==================== PRODUCTO ====================
    public static final int MAX_PRODUCT_NAME_LENGTH = 100;
    public static final double MIN_PRICE = 0.01;

    // ==================== TICKET ====================
    public static final int MAX_PRODUCTS_PER_TICKET = 100;
    public static final String TICKET_ID_PATTERN = "YY-MM-dd-HH: mm-";

    // ==================== DESCUENTOS ====================
    public static final int MIN_UNITS_FOR_DISCOUNT = 2;
    public static final double DISCOUNT_MERCH = 0.0;
    public static final double DISCOUNT_STATIONERY = 0.05;
    public static final double DISCOUNT_CLOTHES = 0.07;
    public static final double DISCOUNT_BOOK = 0.10;
    public static final double DISCOUNT_ELECTRONICS = 0.03;
    public static final double CORPORATE_SERVICE_DISCOUNT = 0.15;
    // ==================== FORMATOS ====================
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String CASHIER_ID_PREFIX = "UW";
    public static final int CASHIER_ID_DIGITS = 7;

    // ==================== MENSAJES ====================
    public static final String PROMPT = "tUPM> ";
    public static final boolean ENABLE_DEBUG_LOGGING = false;
    public static final boolean STRICT_VALIDATION_MODE = true;
    //=================== CONTROL TICKETS ===============
    public static final boolean INDIVIDUAL_TICKET_ALLOWS_SERVICES = false;

    //Si clientes individuales pueden crear tickets combinados
    public static final boolean INDIVIDUAL_TICKET_ALLOWS_COMBINED = false;

    // Si clientes corporativos pueden agregar servicios a tickets
    public static final boolean CORPORATE_TICKET_ALLOWS_SERVICES = true;

    // Si clientes corporativos pueden crear tickets combinados
    public static final boolean CORPORATE_TICKET_ALLOWS_COMBINED = true;
}

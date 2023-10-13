package Lab2;

public class Testeo {
    public static void main(String[] args) {
        String encabezado;
        int NUMERO_DE_ITERACIONES = 150;            // numero de iteraciones SIN ENCONTRAR UN NUEVO MINIMO para terminar el programa (NO es el numero de iteraciones totales)
        int BETHA = 40; // esto es que tan importante es la distancia al escoger el siguiente nodo
        int ALPHA = 1;  // esto es que tan importante es la feromona al escoger el siguiente nodo (NO cambiar, da error)
        int NUMERO_DE_HORMIGAS = 150;
        double TASA_DE_EVAPORACION = 0.1;           // esto es que tan rapido se evapora la feromona
        double TASA_DE_DEPOSITO = 1000;               // esto es que tan rapido se deposita la feromona
        double FEROMONA_INICIAL = 1; 
        Lab2.NUMERO_DE_ITERACIONES = NUMERO_DE_ITERACIONES;
        Lab2.BETHA = BETHA;
        Lab2.ALPHA = ALPHA;
        Lab2.NUMERO_DE_HORMIGAS = NUMERO_DE_HORMIGAS;
        Lab2.TASA_DE_EVAPORACION = TASA_DE_EVAPORACION;
        Lab2.TASA_DE_DEPOSITO = TASA_DE_DEPOSITO;
        Lab2.FEROMONA_INICIAL= FEROMONA_INICIAL;
        encabezado = " HORMIGAS: "+NUMERO_DE_HORMIGAS+
        " EVAPORACION: "+TASA_DE_EVAPORACION;

        for (int i = 0; i < 3; i++) {
            Lab2.main(new String[]{"a280.tsp",encabezado});
            encabezado = "";
        }
    }   
}

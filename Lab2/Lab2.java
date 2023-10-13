package Lab2;
/* enunciado:
   Implement an Ant Colony algorithm to solve the Travelling Salesperson Problem found below. The optimum route is 2579.
   How close does your algorithm get to that route?  How long does it take, on average?  If you try different parameters
   (number of ants, rate of evaporation), does it get any better? */

import java.util.ArrayList;
import java.util.List;

public class Lab2 {
    // constantes
    public static final int NUMERO_DE_ITERACIONES = 200;            // numero de iteraciones SIN ENCONTRAR UN NUEVO MINIMO para terminar el programa (NO es el numero de iteraciones totales)
    public static final int PENALIZACION_DISTANCIA_HEURISTICA = 50; // esto es que tan importante es la distancia al escoger el siguiente nodo (mas penalizacion = mas importante la distancia)
    public static final int NUMERO_DE_HORMIGAS = 2000;
    public static final double TASA_DE_EVAPORACION = 0.6;           // esto es que tan rapido se evapora la feromona
    public static final double TASA_DE_DEPOSITO = 10;               // esto es que tan rapido se deposita la feromona
    public static final double FEROMONA_INICIAL = 0.001;            // esto es la cantidad de feromona inicial en cada arista
    
    // variables
    public static double[][] feromonas;
    public static double[][] distancias;  
     
    // clase hormiga
    class Hormiga{
        ArrayList<Integer> faltaVisitar;
        List<Integer[]> recorrido = new ArrayList<>();
        double distancia;
        int posicion;
        double suma; // esto es para la probabilidad de ir a cada nodo
        public Hormiga(){
            // comprobar que el mapa ya esta inicializado
            if (feromonas == null || distancias == null) {
                System.err.println("Error: el mapa no esta inicializado.");
                System.exit(1);
            }
            posicion = 0;   // asumo que el hormiguero esta en el nodo 0
            distancia = 0;
            faltaVisitar = new ArrayList<>();
            for (int i = 1; i < distancias.length; i++) {
                faltaVisitar.add(i);
            }
        }

        public Hormiga(Hormiga original){
            this.distancia = original.distancia;
            this.posicion = original.posicion;
            this.suma = original.suma;
            this.faltaVisitar = new ArrayList<>(original.faltaVisitar);
            this.recorrido = new ArrayList<>(original.recorrido);
        }

        public void recorrer(){
            calcularSuma();
            while (hayNodosSinVisitar()) {
                int nodo = siguienteNodo();
                mover(nodo);
            }
            // volver al hormiguero
            mover(0);
        }

        public void actualizaFeromonas(){  // dejar el rastro de feromonas
            for (int i = 0; i < recorrido.size(); i++) {
                int origen = recorrido.get(i)[0];
                int destino = recorrido.get(i)[1];
                feromonas[origen][destino] += TASA_DE_DEPOSITO / distancia;
                feromonas[destino][origen] = feromonas[origen][destino];
            }
        }

        public String verRecorrido(){
            String recorrido = "";
            for (int i = 0; i < this.recorrido.size()-1; i++) {
                if (i > 171) {
                    recorrido += (this.recorrido.get(i)[0]+1) + " -> ";
                }else{
                    recorrido += this.recorrido.get(i)[0] + " -> ";
                }
            }
            recorrido += "0";
            return recorrido;
        }

        public List<Integer[]> getRecorrido(){
            return recorrido;
        }
        private void mover(int destino){
            distancia += distancias[posicion][destino];
            if (destino != 0) {
                faltaVisitar.remove(faltaVisitar.indexOf(destino));
            }
            recorrido.add(new Integer[]{posicion, destino});
            posicion = destino;
        }

        private boolean hayNodosSinVisitar(){
            if (faltaVisitar.size() > 0) {
                return true;
            }
            return false;
        }

        private int siguienteNodo(){
            double random = Math.random();
            double probabilidad = 0;
            calcularSuma();
            if (suma == 0) {    // esto es para cuando las feromonas son muy bajas
                                // por lo que ya no hay forma de escoger un nodo
                                // esto ayuda a que exploren mas
                System.err.println("las feromonas son muy bajas, se escogera un nodo al azar");
                return faltaVisitar.get((int)(Math.random() * faltaVisitar.size()));
            }else{
                for (int i = 0; i < faltaVisitar.size(); i++) {
                    probabilidad += feromonas[posicion][faltaVisitar.get(i)] * heuristica(faltaVisitar.get(i)) / suma;
                    if (random <= probabilidad) {
                        return faltaVisitar.get(i);
                    }
                }
            }
            
            System.err.println("Error: no se encontro el siguiente nodo. "
                    + "\n\trandom: " + random + "\n\tprobabilidad: " + probabilidad
                    + "\n\tsuma: " + suma + "\n\tposicion: " + posicion + "\n\tfaltaVisitar: " + faltaVisitar 
                    + "\n\ttamano faltaVisitar: " + faltaVisitar.size() + "\n"
                    + "\n\trecorrido: " + recorrido.toString() + "\n\ttamano recorrido: " + recorrido.size() + "\n");

            if (Double.compare(suma, Double.POSITIVE_INFINITY) == 0) {
                System.out.println("Verificar que no se haya repetido un nodo");
            }

            return -1;
        }

        private void calcularSuma(){
            suma = 0;
            for (int i = 0; i < faltaVisitar.size(); i++) {
                suma += feromonas[posicion][faltaVisitar.get(i)] * heuristica(faltaVisitar.get(i));
            }
        }
        
        private double heuristica(int nodo){
            return 1/Math.pow(distancias[posicion][nodo], PENALIZACION_DISTANCIA_HEURISTICA);}
    }

    // main
    public static void main(String[] args) {
        String archivo;
        // ver si se paso el archivo como argumento
        if (args.length != 1) {
            archivo = "a280.tsp";
        }else{
            archivo = args[0];
        }
        leerArchivo(archivo);
        System.out.println("Archivo leido: " + archivo);

        // crear las hormigas
        Hormiga[] hormigas = new Hormiga[NUMERO_DE_HORMIGAS];
        for (int i = 0; i < hormigas.length; i++) {
            hormigas[i] = new Lab2().new Hormiga();
        }
        // guardo una copia de la hormiga con la menor distancia
        Hormiga mejorHormiga = new Lab2().new Hormiga(hormigas[0]);
        int contador = 0;
        // bucle principal
        while (contador < NUMERO_DE_ITERACIONES) {
            for (int i = 0; i < hormigas.length; i++) {
                hormigas[i] = new Lab2().new Hormiga();
            }
            // recorrer con las hormigas
            for (int i = 0; i < hormigas.length; i++) {
                hormigas[i].recorrer();
            }
            // evaporar la feromona
            for (int i = 0; i < feromonas.length; i++) {
                for (int j = i; j < feromonas.length; j++) {
                    feromonas[i][j] *= (1 - TASA_DE_EVAPORACION);
                    feromonas[j][i] = feromonas[i][j];
                }
            }
            // actualizar la feromona
            for (int i = 0; i < hormigas.length; i++) {
                hormigas[i].actualizaFeromonas();
            }
            contador++;
            // imprimir el recorrido de la hormiga con la menor distancia
            for (int i = 1; i < hormigas.length; i++) {
                if (hormigas[i].distancia < mejorHormiga.distancia || mejorHormiga.distancia == 0) {
                    mejorHormiga = new Lab2().new Hormiga(hormigas[i]);
                    System.out.println("Distancia de la hormiga con la menor distancia: " + mejorHormiga.distancia);
                    contador = 0;
                }
            }
            
        }
        System.out.println("Distancia de la hormiga con la menor distancia: " + mejorHormiga.distancia);
        System.out.println("Recorrido de la hormiga con la menor distancia: " + mejorHormiga.verRecorrido());
        // evaluarCamino(mejorHormiga.getRecorrido());
    }

    // funciones
    public static void leerArchivo(String archivo) {
        int[][] coordenadas = null;
        archivo = "Lab2/" + archivo;
        System.out.println("Leyendo archivo: " + archivo);
        // leer el archivo
        java.io.File file = new java.io.File(archivo);
        try {
            java.util.Scanner input = new java.util.Scanner(file);
            // buscar linea DIMENSION
            while (input.hasNext()) {
                String linea = input.nextLine();
                if (linea.contains("DIMENSION")) {
                    coordenadas = new int[Integer.parseInt(linea.split(" ")[1])][2];
                    break;
                }
            }
            // buscar linea NODE_COORD_SECTION
            while (input.hasNext()) {
                String linea = input.nextLine();
                if (linea.equals("NODE_COORD_SECTION")) {
                    break;
                }
            }
            // leer las coordenadas
            int i = 0;
            while (input.hasNext()) {
                String linea = input.nextLine();
                if (linea.equals("EOF")) {
                    break;
                }
                // separar la linea en 3 partes separadas por espacios (pueden ser varios espacios)
                String[] coordenada = linea.trim().split("\\s+");
                if (coordenada[0].equals("172")) { // esto es para saltar el nodo 172 que esta repetido
                    continue;
                }
                coordenadas[i][0] = Integer.parseInt(coordenada[1]);
                coordenadas[i][1] = Integer.parseInt(coordenada[2]);
                i++;
            }
            input.close();
            llenarMapa(coordenadas);

        } catch (java.io.FileNotFoundException ex) {
            System.err.println("Error: " + archivo + " no existe.");
            System.exit(1);
        }

    }

    public static void llenarMapa(int[][] coordenadas){
        feromonas = new double[coordenadas.length][coordenadas.length];
        distancias = new double[coordenadas.length][coordenadas.length];
        for (int i = 0; i < coordenadas.length; i++) {
            for (int j = i; j < coordenadas.length; j++) {
                feromonas[i][j] = FEROMONA_INICIAL;
                distancias[i][j] = distancia(coordenadas[i], coordenadas[j]);
                feromonas[j][i] = feromonas[i][j];
                distancias[j][i] = distancias[i][j];
            }
        }
    }

    public static double distancia(int[] x, int[] y){
        return Math.sqrt(Math.pow(x[0]-y[0], 2) + Math.pow(x[1]-y[1], 2));
    }

    public static void evaluarCamino(List<Integer[]> camino){
        double distancia = 0;
        for (int i = 0; i < camino.size(); i++) {
            distancia += distancias[camino.get(i)[0]][camino.get(i)[1]];
        }
        System.out.println("evaluacion: " + distancia);
    }
}

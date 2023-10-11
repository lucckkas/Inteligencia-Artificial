package Lab2;
/* enunciado:
   Implement an Ant Colony algorithm to solve the Travelling Salesperson Problem found below. The optimum route is 2579.
   How close does your algorithm get to that route?  How long does it take, on average?  If you try different parameters
   (number of ants, rate of evaporation), does it get any better? */

import java.util.ArrayList;
import java.util.List;

public class Lab2 {
    // clase hormiga
    class Hormiga{
        ArrayList<Integer> faltaVisitar;
        List<Integer[]> recorrido = new ArrayList<>();
        double distancia;
        int posicion;
        double suma; // esto es para la probabilidad de ir a cada nodo
        public Hormiga(){
            // comprobar que el mapa ya esta inicializado
            if (mapa == null) {
                System.err.println("Error: el mapa no esta inicializado.");
                System.exit(1);
            }
            posicion = 0;   // asumo que el hormiguero esta en el nodo 0
            distancia = 0;
            faltaVisitar = new ArrayList<>();
            for (int i = 1; i < mapa.length; i++) {
                faltaVisitar.add(i);
            }
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
                mapa[origen][destino][0] += TASA_DE_DEPOSITO / distancia;
                mapa[destino][origen][0] = mapa[origen][destino][0];
            }
        }

        public String verRecorrido(){
            String recorrido = "0 -> ";
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

        private void mover(int destino){
            distancia += mapa[posicion][destino][1];
            if (destino != 0) {
                faltaVisitar.remove(faltaVisitar.indexOf(destino));
            }
            posicion = destino;
            recorrido.add(new Integer[]{posicion, destino});
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
                    probabilidad += mapa[posicion][faltaVisitar.get(i)][0] * heuristica(faltaVisitar.get(i)) / suma;
                    if (random <= probabilidad) {
                        return faltaVisitar.get(i);
                    }
                }
            }
            
            System.err.println("Error: no se encontro el siguiente nodo. "
                    + "\n\trandom: " + random + "\n\tprobabilidad: " + probabilidad
                    + "\n\tsuma: " + suma + "\n\tposicion: " + posicion + "\n\tfaltaVisitar: " + faltaVisitar 
                    + "\n\ttamano faltaVisitar: " + faltaVisitar.size() + "\n");

            return -1;
        }

        private void calcularSuma(){
            suma = 0;
            for (int i = 0; i < faltaVisitar.size(); i++) {
                suma += mapa[posicion][faltaVisitar.get(i)][0] * heuristica(faltaVisitar.get(i));
            }
        }
        
        private double heuristica(int nodo){
            return 1/Math.pow(mapa[posicion][nodo][1], 3);}
    }

    // constantes
    public static final int NUMERO_DE_HORMIGAS = 200;
    public static final double TASA_DE_EVAPORACION = 0.01;   // esto es que tan rapido se evapora la feromona  .4
    public static final double TASA_DE_DEPOSITO = 50;      // esto es que tan rapido se deposita la feromona  .6
    public static final double FEROMONA_INICIAL = 0.1;   // esto es la cantidad de feromona inicial en cada arista
    
    // variables
    public static double[][][] mapa;    // matriz de adyacencia que contiene la feromona en cada arista,
                                        // ejemplo mapa[i][k][0] es la feromona en la arista i->k y mapa[i][k][1] es la distancia en la arista i->k
     
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
        Hormiga mejorHormiga = hormigas[0];
        double promedio = 0;
        int contador = 0;
        // bucle principal
        while (contador < 1000) {
            for (int i = 0; i < hormigas.length; i++) {
                hormigas[i] = new Lab2().new Hormiga();
            }
            // recorrer con las hormigas
            for (int i = 0; i < hormigas.length; i++) {
                hormigas[i].recorrer();
            }
            // actualizar la feromona
            for (int i = 0; i < hormigas.length; i++) {
                hormigas[i].actualizaFeromonas();
            }
            promedio = hormigas[0].distancia;
            contador++;
            // imprimir el recorrido de la hormiga con la menor distancia
            for (int i = 1; i < hormigas.length; i++) {
                promedio += hormigas[i].distancia;
                if (hormigas[i].distancia < mejorHormiga.distancia || mejorHormiga.distancia == 0) {
                    mejorHormiga = hormigas[i];
                    System.out.println("Distancia de la hormiga con la menor distancia: " + mejorHormiga.distancia);
                    //System.out.println("Recorrido de la hormiga con la menor distancia: " + mejorHormiga.verRecorrido());
                    contador = 0;
                }
            }
            promedio /= hormigas.length;
            // evaporar la feromona
            for (int i = 0; i < mapa.length; i++) {
                for (int j = i; j < mapa.length; j++) {
                    mapa[i][j][0] *= (1 - TASA_DE_EVAPORACION);
                    mapa[j][i][0] = mapa[i][j][0];
                }
            }
        }
        System.out.println(promedio);
        System.out.println("Distancia de la hormiga con la menor distancia: " + mejorHormiga.distancia);
        System.out.println("Recorrido de la hormiga con la menor distancia: " + mejorHormiga.verRecorrido());
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
        mapa = new double[coordenadas.length][coordenadas.length][2];
        // llenar la matriz, optimizando que mapa[i][k] = mapa[k][i], por lo que NO necesito 2 for con coordenadas.length
        for (int i = 0; i < coordenadas.length; i++) {
            for (int j = i; j < coordenadas.length; j++) {
                mapa[i][j][0] = FEROMONA_INICIAL;
                mapa[i][j][1] = distancia(coordenadas[i], coordenadas[j]);
                mapa[j][i] = mapa[i][j];
            }
        }
    }

    public static double distancia(int[] x, int[] y){
        return Math.sqrt(Math.pow(x[0]-y[0], 2) + Math.pow(x[1]-y[1], 2));
    }

}

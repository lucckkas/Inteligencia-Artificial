import copy
import psutil

# numero de movimientos aleatorios para mezclar el tablero
# las tiradas escogen un movimiento aleatorio entre los movimientos validos, 
# si el movimiento no es valido, se descuenta una tirada sin realizar ningun movimiento
numero_mezclas = 12

""" enunciado:
   Implement solvers for the 8-Tile Puzzle using both Breadth-First (Busqueda en amplitud) and Depth-first searches.
   Choose a random board and count how many nodes were expanded and how many were simultaneously stored in memory
   before your computer ran out of memory (or before you ran out of paitence).  Turn in both your code and the results of the search.
 """
class Nodo:
    def __init__(self, tablero, padre = None):
        self.tablero = tablero
        self.hijos = []
        self.profundidad = padre.getprofundidad() + 1 if padre != None else 0
    
    def getprofundidad(self):
        return self.profundidad
    
    def gethijos(self):
        return self.hijos
    
    def gettablero(self):
        return self.tablero
    
    def agregar_hijo(self, hijo):
        self.hijos.append(hijo)
    


# diccionario direcciones 
direcciones = {"arriba": [-1,0], "abajo": [1,0], "izquierda": [0,-1], "derecha": [0,1]}

board = [["1", "2", "3"], 
         ["4", "5", "6"], 
         ["7", "8", "_"]]

def mezclar_tablero(tablero = board, n = 100):
    import random
    # realizar movimientos aleatorios
    for i in range(n):
        # elegir una direccion aleatoria
        direccion = random.choice(list(direcciones))
        # si el movimiento es valido
        if validar_movimiento(direccion, tablero):
            # mover el _
            tablero = mover(direccion, tablero)
    return tablero

def sumar(arr1, arr2):
    return [arr1[0] + arr2[0], arr1[1] + arr2[1]]

def mostrar_tablero(tablero = board):
    for i in range(len(tablero)):
        for j in range(len(tablero[i])):
            print(tablero[i][j], end = "\t")
        print()
    print()

def calular_solucion(tablero = board):
    # crea un tablero solucion del mismo tamaño que el tablero dado
    solucion = []
    for i in range(len(tablero)):
        solucion.append([])
        for j in range(len(tablero[i])):
            solucion[i].append(str(i*3 + j + 1))
    solucion[-1][-1] = "_"
    return solucion

def mover(direccion, tableroaux = board.copy()): 
    tablero = copy.deepcopy(tableroaux)
    vacio = localizar_vacio(tablero)
    x = vacio[0]
    y = vacio[1]
    
    posDestino = sumar(direcciones[direccion], [x, y])

    # intercambiar las posiciones
    tablero[x][y], tablero[posDestino[0]][posDestino[1]] = tablero[posDestino[0]][posDestino[1]], tablero[x][y]
    return tablero

def validar_movimiento(direccion, tablero = board):
    vacio = localizar_vacio(tablero)
    x = vacio[0]
    y = vacio[1]
    posDestino = sumar(direcciones[direccion], [x, y])
    
    # si la posicion destino esta fuera del tablero
    if posDestino[0] < 0 or posDestino[0] > len(tablero) - 1 or posDestino[1] < 0 or posDestino[1] > len(tablero[0]) - 1:
        return False
    return True

def localizar_vacio(tablero = board):
    vacio = [None, None]
     # encontrar la posicion del _
    for i in range(len(tablero)):
        for j in range(len(tablero[i])):
            if tablero[i][j] == "_":
                x = i
                y = j
                break
    
    # si no encontro el _
    if x == None or y == None:
        return False
    vacio[0] = x
    vacio[1] = y
    return vacio

def busqueda(tipo):
    memoriaInicial = psutil.Process().memory_info().rss/1024/1024
    print("Memoria usada al inicio", memoriaInicial," MB")
    # crear nodo raiz
    nodoRaiz = Nodo(mezclar_tablero(board, numero_mezclas))
    solucion = calular_solucion(nodoRaiz.gettablero())
    # crear cola
    cola = []
    cola.append(nodoRaiz)
    # lista de tableros ya visitados
    visitados = []

    # mientras la cola no este vacia
    while len(cola) > 0:
        # sacar el primer elemento de la cola
        if tipo == "amplitud":
            nodoActual = cola.pop(0)
        elif tipo == "profundidad":
            nodoActual = cola.pop()
        else:
            print("Error, tipo de busqueda no valido")
            return False
        # agregar el tablero del nodo actual a la lista de tableros visitados
        visitados.append(nodoActual.gettablero())
        # si el tablero del nodo actual es igual al tablero solucion
        if nodoActual.gettablero() == solucion:
            print("Solucion encontrada")
            print("tablero inicial: ")
            mostrar_tablero(nodoRaiz.gettablero())
            # mostrar la profundidad del nodo solucion
            print("Profundidad: ", nodoActual.getprofundidad())
            # mostrar el numero de nodos expandidos
            print("Nodos visitados: ", len(visitados))
            mem = psutil.Process().memory_info().rss/1024/1024
            print("Memoria usada al final ", mem, " MB")
            print("Diferencia " ,mem - memoriaInicial , " MB")
            # terminar el programa
            return True
        # si el tablero del nodo actual no es igual al tablero solucion
        else:
            # crear un nodo hijo por cada movimiento posible
            for direccion in direcciones:
                # si el movimiento es valido
                if validar_movimiento(direccion, nodoActual.gettablero()):
                    # crear un nodo hijo
                    aux2 = mover(direccion, copy.deepcopy(nodoActual.gettablero()))
                    nodoHijo = Nodo(aux2 , nodoActual)
                    # mostrar el tablero del nodo hijo

                    # agregar el nodo hijo al nodo actual
                    aux = copy.deepcopy(nodoHijo)
                    nodoActual.agregar_hijo(aux)
                    # agregar el nodo hijo a la cola
                    if nodoHijo.gettablero() not in visitados:
                        cola.append(aux)

def main():
    # menu
    print("1. Busqueda en amplitud")
    print("2. Busqueda en profundidad")
    opcion = input("Ingrese una opcion: ")
    if opcion == "1":
        print()
        busqueda("amplitud")
    elif opcion == "2":
        print()
        busqueda("profundidad")

# main 
if __name__ == "__main__":
    main()
    


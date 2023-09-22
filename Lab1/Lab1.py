# enunciado:
#   Implement solvers for the 8-Tile Puzzle using both Breadth-First (Busqueda en amplitud) and Depth-first searches.
#   Choose a random board and count how many nodes were expanded and how many were simultaneously stored in memory
#   before your computer ran out of memory (or before you ran out of paitence).  Turn in both your code and the results of the search.

# en español:
#   Implementar solucionadores para el rompecabezas de 8 fichas utilizando tanto la busqueda en amplitud como la busqueda en profundidad.
#   Elija un tablero aleatorio y cuente cuantos nodos se expandieron y cuantos se almacenaron simultaneamente en la memoria
#   antes de que su computadora se quedara sin memoria (o antes de que se quedara sin paciencia). Entregue tanto su codigo como los resultados de la busqueda.

# como recordatorio:
#  busqueda en amplitud: expande todos los nodos de un nivel antes de pasar al siguiente nivel
#  busqueda en profundidad: expande un nodo y luego expande el primer hijo de ese nodo, luego el primer hijo de ese nodo, etc



# diccionario direcciones 
direcciones = {"arriba": [-1,0], "abajo": [1,0], "izquierda": [0,-1], "derecha": [0,1]}

board = [["1", "2", "3"],
         ["4", "5", "6"],
         ["7", "8", "_"]]

vacio = [-1, -1]

def mezclar_tablero(tablero = board):
    import random
    # mezclar el tablero
    for i in range(len(tablero)):
        for j in range(len(tablero[i])):
            x = random.randint(0, len(tablero) - 1)
            y = random.randint(0, len(tablero[i]) - 1)
            tablero[i][j], tablero[x][y] = tablero[x][y], tablero[i][j]

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


def mover(direccion): 
    x = vacio[0]
    y = vacio[1]
    # comprobar si se puede mover en esa direccion
    if not validar_movimiento(direccion):
        return False
    
    posDestino = sumar(direcciones[direccion], [x, y])

    # intercambiar las posiciones
    board[x][y], board[posDestino[0]][posDestino[1]] = board[posDestino[0]][posDestino[1]], board[x][y]
    vacio[0] = posDestino[0]
    vacio[1] = posDestino[1]


def validar_movimiento(direccion):
    x = vacio[0]
    y = vacio[1]
    if x + direcciones[direccion][0] < 0 or x + direcciones[direccion][0] > len(board) - 1 or y + direcciones[direccion][1] < 0 or y + direcciones[direccion][1] > len(board[0]) - 1:
        return False
    return True


def localizar_vacio(tablero = board):
     # encontrar la posicion del _
    for i in range(len(board)):
        for j in range(len(board[i])):
            if board[i][j] == "_":
                x = i
                y = j
                break
    
    # si no encontro el _
    if x == None or y == None:
        return False
    vacio[0] = x
    vacio[1] = y


def main():
    mezclar_tablero()
    localizar_vacio()
    # menu mover fichas
    while True:
        mostrar_tablero()
        print("1. arriba\n2. abajo\n3. izquierda\n4. derecha\n5. salir")
        opcion = input("Ingrese una opcion: ")
        if opcion == "1":
            mover("arriba")
        elif opcion == "2":
            mover("abajo")
        elif opcion == "3":
            mover("izquierda")
        elif opcion == "4":
            mover("derecha")
        elif opcion == "5":
            break
        else:
            print("Opcion no valida")
        if board == calular_solucion():
            print("Felicidades, has ganado!")
            break
    


# main 
if __name__ == "__main__":
    main()


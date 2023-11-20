El archivo excel contiene una macro para realizar los calculos con mayor facilidad,
si no se activan las macros va a fallar al realizar la llamada a la macro.

En caso de que se haya perdido la macro al guardar y mandar el archivo este es el
codigo que deberia tener la macro:

Function I(N1 As Double, N2 As Double) As Double
    If N1 > 0 And N2 > 0 Then
        I = -N1 * Log(N1) / Log(2) - N2 * Log(N2) / Log(2)
    Else
        I = 0
    End If
End Function

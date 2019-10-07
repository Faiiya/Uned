#!/bin/bash
#este archivo lanza un proceso para que ocupe el procesador y luego llama a mitop para compbrobar su funcionamient0
#es de esperar que el proceso yes aparezca en la parte alta de top.
#crea tres procesos inútiles en segundo plano
yes > /dev/null &

#ejecuta el programa del estudiante
./Trabajo1/mitop.sh

#elimina los procesos en segundo plano creados con anterioridad
kill $(jobs -p)





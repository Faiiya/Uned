#!/bin/bash
#este archivo es un scrip que:

#hacemos cd para facilitar los otros comandos
cd ./Trabajo2
#1 compila los fuentes padre.c e hijo.c con gcc
gcc ./padre.c -o padre.exe
gcc ./hijo.c -o hijo.exe
#2 crea el fihero fifo "resultado"
mkfifo resultado
#inicializa variables
number="10"
path="./padre.exe"
#lanza un cat en segundo plano para leer "resultado"  
#lanza el proceso padre
cat resultado & ./padre.exe $path $number
#al acabar limpia todos los ficheros que ha creado
#borra los exe y el FIFO
rm ./padre.exe
rm ./hijo.exe
rm resultado




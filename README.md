<h1 align="center">UNED</h1>


Compilacion de practicas de ingenieria informatica de la Uned.

- [Como contribuir](https://github.com/Faiiya/Uned#contribuir)
- [Lista de Asignaturas](https://github.com/Faiiya/Uned#asignaturas)


Si no tienes git en tu equipo, puedes encontrar instrucciones para instalarlo en [este enlace]( https://help.github.com/articles/set-up-git/ ).

<h2 align="center">Como contribuir</h1>

Para poder contribuir simplemente hay que seguir un par de sencillos pasos.
- Haz Fork del repositorio
- Clona el repositorio
- Crea una rama
- Haz cambios y confirmalos
- Manda (Push) los cambios
- Envia (Submit) los cambios


### Haz fork del repositorio 

Para ello simplemente clica en el boton de fork que hay en la parte superior de esta pagina. Asi crearas una copia de este repositorio en tu cuenta


### clona el repositorio

Ahora que has hecho Fork al repositorio , ve a tu cuenta de github y abre el repositorio que acabas de forkear, clicla en el botton de clonar, luego clica en el boton de copiar al portapapeles

En un terminal ejecuta el siguiente comando
```
git clone "url que has copiado"
```
donde "url que has copiado" (sin comillas) es la url de tu fork de este repositorio
por ejemplo

```
git clone https://github.com/USERNAME/Uned.git
```
Donde USERNAME seria tu nombre de usuario

### crea una rama

Cambia el directorio de tu ordenador al del repositorio (si no estas ya)

```
cd Uned
```
Ahora crea una rama (Branch) usando `git checkout` command:

```
git checkout -b <nombre-de-tu-rama>
```

Por ejemplo:

```
git checkout -b nueva-practica-sidi
```
### Haz los cambios necesarios y confirma (*Commit*) esos cambios

Si vas al directorio del proyecto y ejecutas el comando  `git status`, verás si hay cambios.

Agrega esos cambios a la rama (*branch*) que creaste anteriormente usando el comando `git add`:

```
git add tus-cambios
```
tus cambios puedes ser por ejemplo una practica de una asignatura

Para añadir una practica por favor añadidla dentro de la asignatura y el año correspondiente y utilizar
el formato `Nombre_Apellido_Nota` la nota es opcional pero ayudara a las futuras personas a buscar practicas con buenas notas.

Ahora haz un *commit* sobre estos cambios ejecutando el comando `git commit`:
```
git commit -m "mensaje del commit"
```
### Manda (*Push*) tus cambios a GitHub

Haz *push* de tus cambios usando el comando `git push`:
```
git push origin <añade-el-nombre-de-la-rama>
```
Reemplaza `<añade-el-nombre-de-la-rama>` con el nombre de la rama que creaste anteriormente.

### Envía (*Submit*) tus cambios para ser revisados

Si vas a tu repositorio en GitHub, verás un botón `Compare & pull request`. Haz click sobre este botón.

Ahora envía la *pull request*.

<h2 align="center">Lista de Asignaturas</h1>

## Primero


## Segundo


## Tercero
- [DyASO](https://github.com/Faiiya/Uned/tree/master/DyASO)
- [PL1](https://github.com/Faiiya/Uned/tree/master/PL1)
- [SIDI](https://github.com/Faiiya/Uned/tree/master/SIDI)

## Cuarto

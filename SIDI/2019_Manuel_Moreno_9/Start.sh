#!/bin/bash 

if gnome-terminal -- /bin/sh -c "exec java -jar Database.jar"; then
	sleep 1
	if gnome-terminal -- /bin/sh -c "exec java -jar Servidor.jar"; then
		sleep 1
		gnome-terminal -- /bin/sh -c "exec java -jar Usuario.jar"
		gnome-terminal -- /bin/sh -c "exec java -jar Usuario.jar"
	else
		echo "[Error] no se ha podido iniciar el servidor"
	fi
else
	echo "[Error] no se ha podido iniciar la base de datos"
fi



#!/bin/bash

# Esperar a que MySQL esté disponible
sleep 10

# Crear la base de datos si todavía no existe
mysql -h mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" \
  -e "CREATE DATABASE IF NOT EXISTS dvja CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"

# Comprobar si la tabla users ya existe
TABLE_EXISTS=$(mysql -h mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" \
  -N -s -e "SELECT COUNT(*) FROM information_schema.tables
            WHERE table_schema='dvja' AND table_name='users';")

# Importar el esquema solo cuando la base está vacía
if [ "$TABLE_EXISTS" -eq 0 ]; then
  echo "Inicializando esquema de la base de datos..."
  mysql -h mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" dvja < ./db/schema.sql
else
  echo "La base de datos ya está inicializada. Se conservan los datos existentes."
fi

mvn jetty:run
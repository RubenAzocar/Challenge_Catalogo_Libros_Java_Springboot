#!/bin/bash

# Script para configurar variables de entorno en Linux/Mac
# Dale permisos de ejecución: chmod +x run.sh
# Ejecuta: ./run.sh

echo "================================================"
echo "Configurando variables de entorno para LiterAlura"
echo "================================================"
echo

# Configurar variables de entorno
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=literalura
export DB_USER=postgres

# Solicitar contraseña
read -sp "Ingrese la contraseña de PostgreSQL: " DB_PASSWORD
export DB_PASSWORD
echo

echo
echo "Variables de entorno configuradas:"
echo "DB_HOST=$DB_HOST"
echo "DB_PORT=$DB_PORT"
echo "DB_NAME=$DB_NAME"
echo "DB_USER=$DB_USER"
echo "DB_PASSWORD=****** (oculta por seguridad)"
echo
echo "================================================"
echo "Iniciando aplicación LiterAlura..."
echo "================================================"
echo

# Ejecutar la aplicación con Maven
./mvnw spring-boot:run

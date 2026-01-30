@echo off
REM Script para configurar variables de entorno en Windows
REM Ejecuta este archivo antes de iniciar la aplicación

echo ================================================
echo Configurando variables de entorno para LiterAlura
echo ================================================
echo.

REM Configurar variables de entorno
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=literalura
set DB_USER=postgres

REM Solicitar contraseña de forma segura
set /p DB_PASSWORD="Ingrese la contraseña de PostgreSQL: "

echo.
echo Variables de entorno configuradas:
echo DB_HOST=%DB_HOST%
echo DB_PORT=%DB_PORT%
echo DB_NAME=%DB_NAME%
echo DB_USER=%DB_USER%
echo DB_PASSWORD=****** (oculta por seguridad)
echo.
echo ================================================
echo Iniciando aplicación LiterAlura...
echo ================================================
echo.

REM Ejecutar la aplicación con Maven
mvnw.cmd spring-boot:run

pause

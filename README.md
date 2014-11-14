Instrucciones para la ejecución de la API libreria-project

1- Descargar proyecto de GitHub 

2- Construir proyecto Maven desde terminal para ello acceder al directorio donde se encuentra el proyecto y alli:
    >mvn clean
    >mvn package 
3- Copiar el archivo .war generado (./target/libreria-api.war) en la carpeta /webapps del directorio donde tengamos instalado Tomcat 

4- Construcción de la BD en MySQL
    Acceder a MySQL como root y llamar (source) al archivo libreriadb-user.sql
    Acceder a MySQL con los datos del nuevo usuario (librero, librero) y llamar al archivo libreriadb-schema.sql, si se desea cargar la base de datos llamar al libreriadb-data.sql

5- Ejecutar Tomcat y servidor MySQL 

6- Realizar peticiones HTTP desde Postman

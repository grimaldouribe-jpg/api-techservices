# TechServices Peru — Ejercicio 1 (proyecto corregido)

## Errores corregidos
Se completaron los cuatro archivos vacios del RAR original:
`model/UsuarioApi.java`, `repository/UsuarioApiRepository.java`,
`config/SecurityConfig.java` y `controller/ApiExceptionHandler.java`.
Se revisaron las entidades, la configuracion JDBC/MySQL, los mensajes de error
y la seguridad. Se excluyeron los artefactos `target/` anteriores.

## 1. Abrir en Visual Studio Code
Abrir la carpeta `api-techservices` que contiene `pom.xml`.
Requiere **JDK 21**, XAMPP con MySQL/MariaDB en **3306** (o MySQL Server),
y **Maven Wrapper** del proyecto. El archivo `mvnw.cmd` ya esta incluido.

## 2. Base de datos
La BD del usuario **ya existe** (`bd_techservices`, `usuarios_api`, `clientes`).
**No ejecute otra vez la carga SQL si ya tiene esos datos**.
En otra maquina o servidor vacio use `sql/01_bd_techservices.sql`.
Para verificar registros use `sql/02_verificar.sql`.
El codigo espera **5 clientes**, **2 usuarios activos**, y valores SHA-256
que se insertaron mediante `SHA2(...,256)`.

## 3. Ejecutar localmente (PowerShell)
Iniciar XAMPP MySQL. En VS Code:

```powershell
cd C:\Users\UPSJB\Downloads\api-techservices
# Opcional si se usa un usuario diferente o se asigno contrasena:
# $env:DB_USER = 'usuario_mysql'
# $env:DB_PASSWORD = 'contrasena_de_mysql'
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Por defecto `application.properties` usa `jdbc:mysql://127.0.0.1:3306/bd_techservices`, usuario `root` y contrasena vacia, **solo para desarrollo local**. Si MySQL usa otra cuenta, configure `DB_USER` y `DB_PASSWORD` en las variables de entorno.
El test incluido verifica validaciones basicas y **no requiere conexion a BD**; el arranque normal SI requiere MySQL.

## 4. Postman
Importe `postman/TechServices_Ejercicio1.postman_collection.json`
o cree una peticion:

```http
GET http://localhost:8080/api/clientes/1
x-api-key: MI_API_KEY_2026
Authorization: Bearer TOKEN_SEGURO_123456
```

Si el usuario inicia con 10 tokens, la primera respuesta 200 incluye un objeto `cliente`,
`mensaje: "Consulta realizada correctamente"` y `tokensRestantes: 9`.
Credenciales incorrectas: **401**, cliente inexistente: **404**,
saldo agotado: **429**. Solo las consultas exitosas descuentan uno.
**Cada vez que pulse Send con credenciales correctas y cliente existente,
consumira un token**.

## 5. Render (obligatorio en el examen)
1. Suba este proyecto a un repositorio GitHub (no suba contrasenas ni `.env`).
2. Obtenga una **base MySQL accesible desde Internet**. XAMPP local en `127.0.0.1`
   **no es accesible desde Render**. Importe allí el esquema y los datos.
3. En Render cree un **Web Service** desde GitHub, elija entorno **Docker**:
   se incluye `Dockerfile` (Maven + Java 21).
4. Configure en Render estas variables, con los valores reales del proveedor MySQL:
   `DB_URL` = `jdbc:mysql://HOST_REMOTO:3306/bd_techservices?sslMode=REQUIRED`
   `DB_USER` = usuario de BD remoto
   `DB_PASSWORD` = contrasena del usuario remoto
   No deje los valores de ejemplo `root` y vacio en Internet.
5. Deje que Render determine `PORT` automaticamente; `server.port=${PORT:8080}` lo utiliza.
6. Pruebe desde **Postman Web** cambiando `baseUrl` por la URL real publica
   que Render haya asignado (por ejemplo `https://su-servicio.onrender.com`).
   El nombre y la URL definitivos solo existen cuando usted crea el servicio.
7. Verifique que el proveedor remoto y Render permitan conexiones de red y SSL.
   Algunos proveedores requieren otros puertos o una CA, adapte `DB_URL`.

## Seguridad y alcance
Los valores API Key y token son **solo academicos**. Para publicacion real:
genere claves aleatorias diferentes, limite permisos de BD, active TLS verificado
y gestione secretos en variables de entorno. La autenticacion se valida
obligatoriamente en `ClienteService` antes de leer los clientes.

## Nota sobre verificaciones
La compilacion real y la conexion a su instancia MySQL deben comprobarse en
la computadora del usuario. En esta entrega se comprobaron archivos y rutas;
no se debe interpretar la preparacion de este ZIP como una prueba con MySQL real.

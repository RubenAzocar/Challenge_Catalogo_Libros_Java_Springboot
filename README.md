# 📚 LiterAlura - Catálogo de Libros

## 📋 Descripción

**LiterAlura** es una aplicación de consola desarrollada en Java 17 con Spring Boot 3.x que permite gestionar un catálogo de libros consumiendo la API pública de [Gutendex](https://gutendex.com/books/). La aplicación permite buscar libros, registrarlos en una base de datos PostgreSQL y realizar consultas avanzadas sobre autores y libros.

## 🎯 Características

- ✅ Búsqueda de libros por título mediante API REST
- ✅ Persistencia en base de datos PostgreSQL
- ✅ Prevención de duplicados automática
- ✅ Gestión de autores con relaciones bidireccionales
- ✅ Consultas JPQL personalizadas
- ✅ Menú interactivo de consola
- ✅ Filtrado por idioma (ES, EN, FR, PT)
- ✅ Búsqueda de autores vivos en un año específico

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Jackson** (para mapeo JSON)
- **HttpClient** (para consumo de API)

## 📁 Estructura del Proyecto

```
literalura/
├── src/main/java/com/alura/literalura/
│   ├── dto/                    # Data Transfer Objects
│   │   ├── DatosAutor.java
│   │   ├── DatosLibro.java
│   │   └── DatosRespuesta.java
│   ├── model/                  # Entidades JPA
│   │   ├── Autor.java
│   │   └── Libro.java
│   ├── repository/             # Repositorios JPA
│   │   ├── AutorRepository.java
│   │   └── LibroRepository.java
│   ├── service/                # Lógica de negocio
│   │   ├── AutorService.java
│   │   ├── LibroService.java
│   │   ├── ConsumoAPI.java
│   │   ├── ConvierteDatos.java
│   │   └── IConvierteDatos.java
│   ├── principal/              # Interfaz de consola
│   │   └── Principal.java
│   └── LiteraluraApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## ⚙️ Configuración

### Prerrequisitos

1. **Java 17** o superior instalado
2. **Maven 3.6+** instalado
3. **PostgreSQL** instalado y en ejecución

### Variables de Entorno

Antes de ejecutar la aplicación, configura las siguientes variables de entorno:

```bash
# Windows (PowerShell)
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="literalura"
$env:DB_USER="postgres"
$env:DB_PASSWORD="tu_contraseña"

# Linux/Mac
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=literalura
export DB_USER=postgres
export DB_PASSWORD=tu_contraseña
```

### Crear Base de Datos

Conecta a PostgreSQL y crea la base de datos:

```sql
CREATE DATABASE literalura;
```

## 🚀 Ejecución

### Opción 1: Con Maven

```bash
mvn clean install
mvn spring-boot:run
```

### Opción 2: Desde el IDE

Ejecuta la clase principal `LiteraluraApplication.java`

## 📖 Uso de la Aplicación

Al ejecutar la aplicación, verás un menú interactivo:

```
==================================================
📚 LITERALURA - CATÁLOGO DE LIBROS
==================================================
1 - Buscar libro por título
2 - Listar libros registrados
3 - Listar autores registrados
4 - Listar autores vivos en un determinado año
5 - Listar libros por idioma
0 - Salir
==================================================
```

### Funcionalidades

1. **Buscar libro por título**: Busca en la API de Gutendex y guarda en la BD
2. **Listar libros registrados**: Muestra todos los libros guardados
3. **Listar autores registrados**: Muestra todos los autores con sus libros
4. **Autores vivos en un año**: Consulta JPQL que filtra autores vivos en un año específico
5. **Libros por idioma**: Filtra libros por código de idioma (es, en, fr, pt)

## 🔍 Consultas JPQL Destacadas

### Autores Vivos en un Año

```java
@Query("SELECT a FROM Autor a WHERE " +
       "(a.anioNacimiento IS NULL OR a.anioNacimiento <= :anio) AND " +
       "(a.anioFallecimiento IS NULL OR a.anioFallecimiento >= :anio)")
List<Autor> findAutoresVivosEnAnio(@Param("anio") Integer anio);
```

Esta consulta maneja casos donde:
- El año de nacimiento es NULL (desconocido)
- El año de fallecimiento es NULL (aún vivo)
- El autor nació antes o durante el año consultado
- El autor murió después o durante el año consultado

## 🗃️ Modelo de Datos

### Entidad Autor

```java
@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(name = "anio_nacimiento")
    private Integer anioNacimiento;

    @Column(name = "anio_fallecimiento")
    private Integer anioFallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> libros;
}
```

### Entidad Libro

```java
@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String titulo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "autor_id")
    private Autor autor;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    private List<String> idiomas;

    @Column(name = "numero_descargas")
    private Integer numeroDescargas;
}
```

## 🔐 Prevención de Duplicados

La aplicación implementa verificaciones antes de guardar:

```java
// Verifica si el libro ya existe por título
Optional<Libro> libroExistente = libroRepository.findByTituloIgnoreCase(titulo);

// Verifica si el autor ya existe por nombre
Optional<Autor> autorExistente = autorRepository.findByNombreIgnoreCase(nombre);
```

## 📡 API de Gutendex

La aplicación consume la API de Gutendex:

- **URL Base**: `https://gutendex.com/books/`
- **Búsqueda por título**: `https://gutendex.com/books/?search=don%20quixote`
- **Formato de respuesta**: JSON

## 👨‍💻 Autor

**Rubén** - Estudiante de Alura ONE (Oracle Next Education)

## 📝 Licencia

Este proyecto es parte del challenge de Alura Latam.

## 🙏 Agradecimientos

- [Alura Latam](https://www.aluracursos.com/)
- [Oracle Next Education](https://www.oracle.com/education/)
- [Gutendex API](https://gutendex.com/)

---

⭐ **¡Si te gustó este proyecto, dale una estrella!** ⭐
# Challenge_Catalogo_Libros_Java_Springboot

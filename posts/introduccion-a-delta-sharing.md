---
authors:
- Antonio Saldivar
tags:
- Data
- Delta Sharing
- Python
- Delta Lake
- Open Source
date: 2023-02-14T12:30:00.000Z
title: "Delta Sharing - protocolo para compartir datos de manera segura"
image: 
---

# Introducción 
[Delta Sharing](https://delta.io/sharing/) es un protocolo libre para intercambiar grandes cantidades de datos de manera segura y en tiempo real entre organizaciones, este accede y transfiere de manera segura un `Dataset` que se encuentra almacenado en la nube, en sistemas como AWS S3, Azure ADLS y Google GCS, permitiendo que los consumidores de datos internos o externos que no se encuentran en la misma plataforma, puedan acceder a ese `Dataset`.

## Ecosistema de Delta Sharing
![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/02/deltasharing-eco.png)

Una vez configurada la conexión y los permisos en el servidor de Delta Sharing, los clientes destino podrán acceder y conectarse de forma directa a los datos, usando librerías tales como `pandas`, `Apache Spark` o herramientas como `Databricks`, `Power BI` y `Tableau` Este acceso es posible sin la necesidad de crear y configurar una infraestructura especializada, logrando que los clientes que cuenten con el acceso requerido puedan empezar a hacer uso de los datos compartidos en cuestión de minutos.

![](https://raw.githubusercontent.com/ippontech/blog-usa/master/images/2023/02/deltasharing.png)
# Instalación del conector para Python
El conector para Python implementa el protocolo para leer datos en formato de tablas desde un servidor de `Delta Sharing`. Estas tablas se pueden mostrar a un `Dataframe` utilizando  Pandas o Apache Spark.


## Instalación de la librería

```python
# Python version 3.6+
pip install delta-sharing
```

## Crear el archivo de perfiles
El formato necesario para crear este archivo es el formato JSON porque contiene la información requerida para que el cliente que va a consumir los datos tenga acceso al servidor de Delta Sharing.
- `shareCredentialsVersion`: Es la versión del archivo que es utilizado para el perfil especificado.
- `endpoint`: La URL del servidor en donde se configuran los accesos.
- `bearerToken`: La llave de acceso para el servidor.
- `expirationTime`: El tiempo de expiración para la llave de acceso

```json
{
  "shareCredentialsVersion": 1,
  "endpoint": "https://sharing.delta.io/delta-sharing/",
  "bearerToken": "<token>",
  "expirationTime": "2023-10-15T00:12:30.0Z"
}

```
## Utilizar el conector para Python

Una vez creado el archivo del perfil con las llaves de acceso y guardado como archivo local o en un almacenamiento remoto en la nube, se podrá configurar el conector con este perfil.

```python
import delta_sharing

profile = "profile_file.json"
client = delta_sharing.SharingClient(profile_file)

table_url = profile_file + "#<share-name>.<schema-name>.<table-name>"

# Cargar los datos en Pandas Dataframe
delta_sharing.load_as_pandas(table_url)

# Cargar los datos en PySpark Dataframe
delta_sharing.load_as_spark(table_url)

```
### Conector para Apache Spark
Otra forma de lograrlo es utilizar el conector para `Apache Spark`

```bash
pyspark --packages io.delta:delta-sharing-spark_2.12:0.6.2
```

```python
df = (spark.read
  .format("deltasharing")
  .load("<profile_path>#<share_name>.<schema_name>.<table_name>")
)
```

# Beneficios de usar Delta Sharing

## Mejora la capacidad del diseño
- Admite el intercambio de datos en diferentes plataformas informáticas, lenguajes de programación y sistemas de almacenamiento.
- Los datos enviados a los consumidores siempre serán consistentes ya que el proveedor realizará las transacciones [ACID](https://www.databricks.com/glossary/acid-transactions#:~:text=ACID%20is%20an%20acronym%20that,operations%20are%20called%20transactional%20systems.) en Deltalake.
- Está diseñado para superar los retos del intercambio de datos, como la necesidad de múltiples copias de datos, transferencias de datos lentas y costos elevados.

## Seguridad al compartir datos
- Provee control de acceso detallado y atributos de privacidad de datos, como el cifrado de datos para garantizar seguridad durante el intercambio de datos.
- Se verifica el destinatario utilizando el token del proveedor para que se pueda ejecutar la consulta a la tabla de datos.
- Incluye un [Unity Catalog](https://www.databricks.com/product/unity-catalog) que provee prácticas de gobernanza para lograr una mejor administración y mejores controles de seguridad a la hora de compartir los datos a clientes externos e internos.

# Conclusión

En este blog se mostró una pequeña introducción a `Delta Sharing` con sus requerimientos básicos para dar acceso al consumidor de datos. También se demostró como el uso de estas configuraciones, nos brinda más opciones fuera de limitarnos a una sola plataforma o sistema para poder compartir datos almacenados en `Delta Lake`.

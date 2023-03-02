---
authors:
- Antonio Saldivar
tags:
- Data
- Delta Sharing
- Python
- AWS
date: 2023-02-14T12:30:00.000Z
title: "Delta Sharing - protocolo para compartir datos de forma segura"
image: 
---

# Introducción 
[Delta Sharing](https://delta.io/sharing/) es un protocolo libre para intercambiar grandes cantidades de datos de forma segura en tiempo real entre organizaciones, accesa y transfiere de manera segura un `Dataset` que se encuentra almacenado con sistemas en la nube como AWS S3, Azure ADLS y Google GCS, esto permite una conexión a los datos y administración de los consumidores de datos internos o externos que no se encuentran en la misma plataforma.

## Ecosistema de Delta Sharing
![](https://github.com/asaldivar10/blog-usa/blob/spanish-delta_sharing/images/2023/02/deltasharing-eco.png)

Una vez configurada la conexión y permisos en el servidor Delta Sharing , los clientes destino podrán acceder y conectarse de forma directa usando librerias como `pandas`, `Apache Spark` o herramientas como `Databricks`, `Power BI` y `Tableau`, esto sin la necesidad de crear y configurar una infraestructura especializada, los clientes que cuenten con el acceso requerido podrán empezar a hacer uso los datos compartidos en minutos.

![](https://github.com/asaldivar10/blog-usa/blob/spanish-delta_sharing/images/2023/02/deltasharing.png)
# instalación del conector para Python
El conector para Python implementa el protocolo para leer datos en formato de tablas desde un servidor de `Delta Sharing`, estas tablas se pueden mostrar a un `Dataframe` utilizando  Pandas o Apache Spark.


## Instalación de la libreria

```python
# Python version 3.6+
pip install delta-sharing
```

## Crear el archivo de perfiles
este archivo debe ser en formato JSON que contiene información para que el cliente que va a consumir los datos tenga acceso al servidor de Delta Sharing.
- `shareCredentialsVersion`: Es la versión del archivo que es utilizado para el perfil especificado.
- `endpoint`: La URL del servidor en donde se configuran los accessos.
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

Una vez creado el archivo del perfil con las llaves de acceso y guardado como archivo local o en un almacenamiento remoto en la nube, se podra configurar el conector con ese perfil.

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
Otra forma es utilizar el conector para `Apache Spark`

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
- Los proveedores y destinatarios de datos no necesitan estar en la misma plataforma, la transferencia es rápida, tiene bajo costo y se puede transferir en paralelo.

- Los datos enviados a los consumidores siempre van a ser consistentes ya que el proveedor realizará las transacciones [ACID](https://www.databricks.com/glossary/acid-transactions#:~:text=ACID%20is%20an%20acronym%20that,operations%20are%20called%20transactional%20systems.) en Deltalake.

## Seguridad al compartir datos
- Se verifica el destinatario utilizando el token del proveedor para que se pueda ejecutar la consulta a la tabla de datos.
- Delta sharing incluye un catálogo de unidad que ayuda con una mejor administración y controles de seguridad a la hora de compartir los datos a clientes externos e internos.

# Conclusión

En este blog se mostró una pequeña introducción a `Delta Sahring` con sus requerimientos básicos para dar acceso al consumidor de datos y como utilizando estas configuraciones, no nos limitamos a una sola plataforma o sistema para poder compartir datos almacenados en `Delta Lake`.



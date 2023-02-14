---
authors:
- Antonio Saldivar
tags:
- Data
- Delta Sharing
- Python
- AWS
date: 2023-02-14T12:30:00.000Z
title: "Delta Sharing - protocolo para datos compartidos de forma segura"
image: 
---

# Introducción 
[Delta Sharing](https://delta.io/sharing/) es un protocolo libre para intercambiar grndes cantidades de datos de forma segura en tiempo real entre organizaciones, accesa y transfiere de manera segura un conjunto de datos que se encuentra en systemas en la nube como AWS S3, Azure ADLS y Google GCS.

Una vez configurado, los usuarios podrán acceder y conectarse de forma directa usando librarias como `pandas`, `Apache Spark` ó herramientas como `Power BI` y `Tableau`, esto sin la necesidad de configurar y liberar una infrastructura especializada, los usuarios podrán empezar a hacer uso los datos compartidos en minutos.

# instalación del conector para Python
El conector para Python implementa el protocolo para leer datos en formato de tablas desde un servidor de `Delta Sharing`, estas tablas se pueden mostrar an un marco de datos ó `Dataframe` utilizando  pandas o apache Spark.

## instalar la libreria

```python
# Python version 3.6+
pip install delta-sharing
```

## Crear el archivo de perfiles
este archivo debe ser en formato JSON que contiene informacion para que el cliente que va a consumir los datos tenga acceso al servidor de Delta Sharing.
- `shareCredentialsVersion`: Es la version del archivo que es utilizado para el perfil especificado.
- `endpoint`: La URL del servidor en donde se configuran los accessos.
- `bearerToken`: La llave de acceso para el servidor.
- `expirationTime`: El tiempo de expiracion para la llave de acceso

```json
{
  "shareCredentialsVersion": 1,
  "endpoint": "https://sharing.delta.io/delta-sharing/",
  "bearerToken": "<token>",
  "expirationTime": "2023-10-115T00:12:30.0Z"
}

```
## Utilizar el conector para Python

Unavez creado el archivo del perfil con las llaves de acceso y guardado como archivo local o en un almacenamiento remoto en la nube, se podra configurar el conector con ese perfil.

```python
import delta_sharing

profile = "profile_file.json"
client = delta_sharing.SharingClient(profile_file)

table_url = profile_file + "#<share-name>.<schema-name>.<table-name>"

# Cargar los datos en Pandas Datframe
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



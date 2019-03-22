---
authors:
- Mallik Sambaraju
tags:
- Machine Learning
- Data Science
- AWS
- Sagemaker
- Python
date: 2019-03-22T12:24:13.000Z
title: "Linear Regression using AWS Sagemaker"
image: 
---

Various industries such as Banking, Manufacturing, Insurance, Health, Defence e.t.c are using Machine Learning to solve many use cases such as Fraud Detection, Improving Healthcare, Personal security, Providing more secured transportation e.t.c. Recently Ippon Technologies sent me to a summit on Data Science where I learnt about how organizations are using Machine Learning to improve their business and productivity. During the course of the summit many service and cloud providers presented various tools, libraries, algorithms and platforms for developing Machine Learning applications. One of the services introduced to me was Sagemaker by AWS. In this blog I will talk about How I implemented a basic regression model.

# Machine Learning Process
Typically Machine Learning process is an iterative process, it starts with identifying a use case to solve. Some of the steps involved in this process are as follows. This could change based on the use case you are trying to solve.

* Identify and Obtain Data.
* Pre-process and Prepare Data.
* Train a Model using the Prepared Data.
* Evalate the Model.
* Deploy the Model.
* Obtain Feedback of the Model.

Each of the above steps are iterative by themselves. Multiple iterations can happen during each of these steps depending on the data and performance of the model e.t.c.

![Machine Learning Process](https://raw.githubusercontent.com/msambaraju/blog-usa/master/images/2019/03/Machine_Learning.png)

# AWS Sagemaker
AWS Sagemaker is a fully managed AWS Machine Learning service which helps in building, training and deploying Machine Learning models. It has a rich set API's, built-in algorithms, integration with various popular libraries such as Tensorflow, PyTorch, SparkML e.t.c., developers tools for authoring models, and hosted production environment for deploying the models.

# Example Regression Model
In this example we will develop a regression model to predict body fat percentage based on 14 parameters like Age, Height, Weight, Abdomen circumference e.t.c. This data is widely available on the internet. A more advanced process is to find only the relevent features or parameters to use.

We will use the Jupyter Notebook authoring environment provided by Sagemaker to Prepare Data, Train and Evaluate Model, Deploy and Test the deployed Model. The Notebook environment also supports version control systems like CodeCommit or GitHub. Upload any test data used by the Notebooks into the environment.

In order to use the Jupyter Notebook, we need to create a Notebook Instance by providing an instance type such as ml.t2.medium. Provide IAM role with proper access control during instance creation.

Now create a new Jupyter notebook to interactively author, train and deploy the model.

![Jupyter Notebook](https://raw.githubusercontent.com/msambaraju/blog-usa/master/images/2019/03/Jupiter_Notebook_Env.png)


Prepare the data by reading training dataset from the S3 bucket or from an uploaded file. Format the data read into an acceptable format usually in the form of arrays and vectors.

```python
import pandas as pd

//Read from csv or someother location like s3.
dataset = pd.read_csv("Bio_Train.csv")

// Determine the features and labels.
feature_dataset = dataset[['Density', 'Age', 'Wt', 'Ht', 'Neck', 'Chest', 'ABD', 'Hip', 'Thigh', 'Knee', 'Ankle', 'Biceps', 'Farm', 'Wrist']]
features = np.array(feature_dataset.values).astype('float32')

label_dataset= dataset[['BFat']]
labels = np.array(label_dataset.values).astype('float32')
labels_vec = np.squeeze(np.asarray(labels))

```


Upload the prepared data into S3 bucket.

``` python

buffer = io.BytesIO()
smac.write_numpy_to_dense_tensor(buffer, features, labels_vec)
buffer.seek(0)

key = 'linearregression'
boto3.resource('s3').Bucket(bucket).Object(os.path.join(prefix, 'train', key)).upload_fileobj(buf)
s3_training_data_location = 's3://{}/{}/train/{}'.format(bucket, prefix, key)

```


Fetch the container with proper algorithm to use from the list for pre-defined Sagemaker algorithms or give your own custom container to support custom algorithms. In this case linear-learner algorithm is used which is a pre-defined algorithm.

``` python

from sagemaker.amazon.amazon_estimator import get_image_uri
container = get_image_uri(boto3.Session().region_name, 'linear-learner')

```


Now train the model using the container and the training data previously prepared. Create a new instance for training the model, provide the instance type needed. The trained model is stored in the S3 bucket as a tar file so provide S3 bucket details.

``` python
from sagemaker import get_execution_role

role = get_execution_role()

// Provide the container, role, instance type and model output location
linear = sagemaker.estimator.Estimator(container,
                                       role=role, 
                                       train_instance_count=1, 
                                       train_instance_type='ml.c4.xlarge',
                                       output_path=output_location,
                                       sagemaker_session=sess)

// Provide the number of features identified during data preparation
// Provide the predictor_type 

linear.set_hyperparameters(feature_dim=14,
                           mini_batch_size=4,
                           predictor_type='regressor')

// Train the model using the previously prepared test data and validate the 
//data by providing the validation data.

linear.fit({'train': s3_training_data_location})

```


Deploy the trained model using the Sagemaker API. Provide instance type and instance count as required. Once the deployment is complete the test data is used to test the deployed application. Once tha model is deployed an Http Endpoint is generated which can be used by other applications to invoke deployed Machine Learning model.

``` python

linear_predictor = linear.deploy(initial_instance_count=1,
                                 instance_type='ml.c4.xlarge')

from sagemaker.predictor import csv_serializer, json_deserializer

linear_predictor.content_type = 'text/csv'
linear_predictor.serializer = csv_serializer
linear_predictor.deserializer = json_deserializer

test_dataset = pd.read_csv("Bio_Train.csv")
test_feature_dataset = test_dataset[['Density', 'Age', 'Wt', 'Ht', 'Neck', 'Chest', 'ABD', 'Hip', 'Thigh', 'Knee', 'Ankle', 'Biceps', 'Farm', 'Wrist']]

test_features = np.array(test_feature_dataset.values).astype('float32')

for tf in test_features:
    result = linear_predictor.predict(tf)
    print(result)

```

# Conclusion
AWS Sagemaker provides capabilities to author, train, deploy and monitor Machine Learning applications using wide variety of algorithms, libraries and infrastructure.






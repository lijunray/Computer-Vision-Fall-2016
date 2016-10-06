# Computer Vision Fall 2016 Project

### Instructions

Build a classifier based on <a href="http://www.ibm.com/watson/developercloud/visual-recognition.html">IBM Watson Visual Recognition Service</a>, and test it by classifying both positive and negative images. Given a threshold, divide the scores fetched from server to true or false. By all these data, calculate <a href="https://en.wikipedia.org/wiki/Receiver_operating_characteristic">ROC</a>.

This program is to randomly select 80% positive and negative pictures for training for a classifier, and the rest 20% positive and negative pictures for testing the classifier. Then automatically calculate all scores to get ROC points. 

### How to use it

- This project is based on IntelliJ 14.1.5, so please open it with it.
- Directly under /src there is a Main class. Put your API Key, positive directories, negative directory there. 
- There is a resource folder, where you can put your own pictures there. But please add the path by yourself.
- All points are given out automatically to a Map, say if you have a folder named "camel", then you can get all points by `map["camel"]`.



### TODO

- Generate ROC plot based on ROC points.
- Get confidence intervals
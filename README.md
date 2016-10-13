# Computer Vision Fall 2016 Project

### Instructions

Build a classifier based on <a href="http://www.ibm.com/watson/developercloud/visual-recognition.html">IBM Watson Visual Recognition Service</a>, and test it by classifying both positive and negative images. Given a threshold, divide the scores fetched from server to true or false. By all these data, calculate <a href="https://en.wikipedia.org/wiki/Receiver_operating_characteristic">ROC</a>.

This program is to randomly select 80% positive and negative pictures for training for a classifier, and the rest 20% positive and negative pictures for testing the classifier. Then automatically calculate all scores to get ROC points. 

### How to use it

- This project is based on IntelliJ 14.1.5, so please open it with it.

- Edit the `info.properties` file to give necessary information the program needs.

- There is a `resource` directory, where you can put your own pictures there. Default setting for the path is set to `resource` directory. That means if you put your pictures there, you should delete `POSITIVE_DIRECTORY_PATH` and `NEGATIVE_DIRECTORY_PATH` properties in the `info.properties` since the program doesn't need those values;

- You can also put your images anywhere, but you should add `POSITIVE_DIRECTORY_PATH` and `NEGATIVE_DIRECTORY_PATH` to point to your directory's path.

- There are 2 main methods in `Handler` class: `handle`, which creates a new classifier and test it, and `classify`, which fetches the existing classifiers and choose the first one directly to test it. But both of them use 10 positive images and 10 negative images.

- The resource structure should be like below (pay attention to `positive` directory's path):

  <pre>

  CVProject

  |---- resource

  |      |---- pictures

  |      |      |---- positive

  |      |      |      |---- cat

  |      |      |      |      |---- cat1.jpg

  |      |      |      |      |---- ...

  |      |      |      |---- dog

  |      |      |---- negative

  |      |      |      |---- neg1.jpg

  |      |      |      |---- ...

  </pre>

- Add necessary dependencies by maven. All 3rd party libraries are listed there.



### TODO

- Get confidence intervals

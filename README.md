# Computer Vision Fall 2016 Project

### Instructions

Build a classifier based on <a href="http://www.ibm.com/watson/developercloud/visual-recognition.html">IBM Watson Visual Recognition Service</a>, and test it by classifying both positive and negative images. Given a threshold, divide the scores fetched from server to true or false. By all these data, calculate <a href="https://en.wikipedia.org/wiki/Receiver_operating_characteristic">ROC</a>.

This program is to randomly select some amount of positive and negative pictures for training for a classifier, and some amount of positive and negative pictures for testing the classifier. Then automatically calculate all scores to get ROC points, Confidence Intervals, and draw ROC plot. 

### How to use it

- This project is based on IntelliJ 14.1.5, so please open it with it.

- Edit the `info.properties` file to give necessary information the program needs.

- There is a `resource` directory, where you can put your own pictures there. Default setting for the path is set to `resource` directory. That means if you put your pictures there, you should delete `POSITIVE_DIRECTORY_PATH` and `NEGATIVE_DIRECTORY_PATH` properties in the `info.properties` since the program doesn't need those values;

- You can also put your images anywhere, but you should add `POSITIVE_DIRECTORY_PATH` and `NEGATIVE_DIRECTORY_PATH` to point to your directory's path.

- There are 4 main methods in `Handler` class, two of which are to create a new classifier, another two are to classify images. 

- For the 2 training methods, one of them creates a new classifier by selecting random positive and negative images, while another creates a new classifier with rotated images generated from previous training. 

- For the 2 classifying methods, one of them selects randomly some images as testing images and test the classifier on the server. Another classifies images in a directory directly,

- Since there is a limit of images count in any zip file, 20, if the number of images is more than 20, the methods will automatically split those files into several zip files and test them one by one.

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

  |      |---- scores(save scores as json format)

  </pre>

- Add necessary dependencies by maven. All 3rd party libraries are listed there.

- In `Main` class, there are 4 handle methods, all of which are commented. Read the comments and choose one to run. Classifying methods return a `Map` with positive and negative scores. With some lines of code below the call, it calculates the TPRs and FPRs to get ROC points and draw the plot. There are also 2 methods in `Drawer`, one of which is to draw a single line, another is to draw 2 lines, one normal, and another's classifier is trained with rotated images.

- In `Main` class, call `Handler.getCI()`, the program will run `CITimes` times to get Confidence Intervals and print the result to the console with a specific format.


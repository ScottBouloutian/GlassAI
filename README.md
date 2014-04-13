Glass AI
========================

This project is built on top of Google Mirror API's Quickstart for Java.

The documentation for this quickstart is maintained on developers.google.com.
Please see here for more information:
https://developers.google.com/glass/quickstart/java

This project takes an image shared from a user's timeline on Google Glass and uploads in via an HTML POST request to an Amazon EC2 server, where OpenCV functions process and return a result, which is in turn displayed on the user's Google Glass overlayed on the original image.

![alt tag](https://s3.amazonaws.com/Scott_Bouloutian_Files/ConnectFour.png)

## Building
  mvn jetty:run
  mvn war:war
  mvn clean install

## License
Google's Code in this project is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
and content is licensed under the
[Creative Commons Attribution 3.0 License](http://creativecommons.org/licenses/by/3.0/).

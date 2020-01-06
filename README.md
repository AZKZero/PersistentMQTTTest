# PersistentMQTTTest
A sample app demonstrating a quick workaround to using PahoMQTT in Android Oreo+ using foreground notification

## Background
* The ```PahoMQTT``` Class is controlled by the singleton ```ConnectionHolder```, which manages the mqttCallback.
* Due to a restriction on the background services in Android Oreo onwards, the MQTT service is wrapped in the<br>```NotificationService```
  which is a foreground service with a persistent notification.
* The notification can be modified, and the sample start button listener can be used to determine how to start the service.

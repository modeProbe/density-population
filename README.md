Density Population Calculator

This is a simple application exposes services to calculate density population. 
This is a Spring boot application written in JAVA12 and it uses MAVEN for building and packaging.
by default the app is running on port 8080 

To build the app just open a terminal and go in the app folder and run the following command:
**mvn clean install**

To launch the app after building it: 

**java -jar {artifact_name}**

By default the world is divided into a grid with latitude from -90 to 90 and longitude from -180 to 180
This values can be modified. You just have to go on the application-properties file and change the values of the following parameters: 
**coordinates.min-latitude
coordinates.max-latitude
coordinates.min-longitude
coordinates.max-longitude**

This application exposes 3 routes: 
- **/upload** (POST): 
This route can be used to upload a TSV (Tab Separated values) containing all the Point of Interests (POI) in the world
It can be used like this: POST: http://localhost:8080/upload (with multi part file in body)
You can find example of TSV files in the app in the resources folder
It will return the list of all POI that have been stored

- **/zone/number-poi/{min-lat}/{min-lon}** (GET): 
You can call this route and given the latitude and longitude it will returns all the POI in the specified zone
It will return 0 if the list of POI is empty

- **/zones/{nb-zones}/most-dense-zone** (GET):
When calling this route it will returns the most **nb-zones** densest zones 
If the nb-zones given is negative it will return a BAD_REQUEST error
If the nb-zones given is greater than the all list of zones, the all list will be return
It will return an empty array if given List of POI is empty (uploaded from first route)
The route will return the exact nb-zones zones e.g with nb-zones = 3 the call will return the most 3 densest zones even if the 4th zone as the same density as the 3rd
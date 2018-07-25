# LiveTracking

Note: I haven't used any Background Services like Intent Services etc..since they easily get destroyed in modern smartphones due their optimized architechture to prevent battery life.
That's why i have used Alarm Service.

## 1.Signup & Login

Sign Up             |  Login
:-------------------------:|:-------------------------:
![](https://firebasestorage.googleapis.com/v0/b/test-eff6f.appspot.com/o/LiveTracking%2F1.jpg?alt=media&token=94a0d03c-3327-40b1-913b-70594e37a23c)  |  ![](https://firebasestorage.googleapis.com/v0/b/test-eff6f.appspot.com/o/LiveTracking%2F2.jpg?alt=media&token=b6cc8233-138d-43bb-bc65-2ba065874973)

Points:
1. Username length should be > 3.
2. Password length should be > 3.
3. Mobile number length = 10.
Used Sqlite to store these informations.

**************
**************

## 2.Home -> To start Service

![](https://firebasestorage.googleapis.com/v0/b/test-eff6f.appspot.com/o/LiveTracking%2F3.png?alt=media&token=ba114b99-1f5d-471c-b742-72323ca92931)

Points:
1. When service is started, then alarm service will be called. It will store current GPS location every 5 to 10 minutes of interval.
2. The results with time will be stored in SQLite in the form (id,Logitude,latitude,time).
3. The 3 vertical dots in top right corner is for Logout.
* _Note: We are not storing last khown location, instead our app will explicitly request new current GPS location._

***************
**************

## 3.Location History
![](https://firebasestorage.googleapis.com/v0/b/test-eff6f.appspot.com/o/LiveTracking%2F4.png?alt=media&token=3e48144c-2bdc-43e3-86d5-dadf1d408959)

Points:
1. These are the history of all locations.
2. Sorted base on Recent time.
3. "Clear All" option given at top to clear all locations.
4. "Clear All" only visible in History Fragment not in others fragment.
5. When an item is click from list, it will open the Map with clicked location.

*************
************

## 4.MAP

When service is stopped    |  When Directly click on Map  | When item of history is clicked
:-------------------------:|:-------------------------:|:-------------------------:
![](https://firebasestorage.googleapis.com/v0/b/test-eff6f.appspot.com/o/LiveTracking%2F5.png?alt=media&token=0b97b96b-6291-4014-93b7-adb7c6c3acd8)  |  ![](https://firebasestorage.googleapis.com/v0/b/test-eff6f.appspot.com/o/LiveTracking%2F6-min.jpg?alt=media&token=ca557fb7-7794-43bc-8a68-5c3f35844cae) |  ![](https://firebasestorage.googleapis.com/v0/b/test-eff6f.appspot.com/o/LiveTracking%2F7.png?alt=media&token=47c02a12-a50c-45c4-86c3-5fdf0c70cc40)

Points:
* See the bottom Menus for differences in Map.
* There are 3 ways Map opens.
    * When you stop service, it will open the map with last save location.
    * When click on one of the item of Location history, it will open mapp with this location on it.
    * When directly click the map button, it will start requesting GPS locations, and keep on monitoring and changing the location like Google map.
      * As soon as you go to other fragment(Home or history) from Map, GPS service will get destroyed, since this GPS service is linked with Map fragment.
      * This GPS service and the GPS service invoke by Alarm service are 2 different services. 
* We have only 1 map which is placed in middle fragment named MAP. But above is an example of opening the same fragmentr/map in defferent ways with different responses.

#### I have mainly focus on Performance, quality and trying to check all loophole.

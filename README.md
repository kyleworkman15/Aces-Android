An application for Augustana College
We designed an app to improve the services of ACES at Augustana College. ACES is a ride request service that accepts rides from students
through phone calls. Our app is meant to remove the phone call process as well as make it easier for ACES to accept rides and pick up students.
The ACES app also stores data about each ride in a firebase database with the following information: rider email, start location, end location,
start time, end time, number of riders, and wait time. This way ACES can see how accurate their wait times are and they can look into ways for
improving the service. 

How it works
SIGNIN:
The user opens up the application and is greeted by an ACES logo with a Google Signin button beneath it. The user clicks on the signin button
and is prompted to turn on location services. If the user agrees, they will again be prompted to then sign into their google account or to choose
an already signed in account. The user must be signing in with an augustana email address, otherwise the request will be rejected. This screen
also checks to see if the ACES service is currently running. If ACES is offline the signin will be redirected to a screen which provides information
on ACES working hours.
REQUEST RIDE:
After signing in a screen displaying a map, start location and end location fields, number of riders field, and a request ride button will appear. From this screen
the user can either enter a start location or choose their current location. They must then choose their destination and, if applicable, number of riders.
Once the user chooses their start and end location, pins will appear on the map and they will be able to request a ride. 
AFTER REQUEST:
After requesting a ride, the user will be sent to a screen with a pending wait time, ETA, and a cancel button. The user can cancel a ride
at any time. This request is sent to the ACES service which will assign an estimated wait time to the user's ride. Once this time is assigned
and the ride is accepted and sent through to a driver, the wait time and ETA will be updated. The user can close the app or keep it open and check
to see if their ride has been accepted and what the ETA will be.
IN THE BACKGROUND:
Ride information is stored in a firebase database, which is also connected to an application used by the dispatcher and driver. When the user
clicks the request ride button, an entry is created in the database. The dispatcher can view a list of requested rides and assign wait times and push
them through to the driver. In the database we keep track of pending rides, active rides, and archived rides. There is also a flag stored
in the database for determining if ACES is offline or online.


Testing
We tested our application on a few different devices. We used emulators, Tablets running API 20, Tablet running API 23, and a phone running
the most recent version. Some specific test cases we tried: 
1.) User enters a location that does not exist within Google Maps - Catch this error and display a message telling the user to enter a valid
location.
2.) User wants to cancel a pending or active ride - clear this ride from the dispatch screen, firebase, and return the user to the map screen.
3.) User wants to enter a number of riders to accompany them - limit the number of options by using a drop down tab giving anywhere
between 1 (minimum) and 7 (maximum) riders. 
4.) User closes the app after requesting a ride - When they open the app again, check to see if they have a pending or active ride. If
they do, launch after ride request activity and display wait time and ETA. If they do not, send them to the map screen. 

Built With
Google Maps - Display users start and end location in the background on the request screen. Allow user to choose locations from Google
Maps
Google Firebase, Authentication and Realtime Database - Used for Google Signin and storing ride data / communcating with dispatch app.

Authors with guidance from professor Forrest Stonedahl

Tan Nguyen
Megan Janssen
Tyler May
Kevin Barbian 

Contributions

Kevin Barbian:
Pair programmed with Tyler for much of the work. Work involved:
Google Signin--Making it so that the user can sign in to our application using their Augustana gmail account.
Permissions--Helped to set up permissions on the signin screen, and made it so the user can sign in after allowing location services.
Firebase Database--Set up and communcation between user and dispatch applications. Made it so that key features such as wait time, ETA,
on/off flag updated and were checked in realtime. 





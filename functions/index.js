
// Created by Kyle Workman
// Handles Cloud Messaging/Notifications through Firebase

let functions = require('firebase-functions');
let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/ACTIVE RIDES/{email}/notify').onCreate((snapshot, context) => {
	
	if(snapshot.hasChildren()) {
		//get the email + message
		const id = snapshot.child('id').val();
		const token = snapshot.child('token').val();
		var message = "Test";
		var title = "Test";
		if (id == 0) { // on the way notification
			title = "Your ride is on the way!";
			message = "Watch for the " + snapshot.child('vehicle').val();
		} else if (id == 1) { // here notification
			title = "Your ride is here!";
			message = "Hop in the " + snapshot.child('vehicle').val();
		}

		if (token != null) {
			//send the message
			console.log("Construction the notification message.");
			const payload = {
				notification: {
					title: title,
					body: message,
					sound: 'default'
				},
				data: {
					title: title,
					message: message,
					sound: 'default'
				}
			};
	
			return admin.messaging().sendToDevice(token, payload)
						.then(function(response) {
							console.log("Successfully sent message:", response);
				  		})
				  		.catch(function(error) {
							console.log("Error sending message:", error);
				  		});
			}
	}
});
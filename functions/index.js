
// Created by Kyle Workman
// Handles Cloud Messaging/Notifications through Firebase

let functions = require('firebase-functions');
let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/ACTIVE RIDES/{email}/notify').onWrite((change, context) => {
	
	//get the email + message
	const receiverEmail = change.after.child('email').val();
	const message = "Watch for the " + change.after.child('message').val() + " ACES vehicle.";
	
	//get the token of the user receiving the message
	return admin.database().ref("/ACTIVE RIDES/" + receiverEmail).once('value').then(snap => {
		const token = snap.child("token").val();
		console.log("token: ", token);
		
		//send the message
		console.log("Construction the notification message.");
		const payload = {
			notification: {
				title: "Your ride is here!",
				body: message,
				sound: 'default'
			},
			data: {
				data_type: "direct_message",
				title: "Your ride is here!",
				message: message,
				message_id: "messageId",
			}
		};
		
		return admin.messaging().sendToDevice(token, payload)
					.then(function(response) {
						console.log("Successfully sent message:", response);
					  })
					  .catch(function(error) {
						console.log("Error sending message:", error);
					  });
	});
});
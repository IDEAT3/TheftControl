# TheftControl
This is an android application created as a project in Computer Security.

Problem Definition:
    Develop an android application for Theft Control. A trusted phone number needs to be configured by the user. The app sends a warning SMS to the trusted phone number once it detects three invalid password attempts, notifying the user about this event. The app will wipe out all the data from the phone, once it receives two missed calls from the trusted phone number and sends the approximate location via SMS. The SMS is a link to google maps generated using the longitude and latitude of the location, which is obtained using GPS. If the call is attended, data is not wiped out, but location is sent and the app listens for two missed calls again to wipe out the data. If the user is successfully logged in, then the app listens for another invalid password attempt.

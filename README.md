# S.P.R.AgentAndroid

S.P.R is an application-based system which designed to youth at risk who are seeking for help and response instantly and easily by an online chat with a representative formally suited to them by service of artificial intelligence algorithm in the cloud. 

The application contains embedded services in the application for navigation to shelters by city/user location and navigation to hospitals and police stations within a radius of 10-kilometre from the current location of the user.

There are 2 UI's based Android OS.
One for the youths: https://github.com/HadarPur/S.P.R.Android
The other one for the volunteers: https://github.com/HadarPur/S.P.R.AgentAndroid

In both apps, a questionnaire will be included when registering which will be answered by both so when there is ask for a new call, the machine learning algorithm will find the maximum fit and map between user and volunteer according to the information received from the user and the volunteers in the application system. 

There are 3 servers that built for this project:
One for push notifications to prepare him to for new match and to enable the agent to pickup VoIP calls: https://github.com/HadarPur/S.P.R.PushNotificationServer

Second to ensure connection end to end between the user and the agent via VoIP based on TokBox SDK: https://github.com/HadarPur/S.P.R.TokBoxServer

Third for KNN algorithm to ensure a maximum fit between the user and the agent: https://github.com/HadarPur/S.P.R.KnnAlgorithmServer

## Installation:

To install the app on your adnroid app please navigate to S.P.R.AgentAndroid⁩ ▸ ⁨app⁩ ▸ ⁨build⁩ ▸ ⁨outputs⁩ ▸ ⁨apk⁩ ▸ ⁨debug⁩ and install the .apk file on your smartphone


## App design:

<img src="https://github.com/HadarPur/S.P.R.AgentAndroid/blob/master/agentApp.png" />

## App flow:

<img src="https://github.com/HadarPur/S.P.R.AgentAndroid/blob/master/agentFlow.png" />

## App model:

<img src="https://github.com/HadarPur/S.P.R.AgentAndroid/blob/master/agentModelApp.png" />

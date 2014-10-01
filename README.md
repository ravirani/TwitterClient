# TwitterClient

A simple Android Twitter client.
The full requirements for this app can be found [here](http://courses.codepath.com/courses/intro_to_android/week/3#!module).

Time spent: 10 hours

### Completed user stories:

Following user stories were completed:

#### Required:

 * [x] User can sign in to Twitter using OAuth login
 * [x] User can view the tweets from their home timeline
	- User should be displayed the username, name, and body for each tweet
	- User should be displayed the relative timestamp for each tweet "8m", "7h"
	- User can view more tweets as they scroll with infinite pagination 
 * [x] User can compose a new tweet
	- User can click a “Compose” icon in the Action Bar on the top right
	- User can then enter a new tweet and post this to twitter
	- User is taken back to home timeline with new tweet visible in timeline
	
#### Optional:
 
 * [x] Compose tweet is shown in a dialog fragment
 
#### Advanced:

 * [x] Add pull-to-refresh (I used the third party one)
 * [x] Offline access to tweets
 * [x] Tweets are persisted locally 
 * [x] Infinite scrolling and pull to refresh correctly use since_id and max_id concepts of Twitter API
 * [x] Improved look and feel to be like Twitter
 
### Notes:

- Used Android Studio for development
- Used Genymotion emulator 

### Walkthrough of all user stories:
<br />
![Video Walkthrough](TwitterClient.gif)
<br />
<br />

GIF created with [LiceCap](http://www.cockos.com/licecap/).<br />

### Third party libraries, tools, and sites used:

- [Rest Client Template](https://github.com/thecodepath/RestClientTemplate/) for Oauth, image loading, and network requests.
- [iconmonstr](http://iconmonstr.com) for images/assets in the app.

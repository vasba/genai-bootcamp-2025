A romanian learning multiplatform app in the frontend folder with following purposes:
- launch study activities
- store group and explore romanian vocabulary
- review study progress 

Use the api provided by the code in backend folder.

Technical requirements:
- implemented with Compose Kotlin multiplatform
- should support both web, Android an iOS

Frontend Routes
This is a list of routes for our app, each of the routes are a page and we'll describe them in more details under the page heading.
- /dashboard
- /study-activities
- /study-activities/:id
- /words
- /words/:id
- /groups
- /groups/:id
- /sessions
- /settings

The default route should forward to /dashboard

Global Components
Navigation

There will be a horizontal navigation bar with the following links:
- Dashboard
- Study Activities
- Words
- Wortd Groups
- Sessions
- Settings

# Pages

## Dashboard

This page provides a summary of the student's progression

- Last session

## Study activities index

This is a grade of cards which represent an activity.

A card has a: 
- thumbnail
- title
- Launch button
- View button

The Launch button will open the activity page and View button will open Student Sctivities Show PAge

## Study Activities show

the route to this page is /study-activities/:id

It will contain:
- thumbnail
- title
- description
- launch button
- list of sessions, where each session item will contain
    * group name: So you know what group name was used for for the sessions
    * link to the group show page
    * Session Start time: YYYY-MM-DD HH:MM
    * End Time
    * Review items: the number of review items

## Words index

the route to this page is /words

This is table of words with following cells:
- Romanian
- English
- Correct: Number of correct word review items
- Wrong: Number of wrong word review items

There should be 50 words displayed at a time with pagination:
- Previous button: greyed out or not visible if no more page to go back
- Page number of total number of pages
- Next button: greyed out or not visible if no more page to go back

All table headings must be sortable if you click on heading. An ASCII arrow should indicate direction of sort in each heading of the sorted column.

## Words Show

The route of this page is /words/:id

## Word Groups index

The route for this page is /word-groups

A table of word groups with following cells:
- Group name: 
- Link to Word group show
- Words count: the number of words in the group

This page contains same sorting and pagination logic as the Words index page. Reuse code to accomplish this.

## Word Groups Show

The route for this page /words-groups/:id

This has the same components like Words Index but it is scoped to only show words associated with this group.

## Session index

The route for this page /sessions

This page contains a list of sessions similar to Study Activities Show

This page contains same sorting and pagination logic as the Words index page. Reuse code to accomplish this.

## settings page

The route for this page is /settings

## Reset history button

This has a button that allows us to reset the entire database. We need to confirm this action in a dialog and type the word reset me to confirm.

# Dark mode toggle:

This is a toggle that changes from light to dark theme.
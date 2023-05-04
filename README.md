# News api
Rest api for <a href="https://github.com/Rys-Nowak/news-app">news application</a>

## Tools used
* Spring
  * Spring Boot
  * Spring Security
 * PostgreSQL Database
 * Bing Web Search API

## Responsibilities
* User authentication
* Database management
* Communication with Bing Web Search API

## Endpoints
### User authentication
* POST /api/login
  * Enables login by username and password (in request body)
  * Returns user data
* POST /api/register
  * Creates new user with given username and password (in request body)
  * Returns user data
* POST /api/logout
  * Logs user out
* GET /api/user
  * Retrieves currently logged-in user data

### Storing user data
* GET /api/phrase
  * Returns all observed by user phrases
* POST /api/phrase/
  * Adds new phrase to user's observed phrases
  * Takes new phrase in request body
  * Returns created PhraseEntity object - new observed phrase and username
* DELETE /api/phrase
  * Deletes user's observed phrase
  * Takes phrase to delete in request body

### Comunication with Bing API
* GET /api/search
  * Returns news related to user's observed phrases as a JSON array of objects containing news data
  * Takes index of results' page as page _query_ parameter
  * Takes amount of news related to one phrase as _count_ query parameter
* GET api/search/{phrase}
  * Returns news related to given phrase as a JSON array of objects containing news data
  * Takes phrase to search as path variable
  * Takes index of results' page as page _query_ parameter
  * Takes amount of news related to one phrase as _count_ query parameter 

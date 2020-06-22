
## Meeting Scheduler:  
  
This is a basic application to schedule meetings in a conference room with given constraints.  
No two meetings can overlap. Meeting duration cannot be less than 15 mins and more than 2 hours.  
In a week up to 40 hours of meetings can be scheduled. In a day up to 10 hours of meeting can be scheduled.  
Assumption – If a meeting starts at on a certain day and ends next day, meeting is not valid.  
  
### Architecture: Framework: Spring-Boot  
Data storage: In memory  
Data structures used – TreeMap, TreeSet, HashMap, Array List  
TreeMap - key is date and value is an interval TreeSet which contains meeting for that particular day  
HashMap - Key is meeting title, value is the ArrayList address of the meeting  
HashMap is used for indexing which is used while deleting meeting by meeting title  
  
### How to execute:  
1. Install maven  
2. Download/Clone the project repository  
3. Change directory to project home directory  
4. Run the following command in command line to run project  `mvn spring-boot:run`  
5. Server runs on port **8080**  
  
### Set meeting:  
 API URL - `localhost:8080/meetings/setMeeting`    
 Request method - **POST**   
 Request body   
```
{ 
	 “meetingTitle”: TITLE_NAME,
	 “fromTime”: START_TIME of the meeting (epoch time), 
	 “toTime”: END_TIME of the meeting (epoch time) 
}
  ```
 Expected response - saved meeting's payload 
 Example Input1- `localhost:8080/meetings/setMeeting`
 ```
 {
	 "meetingTitle": "Orientation", 
	 "fromTime": "1592924400",
	 "toTime": "1592928000" 
 } 
   ``` 
 Output:
 ``` 
 {
	   "id": "5e79c68b-a5b1-4535-b901-a15983e6103a",
	   "start": "2020-06-23T10:00:00", 
	   "end": "2020-06-23T11:00:00",
	   "title": "Orientation"
} 
   ``` 
   
### Remove Meeting (meetingTitle):  
 API URL - `localhost:8080/meetings/removeMeeting?meetingTitle=title1`   
 Request method - **DELETE**  
 Expected response- 200 OK  
 Example Input1: `localhost:8080/meetings/removeMeeting?meetingTitle=Orientation`   
 Output - 200 OK   

### Remove Meeting (fromTime):  
API URL - `localhost:8080/meetings/removeMeeting?fromTime=epochtime`    
Request method - **DELETE**    
Expected response- 200 OK     
Example: 
 `localhost:8080/meetings/removeMeeting?fromTime=1592924400`     
Output - 200 OK       
  
### Remove Meeting (fromTime and meetingTitle):  
API URL – `localhost:8080/meetings/removeMeeting?meetingTitle=title1&fromTime=epochtime`  
Request method- **DELETE**    
Expected response- 200 OK  
Example: `localhost:8080/meetings/removeMeeting?meetingTitle=Orientation&fromTime=1592924400`   
Output - 200 OK   

### GetNextMeeting:  
 API URL - `localhost:8080/meetings/nextMeeting`  
Request method - **GET**  
Expected response - Meeting payload of the next scheduled meeting from current timestamp   
Example input: `localhost:8080/meetings/nextMeeting`  
Output: 
```
{
    "id": "5e79c68b-a5b1-4535-b901-a15983e6103a", 
    "start": "2020-06-23T10:00:00", 
    "end": "2020-06-23T11:00:00", 
    "title": "Orientation" 
}
```   
  
### Migration to prod database  
In-memory database can be converted to any relational database which supports recursion like MYSQL, PostGres SQL or graph data structure like Neo4j. Appropriate changes are required in **meetingRepository** class.
  


- # CS 122B: Projects in Databases and Web Applications: Project 5

- # General
    - #### Team#: 125
    
    - #### Names: Kanglan Tang, Susie Liu
    
    - #### Project 5 Video Demo Link: 

    	https://youtu.be/4bfMZCLErYg

    - #### Instruction of deployment: 

		1. open your terminal and type "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125.git"; 
	    2. prepare a movie data; 
	    3. run "source create-table.sql" on your terminal; 
	    4. run your movie data file to populate your moviedb database;
	    5. run "mvn package" to generate .war file
	    6. copy the .war file into Tomcat webapp folder to deploy the application
	    7. use port 80 for load balancer (scaled version), port 8080 for http, and port 8443 for https (single version)



    - #### Collaborations and Work Distribution (Project 5): 

    	Kanglan Tang:
    		
    		time statements for measuring TS and TJ, log_processing.java, Apache JMeter test
    	
    	Susie Liu:
    		
    		connection pooling, mysql master/slave replication, load balancing, aws and gcp setup


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/AddMovieServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/AddStarServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/BrowseServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/DashboardServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/LoginServlet.java
    	
    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/MovieListServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/PaymentServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/SingleMovieServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/SingleStarServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/TitleSuggestion.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/UpdateSalesServlet.java

    	Where to enable JDBC Connection Pooling:
    	
    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/web/META-INF/context.xml
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.

    	In /META-INF/context.xml, we define data source with information about the database, username, password, and pooling configuration of the MySQL. JDBC uses credentials to create a connection pool. Each servlet leases connections from this pool when needed and returns when the task is done.

    
    - #### Explain how Connection Pooling works with two backend SQL.

    	We define two data sources in /META-INF/context.xml, jdbc/moviedb_master and jdbc/moviedb. jdbc/moviedb
    	connects to localhost moviedb, while jdbc/moviedb_master connects to master's mysql moviedb. 

    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    	Master SQL:

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/DashboardServlet.java

    	https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/UpdateSalesServlet.java

    	Master/Slave SQL (assigned by load balancer): 

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/AddMovieServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/AddStarServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/BrowseServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/LoginServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/MovieListServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/PaymentServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/SingleMovieServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/SingleStarServlet.java

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/TitleSuggestion.java

		Where to specify the urls of Master/Slave:

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/web/META-INF/context.xml


    - #### How read/write requests were routed to Master/Slave SQL?

    	Both master and slave tomcat server have access to both master and slave database. 

    	All write requests will look up urls with name "jdbc/moviedb_master" in context and be routed to Master SQL, no matter whether it is in Master instance or Slave instance. Read requests will look up urls with name "jdbc/moviedb" in context and be sent to localhost, which will route the request to either Master or Slave SQL. 
    

- # JMeter TS/TJ Time Logs

    - #### How to get log files:

    	1. *Comment out 34-49 lines and uncommnet 29-39 lines in LoginFilter.java to disable login filter for Jmeter test*
    	2. Run Jmeter Test (we set loop count=2642, recycle eof => false, and stop thread eof => true)
    	3. Log file (log1.txt) will be generated in the tomcat/webapps/cs122b-spring20-team125/
    	4. (Rename the file or save/move it to a different place) 

    - #### log files: 
	
	- Single-instance cases:
	
		Use HTTP, 1 thread in JMeter.
	
		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/log_files/single/log-thread1.txt

		Use HTTP, 10 threads in JMeter.

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/log_files/single/log-thread10.txt
	
		Use HTTPS, 10 threads in JMeter.

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/log_files/single/log-thread10-https.txt
	
		Use HTTP, without using Connection Pooling, 10 threads in JMeter.

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/log_files/single/log-thread10-ncp.txt


	- Scaled-version cases:(separate log file for master and slave)

		Use HTTP, 1 thread in JMeter. 
	
		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/tree/master/log_files/aws-1thread

		Use HTTP, 10 threads in JMeter.
	
		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/tree/master/log_files/aws-10thread

		Use HTTP, without using Connection Pooling, 10 threads in JMeter.

		https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/tree/master/log_files/aws-noconnectionpooling
	

    - #### How to use log_processing.java script to process log files:

    	1. open java_test/src
    	2. Compile log_processing.java: javac com/company/log_processing.java
    	3. Run log_processing: java com.company.log_processing <logFile1 logFile2 ...>


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                       |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|----------------|----------------------------------------------------|
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/img/single-http-1thread.png)   | 148                        | 5.8392E7                            | 5.8039E7        | less average query time, but longer total time     |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/img/single-http-10thread.png)   | 590                        | 4.80552E8                           | 4.80401E8       | longer average query time, but larger throughput   |
| Case 3: HTTPS/10 threads                       | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/img/single-https-10thread.png)   | 609                        | 4.94847E8                           | 4.9457E8        | slightly more time than http because SSL handshake |
| Case 4: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/img/single-noconnectionpooling-10thread.png)   | 591                        | 4.99196E8                           | 4.99042E8       | more time b/c every request builds new connection  |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)**| **Analysis**                                        |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------|-----------------------------------------------------|
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/img/loadbalancer-1thread.png)   | 134                        | 5.8165E7                            | 5.792E7        | slightly less than single-instance                  |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/img/loadbalancer-10thread.png)   | 294                        | 2.11172E8                           | 2.11001E8      | less than single b/c requests are balanced to M/S   |
| Case 3: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/img/loadbalancer-noconnectionpooling-10thread.png)   | 306                        | 2.23329E8                           | 2.23174E8      | more time b/c every request builds new connection   |


- # previous projects:

1. Demo video URL 

	    (Project 1): 
	
		https://www.youtube.com/watch?v=xq9lA9oqXO4&feature=youtu.be
	
		(Project 2): 
	
		https://youtu.be/Xd9wJLdDiis
	
    	(Project 3): 
	
		https://youtu.be/nEmwS-B2_bA
	    
	    (Project 4): 
	
		https://youtu.be/YknaC1sOyK4

	


2. How to deploy our application with Tomcat: 

	    a. open your terminal and type "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125.git"; 
	    b. prepare a movie data; 
	    c. run "source create-table.sql" on your terminal; 
	    d. run your movie data file to populate your moviedb database;
	    e. run "mvn package" to generate .war file
	    f. copy the .war file into Tomcat webapp folder to deploy the application
	    
	    
   How to deploy our android app:

		a. build apk file
		b. use apk file to install application on android device or emulator
		c. connect to tomcat server and run app


3. Substring matching design

	    Users keyword will be searched via the following substring matching design:
			like 'ABC%': All strings that start with 'ABC'. E.g. 'ABCD' and 'ABCABC'.
			like '%XYZ': All strings that end with 'XYZ'. E.g. 'WXYZ' and 'ZZXYZ'.
			like '%AN%': All strings that contain the pattern 'AN' anywhere. E.g. 'LOS ANGELES' and 'SAN FRANCISCO'.


4. Queries with Prepared Statements


        AddMovieServlet.java
        https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/AddMovieServlet.java

        AddStarServlet.java
        https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/AddStarServlet.java
        
        LoginServlet.java
        https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/LoginServlet.java

        MovieListServlet.java
        https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/MovieListServlet.java
	
        MainParser.java
        https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/sax-parser/src/main/java/MainParser.java
        
        PaymentServlet.java
        https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/PaymentServlet.java
        
        UpdateSalesServlet.java
        https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/web-module/src/main/java/UpdateSalesServlet.java
        

5. Parsing Time Optimization

	We used two different optimization strategies including
	
		1. Save parsed data into files and use "load data" to store in sql
			files that save newly parsed data: newStars.txt, new Movies.txt, new StarsInMovies.txt

		2. Retrieve queries and store in local hash table
			hash table built for genres in MainParser.java, stars in CastParser.java

	Parsing time comparison
	
		naive approach with prepared statement and "set auto-commit off"
			1173.32s  ~approximately: 19m33s
		1. After enabled "load data": 
			566.95s   ~approximately: 9m25s 
		2. After enabled hash table and "load data"
			38.04s
	


6. Parsing assumption & Inconsistent data report

        Parsing assumption:
        actor64.xml:
            for each actor, we retrieve stagename as star's name and dob as star's birth year
            star without name is considered inconsistent when inserting to database
        
        main32.xml:
            for each movie, we retrieve dirname as director, t as title, year as year, cat as genre and fid as movie's fid in our fidMid table
            movie without fid, title, director, year, or valid genre is considered inconsistent when inserting to database
            genre without matching category name on http://infolab.stanford.edu/pub/movies/doc.html#CATS is considered inconsistent when inserting to database
            
        actor64.xml:
            for each movie, we retrieve "is" as director, t as title, a as star, and f as movie's fid in our fidMid table
            star&movie pair without matching star name in star table, or matching movie title,year,and director, is considered inconsistent when inserting to database
        
        
	    Inconsistent data are generated by parser and stored in report.txt
	    link: https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125/blob/master/sax-parser/report.txt




7. Fuzzy Search Implementation

		take the union of result from full text search and edit distance search
		
		for fuzzy search edit distance, we set our threshold based on the query length, 
		we allow users to make:
			1. 1 typo if word length is less or equal to 3
			2. 2 typos if word length is greater than 3 and less or equal to 6
			3. 3 typos if word length is greater than 6


8. Project1 Each Member's Contribution:

		Kanglan Tang: 
			1. wrote movie list page;
			2. wrote single movie page;
			3. linked movie list page and single movie page;
			4. added .gitignore file
			5. wrote README.md
		Susie Liu:
			1. wrote movie list page;
			2. wrote single star page;
			3. linked movie list page and single star page;
			4. modified css;
			5. created create_table.sql



   Project 2 Each Member's Contribution:
	    
	    Kanglan Tang: 
		    Logout, main page, browsing, single page sorting, jump functionality, shopping cart page, confirmation page, place order action
	    Susie Liu:
		    Login, login filter, searching, prev/next button, number of listings, movielist sorting, payment page, place order action


   Project 3 Each Member's Contribution:
	    
	    Kanglan Tang: 
		    password encryption, prepared statement, dashboard employee credentials and add movie, parsing main & actor & casts, optimization using load data, inconsistency data report
	    Susie Liu:
		    Recaptcha implementation, https redirection, dashboard display metadata and add star, parsing main & actor & casts, optimization using hashtable, inconsistency data report
		    
		    

   Project 4 Each Member's Contribution:
	    
	    Kanglan Tang: 
		    autocomplete UI, autocomplete search, android login, android main page and search, android movielist page display, fuzzy search
	    Susie Liu:
		    full text search, autocomplete jump, javascript console log, android movielist page jump and pagination, android single page


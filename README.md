CS 122B: Projects in Databases and Web Applications 

Group members:
Kanglan Tang; 
Susie Liu


1. Demo video URL 
	(Project 1): https://www.youtube.com/watch?v=xq9lA9oqXO4&feature=youtu.be
	(Project 2): https://youtu.be/Xd9wJLdDiis
(In the demo video, We accidentally showed the wrong saleDate in the beginning of the video! Later, we demonstrated adding to cart again and showed the updated table. Sorry for the inconvenience!)


2. How to deploy our application with Tomcat: 

	a. open your terminal and type "git clone https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-125.git"; 

	b. prepare a movie data; 

	c. run "source create-table.sql" on your terminal; 

	d. run your movie data file to populate your moviedb database;

	e. run "mvn package" to generate .war file

	f. copy the .war file into Tomcat webapp folder to deploy the application


3. Substring matching design
	Users keyword will be searched via the following substring matching design:
		like 'ABC%': All strings that start with 'ABC'. E.g. 'ABCD' and 'ABCABC'.
		like '%XYZ': All strings that end with 'XYZ'. E.g. 'WXYZ' and 'ZZXYZ'.
		like '%AN%': All strings that contain the pattern 'AN' anywhere. E.g. 'LOS ANGELES' and 'SAN FRANCISCO'.

4. Project1 Each Member's Contribution:

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

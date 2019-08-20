1. Assumption
	DB Name: eliteaaa
	User Name: eliteaaa
	Password: eliteaaa

2. For configure scheduler in Postgres we need to install "pgagent_96.x86_64".
	$su - root
	#yum install pgagent_96.x86_64 
	
3. Create eliteaaa applicationm as superuser.
	
	- $su - postgres

	- Create directory for user
		$mkdir -p /path_of_postgres/eliteaaa(username)
	
	- $psql -U postgres
		
		postgres=#\o eliteaaa-schema.log
		postgres=# \i eliteaaa-schema.sql
		eliteaaa=#\q

4. execute eliteaaa.sql, pgagent.sql within eliteaaa user.

		$psql -U eliteaaa -d eliteaaa
		eliteaaa=#show search_path;
		Note: reseult must be eliteaaa

		eliteaaa=#\o eliteaaa.log
		eliteaaa=#\i eliteaaa.sql

		eliteaaa=#\o pgagent.log
		eliteaaa=#\i pgagent.sql

5. execute pkg_eliteaaa_ippool.sql within eliteaaa user.

		eliteaaa=#\o pkg_eliteaaa_ippool.log
		eliteaaa=#\i pkg_eliteaaa_ippool.sql


Postrequisites:

6. Configure application scheduler
	$su - postgres
	$pgagent_96 hostaddr=<ip_of_postgres_server> user=eliteaaa dbname=eliteaaa password=eliteaaa

7. Verify genrated log files
	- eliteaaa-schema.log
	- eliteaaa.log
	- pgagent.log
	- pkg_eliteaaa_ippool.log
	
	-Note: Make sure there is no error in any of the above files.

#!/bin/csh

#!/bin/csh
set portspec=
set error=
set pwd='root'
#set mysqlcmd=/tracweb-main/soft/mysql4/bin/mysql
set mysqlcmd=mysql

set curdir=`pwd`

# create the additional tables
$mysqlcmd --user=root --password=$pwd --local-infile=1 --host=localhost --port=3306 chain << create_tables

DROP TABLE IF EXISTS day;
CREATE TABLE  day (
  chainid int(8),
  day date
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOAD DATA LOCAL INFILE "$curdir/workout_2012.tab"
IGNORE
INTO TABLE day
;

create_tables


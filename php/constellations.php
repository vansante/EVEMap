<?php

require('connect.php');

$sql  = "SELECT * ";
$sql .= "FROM mapConstellations ORDER BY constellationName ASC";

$result = mysql_query($sql) or die("<br><br>".$sql . "<br><br>".mysql_error());

while ($data = mysql_fetch_array($result, MYSQL_ASSOC)) {
	echo $data['constellationName'].','.$data['constellationID'].','.$data['regionID'].','.$data['x'].','.$data['y'].','.$data['z']."\n";
}

?>
<?php

require('connect.php');

$sql  = "SELECT * ";
$sql .= "FROM mapSolarSystemJumps";

$result = mysql_query($sql) or die("<br><br>".$sql . "<br><br>".mysql_error());

$done = array();

while ($data = mysql_fetch_array($result, MYSQL_ASSOC)) {
	if (!in_array($data['toSolarSystemID'].','.$data['fromSolarSystemID'], $done)) {
		echo $data['fromSolarSystemID'].','.$data['toSolarSystemID']."\n";
		
		$done[] = $data['fromSolarSystemID'].','.$data['toSolarSystemID'];
	}
}

?>
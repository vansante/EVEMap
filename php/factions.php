<?php

require('connect.php');

$sql  = "SELECT * ";
$sql .= "FROM chrFactions ORDER BY factionName ASC";

$result = mysql_query($sql) or die("<br><br>".$sql . "<br><br>".mysql_error());

while ($data = mysql_fetch_array($result, MYSQL_ASSOC)) {
	echo $data['factionName'].','.$data['factionID']."\n";
}

?>
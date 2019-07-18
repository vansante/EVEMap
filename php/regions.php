<?php

require('connect.php');

$sql  = "SELECT * ";
$sql .= "FROM mapRegions ORDER BY regionName ASC";

$result = mysql_query($sql) or die("<br><br>".$sql . "<br><br>".mysql_error());

while ($data = mysql_fetch_array($result, MYSQL_ASSOC)) {
	
	if ($data['regionID'] >= 11000000) {
		$data['y'] = $data['y'] - (3150 * 4.8445284569785e14);
	}
	
	echo $data['regionName'].','.$data['regionID'].','.$data['x'].','.$data['y'].','.$data['z'].',';
	if ($data['factionID'] == null) {
		echo 0;
	} else {
		echo $data['factionID'];
	}
	echo "\n";
}

?>
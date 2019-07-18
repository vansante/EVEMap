<?php

$classdata = file_get_contents('classdata.csv');
$wormholes = explode("\r\n", $classdata);

$classdata = array();
foreach ($wormholes as $wormhole) {
	$temp = explode(',', $wormhole);
	$classdata[$temp[0]] = array('class' => $temp[1], 'anomaly' => $temp[2]);
}

require('connect.php');

$sql  = "SELECT * ";
$sql .= "FROM mapSolarSystems ORDER BY solarSystemName ASC";

$result = mysql_query($sql) or die("<br><br>".$sql . "<br><br>".mysql_error());

while ($data = mysql_fetch_array($result, MYSQL_ASSOC)) {
	
	if (array_key_exists($data['solarSystemName'], $classdata)) {
		$data['class'] = $classdata[$data['solarSystemName']]['class'];
		if ($classdata[$data['solarSystemName']]['anomaly'] > 0) {
			$data['anomaly'] = $classdata[$data['solarSystemName']]['anomaly'];
		} else {
			$data['anomaly'] = 0;
		}
	} else if ($data['security'] < 0) {
		$data['class'] = 9;
		$data['anomaly'] = 0;
	} else if ($data['security'] >= 0 && $data['security'] < 0.45) {
		$data['class'] = 8;
		$data['anomaly'] = 0;
	} else {
		$data['class'] = 7;
		$data['anomaly'] = 0;
	}
	
	$sql = "SELECT COUNT(solarSystemID) AS planetcount FROM mapDenormalize WHERE groupID=7 AND solarSystemID=".$data['solarSystemID'];
	$data['planetcount'] = mysql_result(mysql_query($sql), 0, 'planetcount');
	
	$sql = "SELECT COUNT(solarSystemID) AS mooncount FROM mapDenormalize WHERE groupID=8 AND solarSystemID=".$data['solarSystemID'];
	$data['mooncount'] = mysql_result(mysql_query($sql), 0, 'mooncount');
	
	$sql = "SELECT COUNT(solarSystemID) AS beltcount FROM mapDenormalize WHERE groupID=9 AND solarSystemID=".$data['solarSystemID'];
	$data['beltcount'] = mysql_result(mysql_query($sql), 0, 'beltcount');
	
	echo $data['solarSystemName'].','.$data['solarSystemID'].','.$data['constellationID'].','.$data['x'].','.$data['y'].','.$data['z'].','.$data['security'].",".$data['planetcount'].','.$data['mooncount'].','.$data['beltcount'].','.$data['class'].','.$data['anomaly']."\n";
}

?>
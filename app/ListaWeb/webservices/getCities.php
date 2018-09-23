<?php
 
require_once '../includes/init.php'; 

$response = array();

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['jsonData']) ) {	
			
		$jsonData = $_POST['jsonData'];
		
		$json = json_decode($jsonData, true);
		
		$db = new DbOperations();
		
		$cities = array();
		$cities = $db->getCities();
		
		$db->disconnect();
		
		$response['error'] = false;		
		$response['cities'] = $cities;
		$response['message'] = $lang['cities_select_ok'];
					
	} else {
		$response['error'] = true;
		$response['message'] = $lang['required_fields'];
	}
		
		
} else {
	$response['error'] = true;
	$response['message'] = $lang['invalid_request'];
}

echo json_encode($response);
 
?>
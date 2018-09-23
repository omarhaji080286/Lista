<?php
 
require_once '../includes/init.php'; 

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['jsonData']) ) {	
			
	
		$db = new DbOperations();
		$result = $db->deleteAll();
		
		$group = array();
		
		switch($result){
			case SUCCESS :
				$response['error'] = false;
				$response['message'] = "All data deleted on server";
				
				break;
			case ERROR :
				$response['error'] = true;
				$response['message'] = $lang['error_message'];
				break;
		}
		$db->disconnect();
				
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
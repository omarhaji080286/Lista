<?php
 
require_once '../includes/init.php'; 

$response = array();

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['jsonData']) ) {	
			
		$jsonData = $_POST['jsonData'];
		
		$json = json_decode($jsonData, true);
						
		$currentServerUserId = $json['currentServerUserId'];	
		
				
		$db = new DbOperations();
		
		$YES = 1;
		$NO = 0;
		
		$update_available = $db->getUpdateAvailableByUserId($currentServerUserId);
		
		$categories = array();
		$goods = array();
		
		if ($update_available==$YES){

			$categories = $db->getUpdatedCategoriesFromServer($currentServerUserId);
			$goods = $db->getUpdatedGoodsFromServer($currentServerUserId);
				
			$result = $db->updateAvailableUpdateforUser($currentServerUserId);
			
			switch($result){
				case SUCCESS :
				$response['message'] = "update available";
				$response['error'] = false;
				break;
				case ERROR :
					$response['error'] = true;
					$response['message'] = $lang['error_message'];
				break;
			}
		
		} else {
			$response['message'] = $lang['no_update_available'];
			$response['error'] = false;
		}
		
		$response['categories'] = $categories;
		$response['goods'] = $goods;
		
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
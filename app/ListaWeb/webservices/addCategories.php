<?php
 
require_once '../includes/init.php'; 

$response = array();

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['jsonData']) ) {	
			
		$jsonData = $_POST['jsonData'];
		
		$json = json_decode($jsonData, true);
			
		$categories = $json['jsonCategories'];
		$serverUserId = $json['currentServerUserId'];		
					
		$db = new DbOperations();
		
		$result = $db->addCategories($categories, $serverUserId);
			
		switch($result){
			case SUCCESS :
				$response['error'] = false;	
				$response['message'] = $lang['add_category_ok'];
				$categoriesIds = array();
				foreach ($categories as $category) {
					$Ids = array();
					$Ids['deviceCategoryId'] = $category['categoryId'];
					$Ids['serverCategoryId'] = $db->getCategoryIdByServerUserIdAndDeviceCategoryId($serverUserId,
																								$category['categoryId']);
					array_push($categoriesIds, $Ids);
				}
				$response['categoriesIds'] = $categoriesIds;
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
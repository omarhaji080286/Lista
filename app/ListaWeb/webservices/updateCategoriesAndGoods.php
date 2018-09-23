<?php
 
require_once '../includes/init.php'; 

$response = array();

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['jsonData']) ) {	
			
		$jsonData = $_POST['jsonData'];
		
		$json = json_decode($jsonData, true);
		
		$updatedCategoriesOnMobile = array();
		$updatedGoodsOnMobile = array();
						
		$updatedCategoriesOnMobile = $json['jsonUpdatedCategories'];		
		$updatedGoodsOnMobile = $json['jsonUpdatedGoods'];
		$currentServerUserId = $json['currentServerUserId'];	
					
		$db = new DbOperations();
		
			$result = $db->updateCategoriesAndGoods($updatedCategoriesOnMobile, $updatedGoodsOnMobile, $currentServerUserId);

			switch($result){
				case SUCCESS :
					$response['error'] = false;
					$response['message'] = $lang['update_categories_and_goods_ok'];

					break;
				case ERROR :
					$response['error'] = true;
					$response['message'] = $lang['error_message'];
					break;
			}
		
		
		$response['error'] = false;		

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
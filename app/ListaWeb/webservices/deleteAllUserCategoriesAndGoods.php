<?php
 
require_once '../includes/init.php'; 

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['server_user_id']) && isset($_POST['server_co_user_id']) ) {	
			
		$server_user_id = $_POST['server_user_id'];
		$server_co_user_id = $_POST['server_co_user_id'];

		
		$db = new DbOperations();
		$result = $db->deleteAllUserCategoriesAndGoods($server_user_id);
		
		$group = array();
		
		switch($result){
			case SUCCESS :
				$response['error'] = false;
				$response['message'] = $lang['delete_user_categories_and_goods_ok'];
				
				$group = $db->getGroupByServerCoUserUserId($server_co_user_id);
				$response['group'] = $group;
				
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
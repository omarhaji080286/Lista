<?php
 
require_once '../includes/init.php'; 

$response = array();

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['server_group_id']) && isset($_POST['jsonData']) ) {	
			
		$server_group_id = $_POST['server_group_id'];
		$jsonData = $_POST['jsonData'];
		
		$json = json_decode($jsonData, true);
		
		$excludedCategoriesServerIds = array();
		$excludedGoodsServerIds = array();
					
		$excludedCategoriesServerIds = $json['categoriesServerIds'];		
		$excludedGoodsServerIds = $json['goodsServerIds'];		
		$currentServerUserId = $json['serverUserId'];
		
		$db = new DbOperations();
		
		$categories = array();
		$categories = $db->getGroupCategories($server_group_id, $excludedCategoriesServerIds, $currentServerUserId);
		
		$goods = array();
		$goods = $db->getGroupGoods($server_group_id, $excludedGoodsServerIds, $currentServerUserId);

		$response['error'] = false;		
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
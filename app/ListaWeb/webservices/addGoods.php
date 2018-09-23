<?php
 
require_once '../includes/init.php'; 

$response = array();

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['jsonData']) ) {	
			
		$jsonData = $_POST['jsonData'];
		
		$json = json_decode($jsonData, true);
			
		$goods = array();
		
		$goods = $json['jsonGoods'];
		$serverUserId = $json['currentServerUserId'];		
					
		$db = new DbOperations();
		
		$result = $db->addGoods($goods,$serverUserId);
			
		switch($result){
			case SUCCESS :
				$response['error'] = false;	
				$response['message'] = $lang['add_goods_ok'];
				$goodsIds = array();

				foreach ($goods as $good) {
					$Ids = array();
					$Ids['deviceGoodId'] = $good['goodId'];
					$Ids['serverGoodId'] = $db->getServerGoodIdByOriginatorIdAndDeviceId($serverUserId, $good['goodId']);
					
					array_push($goodsIds, $Ids);
				}
				$response['goodsIds'] = $goodsIds;
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
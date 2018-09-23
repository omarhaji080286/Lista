<?php
 
require_once '../includes/init.php'; 

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['email']) && isset($_POST['password'])
		&& isset($_POST['user_name']) && isset($_POST['sign_up_type']) ) {	
			
		$email = $_POST['email'];
		$password = MD5($_POST['password']);
		$user_name = $_POST['user_name'];
		$signUpType = $_POST['sign_up_type'];
	
		$db = new DbOperations();
		$result = $db->registerUser($email, $password, $user_name, $signUpType);
		switch($result){
			case SUCCESS :
				$response['error'] = false;
				$response['message'] = $lang['registring_user_ok'];
				$response['server_user_id'] = $db->getUserIdByEmail($email, $signUpType);
				$response['default_categories'] = $db->getdefaultCategories();
				$response['default_goods'] = $db->getdefaultGoods();
				
				break;
			case ERROR :
				$response['error'] = true;
				$response['message'] = $lang['error_message'];
				break;
			case EXISTING_ITEM :
				$response['error'] = true;
				$response['message'] = $lang['existing_user'];
				break;
		}
				
	} else {
		$response['error'] = true;
		$response['message'] = $lang['required_fields'];
	}
	$db->disconnect();	
		
} else {
	$response['error'] = true;
	$response['message'] = $lang['invalid_request'];
}

echo json_encode($response);
 
?>
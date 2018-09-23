<?php
 
require_once '../includes/init.php'; 

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['co_user_email']) && isset($_POST['email'])
		&& isset($_POST['device_co_user_id']) && isset($_POST['sign_up_type'])  ) {	
			
		$co_user_email = $_POST['co_user_email'];
		$email = $_POST['email'];
		$device_co_user_id = $_POST['device_co_user_id'];
		$signUpType = $_POST['sign_up_type'];

		
		$db = new DbOperations();
		$result = $db->addCoUser($co_user_email, $email, $device_co_user_id, $signUpType);
		
		switch($result){
			case SUCCESS :
				$response['error'] = false;
				$response['message'] = $lang['co_user_ok'];
				$response['server_co_user_id'] = $db->getServerCoUserId($email, $co_user_email);
				break;
			case ERROR :
				$response['error'] = true;
				$response['message'] = $lang['error_message'];
				break;
			case EXISTING_ITEM :
				$response['error'] = true;
				$response['message'] = $lang['existing_co_user'];
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
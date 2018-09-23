<?php
 
require_once '../includes/init.php'; 

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['email']) && isset($_POST['password']) && isset($_POST['sign_up_type']) ) {	
			
		$email = $_POST['email'];
		$password = $_POST['password'];
		$signUpType = $_POST['sign_up_type'];
		
		$db = new DbOperations();
		
		$serverUserId = $db->loginUser($email, $password, $signUpType);
		
		if ($serverUserId > 0) {
			$response['error'] = false;
			$response['message'] = $lang['login_success'];
			$response['server_user_id'] = $serverUserId ;
		} else {
			$response['error'] = true;
			$response['message'] = $lang['login_not_ok'];
		};
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
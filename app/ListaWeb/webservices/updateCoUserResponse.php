<?php
 
require_once '../includes/init.php'; 

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['server_co_user_id']) && isset($_POST['confirmation_status'])
		&& isset($_POST['server_user_id'])&& isset($_POST['server_group_id'])  ) {	
			
		$server_co_user_id = $_POST['server_co_user_id'];
		$confirmation_status = $_POST['confirmation_status'];
		$server_user_id = $_POST['server_user_id'];
		$server_group_id = $_POST['server_group_id'];
		
		$db = new DbOperations();
		if ($confirmation_status==1 || $confirmation_status==-1){
		
			$result = $db->updateCoUser($server_co_user_id, $confirmation_status, $db->HAS_RESPONDED_YES);
			switch($result){
				case SUCCESS :
					$response['error'] = false;
					$response['message'] = $lang['update_co_user_ok'];
					if ($confirmation_status==1){
						$db->updateUserGroup($server_user_id, $server_group_id);
					}
					break;
				case ERROR :
					$response['error'] = true;
					$response['message'] = $lang['error_message'];
					break;
			}
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
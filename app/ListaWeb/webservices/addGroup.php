<?php
 
require_once '../includes/init.php'; 

if ($_SERVER['REQUEST_METHOD']=='POST'){
		
	if (isset($_POST['group_name']) && isset($_POST['owner_email'])
		&& isset($_POST['server_owner_id']) && isset($_POST['sync_status'])) {	
			
		$group_name = $_POST['group_name'];
		$owner_email = $_POST['owner_email'];
		$server_owner_id = $_POST['server_owner_id'];
		$sync_status = $_POST['sync_status'];

		
		$db = new DbOperations();
		$result = $db->addGroup($group_name, $owner_email, $server_owner_id, $sync_status);
		
		
		switch($result){
			case SUCCESS :
				$response['error'] = false;
				$response['message'] = $lang['add_group_ok'];
				$response['server_group_id'] = $db->getGroupIdByOwnerId($server_owner_id);
				$db->updateUserGroup($server_owner_id, $response['server_group_id']);
				break;
			case ERROR :
				$response['error'] = true;
				$response['message'] = $lang['error_message'];
				break;
			case EXISTING_ITEM :
				$response['error'] = true;
				$response['message'] = $lang['existing_group'];
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